package com.jobbrown.auction_room.enums;

/**
 * Created by job on 21/10/14.
 */
public enum Category {
    COLLECTABLES (null, "COLLECTABLES", "Collectables"),

    ELECTRONICS (null, "ELECTRONICS", "Electronics"),
    MOBILEPHONES (ELECTRONICS, "MOBILEPHONES", "Mobile Phones"),
    SOUND (ELECTRONICS, "SOUND", "Sound & Vision"),
    GAMES (ELECTRONICS, "GAMES", "Video Games"),
    COMPUTERS (ELECTRONICS, "COMPUTERS", "Computers & Tablets"),

    MOTORS (null, "MOTORS", "Motors"),
    CARS (MOTORS, "CARS", "Cars"),
    MOTORBIKES(MOTORS, "MOTORBIKES", "Motorbikes"),

    OTHER (null, "OTHER", "Other listings");

    private final Category parent;
    private final String code;
    private final String title;

    Category(Category parent, String code, String title) {
        this.parent = parent;
        this.code = code;
        this.title = title;
    }

    public Category getParent() {
        return this.parent;
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}