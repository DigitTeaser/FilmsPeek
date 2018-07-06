package com.example.android.filmspeek.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.filmspeek.Film;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * These utilities are used to communicate with the Movie Database API and parse the data received.
 */
public final class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static final String FILM_BASE_URL = "https://api.themoviedb.org/3/movie";

    /**
     * The Movie Database API Key defines here. DO NOT share the API Key at all times.
     */
    private static final String api_key = "YOUR_OWN_API_KEY";

    public static final String sort_by_popularity = "popular";

    public static final String sort_by_rate = "top_rated";

    private static final String API_KEY_PARAM = "api_key";

    private static final String PAGE_PARAM = "page";

    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p";

    private static final String POSTER_SIZE_PARAM = "w185";

    public static int current_page, total_pages;

    public static String buildPosterUrlString(String posterPath) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(POSTER_BASE_URL).newBuilder();
        urlBuilder.addEncodedPathSegments(POSTER_SIZE_PARAM + posterPath);

        return urlBuilder.build().toString();
    }

    public static String buildFilmUrlString(String sort, int page) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(FILM_BASE_URL).newBuilder();
        urlBuilder.addPathSegment(sort);
        urlBuilder.addQueryParameter(API_KEY_PARAM, api_key);
        urlBuilder.addQueryParameter(PAGE_PARAM, Integer.toString(page));

        return urlBuilder.build().toString();
    }

    public static List<Film> fetchFilmData(String requestUrlString) {
        // Instantiate an OkHttpClient to fetch film data from the Movie Database API.
        // It should be a singleton.
        OkHttpClient okHttpClient = new OkHttpClient();

        String responseData = null;
        // Set a synchronous network call through OkHttp library
        // and get response JSON results if succeeded.
        try {
            Request request = new Request.Builder().url(requestUrlString).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                responseData = response.body().string();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request. ", e);
        }

        return extractFeatureFromJson(responseData);
    }

    private static List<Film> extractFeatureFromJson(String responseData) {

        final String TMDB_CURRENT_PAGE="page";
        final String TMDB_TOTAL_PAGES = "total_pages";

        /* Film information. Each film's info is an element of the "results" array */
        final String TMDB_RESULTS = "results";

        final String TMDB_TITLE = "title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_OVERVIEW = "overview";

        // If the JSON string is empty or null, return early.
        if (TextUtils.isEmpty(responseData)) {
            return null;
        }

        // Create an empty ArrayList that can start adding Film to.
        List<Film> filmsList = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(responseData);

            // Get the page info of the response data.
            current_page = baseJsonResponse.getInt(TMDB_CURRENT_PAGE);
            total_pages = baseJsonResponse.getInt(TMDB_TOTAL_PAGES);

            JSONArray resultsJson = baseJsonResponse.getJSONArray(TMDB_RESULTS);

            for (int i = 0; i < resultsJson.length(); i++) {
                JSONObject currentFilm = resultsJson.getJSONObject(i);
                String title = currentFilm.getString(TMDB_TITLE);
                String releaseDate = currentFilm.getString(TMDB_RELEASE_DATE);
                String posterPath = currentFilm.getString(TMDB_POSTER_PATH);
                String averageVote = currentFilm.getString(TMDB_VOTE_AVERAGE);
                String overview = currentFilm.getString(TMDB_OVERVIEW);

                filmsList.add(new Film(title, releaseDate, posterPath, averageVote, overview));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Film Json results. ", e);
        }

        return filmsList;
    }
}
