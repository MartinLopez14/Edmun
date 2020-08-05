package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class AdminEditPasswordRequest {

    /**
     * New user password, matched against pattern and cannot be null
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    @NotNull
    @JsonProperty("new_password")
    public String newPassword;


    /**
     * Repeated new user password, matched against pattern and cannot be null
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    @NotNull
    @JsonProperty("repeat_password")
    public String repeatPassword;


}
