package com.ariche.domotest.jeedom.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Scenario {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("isActive")
    private String isActive;

    @JsonProperty("state")
    private String state;

    @JsonProperty("lastLaunch")
    private String lastLaunch;

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | IsActive: %s | State: %s | Last launch: %s",
                id, name, isActive, state, lastLaunch);
    }

    public boolean isActivated() {
        return "1".equals(isActive);
    }
}
