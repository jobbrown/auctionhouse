package com.jobbrown.auction_room.exceptions;

/**
 * Created by job on 30/10/14.
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException() {

    }

    public UserNotFoundException(String message)
    {
        super(message);
    }
}
