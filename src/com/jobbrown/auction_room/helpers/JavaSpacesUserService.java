package com.jobbrown.auction_room.helpers;

import com.jobbrown.auction_room.exceptions.IncorrectLoginException;
import com.jobbrown.auction_room.exceptions.UserNotFoundException;
import com.jobbrown.auction_room.interfaces.helpers.UserService;
import com.jobbrown.auction_room.models.Tail;
import com.jobbrown.auction_room.models.User;
import com.jobbrown.auction_room.thirdparty.gallen.SpaceUtils;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.space.JavaSpace;

import java.util.ArrayList;

/**
 * Created by job on 01/12/14.
 */
public class JavaSpacesUserService implements UserService {
    JavaSpace space;
    String channelName = "users";

    User activeUser = null;

    public JavaSpacesUserService() { this.space = SpaceUtils.getSpace(); }

    
    /**
     * Attempts to a log a user into the system using supplied credentials
     * @param username the username to attempt to login with
     * @param password the password to attempt to login with
     * @return success as a boolean
     * @throws IncorrectLoginException with message referring to error
     */
    @Override
	public boolean login(String username, String password) throws IncorrectLoginException {
        User user = new User();
        user.username = username;

        try {
            User foundUser = this.searchForUser(user);

            if(foundUser.password.equals(foundUser.hashPassword(password))) {
                this.activeUser = foundUser;

                return true;
            } else {
                throw new IncorrectLoginException("That password was incorrect.");
            }
        } catch (UserNotFoundException e) {
            throw new IncorrectLoginException("That username was not found.");
        }
    }

    /**
     * Unsets the current logged in user
     */
    @Override
	public boolean logout() {
        this.activeUser = null;

        return true;
    }

    /**
     * If a user is currently logged into the System, returns that User object
     * @return User
     */
    @Override
    public User getCurrentUser() {
        return this.activeUser;
    }

    /**
     * Given a template, check if it matches a user in the system
     * @param u User to search for
     * @return
     * @throws UserNotFoundException
     */
    @Override
	public User searchForUser(User u) throws UserNotFoundException {
        User user = null;

        try {
            user = (User) space.readIfExists(u, null, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(user == null) {
            throw new UserNotFoundException();
        } else {
            return user;
        }
    }

    /**
     * Checks if a username is unused by any other user
     * @param username
     * @return if username is unique
     */
    public boolean isUsernameUnique(String username) {
        User user = new User();
        user.username = username;

        try {
            user = this.searchForUser(user);
        } catch (UserNotFoundException e) {
            return true;
        }
        
        return (user == null);
    }

    /**
     * Checks if a given e-mail address is unused by any other user
     * @param email
     * @return if email is unique
     */
    public boolean isEmailUnique(String email) {
        User user = new User();
        user.email = email;

        try {
            this.searchForUser(user);
        } catch (UserNotFoundException e) {
            return true;
        }

        return (user == null);
    }
    /**
     * Given params, create a user with the passed values
     * @param username entered username
     * @param password entered password
     * @param email entered email
     * @return success as a boolean
     * @throws Exception if username or email already in use
     */
    @Override
	public boolean createUser(String username, String password, String email) throws Exception {
        // Create two users, one from the username and one from the e-mail to check that
        // these don't exist
        User userCheck = new User();
        userCheck.username = username;

        if(isUsernameUnique(username)) {
            if(isEmailUnique(email)) {
                User user = new User();

                user.username = username;
                user.email = email;

                // This will automatically salt it
                user.setPassword(password);

                if(this.addUser(user)) {
                    return true;
                }

                return false;
            }

            throw new Exception("That e-mail is already used by another user");
        }

        throw new Exception("That username is already in use by another user");
    }

    /**
     * Adds a User to the JavaSpace
     * @param user
     * @return success as a boolean
     */
    public boolean addUser(User user) {
        JavaSpacesTransactionService ts = new JavaSpacesTransactionService();
        Transaction transaction = ts.getTransaction();

        // Get the tail
        Tail tail = getTail(transaction);

        // Increment the tail
        tail.increment();

        // Set the ID on the new lot
        user.id = tail.position;

        // Write the tail back, and the new object
        try {
            try {

                space.write(user, null, Lease.FOREVER);
                space.write(tail, null, Lease.FOREVER);
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
     * Returns an ArrayList of all users registered on the system
     * @return ArrayList of users
     */
    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> returnable = new ArrayList<User>();

        // Read the tail
        Tail tail = readTail();

        // Loop around all of the objects in the space,
        for(int i = 1; i <= tail.position; i++) {
            // Create a quick template
            User user = new User();
            user.id = i;

            // Search for it
            User returnedUser = null;
            try {
                returnedUser = searchForUser(user);
            } catch (UserNotFoundException e) {
            }

            if(returnedUser != null)
                returnable.add(returnedUser);
        }

        return returnable;
    }

    /**
     * This will read the tail from the JavaSpace of the users channel. If the Tail doesn't exist, it will be created.
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
     * This will take the tail from the users channel, from the JavaSpace.
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
     * Set up method to create 'users' channel, should it not exist
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
