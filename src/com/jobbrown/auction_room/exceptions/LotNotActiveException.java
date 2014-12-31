package com.jobbrown.auction_room.exceptions;

/**
 * Created by job on 30/10/14.
 */
public class LotNotActiveException extends Exception {
	private static final long serialVersionUID = -2058650028291023046L;

	public LotNotActiveException() {

    }

    public LotNotActiveException(String message)
    {
        super(message);
    }
}
