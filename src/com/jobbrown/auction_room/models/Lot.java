package com.jobbrown.auction_room.models;

import com.jobbrown.auction_room.enums.Category;

import net.jini.core.entry.Entry;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by job on 21/10/14.
 */
public class Lot implements Entry
{
    private static final long serialVersionUID = 5708385011852899669L;

    public Integer id;
    public Date startTime;
    public Date endTime;
    public String title;
    public String description;
    public Integer seller;
    public Double price;
    public ArrayList<Bid> bids;
    public Boolean active;
    public Bid winningBid;
    public Category category;

    /*
     * No arg constructor
     */
    public Lot() {
    }

    @Override
    public String toString() {
        return
                this.id + "\n" +
                        this.title + "\n" +
                        this.description + "\n" +
                        this.seller  + "\n" +
                        this.price + "\n";
    }

    /**
     * Given a Bid, adds it to the list of bids on this object
     * @param b
     * @return
     */
    public boolean addBidToLot(Bid b) {
    	if(bids == null)
    		initializeBids();
    	
    	bids.add(b);
    	
    	return true;
    }
    
    private void initializeBids() {
    	bids = new ArrayList<Bid>();
    }
}

