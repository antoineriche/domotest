package com.ariche.domotest.jeedom.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.ariche.domotest.http.client.HttpClient;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.jeedom.model.Scenario;
import com.ariche.domotest.ui.lights.LightsFragment;
import com.ariche.domotest.utils.PreferenceHelper;
import com.ariche.domotest.utils.PropertyUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public final class JeedomClient extends HttpClient implements SharedPreferences.OnSharedPreferenceChangeListener {

    /* WEATHER */
    private static final Integer WEATHER_CONDITION_1 = 36;
    private static final Integer WEATHER_CONDITION_2 = 38;
    private static final Integer WEATHER_CONDITION_3 = 40;
    private static final Integer WEATHER_CONDITION_4 = 42;
    private static final Integer WEATHER_TEMPERATURE = 16;
    private static final Integer WEATHER_SUNRISE = 22;
    private static final Integer WEATHER_SUNSET = 21;

    /* LIVING LIGHT */
    private static final Integer LIGHT_STATUS = 8;
    private static final Integer LIGHT_SWITCH_ON = 9;
    private static final Integer LIGHT_SWITCH_OFF = 10;
    private static final Integer LIGHT_POWER = 4;
    private static final Integer LIGHT_BRIGHTNESS = 5;
    private static final Integer LIGHT_CONSUMPTION = 6;

    /* LIVING CEILING LIGHT */
    private static final Integer CEILING_LIGHT_STATUS = 51;
    private static final Integer CEILING_LIGHT_SWITCH_ON = 52;
    private static final Integer CEILING_LIGHT_SWITCH_OFF = 53;
    private static final Integer CEILING_LIGHT_BRIGHTNESS_STATUS = 55;
    private static final Integer CEILING_LIGHT_UPDATE_BRIGHTNESS = 56;
    private static final Integer CEILING_LIGHT_UPDATE_COLOR = 58;
    private static final Integer CEILING_LIGHT_TEMPERATURE = 60;

    /* LIGHT PATH */
    private static final Integer LIGHT_PATH_STATUS = 12;
    private static final Integer LIGHT_PATH_SWITCH_ON = 13;
    private static final Integer LIGHT_PATH_SWITCH_OFF = 14;

    private static final Integer TV_STATUS = 98;
    private static final Integer TV_TOGGLE = 99;


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

    private String sendJeedomCommand(final int commandId) throws HttpClientException {
        return super.get("&type=cmd&id=" + commandId);
    }

    private String extractHourFromWeather(final String weatherResponse,
                                          final int hourDigit) {

        final String hour = weatherResponse.substring(0, hourDigit);
        final String minute = weatherResponse.substring(hourDigit);
        return hour + ":" + minute;
    }

    public List<String> getWeather() throws HttpClientException {
        final String condition1 = sendJeedomCommand(WEATHER_CONDITION_1);
        final String condition2 = sendJeedomCommand(WEATHER_CONDITION_2);
        final String condition3 = sendJeedomCommand(WEATHER_CONDITION_3);
        final String condition4 = sendJeedomCommand(WEATHER_CONDITION_4);
        final String temperature = sendJeedomCommand(WEATHER_TEMPERATURE) + " °C";

        final String sunrise = extractHourFromWeather(sendJeedomCommand(WEATHER_SUNRISE), 1);
        final String sunset = extractHourFromWeather(sendJeedomCommand(WEATHER_SUNSET), 2);
        return Arrays.asList(condition1, condition2, condition3, condition4, temperature, sunrise, sunset);
    }

    public String toggleLight(boolean on, final LightsFragment.Light light) throws HttpClientException {
        final int commandId;
        switch (light) {
            case LIGHT_PATH:
                commandId = on ? LIGHT_PATH_SWITCH_ON : LIGHT_PATH_SWITCH_OFF;
                break;
            case LIVING_CEILING:
                commandId = on ? CEILING_LIGHT_SWITCH_ON : CEILING_LIGHT_SWITCH_OFF;
                break;
            default:
            case LIVING:
                commandId = on ? LIGHT_SWITCH_ON : LIGHT_SWITCH_OFF;
                break;
        }
        return sendJeedomCommand(commandId);
    }

    public String toggleTV() throws HttpClientException {
        return sendJeedomCommand(TV_TOGGLE);
    }

    public boolean isLightOn(final LightsFragment.Light light) throws HttpClientException {
        switch (light) {
            case LIVING:
                return "1".equals(sendJeedomCommand(LIGHT_STATUS));
            case LIGHT_PATH:
                return "1".equals(sendJeedomCommand(LIGHT_PATH_STATUS));
            case LIVING_CEILING:
                return "1".equals(sendJeedomCommand(CEILING_LIGHT_STATUS));
            default:
                return false;
        }
    }

    public boolean isTVOn() throws HttpClientException {
        return "1".equals(sendJeedomCommand(TV_STATUS));
    }

    public int getCeilingBrightnessValue() throws HttpClientException {
        return Integer.parseInt(sendJeedomCommand(CEILING_LIGHT_BRIGHTNESS_STATUS));
    }

    public int updateCeilingBrightnessValue(final int value) throws HttpClientException {
        int brightnessValue = Math.min(value, 254);
        brightnessValue = Math.max(0, brightnessValue);
        final String endpoint = "&type=cmd&id=" + CEILING_LIGHT_UPDATE_BRIGHTNESS + "&slider=" + brightnessValue;
        super.get(endpoint);
        return  brightnessValue;
    }

    public void updateCeilingColor(final String color) throws HttpClientException {
        final String endpoint = "&type=cmd&id=" + CEILING_LIGHT_UPDATE_COLOR + "&color=%23" + color.replace("#", "");
        super.get(endpoint);
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

    public static String buildPublicURL(final Context context) {
        final Properties properties = PropertyUtils.loadJeedomProperties(context);
        return properties.getProperty("api.public.base_url")
                .concat(":")
                .concat(properties.getProperty("api.public.port"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceHelper.PI_ADDRESS.equalsIgnoreCase(key)) {
            final String piAddress = PreferenceHelper.getPreference(key, context);
            setPiAddress(piAddress);
        }
    }

}
