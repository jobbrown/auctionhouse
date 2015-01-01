package com.jobbrown.auction_room.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;
import org.controlsfx.dialog.Dialogs;

import com.jobbrown.auction_room.exceptions.LotNotActiveException;
import com.jobbrown.auction_room.exceptions.LotNotFoundException;
import com.jobbrown.auction_room.helpers.JavaSpacesLotService;
import com.jobbrown.auction_room.helpers.JavaSpacesUserService;
import com.jobbrown.auction_room.models.Bid;
import com.jobbrown.auction_room.models.Lot;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

@SuppressWarnings("deprecation")
public class AddBidController implements Initializable {
	/**
	 * Contains a reference to the lot which view is representing
	 */
	public Lot lot = null;
	
	/**
	 * Contains a reference to the popover which this view is contained within
	 */
	public PopOver popover = null;
	
	@FXML public Label lotName;
	@FXML public TextField bidAmount;
	@FXML public ComboBox<String> publicPrivateBid;
	
	@FXML public Button cancelButton;
	@FXML public Button addBidButton;
	
	/**
	 * Sets the lot that this view is representing. Reloads it form the space, just to ensure its up to date. 
	 * @param t the lot
	 */
	public void setLot(Lot t) {
		// Reload it from the space
		JavaSpacesLotService ls = new JavaSpacesLotService();
		
		Lot returnedLot = null;
		try {
			returnedLot = (Lot) ls.searchForLot(t);
		} catch (LotNotFoundException e) {
			System.out.println("Unexpected error in AddBidController");
		}
		
		if(returnedLot != null) {
			this.lot = returnedLot;
			this.loadLot();
		}
	}
	
	/**
	 * After a lot has been set, load the relevent variables onto the view
	 */
	public void loadLot() {
		this.lotName.setText(this.lot.title);
	}
	
	/**
	 * This is fired when the cancel button is clicked. Simply hide the popover we're contained
	 * within.
	 */
	public void cancelButtonClicked() {
		popover.hide();
	}
	
	/**
	 * This method takes care of displaying the errors in a nice dialog.
	 */
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
	
	/**
	 * This is fired when the add button is clicked. It creates the bid, then using the LotService helper to
	 * add the bid to the lot. 
	 */
	@SuppressWarnings("deprecation")
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
				if(ls.addBidToLot(this.lot, newBid)) {
					Dialogs.create()
						.owner(this.popover)
						.title("Success")
						.message("That bid has been added")
						.showInformation();
				
					this.popover.hide();
				} else {
					Dialogs.create()
						.owner(this.popover)
						.title("Error")
						.masthead("Error")
						.message("Failed to add bid to lot")
						.showError();
					
					this.popover.hide();
				}
			} catch (LotNotFoundException e) {
				System.out.println("LotNotFound exception");
			} catch (LotNotActiveException e1) {
				Dialogs.create()
					.owner(this.popover)
					.title("Error")
					.masthead("Error")
					.message("That lot is no longer active so your bid couldn't be placed.")
					.showError();
			}
		}
	}
	
	/**
	 * Looks at all the input fields and builds an arraylist of errors
	 * @return ArrayList<String> all the errors
	 */
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
	
	/**
	 * Takes an arraylist of errors, and produces a concatenated String
	 * @param errors all errors
	 * @return String
	 */
	private String buildErrorString(ArrayList<String> errors) {
		String builtString = "";
		for(String s : errors)  {
			builtString = builtString + s + "\n\n";
		}
		return builtString;
	}
	
	/**
	 * Returns whether the form is valid
	 * @return boolean
	 */
	public boolean isValid() {
		return this.getValidationErrors().size() == 0;
	}
	
	/**
	 * Initialize view elements
	 */
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

	/**
	 * Initialize view elements
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeComboBox();
	}
}
