package com.jobbrown.auction_room.views;

import java.awt.Button;
import java.net.URL;
import java.util.ResourceBundle;

import com.jobbrown.auction_room.enums.Category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

public class MainWindowController implements Initializable {	
	// View Lots Tab
	
	
	// Create Lot Tab
	@FXML public ComboBox<Category> createLotLotType;
	@FXML public Button testButton;
	
	// My Account Tab
	
	// View Lots
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("im here2");
		preloadLotTypeComboBox();
	}
	
	// Methods
	
	@FXML public void test() {
		System.out.println("clicked");
	}
	
	// View Lots Tab
	
	// Create Lot Tab
	
	
	
	// My Account Tab
	
	
	
	private void preloadLotTypeComboBox() {
		System.out.println("im here");
		ObservableList<Category> options = 
			    FXCollections.observableArrayList(
			    		Category.values()
			    );
		createLotLotType.setItems(options);
	}
}
