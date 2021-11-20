package com.ariche.domotest.http.client;

import com.ariche.domotest.http.error.HttpClientError;
import com.ariche.domotest.http.error.HttpClientException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ariche.domotest.utils.LogUtils.logError;

@Getter
@Setter
public abstract class HttpClient {

    private String baseUrl;
    public final OkHttpClient okClient;
    protected ObjectMapper objectMapper;

    public HttpClient(final String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl.concat("/");
        this.okClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public HttpClient() {
        this.baseUrl = null;
        this.okClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    protected String get(final String endPoint) throws HttpClientException {
        return get(endPoint,null);
    }

    protected String get(final String endPoint,
                         final Headers headers) throws HttpClientException {
        return performRequest(endPoint, "GET", null, headers);
    }

    protected <T> T getForType(final String endPoint,
                               final Class<T> tClass) throws HttpClientException {
        final String res = get(endPoint);
        try {
            return objectMapper.readValue(res, tClass);
        } catch (IOException e) {
            throw new HttpClientException(HttpClientError.PARSING, e.getMessage(), e);
        }
    }

    protected <T> T getForType(final String endPoint,
                               final Headers headers,
                               final TypeReference<T> typeReference) throws HttpClientException {
        final String res = get(endPoint, headers);
        try {
            return objectMapper.readValue(res, typeReference);
        } catch (IOException e) {
            throw new HttpClientException(HttpClientError.PARSING, e.getMessage(), e);
        }
    }

    protected <T> T getForType(final String endPoint,
                               final TypeReference<T> typeReference) throws HttpClientException {
        return getForType(endPoint, null, typeReference);
    }

    protected String postJSON(final String endPoint,
                              final Object body) throws HttpClientException {
        return post(endPoint, body, "application/json");
    }

    protected <T> T postJSONForType(final String endPoint,
                                    final Object body,
                                    final TypeReference<T> typeReference) throws HttpClientException {
        return postForType(endPoint, body, "application/json", typeReference);
    }

    protected String post(final String endPoint,
                          final RequestBody body) throws HttpClientException {
        return postJSON(endPoint, body);
    }

    protected <T> T postForType(final String endPoint,
                                final Object body,
                                final String mediaType,
                                final TypeReference<T> typeReference) throws HttpClientException {
        final String res = post(endPoint, body, mediaType);
        try {
            return objectMapper.readValue(res, typeReference);
        } catch (IOException e) {
            throw new HttpClientException(HttpClientError.PARSING, e.getMessage(), e);
        }
    }

    protected String post(final String endPoint,
                          final Object body,
                          final String mediaType) throws HttpClientException {

        final RequestBody requestBody;
        if (body instanceof RequestBody) {
            requestBody = (RequestBody) body;
        } else {
            try {
                requestBody = RequestBody.create(objectMapper.writeValueAsString(body), MediaType.parse(mediaType));
            } catch (IOException e) {
                throw new HttpClientException(HttpClientError.MAPPING, e.getMessage(), e);
            }
        }
        return performRequest(endPoint, "POST", requestBody);
    }

    private String performRequest(final String endPoint,
                                  final String method,
                                  final RequestBody body) throws HttpClientException {
        return performRequest(endPoint, method, body, null);
    }

    private String performRequest(final String endPoint,
                                  final String method,
                                  final RequestBody body,
                                  final Headers headers) throws HttpClientException {
        final String url;

        try {
            url = new URL(baseUrl + endPoint).toString();
        } catch (Exception e) {
            throw new HttpClientException(HttpClientError.REQUEST, e.getMessage(), e);
        }

        final Request.Builder builder = new Request.Builder()
                .method(method, body)
                .url(url);

        if (Objects.nonNull(headers)) {
            builder.headers(headers);
        }

        final Request request = builder.build();

        try (Response response = okClient.newCall(request).execute()) {
            if (Objects.nonNull(response.body())) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                throw new HttpClientException(HttpClientError.NULL_RESPONSE);
            }
        } catch (IOException e) {
            logError("Error while calling api_version: " + e.getMessage(), e);
            throw new HttpClientException(HttpClientError.REQUEST, e.getMessage(), e);
        }
    }
}
