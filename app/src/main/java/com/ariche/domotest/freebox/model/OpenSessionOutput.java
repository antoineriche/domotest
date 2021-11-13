package com.ariche.domotest.freebox.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OpenSessionOutput {

    @JsonProperty("session_token")
    private String sessionToken;

    @JsonProperty("challenge")
    private String challenge;

    @JsonProperty("permissions")
    private Map<String, Boolean> permissions;

}
