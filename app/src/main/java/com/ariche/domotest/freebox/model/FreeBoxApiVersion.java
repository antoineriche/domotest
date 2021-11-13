package com.ariche.domotest.freebox.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FreeBoxApiVersion {

    @JsonProperty("box_model_name")
    private String boxModelName;

    @JsonProperty("api_base_url")
    private String apiBaseUrl;

    @JsonProperty("https_port")
    private int httpsPort;

    @JsonProperty("device_name")
    private String deviceName;

    @JsonProperty("device_type")
    private String deviceType ;

    @JsonProperty("https_available")
    private boolean httpsAvailable;

    @JsonProperty("box_model")
    private String boxModel;

    @JsonProperty("api_domain")
    private String apiDomain;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("api_version")
    private String apiVersion;
}
