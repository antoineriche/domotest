package com.ariche.domotest.freebox.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class LoginStatusOutput {

    @JsonProperty("status")
    private String status;

    @JsonProperty("challenge")
    private String challenge;

    @JsonProperty("password_salt")
    private String passwordSalt;

}
