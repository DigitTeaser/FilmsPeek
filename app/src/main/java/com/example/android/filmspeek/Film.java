package com.example.android.filmspeek;

import org.parceler.Parcel;

/**
 * A Film object contains information related to a film.
 * it includes the title, release date, poster path, average vote and overview.
 *
 * Note: This class was Annotated with the @Parcel decorator, which means it can be wrapped up into
 *       a Parcelable through the Parceler library.
 */
@Parcel
public class Film {

    /**
     * Variables need to be in public fields so they can be detected during the annotation.
     */
    String mTitle, mReleaseDate, mPosterPath, mAverageVote, mOverview;

    /**
     * Empty constructor needed by the Parceler library.
     */
    public Film() {
    }

    public Film(String title, String releaseDate, String posterPath,
                String averageVote, String overview) {
        mTitle = title;
        mReleaseDate = releaseDate;
        mPosterPath = posterPath;
        mAverageVote = averageVote;
        mOverview = overview;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getAverageVote() {
        return mAverageVote;
    }

    public String getOverview() {
        return mOverview;
    }
}
