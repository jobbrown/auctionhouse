package com.jobbrown.auction_room.helpers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import net.jini.space.JavaSpace05;
import javafx.util.Callback;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

import com.jobbrown.auction_room.exceptions.LotNotFoundException;
import com.jobbrown.auction_room.exceptions.NotificationNotFoundException;
import com.jobbrown.auction_room.interfaces.helpers.CallBack;
import com.jobbrown.auction_room.interfaces.helpers.TransactionService;
import com.jobbrown.auction_room.models.Notification;
import com.jobbrown.auction_room.models.Tail;
import com.jobbrown.auction_room.thirdparty.gallen.SpaceUtils;

public class JavaSpacesNotificationService {
	public JavaSpace space;
	public String channelName = null;
	
	public JavaSpacesNotificationService() {
		// Grab the space
		space = SpaceUtils.getSpace();
		
		// Set the channel name
		this.setChannelName();
	}
	
	public void createCallBack(Notification template, CallBack cb) {
		RemoteEventListener rel = new RemoteEventListener() {
			@Override
			public void notify(RemoteEvent e) throws UnknownEventException, RemoteException {
				System.out.println("Activated");
				cb.notified();
			}
		};
		
		// Create an exporter
		Exporter myDefaultExporter = 
				new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						new BasicILFactory(), false, true);
		
		try {
			RemoteEventListener relE = (RemoteEventListener) myDefaultExporter.export(rel);
			
			space.notify(template, null, relE, Lease.FOREVER, null);
			
			System.out.println("Finished registering");
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Exception here");
		}
	}
	
	/**
	 * 
	 */
	public boolean markNofificationAsRead(Notification n) {		
		// Create a blank notification
		Notification t = new Notification();
		t.id = n.id;
		
		Notification returnedNotification = null;
		try {
			returnedNotification = (Notification) this.searchForNotification(t);
		} catch (NotificationNotFoundException e) {
			return false;
		}
		
		if(returnedNotification == null) {
			return false;
		}
		
		returnedNotification.read = true;
		return this.updateNotification(returnedNotification);
		
	}
	
	/**
     * A quick override of the method requiring a transaction. Creates a transaction and uses that on the fly.
     */
	public boolean addNotification(Notification t) {
        TransactionService ts = new JavaSpacesTransactionService();
        Transaction transaction = ts.getTransaction();
        
        return addNotification(t, transaction);
    }
    
    
    public boolean addNotification(Notification t, Transaction transaction) {
    	return this.addNotification(t, transaction, true);
    }
    
    /**
     * This will take a Notification and then add it to the Lot. It will create a transaction, take the tail,
     * increment and commit all changes.
     *
     * @param t The notification to be added
     * @param t The transaction to be used
     * @param updateID should we increment the ID used for this object
     * @return boolean success
     */
    public boolean addNotification(Notification t, Transaction transaction, boolean updateID) {
    	String origChannelName = this.channelName;
    	this.channelName = t.owner;
    	
    	// Get the tail
        Tail tail = getTail(transaction);

        if(updateID) {
	        // Increment the tail
	        tail.increment();
        
        	// Set the ID on the new lot
            t.id = tail.position;
        }

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
        

    	this.channelName = origChannelName;
        return true;
    }
    
    /**
     * Updates a lot on the JavaSpace, the old one is removed by ID then replaced with this
     * @param t
     * @return success boolean
     */
    public boolean updateNotification(Notification t) {
    	// Create a transaction
    	TransactionService ts = new JavaSpacesTransactionService();
        Transaction transaction = ts.getTransaction();
           	
        // Make a new lot from the ID
        Notification notification = this.stripAllProperties(t);
        
        // Take it from the space
        Notification receivedNotification = (Notification) getNotification(notification, transaction);
        
        // Put the new one back in
        if(addNotification(t, transaction, false)) {
        	return true;
        }
    	
    	return false;
    }
    
    /**
     * Removes a notification from the space
     * @param t the notification
     * @param transaction a transaction to operate this under
     * @return the notification
     */
    public Entry getNotification(Notification t, Transaction transaction) {    	
		// Take the ID from the Lot and create a fresh template
    	Notification searchNotification = this.stripAllProperties(t);
    	
    	// Lets take the lot by that ID
    	Notification retrievedNotification = null;
    	try {
    		retrievedNotification = (Notification) space.take(searchNotification, transaction, 5000);
		} catch (RemoteException | UnusableEntryException
				| TransactionException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to get the notification");
			e.printStackTrace();
		}

    	return retrievedNotification;
    }
	
	/**
     * Given a template t, this will search the JavaSpace for a Lot which matches that template.
     * If a match is successful, the first result will be returned.
     *
     * @param t Lot template
     * @return Lot the lot found
     * @throws LotNotFoundException
     */
    public Entry searchForNotification(Notification t) throws NotificationNotFoundException {
        Notification notification = null;

        try {
            notification = (Notification) space.readIfExists(t, null, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(notification == null) {
            throw new NotificationNotFoundException();
        } else {
            return notification;
        }
    }
	
	/**
	 * Gets an arraylist of all the notifications in the space
	 * @return ArrayList<Notification>
	 */
	public ArrayList<Notification> getAllNotifications() {
		ArrayList<Notification> returnable = new ArrayList<Notification>();

        // Read the tail
        Tail tail = readTail();
        
        // Loop around all of the objects in the space,
        for(int i = 1; i <= tail.position; i++) {
            // Create a quick template
            Notification notification = new Notification();
            notification.id = i;
            notification.owner = this.channelName;

            // Search for it
            Notification returnedNotification = null;
            try {
            	returnedNotification = (Notification) searchForNotification(notification);
            } catch (NotificationNotFoundException e) {
                //e.printStackTrace();
            }

            if(returnedNotification != null)
                returnable.add(returnedNotification);
        }

        return returnable;
	}
	
	/**
	 * This will set the active channel name to the users username and should be called
	 * whenever this is initialized
	 */
	private void setChannelName() {
		JavaSpacesUserService us = JavaSpacesUserService.getInstance();
		this.channelName = us.getCurrentUser().username;
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
    
    /**
     * Strips all the properties except for ID (for accurate searchign)
     * @param t Notification
     * @return Notification
     */
    private Notification stripAllProperties(Notification t) {
    	Notification returnable = new Notification();
    	returnable.id = t.id;
    	
    	return returnable;
    }
}
