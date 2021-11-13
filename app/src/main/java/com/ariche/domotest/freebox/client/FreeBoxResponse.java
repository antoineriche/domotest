package com.ariche.domotest.freebox.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FreeBoxResponse<T> {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("result")
    private T result;

}
