package com.ariche.domotest.freebox.client;

import android.content.Context;

import com.ariche.domotest.freebox.model.FreeBoxApiVersion;
import com.ariche.domotest.freebox.model.FreeBoxDevice;
import com.ariche.domotest.freebox.model.InterfaceOutput;
import com.ariche.domotest.freebox.model.LoginModelInput;
import com.ariche.domotest.freebox.model.LoginModelOutput;
import com.ariche.domotest.freebox.model.LoginStatusOutput;
import com.ariche.domotest.freebox.model.OpenSessionInput;
import com.ariche.domotest.freebox.model.OpenSessionOutput;
import com.ariche.domotest.http.client.HttpClient;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.utils.PreferenceHelper;
import com.ariche.domotest.utils.PropertyUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.util.Arrays;
import java.util.Properties;

import okhttp3.Headers;

public final class FreeBoxClient extends HttpClient {

    private static final String API_VERSION = "api_version";
    private static final String LOGIN = "api/v8/login/authorize";
    private static final String LOGIN_STATUS = "api/v8/login/authorize/";
    private static final String OPEN_SESSION = "api/v8/login/session";
    private static final String LIST_INTERFACES = "api/v8/lan/browser/interfaces";
    private static final String LIST_DEVICES = "api/v8/lan/browser/";

    private static final String AUTH_HEADER = "X-Fbx-App-Auth";

    private final Context context;
    private final Properties properties;
    private String sessionToken;

    public FreeBoxClient(final Context context) {
        super(PropertyUtils.loadFreeBoxProperties(context).getProperty("freebox.base_url"));
        this.context = context;
        this.properties = PropertyUtils.loadFreeBoxProperties(context);
        this.sessionToken = readFreeBoxSessionToken();
    }

    public FreeBoxApiVersion getApiVersion() throws HttpClientException {
        return super.getForType(API_VERSION, FreeBoxApiVersion.class);
    }

    public FreeBoxResponse<LoginModelOutput> login() throws HttpClientException {
        final String appId = properties.getProperty("app.id");
        final String appName = properties.getProperty("app.name");
        final String deviceName = properties.getProperty("app.device_name");
        final LoginModelInput loginModelInput = new LoginModelInput(appId, appName, deviceName);
        return super.postJSONForType(LOGIN, loginModelInput, new TypeReference<FreeBoxResponse<LoginModelOutput>>() {});
    }

    public FreeBoxResponse<OpenSessionOutput> openSession() throws HttpClientException {
        final String appId = properties.getProperty("app.id");
        final String appToken = properties.getProperty("app.token");

        final FreeBoxResponse<LoginStatusOutput> status = getLoginStatus();

        if (!status.isSuccess()) {
            return new FreeBoxResponse<>(false, null);
        }

        final String challenge = status.getResult().getChallenge();
        final String password = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, appToken.getBytes()).hmacHex(challenge.getBytes());

        final OpenSessionInput openSessionInput = new OpenSessionInput(appId, password);
        final FreeBoxResponse<OpenSessionOutput> a = super.postJSONForType(OPEN_SESSION, openSessionInput,
                new TypeReference<FreeBoxResponse<OpenSessionOutput>>() {});

        if (a.isSuccess()) {
            this.sessionToken = a.getResult().getSessionToken();
            storeSessionToken(this.sessionToken);
        }

        return a;
    }

    public FreeBoxResponse<LoginStatusOutput> getLoginStatus() throws HttpClientException {
        final String trackId = properties.getProperty("app.track_id");
        return super.getForType(LOGIN_STATUS + trackId, new TypeReference<FreeBoxResponse<LoginStatusOutput>>() {});
    }

    public FreeBoxResponse<InterfaceOutput[]> listInterfaces() throws HttpClientException {
        final Headers headers = new Headers.Builder().add(AUTH_HEADER, sessionToken).build();
        return super.getForType(LIST_INTERFACES, headers, new TypeReference<FreeBoxResponse<InterfaceOutput[]>>() {});
    }

    public FreeBoxResponse<FreeBoxDevice[]> listConnectedDevices(final String networkInterface) throws HttpClientException {
        final Headers headers = new Headers.Builder().add(AUTH_HEADER, sessionToken).build();
        final FreeBoxResponse<FreeBoxDevice[]> response = super.getForType(LIST_DEVICES + networkInterface, headers,
                new TypeReference<FreeBoxResponse<FreeBoxDevice[]>>() {});

        if (response.isSuccess()) {
            final String piName = properties.getProperty("raspberry.host");
            Arrays.stream(response.getResult())
                    .filter(device -> device.getName().equalsIgnoreCase(piName))
                    .findFirst()
                    .map(FreeBoxDevice::getAddress)
                    .ifPresent(this::storeRaspberryAddress);
        }

        return response;
    }

    public FreeBoxResponse<FreeBoxDevice[]> listPublicInterfaceConnectedDevices() throws HttpClientException {
        final String publicInterface = properties.getProperty("raspberry.interface");
        return listConnectedDevices(publicInterface);
    }

    private String readFreeBoxSessionToken() {
        return PreferenceHelper.getPreference(PreferenceHelper.FREE_BOX_SESSION_TOKEN, context);
    }

    private void storeRaspberryAddress(final String address) {
        PreferenceHelper.storePreference(PreferenceHelper.PI_ADDRESS, address, context);
    }

    private void storeSessionToken(final String sessionToken) {
        PreferenceHelper.storePreference(PreferenceHelper.FREE_BOX_SESSION_TOKEN, sessionToken, context);
    }
}
