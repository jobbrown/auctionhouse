package com.jobbrown.auction_room.interfaces.helpers;

import com.jobbrown.auction_room.exceptions.IncorrectLoginException;
import com.jobbrown.auction_room.exceptions.UserNotFoundException;
import com.jobbrown.auction_room.models.User;

import java.util.ArrayList;


public interface UserService {

    /**
     * Create a new user from required information
     * @param username entered username
     * @param password entered password
     * @param email entered email
     * @return success as a boolean
     * @throws Exception if username or email is already in use
     */
    public boolean createUser(String username, String password, String email) throws Exception;

    /**
     * Get all users currently registered on the system
     * @return list of users
     */
    public ArrayList<User> getAllUsers();

    /**
     * Check a username/password combination to see if it matches one of a stored user
     * @param username username entered
     * @param password password entered
     * @return success as a boolean
     * @throws IncorrectLoginException login incorrect
     */
    public boolean login(String username, String password) throws IncorrectLoginException;

    /**
     * Log the user out of the system
     * @return success as a boolean
     */
    public boolean logout();

    /**
     * Get the current logged in user
     * @return User or null
     */
    public User getCurrentUser();

    /**
     * Search of a user in the system
     * @param u User to search for
     * @return User
     * @throws UserNotFoundException
     */
    public User searchForUser(User u) throws UserNotFoundException;
    
    public User getUserByID(Integer id);
    
    /**
     * Checks if a username is unused by any other user
     * @param username
     * @return if username is unique
     */
    public boolean isUsernameUnique(String username);
    
    /**
     * Checks if a given e-mail address is unused by any other user
     * @param email
     * @return if email is unique
     */
    public boolean isEmailUnique(String email);
    

}
