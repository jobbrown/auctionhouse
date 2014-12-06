package com.jobbrown.auction_room.models;

/**
 * Created by job on 21/10/14.
 */
public class Bid {
	
	// is this a public bid
	public boolean publicBid;
	
	// How much was this bid for
	public double amount;
	
	// Who was it made by
	public Integer bidder;
	
	/**
	 * Empty constructor for good measure
	 */
    public Bid() {

    }
    

    public Bid(Boolean publicBid, double amount, Integer bidder) {
    	this.publicBid = publicBid;
    	this.amount = amount;
    	this.bidder = bidder;
    }

}
