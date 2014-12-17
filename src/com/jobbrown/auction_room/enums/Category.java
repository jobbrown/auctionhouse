package com.jobbrown.auction_room.enums;

/**
 * Created by job on 21/10/14.
 */
public enum Category {
    COLLECTABLES (null, "COLLECTABLES", "Collectables", "CreateGenericLot.fxml"),
    ELECTRONICS (null, "ELECTRONICS", "Electronics", "CreateGenericLot.fxml"),
    MOTORING (null, "MOTORING", "Motoring", "CreateGenericLot.fxml"),
    OTHER (null, "OTHER", "Other listings", "CreateGenericLot.fxml");

    private final Category parent;
    private final String code;
    private final String title;
    private final String filename;

    Category(Category parent, String code, String title, String filename) {
        this.parent = parent;
        this.code = code;
        this.title = title;
        this.filename = filename;
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
    
    public String getFilename() {
    	return this.filename;
    }
    
    @Override
    public String toString() {
    	return this.getTitle();
    }
}