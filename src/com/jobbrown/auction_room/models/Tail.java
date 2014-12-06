package com.jobbrown.auction_room.models;

import net.jini.core.entry.Entry;

/**
 * Created by job on 30/10/14.
 */
public class Tail implements Entry {
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
