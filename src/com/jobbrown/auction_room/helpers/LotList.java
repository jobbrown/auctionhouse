package com.jobbrown.auction_room.helpers;

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
    private ArrayList<Lot> list = new ArrayList<Lot>();
    private JavaSpace space;

    /**
     * Empty constructor. Presets the space.
     */
    public LotList() {
        space = SpaceUtils.getSpace();
    }

    /**
     * Constructor with preexisting list of Lots (for sub searches). Presets the space.
     *
     * @param list The list of obcts to prepopulate the LotList with
     */
    public LotList(ArrayList<Lot> list) {
        space = SpaceUtils.getSpace();
        this.list = list;
    }

    /**
     *
     * @return LotList a LotList object with internal list set to all lots in the space
     */
    public LotList all() {
        // Create a Lot template
        Lot template = new Lot();

        // Do the search on the space

        return new LotList();
    }

    /**
     * Get all lot items from the internal array
     *
     * @return ArrayList all Items in this search
     */
    public ArrayList<Lot> getItems() {
        return this.list;
    }

    public LotList withID(int ID) {
        return new LotList();
    }

    private Transaction createTransaction() throws RemoteException, LeaseDeniedException {
        Transaction.Created trc = TransactionFactory.create(SpaceUtils.getManager(), 3000);
        Transaction txn = trc.transaction;

        return txn;
    }
}
