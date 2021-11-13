package com.ariche.domotest.freebox.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class LoginModelOutput {

    @JsonProperty("app_token")
    private String appToken;

    @JsonProperty("track_id")
    private int trackId;
}
