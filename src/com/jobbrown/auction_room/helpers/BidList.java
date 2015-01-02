package com.jobbrown.auction_room.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jobbrown.auction_room.models.Bid;


public class BidList {
	private ArrayList<Bid> bidList = new ArrayList<Bid>();
	
	public BidList(ArrayList<Bid> bids) {
		if(bids == null) {
			this.bidList = new ArrayList<Bid>();
		} else {
			this.bidList = bids;
		}
	}
	
	/**
	 * Get an arraylist of the bids represented by this bidlist
	 * @return ArrayList<Bid>
	 */
	public ArrayList<Bid> get() {
		return this.bidList;
	}
	
	/**
	 * Return the first bid in this bidlist
	 * @return
	 */
	public Bid one() {
		if(count() == 0) {
			return null;
		}
		return this.bidList.get(0);
	}
	
	/**
	 * Sort the bid list small -> large
	 * @return BidList
	 */
	public BidList sortBySmallestBid() {
		ArrayList<Bid> bidCopy = new ArrayList<Bid>(bidList);
		
		Collections.sort(bidCopy, new Comparator<Bid>() {
			@Override
			public int compare(Bid bid, Bid bid2) {
				return bid.amount.compareTo(bid2.amount);
			}
		});
		
		return new BidList(bidCopy);
	}
	
	/**
	 * Sort the bid list by large -> small
	 * @return BidList
	 */
	public BidList sortByLargestBid() {
		ArrayList<Bid> bidCopy = new ArrayList<Bid>(bidList);
		
		Collections.sort(bidCopy, new Comparator<Bid>() {
			@Override
			public int compare(Bid bid, Bid bid2) {
				return bid2.amount.compareTo(bid.amount);
			}
		});
		
		return new BidList(bidCopy);
	}
	
	/**
	 * Sort the bid list by old -> new
	 * @return BidList
	 */
	public BidList sortByOldest() {
		ArrayList<Bid> bidCopy = new ArrayList<Bid>(bidList);
		
		Collections.sort(bidCopy, new Comparator<Bid>() {
			@Override
			public int compare(Bid bid, Bid bid2) {
				return bid.date.compareTo(bid2.date);
			}
		});
		
		return new BidList(bidCopy);
	}
	
	/**
	 * Sort the bid list by new -> old
	 * @return BidList
	 */
	public BidList sortByNewest() {
		ArrayList<Bid> bidCopy = new ArrayList<Bid>(bidList);
		
		Collections.sort(bidCopy, new Comparator<Bid>() {
			@Override
			public int compare(Bid bid, Bid bid2) {
				return bid2.date.compareTo(bid.date);
			}
		});
		
		return new BidList(bidCopy);
	}
	
	/**
	 * Return only private bids
	 * @return BidList
	 */
	public BidList onlyPrivate() {
		ArrayList<Bid> newBidList = new ArrayList<Bid>();
		
		for(Bid bid : bidList) {
			if(!bid.publicBid) {
				newBidList.add(bid);
			}
		}
		
		return new BidList(newBidList);
	}
	
	/**
	 * Return only public bids
	 * @return BidList
	 */
	public BidList onlyPublic() {
		ArrayList<Bid> newBidList = new ArrayList<Bid>();
		
		JavaSpacesUserService us = JavaSpacesUserService.getInstance();
		
		// Go through all the bids, only pull out public or the active users bids
		for(Bid bid : bidList) {
			if(bid.publicBid || us.getCurrentUser().id.equals(bid.bidder.id)) {
				newBidList.add(bid);
			}
		}
		
		return new BidList(newBidList);
	}
	
	/**
	 * Returns how many bids there are in this BidList
	 * @return int
	 */
	public int count() {
		if(this.bidList == null) {
			return 0;
		}
		return this.bidList.size();
	}

}
