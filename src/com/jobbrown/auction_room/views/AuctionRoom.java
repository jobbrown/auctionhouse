package com.jobbrown.auction_room.views;

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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
