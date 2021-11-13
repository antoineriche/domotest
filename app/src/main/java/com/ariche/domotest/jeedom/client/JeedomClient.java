package com.ariche.domotest.jeedom.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ariche.domotest.http.client.HttpClient;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.utils.PreferenceHelper;
import com.ariche.domotest.utils.PropertyUtils;

import java.util.Objects;
import java.util.Properties;

import static com.ariche.domotest.utils.LogUtils.logInfo;

public final class JeedomClient extends HttpClient implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String piAddress;
    private final Context context;
    private final String apiKey;
    private final String apiContext;

    public JeedomClient(final Context context) {
        final Properties properties = PropertyUtils.loadJeedomProperties(context);
        this.apiKey = properties.getProperty("api.key");
        this.context = context;
        this.apiContext = properties.getProperty("api.context");
        final String raspberryAddress = readRaspberryAddress();
        if (Objects.nonNull(raspberryAddress)) {
            setPiAddress(raspberryAddress);
        }
    }

    public void setPiAddress(String piAddress) {
        this.piAddress = piAddress;
        final String baseUrl = "http://"
                .concat(piAddress)
                .concat("/")
                .concat(apiContext)
                .concat("?apikey=")
                .concat(apiKey);
        super.setBaseUrl(baseUrl);
    }

    public String getWeather() throws HttpClientException {
        final String endPoint = "&type=cmd&id=43";
        return super.get(endPoint);
    }

    public String toggleLight(boolean on) throws HttpClientException {
        final String commandId = on ? "68" : "69";
        final String endPoint = "&type=cmd&id=" + commandId;
        return super.get(endPoint);
    }

    private String readRaspberryAddress() {
        return PreferenceHelper.getPreference("pi-address", null, context);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        logInfo(s);
    }
}
