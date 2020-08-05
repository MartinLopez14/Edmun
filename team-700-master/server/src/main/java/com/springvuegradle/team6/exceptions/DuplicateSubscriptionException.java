package com.springvuegradle.team6.exceptions;

public class DuplicateSubscriptionException extends Exception {
    public DuplicateSubscriptionException() {
        super("Cannot subscribe/follow activity that is already subscribed/followed");
    }
}
