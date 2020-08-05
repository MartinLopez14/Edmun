package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SubscriptionRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("email")
    private String email;

    @NotNull
    @NotEmpty
    @JsonProperty("role")
    private String role;

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
