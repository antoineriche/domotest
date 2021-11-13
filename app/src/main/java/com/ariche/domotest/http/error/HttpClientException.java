package com.ariche.domotest.http.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class HttpClientException extends Exception {
    private HttpClientError error;
    private String detail;
    private Throwable cause;

    public HttpClientException(HttpClientError error) {
        this(error, "", null);
    }
}
