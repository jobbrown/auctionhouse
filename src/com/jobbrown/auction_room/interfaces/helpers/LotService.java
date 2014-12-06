package com.jobbrown.auction_room.interfaces.helpers;

import com.jobbrown.auction_room.exceptions.LotNotFoundException;
import com.jobbrown.auction_room.models.Lot;
import net.jini.core.entry.Entry;

import java.util.ArrayList;

/**
 * Created by job on 01/12/14.
 */
public interface LotService {

    public boolean addLot(Lot t);
    public ArrayList<Lot> getAllLots();
    public Entry searchForLot(Lot t) throws LotNotFoundException;


}
