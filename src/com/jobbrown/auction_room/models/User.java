package com.jobbrown.auction_room.models;

import com.jobbrown.auction_room.thirdparty.fitorec.Hash;
import net.jini.core.entry.Entry;

import java.util.Random;

/**
 * Created by job on 01/12/14.
 */
public class User implements Entry {
	private static final long serialVersionUID = -5039963350541765684L;
	public Integer id = null;
    public String username;
    public String salt;
    public String password;
    public String email;


    /**
     * No arg constructor
     */
    public User() {

    }

    /**
     * Given a password, ensure the salt is set (if not create it), then set the password
     * @param password password
     */
    public void setPassword(String password) {
        if(this.salt == null || this.salt.length() == 0)
            this.setSalt();

        this.password = this.hashPassword(password);
    }

    /**
     * Given a password, use the set salt to crypt the password
     * @param password the password to encrypt
     * @return hashed password
     */
    public String hashPassword(String password) {
        return Hash.getHash(this.salt + ":" + password, "MD5");
    }

    /**
     * Generate a 5 char salt of random numbers that can be used for a salt
     * To improve security this code be made to implement a longer salt, or letters
     */
    public void setSalt() {
        Random r = new Random( System.currentTimeMillis() );
        this.salt = 10000 + r.nextInt(80000) + "";
    }

    @Override
    public String toString() {
        return "\nUsername: " + this.username + "\tEmail:" + this.email + "\tPassword: " + this.password + "\tSalt:" + this.salt;
    }


}
