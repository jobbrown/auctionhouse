package com.jobbrown.auction_room.helpers;

import com.jobbrown.auction_room.interfaces.helpers.TransactionService;
import com.jobbrown.auction_room.thirdparty.gallen.SpaceUtils;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;

/**
 * This class was designed for the ease of managing transactions during
 * operations on the space.
 *
 * When constructed, it will take, or will create its own instance of a TransactionManager.
 * Using this when getTransaction() is called it will create a transaction.
 */
public class JavaSpacesTransactionService implements TransactionService {
    private TransactionManager manager;

    public JavaSpacesTransactionService() {
        this.manager = SpaceUtils.getManager();
    }

    public JavaSpacesTransactionService(TransactionManager manager) {
        this.manager = manager;
    }

    /**
     * Get a Transaction
     *
     * Code lifted from JavaSpaces Principles, Patterns and Practise (Freeman, Hupfer, Arnold).
     *
     * @return Transaction
     */
    @Override
	public Transaction getTransaction() {
        Transaction.Created trc = null;
        try {
            trc = TransactionFactory.create(this.manager, 3000);
        } catch (Exception e) {
            System.err.println("Couldn't created transaction " + e);

        }

        return trc.transaction;
    }
}
