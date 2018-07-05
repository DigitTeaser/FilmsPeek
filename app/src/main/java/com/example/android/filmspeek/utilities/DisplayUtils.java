package com.example.android.filmspeek.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

import com.example.android.filmspeek.R;

/**
 * Class for calculating the column number of the RecyclerView list.
 */
public final class DisplayUtils {

    /**
     * Get the pixels that needed for displaying one CardView.
     *
     * @param context that needed to get the dimension resources.
     * @return the pixel integers that one CardView needed.
     */
    private static int getPixelsForOneCardView(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.poster_width);
    }

    /**
     * Get the pixels of the width of the screen.
     *
     * @param context that needed to get the DisplayMetrics object.
     * @return the pixel integers of the width of the screen.
     */
    private static int getPixelsForScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * Calculate the columns of the RecyclerView list.
     *
     * @param context that needed for two methods above.
     * @return the column integers.
     */
    public static int calculateListColumns(Context context) {
        return getPixelsForScreenWidth(context) / getPixelsForOneCardView(context);
    }
}
