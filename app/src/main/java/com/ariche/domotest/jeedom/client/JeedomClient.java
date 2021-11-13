package com.ariche.domotest.jeedom.client;

import android.content.Context;

import com.ariche.domotest.http.client.HttpClient;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.utils.PropertyUtils;

import java.util.Properties;

public final class JeedomClient extends HttpClient {

    private String piAddress;
    private final String apiKey;
    private final String apiContext;

    public JeedomClient(final Context context) {
        final Properties properties = PropertyUtils.loadJeedomProperties(context);
        apiKey = properties.getProperty("api.key");
        apiContext = properties.getProperty("api.context");
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

}
