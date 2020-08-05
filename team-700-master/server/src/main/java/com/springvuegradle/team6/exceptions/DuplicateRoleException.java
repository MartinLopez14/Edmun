package com.springvuegradle.team6.exceptions;

public class DuplicateRoleException extends Exception {
    public DuplicateRoleException() {
        super("Duplicate role entry is not allowed");
    }
}
