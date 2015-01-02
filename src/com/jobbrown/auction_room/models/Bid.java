package com.jobbrown.auction_room.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by job on 21/10/14.
 */
public class Bid implements Serializable {
	private static final long serialVersionUID = -1716155005012038439L;

	// is this a public bid
	public boolean publicBid;
	
	// How much was this bid for
	public Double amount;
	
	// Who was it made by
	public User bidder;
	
	// When was this bid made
	public Date date;
	
	/**
	 * Empty constructor for good measure
	 */
    public Bid() {

    }
    

    public Bid(Boolean publicBid, double amount, User bidder, Date date) {
    	this.publicBid = publicBid;
    	this.amount = amount;
    	this.bidder = bidder;
    	this.date = date;
    }

    @Override
	public String toString() {
		return "Bid (" + publicBid + ") by User ID: " + this.bidder.username + " for £" + this.amount + " on " + this.date;
    }
}
