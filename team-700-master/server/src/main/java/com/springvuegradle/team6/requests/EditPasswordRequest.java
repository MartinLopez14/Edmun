package com.springvuegradle.team6.requests;

import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EditPasswordRequest {

    /**
     * Old user password, cannot be null
     */
    @NotNull
    @JsonProperty("old_password")
    public String oldpassword;

    /**
     * New user password, matched against pattern and cannot be null
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    @NotNull
    @JsonProperty("new_password")
    public String newpassword;

    /**
     * Repeated new user password, matched against pattern and cannot be null
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    @NotNull
    @JsonProperty("repeat_password")
    public String repeatedpassword;
}