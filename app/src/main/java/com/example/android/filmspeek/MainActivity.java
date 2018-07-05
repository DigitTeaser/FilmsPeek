package com.example.android.filmspeek;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.filmspeek.FilmAdapter.FilmAdapterOnClickHandler;
import com.example.android.filmspeek.data.SortPreferences;
import com.example.android.filmspeek.utilities.DisplayUtils;
import com.example.android.filmspeek.utilities.NetworkUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FilmAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Film>> {

    /**
     * Constant value for the films loader ID, which can be any integer.
     */
    private static final int FILMS_LOADER_ID = 1;

    private Menu mOptionsMenu;

    private GridLayoutManager mLayoutManager;
    private FilmAdapter mFilmAdapter;

    private SwipeRefreshLayout mLoadingIndicator;

    /**
     * Indicator whether the app is loading more data when users reach the bottom of the list.
     */
    private boolean mIsLoading = false;

    /**
     * This integer set the page parameter of the query API. One by default.
     */
    private int mRequestPage = 1;

    /**
     * These three integers help the app estimates whether the list reach bottom or not.
     */
    private int mPastVisibleItems, mVisibleItemCount, mTotalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.rv_list);

        // Get the GridLayoutManager and set it to the RecyclerView.
        // The column numbers were calculated in DisplayUtils class.
        mLayoutManager = new GridLayoutManager(this, DisplayUtils.calculateListColumns(this));
        recyclerView.setLayoutManager(mLayoutManager);
        // The child layout size in the RecyclerView do not change.
        recyclerView.setHasFixedSize(true);

        // Get the LayoutAnimation and set it to the RecyclerView.
        LayoutAnimationController animation =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(animation);

        // Create a new adapter for the RecyclerView by passing in the context and the click handler,
        // both from this MainActivity.
        mFilmAdapter = new FilmAdapter(this, new ArrayList<Film>(), this);
        recyclerView.setAdapter(mFilmAdapter);

        // When the list was scrolled to the bottom, load one more page of data.
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // If the list is still loading, return early.
                if (mIsLoading) {
                    return;
                }

                // Check for the scroll down action.
                if (dy > 0) {
                    mVisibleItemCount = mLayoutManager.getChildCount();
                    mTotalItemCount = mLayoutManager.getItemCount();
                    mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    // Only when the list reach the bottom and there is Internet Connection,
                    // then the app load more data.
                    if (((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) && isConnected()) {
                        // Set the loading indicator to true, because it's about to load data.
                        mIsLoading = true;

                        if (mRequestPage < NetworkUtils.total_pages) {
                            // Add the page by one to request the next page of data.
                            mRequestPage = NetworkUtils.current_page + 1;
                        }
                        getLoaderManager().restartLoader(FILMS_LOADER_ID, null, MainActivity.this);
                    }
                }
            }
        });

        mLoadingIndicator = findViewById(R.id.swipe_container);
        mLoadingIndicator.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mLoadingIndicator.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Only when there is Internet connection, refresh the data.
                if (!isConnected()) {
                    mLoadingIndicator.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                } else {
                    // Set the page to one, because the list is at the top right now.
                    mRequestPage = 1;
                    getLoaderManager().restartLoader(FILMS_LOADER_ID, null, MainActivity.this);
                }
            }
        });

        // When there are contents on screen, maintain them when users rotate their device.
        boolean hasContent = false;
        if (savedInstanceState != null && savedInstanceState.getInt("list_item_count") != 0) {
            hasContent = true;
        }

        if (!isConnected() && !hasContent) {
            setErrorView(true, R.string.no_connection);
        } else {
            mLoadingIndicator.setRefreshing(true);

            getLoaderManager().initLoader(FILMS_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("list_item_count", mFilmAdapter.getItemCount());

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<List<Film>> onCreateLoader(int i, Bundle bundle) {

        // Set the query url string using the method from NetworkUtils class.
        // There are two input parameters: sort and page.
        String urlString = NetworkUtils.buildFilmUrlString(
                SortPreferences.getPreferredApiSort(this), mRequestPage);

        return new FilmsLoader(this, urlString);
    }

    @Override
    public void onLoadFinished(Loader<List<Film>> loader, List<Film> filmsList) {

        // Tell the RecyclerView's onScrollListener that the data loading is done.
        mIsLoading = false;

        mLoadingIndicator.setRefreshing(false);

        // Only when there is a valid list of Film, add them to the adapter's data set.
        if (filmsList != null && !filmsList.isEmpty()) {
            if (mRequestPage == 1) {
                mFilmAdapter.clear();
                mLayoutManager.scrollToPosition(0);
            }
            mFilmAdapter.addAll(filmsList);

            setErrorView(false, null);
        } else if (mFilmAdapter.getItemCount() == 0) {
            if (!isConnected()) {
                setErrorView(true, R.string.no_connection);
            } else {
                setErrorView(true, R.string.something_wrong);
            }
        } else {
            if (!isConnected()) {
                Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Film>> loader) {
        mFilmAdapter.clear();
    }

    /**
     * This method is overridden by MainActivity class in order to handle RecyclerView item clicks.
     */
    @Override
    public void onClick(Film film) {
        Intent intent = new Intent(this, DetailActivity.class);
        // Wrap the Film object and put it into the Intent's extra.
        intent.putExtra("film", Parcels.wrap(film));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        mOptionsMenu = menu;

        int menuItemId = SortPreferences.getPreferredMenuSort(this);
        if (menuItemId == R.id.action_sort_by_rating || menuItemId == R.id.action_sort_by_popularity) {
            setSelectedSortOptionTextColor(menuItemId);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();

        switch (menuItemId) {
            case R.id.action_sort_by_popularity:
                setSelectedSortOptionTextColor(menuItemId);
                // Set the text color of the other option back to default.
                mOptionsMenu.findItem(R.id.action_sort_by_rating)
                        .setTitle(getResources().getString(R.string.action_sort_by_rating));

                // Set the preferred sort option to the SharedPreferences and switch the list.
                SortPreferences.setPreferredSort(this, menuItemId, NetworkUtils.sort_by_popularity);
                switchListSort();
                break;
            case R.id.action_sort_by_rating:
                setSelectedSortOptionTextColor(menuItemId);
                // Set the text color of the other option back to default.
                mOptionsMenu.findItem(R.id.action_sort_by_popularity)
                        .setTitle(getResources().getString(R.string.action_sort_by_popularity));

                SortPreferences.setPreferredSort(this, menuItemId, NetworkUtils.sort_by_rate);
                switchListSort();
                break;
            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * Set the text color of the selected sort option to the ColorAccent.
     *
     * @param menuItemId is the menu item ID.
     */
    private void setSelectedSortOptionTextColor(int menuItemId) {
        MenuItem menuItem = mOptionsMenu.findItem(menuItemId);
        SpannableString spannableString = new SpannableString(menuItem.getTitle().toString());
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)),
                0, spannableString.length(), 0);
        menuItem.setTitle(spannableString);
    }

    /**
     * Helper method that restart the loader to switch the list sort.
     */
    private void switchListSort() {
        if (isConnected()) {
            mLoadingIndicator.setRefreshing(true);
            mRequestPage = 1;
            getLoaderManager().restartLoader(FILMS_LOADER_ID, null, this);
        } else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method that tells whether the device is connected to Internet or not.
     *
     * @return true when the device is connected, false when it is not.
     */
    private boolean isConnected() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Helper Method that set the error state to the TextView
     *
     * @param visibility   means whether empty view is visible or not.
     *                     true means visible, false means gone.
     * @param textStringId is the text string id of the TextView.
     */
    private void setErrorView(boolean visibility, @Nullable Integer textStringId) {
        TextView errorTextView = findViewById(R.id.tv_error);
        if (visibility && textStringId != null) {
            errorTextView.setText(textStringId);
            errorTextView.setVisibility(View.VISIBLE);
        } else {
            errorTextView.setVisibility(View.GONE);
        }
    }
}
