package com.ariche.domotest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;

public final class PreferenceHelper {

    private static final String ROOT_PREFERENCES_KEY = "com.ariche.domotest";

    public static final String PI_ADDRESS = "pi-address";
    public static final String USE_WIFI = "use-wifi";
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

    public static boolean getBooleanPreference(final String key,
                                               final Context context) {
        return getRootPreferences(context).getBoolean(key, false);
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

    public static boolean storePreference(final String key,
                                          final boolean value,
                                          final Context context) {
        final SharedPreferences prefs = getRootPreferences(context);
        if (!prefs.getBoolean(key, false) == value) {
            prefs.edit().putBoolean(key, value).apply();
            return true;
        }
        return false;
    }

    public static void storeRaspberryAddress(final String address, final Context context) {
        storePreference(PreferenceHelper.PI_ADDRESS, address, context);
    }

    public static void storeUseWiFi(final boolean useWiFi, final Context context) {
        storePreference(PreferenceHelper.USE_WIFI, useWiFi, context);
    }

    public static SharedPreferences getRootPreferences(final Context context) {
        return context.getSharedPreferences(ROOT_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static void registerSharedPreferencesListener(final Context context,
                                                         final SharedPreferences.OnSharedPreferenceChangeListener... listeners) {
        Arrays.stream(listeners).forEach(listener ->
                getRootPreferences(context).registerOnSharedPreferenceChangeListener(listener));
    }

    public static void unregisterSharedPreferencesListener(final Context context,
                                                           final SharedPreferences.OnSharedPreferenceChangeListener... listeners) {
        Arrays.stream(listeners).forEach(listener ->
            getRootPreferences(context).unregisterOnSharedPreferenceChangeListener(listener));
    }

    private PreferenceHelper() {
    }
}
