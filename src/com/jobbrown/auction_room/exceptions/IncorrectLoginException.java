package com.jobbrown.auction_room.exceptions;

/**
 * Created by job on 30/10/14.
 */
public class IncorrectLoginException extends Exception {
    public IncorrectLoginException() {

    }

    public IncorrectLoginException(String message)
    {
        super(message);
    }
}
