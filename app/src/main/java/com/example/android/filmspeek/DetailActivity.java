package com.example.android.filmspeek;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.filmspeek.utilities.NetworkUtils;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Use Parceler library to unwrap the Parcelable and get the Film object from the Intent.
        Film film = Parcels.unwrap(getIntent().getParcelableExtra("film"));

        // If the Film object is not null, then set the values to the views.
        if (film != null) {
            ImageView posterImageView = findViewById(R.id.iv_detail_poster);
            TextView titleTextView = findViewById(R.id.tv_title);
            TextView releaseDateTextView = findViewById(R.id.tv_release_date);
            TextView averageVoteTextView = findViewById(R.id.tv_average_vote);
            TextView overviewTextView = findViewById(R.id.tv_overview);

            String posterUrlString = NetworkUtils.buildPosterUrlString(film.getPosterPath());
            GlideApp.with(this).load(posterUrlString).into(posterImageView);

            titleTextView.setText(film.getTitle());
            releaseDateTextView.setText(film.getReleaseDate());
            // Append the average vote point to the TextView.
            averageVoteTextView.append(film.getAverageVote());
            overviewTextView.setText(film.getOverview());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Make the UP button behave like the BACK button, which don't make the loader refresh.
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
