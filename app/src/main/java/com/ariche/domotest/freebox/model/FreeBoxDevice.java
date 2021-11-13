package com.ariche.domotest.freebox.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
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
public final class FreeBoxDevice {

    @JsonProperty("id")
    private String id;

    @JsonProperty("primary_name")
    private String name;

    @JsonProperty("l3connectivities")
    private List<Map<String, String>> l3Connectivities;

    @JsonProperty("vendor_name")
    private String vendorName;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("reachable")
    private boolean reachable;

    @JsonProperty("last_time_reachable")
    private long lastTimeReachable;

    @JsonProperty("last_activity")
    private long lastActivity;

    public String getAddress() {
        return l3Connectivities.isEmpty() ? null : l3Connectivities.get(0).get("addr");
    }
}
