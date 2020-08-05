package com.springvuegradle.team6.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class AdminEditRoleRequest {

    /**
     * Role name string
     */
    @NotNull
    @NotEmpty
    public String role;

    /**
     * returns the role name string of the request
     */
    public String getRole() {
        return role;
    }
}
