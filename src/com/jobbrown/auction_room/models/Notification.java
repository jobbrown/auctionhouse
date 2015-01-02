package com.jobbrown.auction_room.models;

import java.io.Serializable;
import java.util.Date;

import net.jini.core.entry.Entry;

@SuppressWarnings("serial")
public class Notification implements Entry {
	public Integer id;
	public String message;
	public String owner;
	public Date datetime;
	public Boolean read;
	
	public Notification() {
	}
	
	public Notification(String message, String owner) {
		this.message = message;
		this.owner = owner;
		this.datetime = new Date();
		this.read = false;
	}
	
	@Override
	public String toString() {
		return "ID: " + this.id + "\n" + 
				"Message: " + this.message  + "\n" + 
				"Owner: " + this.owner + "\n" + 
				"DateTime: " + this.datetime + "\n" + 
				"Read: " + this.read;
	}
}
