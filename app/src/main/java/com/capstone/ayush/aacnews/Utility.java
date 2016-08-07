package com.capstone.ayush.aacnews;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ayush on 06-08-2016.
 */
public class Utility {
    public static void setSortBy(Context context, String choice) {
        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(R.string.content_authority),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor e = preferences.edit();
        e.putString("sortBy", choice);
        e.apply();
    }

    public static String getSortBy(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(R.string.content_authority),
                Context.MODE_PRIVATE);
        return preferences.getString("sortBy", "top");
    }

    public static void setSource(Context context, String choice) {
        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(R.string.content_authority),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor e = preferences.edit();
        e.putString("source", choice);
        e.apply();
    }

    public static String getSource(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(R.string.content_authority),
                Context.MODE_PRIVATE);
        return preferences.getString("source", "googlenews");
    }
}
