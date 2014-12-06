package com.jobbrown.auction_room.exceptions;

/**
 * Created by job on 30/10/14.
 */
public class LotNotFoundException extends Exception {
    public LotNotFoundException() {

    }

    public LotNotFoundException(String message)
    {
        super(message);
    }
}
