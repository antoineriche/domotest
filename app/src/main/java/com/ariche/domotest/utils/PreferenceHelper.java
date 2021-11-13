package com.ariche.domotest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    private static final String ROOT_PREFERENCES_KEY = "com.ariche.domotest";

    public static String getPreference(final String key,
                                       final String defaultValue,
                                       final Context context) {
        return getRootPreferences(context).getString(key, defaultValue);
    }

    public static void storePreference(final String key,
                                       final String value,
                                       final Context context) {
        final SharedPreferences prefs = getRootPreferences(context);
        prefs.edit().putString(key, value).apply();
    }

    private static SharedPreferences getRootPreferences(final Context context) {
        return context.getSharedPreferences(ROOT_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    private PreferenceHelper() {
    }
}
