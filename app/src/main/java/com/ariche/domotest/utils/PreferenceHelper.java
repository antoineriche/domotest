package com.ariche.domotest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class PreferenceHelper {

    private static final String ROOT_PREFERENCES_KEY = "com.ariche.domotest";

    public static final String PI_ADDRESS = "pi-address";
    public static final String FREE_BOX_SESSION_TOKEN = "free-box-session-token";

    public static String getPreference(final String key,
                                       final String defaultValue,
                                       final Context context) {
        return getRootPreferences(context).getString(key, defaultValue);
    }

    public static String getPreference(final String key,
                                       final Context context) {
        return getPreference(key, null, context);
    }

    public static boolean storePreference(final String key,
                                          final String value,
                                          final Context context) {
        final SharedPreferences prefs = getRootPreferences(context);
        if (!prefs.getString(key, "").equalsIgnoreCase(value)) {
            prefs.edit().putString(key, value).apply();
            return true;
        }
        return false;
    }

    public static void storeRaspberryAddress(final String address, final Context context) {
        storePreference(PreferenceHelper.PI_ADDRESS, address, context);
    }

    public static SharedPreferences getRootPreferences(final Context context) {
        return context.getSharedPreferences(ROOT_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static void registerSharedPreferencesListener(final Context context,
                                                         final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getRootPreferences(context).registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterSharedPreferencesListener(final Context context,
                                                           final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getRootPreferences(context).unregisterOnSharedPreferenceChangeListener(listener);
    }

    private PreferenceHelper() {
    }
}
