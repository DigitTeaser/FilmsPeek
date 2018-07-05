package com.example.android.filmspeek;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.filmspeek.utilities.NetworkUtils;

import java.util.List;

public class FilmsLoader extends AsyncTaskLoader<List<Film>> {

    /**
     * The Query URL.
     */
    private final String mUrl;

    public FilmsLoader(Context context, String url) {
        super(context);

        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Film> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        return NetworkUtils.fetchFilmData(mUrl);
    }
}
