package com.ariche.domotest.http.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HttpClientError {
    REQUEST("Request error"),
    MAPPING("Mapping error"),
    PARSING("Parsing error"),
    NULL_RESPONSE("Empty response body");

    private final String title;
}
