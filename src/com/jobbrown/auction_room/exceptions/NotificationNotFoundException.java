package com.jobbrown.auction_room.exceptions;

/**
 * Created by job on 30/10/14.
 */
public class NotificationNotFoundException extends Exception {
	private static final long serialVersionUID = 4922021629351502845L;

	public NotificationNotFoundException() {

    }

    public NotificationNotFoundException(String message)
    {
        super(message);
    }
}
