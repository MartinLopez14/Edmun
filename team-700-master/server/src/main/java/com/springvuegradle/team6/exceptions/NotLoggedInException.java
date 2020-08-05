package com.springvuegradle.team6.exceptions;

public class NotLoggedInException extends Exception{
    public NotLoggedInException() {
        super("Sorry the required account is not currently logged in.");
    }
}