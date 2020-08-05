package com.springvuegradle.team6.exceptions;

public class RoleNotFoundException extends Exception {
    public RoleNotFoundException() {
        super("Role to be deleted is not associated with the user");
    }
}
