package com.jobbrown.auction_room.helpers;

import com.jobbrown.auction_room.enums.Category;
import com.jobbrown.auction_room.models.Bid;
import com.jobbrown.auction_room.models.Lot;
import com.jobbrown.auction_room.thirdparty.gallen.SpaceUtils;

import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by job on 30/10/14.
 * This will provide the searching on the JavaSpace
 * All functions should return a LotList to allow quick chained queries.
 *
 * eg
 *
 * LotList.getAll().withID(1);
 * LotList.getAll().ownedBy(18).biddedOnBy(12);
 *
 * If its a chained call then we should use the internal results list.
 */

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
