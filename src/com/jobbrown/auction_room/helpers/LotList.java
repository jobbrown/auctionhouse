package com.jobbrown.auction_room.helpers;

import com.jobbrown.auction_room.enums.Category;
import com.jobbrown.auction_room.models.Lot;
import java.util.ArrayList;

public class LotList {
    private ArrayList<Lot> lotList = new ArrayList<Lot>();

    /**
     * Empty constructor. Load all the lots in
     */
    public LotList() {
    	JavaSpacesLotService ls = new JavaSpacesLotService();
    	this.lotList = ls.getAllLots();
    }
    
    /**
     * Constructor with preexisting list of Lots (for sub searches). Presets the space.
     *
     * @param list The list of obcts to prepopulate the LotList with
     */
    public LotList(ArrayList<Lot> list) {
        this.lotList = list;
    }

    /**
     * Get all lot items from the internal array
     *
     * @return ArrayList all Items in this search
     */
    public ArrayList<Lot> all() {
        return this.lotList;
    }

    /**
     * Returns only the active lots from the list of lots
     * 
     * @return LotList
     */
	public LotList active() {
		ArrayList<Lot> newLotList = new ArrayList<Lot>();
		
		// Go through all the bids, only pull out public or the active users bids
		for(Lot lot : lotList) {
			if(lot != null) {
				if(lot.active == true) {
					newLotList.add(lot);
				}
			}
		}
		
		return new LotList(newLotList);
	}
	
	/**
	 * Returns only the ended lots from the list of lots
	 * 
	 * @return LotList
	 */
	public LotList ended() {
		ArrayList<Lot> newLotList = new ArrayList<Lot>();
		
		// Go through all the bids, only pull out public or the active users bids
		for(Lot lot : lotList) {
			if(lot != null) {
				if(!lot.active == false) {
					newLotList.add(lot);
				}
			}
		}
		
		return new LotList(newLotList);
	}
	
	/**
	 * Searches the LotList by lotName (exact or contains matches)
	 * 
	 * @param lotName the value to search by
	 * @return LotList
	 */
	public LotList filterByLotName(String lotName) {
		ArrayList<Lot> newLotList = new ArrayList<Lot>();
		
		// Go through all the bids, only pull out public or the active users bids
		for(Lot lot : lotList) {
			if(lot != null) {
				if(lot.title.contains(lotName) || lot.title.equals(lotName)) {
					newLotList.add(lot);
				}
			}
		}
		
		return new LotList(newLotList);
	}
	
	/**
	 * Searches the LotList by category
	 * 
	 * @param category the value to search by
	 * @return LotList
	 */
	public LotList filterByLotCategory(Category category) {
		ArrayList<Lot> newLotList = new ArrayList<Lot>();
		
		// Go through all the bids, only pull out public or the active users bids
		for(Lot lot : lotList) {
			if(lot != null) {
				if(lot.category.equals(category)) {
					newLotList.add(lot);
				}
			}
		}
		
		return new LotList(newLotList);
	}
}
