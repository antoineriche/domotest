package com.ariche.domotest.jeedom.client;

import android.content.Context;
import android.content.SharedPreferences;

import com.ariche.domotest.http.client.HttpClient;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.jeedom.model.Scenario;
import com.ariche.domotest.utils.PreferenceHelper;
import com.ariche.domotest.utils.PropertyUtils;

import java.util.Arrays;
import java.util.List;
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
        PreferenceHelper.registerSharedPreferencesListener(context, this);
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

    public boolean isLightOn() throws HttpClientException {
        final String endPoint = "&type=cmd&id=67";
        return "1".equals(super.get(endPoint));
    }

    private String readRaspberryAddress() {
        return PreferenceHelper.getPreference(PreferenceHelper.PI_ADDRESS, context);
    }

    public List<Scenario> listScenarios() throws HttpClientException {
        final Scenario[] a = super.getForType("&type=scenario", Scenario[].class);
        return Arrays.asList(a.clone());
    }

    public void toggleScenario(final String scenarioId,
                               final boolean start) throws HttpClientException {
        final String action = start ? "activate" : "deactivate";
        super.get("&type=scenario&id="+scenarioId+"&action=" + action);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceHelper.PI_ADDRESS.equalsIgnoreCase(key)) {
            final String piAddress = PreferenceHelper.getPreference(key, context);
            logInfo("PI Address has been updated to: " + piAddress);
            setPiAddress(piAddress);
        }
    }

}
