package com.example.android.filmspeek;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.android.filmspeek.utilities.NetworkUtils;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmAdapterViewHolder> {

    private List<Film> mFilmList;

    private final Context mContext;

    private final FilmAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface FilmAdapterOnClickHandler {
        void onClick(Film film);
    }

    /**
     * Creates a FilmAdapter.
     *
     * @param context      The Context that Glide needs.
     * @param filmList     The List of Film object.
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public FilmAdapter(Context context, List<Film> filmList, FilmAdapterOnClickHandler clickHandler) {
        mContext = context;
        mFilmList = filmList;
        mClickHandler = clickHandler;
    }

    /**
     * Inner class that defines the cache of the children views for a forecast list item.
     */
    public class FilmAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView mPosterImageView;

        public FilmAdapterViewHolder(View view) {
            super(view);

            mPosterImageView = view.findViewById(R.id.iv_poster);

            // Set an OnClickListener to the list item view.
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param view that was clicked.
         */
        @Override
        public void onClick(View view) {
            mClickHandler.onClick(mFilmList.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public FilmAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.poster_list_item, parent, false);

        return new FilmAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmAdapterViewHolder holder, int position) {
        // Use Glide Generated API to load the poster and display it into the ImageView with
        // the format of CenterCrop and RoundedCorners.
        GlideApp.with(mContext)
                .load(NetworkUtils.buildPosterUrlString(mFilmList.get(position).getPosterPath()))
                .transforms(new CenterCrop(), new RoundedCorners(
                        (int) mContext.getResources().getDimension(R.dimen.poster_corner_radius)))
                .into(holder.mPosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mFilmList == null) {
            return 0;
        }
        return mFilmList.size();
    }

    public void clear() {
        mFilmList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Film> filmList) {
        mFilmList.addAll(filmList);
        notifyDataSetChanged();
    }
}
