package com.springvuegradle.team6.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DeleteRoleRequest {

    @NotNull
    @Email(message = "Email should be valid")
    public String primary_email;

    @NotNull
    @NotEmpty
    public String role_name;

    public String getEmail() {
        return primary_email;
    }

    public String getRole() {
        return role_name;
    }
}
