package com.springvuegradle.team6.exceptions;

public class SubscriptionNotFoundException extends Exception{
    public SubscriptionNotFoundException() {
        super("User is not subscribed/follows this activity");
    }
}
