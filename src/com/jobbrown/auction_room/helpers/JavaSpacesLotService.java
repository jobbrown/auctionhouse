package com.jobbrown.auction_room.helpers;

import com.jobbrown.auction_room.exceptions.LotNotFoundException;
import com.jobbrown.auction_room.interfaces.helpers.LotService;
import com.jobbrown.auction_room.models.Bid;
import com.jobbrown.auction_room.models.Lot;
import com.jobbrown.auction_room.models.Tail;
import com.jobbrown.auction_room.thirdparty.gallen.SpaceUtils;

import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.space.JavaSpace;

import java.util.ArrayList;

/**
 * This class provides the functionality for interacting with the space in relation to Lots.
 * It will act as a wrapper of the JavaSpaces API meaning any underlying changes won't require
 * changing to the application.
 */
public class JavaSpacesLotService implements LotService {
    JavaSpace space;
    String channelName = "lots";

    public JavaSpacesLotService() {
        space = SpaceUtils.getSpace();
    }

    /**
     * This will take a Lot (or a subtype of) and then add it to the Lot. It will create a transaction, take the tail,
     * increment and commit all changes.
     *
     * @param t The lot to be added
     * @return boolean success
     */
    @Override
	public boolean addLot(Lot t) {
        JavaSpacesTransactionService ts = new JavaSpacesTransactionService();
        Transaction transaction = ts.getTransaction();

        // Get the tail
        Tail tail = getTail(transaction);

        // Increment the tail
        tail.increment();

        // Set the ID on the new lot
        t.id = tail.position;

        // Write the tail back
        try {
            try {
                space.write(tail, transaction, Lease.FOREVER);
                space.write(t, transaction, Lease.FOREVER);
            } catch (Exception e) {
                System.err.println("Failed to write that to the space " + e);
                transaction.abort();
                return false;
            }

            transaction.commit();
        } catch (Exception e) {
            System.err.println("Failed to abort or commit " + e);
            return false;
        }

        return true;
    }

    /**
     * Returns an arraylist of all lots in the space
     *
     * @return ArrayList<Lot> all lots in the space
     */
    @Override
	public ArrayList<Lot> getAllLots() {
        ArrayList<Lot> returnable = new ArrayList<Lot>();

        // Read the tail
        Tail tail = readTail();

        // Loop around all of the objects in the space,
        for(int i = 1; i <= tail.position; i++) {
            // Create a quick template
            Lot lot = new Lot();
            lot.id = i;

            // Search for it
            Lot returnedLot = null;
            try {
                returnedLot = (Lot) searchForLot(lot);
            } catch (LotNotFoundException e) {
                e.printStackTrace();
            }

            if(returnedLot != null)
                returnable.add(returnedLot);
        }

        return returnable;

    }

    /**
     * Given a template t, this will search the JavaSpace for a Lot which matches that template.
     * If a match is successful, the first result will be returned.
     *
     * @param t Lot template
     * @return Lot the lot found
     * @throws LotNotFoundException
     */
    @Override
	public Entry searchForLot(Lot t) throws LotNotFoundException {
        Lot lot = null;

        try {
            lot = (Lot) space.readIfExists(t, null, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(lot == null) {
            throw new LotNotFoundException();
        } else {
            return lot;
        }
    }

    /**
     * Updates a lot on the JavaSpace, the old one is removed by ID then replaced with this
     * @param t
     * @return success boolean
     */
    public boolean updateLot(Lot t) {
    	int ID = t.id;
    	
    	
    	
    	
    	
    	return false;
    	
    }
    public boolean addBidToLot(Lot t, Bid b) {
    	// Take the lot from the space
    	// Add the bid
    	
    	
    	return false;
    }
    
    /**
     * This will read the tail from the JavaSpace of the lots channel. If the Tail doesn't exist, it will be created.
     *
     * @return Tail
     */
    private Tail readTail() {
        Tail tail = new Tail(this.channelName);

        Tail readTail = null;
        try {
            readTail = (Tail) space.read(tail, null, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(readTail == null) {
            createChannel();
            return readTail();
        }
        return readTail;
    }

    /**
     * This will take the tail from the lots channel, from the JavaSpace.
     * A transaction must be provided (from TransactionHelper class).
     *
     * @param transaction Transaction to be used
     * @return Tail the tail
     */
    protected Tail getTail(Transaction transaction) {
        Tail tail = new Tail(this.channelName);

        Tail takenTail = null;
        try {
            takenTail = (Tail) space.take(tail, transaction, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(takenTail == null) {
            createChannel();
            return getTail(transaction);
        }
        return takenTail;
    }

    /**
     * Set up method to create 'lots' channel, should it not exist
     */
    private void createChannel() {
        Tail tail = new Tail(this.channelName);
        tail.position = 0;

        try {
            space.write(tail, null, Lease.FOREVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
