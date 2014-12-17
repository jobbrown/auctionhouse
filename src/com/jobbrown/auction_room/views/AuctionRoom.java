package com.jobbrown.auction_room.views;

import java.util.Date;

import com.jobbrown.auction_room.exceptions.LotNotFoundException;
import com.jobbrown.auction_room.helpers.BidList;
import com.jobbrown.auction_room.helpers.JavaSpacesLotService;
import com.jobbrown.auction_room.models.Bid;
import com.jobbrown.auction_room.models.Lot;
import com.jobbrown.auction_room.thirdparty.gallen.SpaceUtils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by job on 21/10/14.
 */
public class AuctionRoom extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
    	Parent root = null;
    	
    	if(SpaceUtils.getSpace() != null) {
    		root = FXMLLoader.load(getClass().getResource("LoginWindow.fxml"));
            primaryStage.setTitle("Auction Room U1150928");
    	} else {
    		root = FXMLLoader.load(getClass().getResource("SpaceError.fxml"));
            primaryStage.setTitle("Error Establishing Connection");
            
    	}
    	
    	primaryStage.setScene(new Scene(root));
        primaryStage.show();
    	
        
    	
    	//AuctionRoom ar = new AuctionRoom();
    }

    public static void main(String[] args) {
        launch(args);
    	//AuctionRoom ar = new AuctionRoom();
    }
    
    public AuctionRoom() {
    	
    }
    
    public AuctionRoom(String str) {
    	// addBidToLot(lotID 1, bid amount, bid public, bid by) 
    	JavaSpacesLotService ls = new JavaSpacesLotService();
    	
    	// Create a quick lot
    	Lot lot = new Lot();
    	lot.title = "My Car";
    	lot.description = "This is my really cool car";
    	
    	/*
    	if(ls.addLot(lot)) {
    		System.out.println("Created lot");
    	} else { System.out.println("Failed to create lot"); }
    	*/
    	
    	// Get it back from the system
    	lot = new Lot();
    	lot.title = "My Car";
    	
    	Lot receivedLot = null;
    	try {
			receivedLot = (Lot) ls.searchForLot(lot);
		} catch (LotNotFoundException e) {
			e.printStackTrace();
		}
    	
    	// Create a few hacky bids
    	Bid bid1 = new Bid(true, 100.00, 1, new Date());
    	Bid bid2 = new Bid(true, 99.99, 2, new Date());
    	Bid bid3 = new Bid(false, 150.00, 3, new Date());
    	
    	ls.addBidToLot(receivedLot, bid1);
    	ls.addBidToLot(receivedLot, bid2);
    	ls.addBidToLot(receivedLot, bid3);
    	
    	// Get it back again
    	lot = new Lot();
    	lot.title = "My Car";
    	
    	receivedLot = null;
    	try {
			receivedLot = (Lot) ls.searchForLot(lot);
		} catch (LotNotFoundException e) {
			e.printStackTrace();
		}
    	
    	BidList bl = new BidList(receivedLot.bids);
    	
    	System.out.println("There are " + bl.count() + " bids");
    	System.out.println(bl.sortByNewest().get());
    }
}
