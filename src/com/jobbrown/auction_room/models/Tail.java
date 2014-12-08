package com.jobbrown.auction_room.models;

import net.jini.core.entry.Entry;

/**
 * Created by job on 30/10/14.
 */
public class Tail implements Entry {
	private static final long serialVersionUID = -2098967834853620599L;
	
	public String channel;
    public Integer position;

    public Tail() {
    }

    public Tail(String channel) {
        this.channel = channel;
    }

    public void increment() {
        this.position = this.position + 1;
    }
}
