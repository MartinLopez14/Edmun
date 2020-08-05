package com.springvuegradle.team6.exceptions;

public class ProfileNotFoundException extends Exception{
    public ProfileNotFoundException(Integer id) {
        super("Could not find account with id: " + id);
    }
}
