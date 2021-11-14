package com.ariche.domotest.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

public final class LogUtils {

    private static final String TAG = "DOMOTEST";

    public static void logInfo(final String message) {
        Log.i(TAG, message);
    }

    public static void logError(final String message, final Throwable throwable) {
        Log.e(TAG, message, throwable);
    }

    public static void logInfo(final String message, @NotNull final Object object) {
        logInfo(String.format("%s: %s", object.getClass(), message));
    }


}
