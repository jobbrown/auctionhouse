package com.jobbrown.auction_room.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;
import org.controlsfx.dialog.Dialogs;

import com.jobbrown.auction_room.exceptions.LotNotFoundException;
import com.jobbrown.auction_room.helpers.JavaSpacesLotService;
import com.jobbrown.auction_room.helpers.JavaSpacesUserService;
import com.jobbrown.auction_room.interfaces.helpers.LotService;
import com.jobbrown.auction_room.interfaces.helpers.UserService;
import com.jobbrown.auction_room.models.Bid;
import com.jobbrown.auction_room.models.Lot;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class AddBidController implements Initializable {
	public Lot lot = null;
	public PopOver popover = null;
	
	@FXML public Label lotName;
	@FXML public TextField bidAmount;
	@FXML public ComboBox<String> publicPrivateBid;
	
	@FXML public Button cancelButton;
	@FXML public Button addBidButton;
	
	
	
	
	
	public void setLot(Lot t) {
		this.lot = t;
		this.loadLot();
	}
	
	public void loadLot() {
		this.lotName.setText(this.lot.title);
	}
	
	public void cancelButtonClicked() {
		popover.hide();
	}
	
	@SuppressWarnings("deprecation")
	public void displayErrors() {
		ArrayList<String> errors = this.getValidationErrors();
		
		Dialogs.create()
			.owner(this.popover)
			.title("Error")
			.masthead("Validation Errors")
			.message(this.buildErrorString(errors))
			.showError();
	}
	
	public void addButtonClicked() {
		if(!this.isValid()) {
			this.displayErrors();
		} else {
			// Load the helpers
			JavaSpacesLotService ls = new JavaSpacesLotService();
			JavaSpacesUserService us = JavaSpacesUserService.getInstance();
			
			// Create the bid
			Bid newBid = new Bid();
			
			// Set the bidder
			newBid.bidder = us.getCurrentUser();
			
			// Set the bid amount
			newBid.amount = Double.parseDouble(this.bidAmount.getText());
			
			// Set the bid date (to now)
			newBid.date = new Date();
			
			// Set the public/private
			if(this.publicPrivateBid.getSelectionModel().getSelectedItem().equals("Public")) {
				newBid.publicBid = true;
			} else {
				newBid.publicBid = false;
			}
			
			try {
				ls.addBidToLot(this.lot, newBid);
			} catch (LotNotFoundException e) {
				System.out.println("LotNotFound exception");
			}
			
			

			System.out.println("Looks like its valid");
		}
	}
	
	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		
		if(this.bidAmount.getText().length() == 0) {
			errors.add("You must supply a value you would like to bid");
		} else {
			Double price = null;
			try {
				price = Double.parseDouble(this.bidAmount.getText());
				
				if(price < 0) {
					errors.add("Bid amount should be greater than 0");
				}
			} catch (NumberFormatException e) {
				errors.add("You should supply bid value in format AB.YZ eg 12.55 or 12.00");
			}
		}
		
		if(publicPrivateBid.getSelectionModel().getSelectedIndex() == -1) {
			errors.add("Select whether you would like this bid to be public or private");
		}		
		
		return errors;
	}
	
	private String buildErrorString(ArrayList<String> errors) {
		String builtString = "";
		for(String s : errors)  {
			builtString = builtString + s + "\n\n";
		}
		return builtString;
	}
	
	public boolean isValid() {
		return this.getValidationErrors().size() == 0;
	}
	
	private void initializeComboBox() {
		ArrayList<String> opts = new ArrayList<String>();
		
		opts.add("Private");
		opts.add("Public");
		
		publicPrivateBid.setItems(
			FXCollections.observableArrayList(
				opts
			)
		);
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeComboBox();
	}
}
