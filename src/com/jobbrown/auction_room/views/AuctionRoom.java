package com.jobbrown.auction_room.views;

import com.jobbrown.auction_room.helpers.JavaSpacesLotService;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by job on 21/10/14.
 */
public class AuctionRoom extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
    	/*
        Parent root = FXMLLoader.load(getClass().getResource("LoginWindow.fxml"));
        primaryStage.setTitle("Auction Room U1150928");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();*/
    	
    	AuctionRoom ar = new AuctionRoom();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public AuctionRoom() {
    	// Example usage
    	
    	// addBidToLot(lotID 1, bid amount, bid public, bid by) 
    	JavaSpacesLotService ls = new JavaSpacesLotService();
    	
    }
}
