package com.jobbrown.auction_room.exceptions;

/**
 * Created by job on 30/10/14.
 */
public class UserNotFoundException extends Exception {
	private static final long serialVersionUID = 4922021629351502845L;

	public UserNotFoundException() {

    }

    public UserNotFoundException(String message)
    {
        super(message);
    }
}
