package com.example.android.filmspeek.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.filmspeek.R;
import com.example.android.filmspeek.utilities.NetworkUtils;

public class SortPreferences {

    /**
     * Unique name for the SharedPreferences.
     */
    private static final String PREF_MENU_SORT = "menu_sort";
    private static final String PREF_API_SORT = "api_sort";

    /**
     * Set the selected sort option to the SharedPreferences.
     *
     * @param context       needed by the PreferenceManager.
     * @param menuSortId    indicates which item was selected, popularity or rating.
     * @param apiSortString specify the api_sort query parameter of the API.
     */
    public static void setPreferredSort(Context context, int menuSortId, String apiSortString) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_MENU_SORT, menuSortId)
                .putString(PREF_API_SORT, apiSortString)
                .apply();
    }

    /**
     * Get the preferred sort menu item ID from the SharedPreferences.
     *
     * @param context needed by the PreferenceManger.
     * @return the item ID, popularity by default.
     */
    public static int getPreferredMenuSort(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getInt(PREF_MENU_SORT, R.id.action_sort_by_popularity);
    }

    /**
     * Get the preferred sort's API string from the SharedPreferences.
     *
     * @param context needed by the PreferenceManger.
     * @return the api_sort query parameter of the API, popularity by default.
     */
    public static String getPreferredApiSort(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(PREF_API_SORT, NetworkUtils.sort_by_popularity);
    }
}
