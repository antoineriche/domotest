package com.ariche.domotest.freebox.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class OpenSessionInput {

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("password")
    private String password;

}
