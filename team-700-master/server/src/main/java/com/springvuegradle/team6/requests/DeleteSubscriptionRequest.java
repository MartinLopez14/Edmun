package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DeleteSubscriptionRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("email")
    private String email;

    public String getEmail() {
        return email;
    }
}
