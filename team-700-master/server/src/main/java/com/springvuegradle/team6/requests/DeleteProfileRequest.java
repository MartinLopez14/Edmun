package com.springvuegradle.team6.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class DeleteProfileRequest {
    @NotNull
    @Email(message = "Email should be valid")
    public String primary_email;

    public String getEmail() {
        return primary_email;
    }
}
