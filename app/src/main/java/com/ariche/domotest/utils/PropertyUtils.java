package com.ariche.domotest.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {

    private static Properties loadPropertiesFromFile(final String fileName,
                                                     final Context context) {
        try {
            final Properties properties = new Properties();
            final InputStream inputStream = context.getAssets().open(fileName);
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Can not read properties from file '" + fileName + "'");
        }
    }

    public static Properties loadFreeBoxProperties(final Context context) {
        return loadPropertiesFromFile("freebox.yml", context);
    }

    public static Properties loadJeedomProperties(final Context context) {
        return loadPropertiesFromFile("jeedom.yml", context);
    }
}
