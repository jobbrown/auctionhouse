package com.jobbrown.auction_room.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;
import org.controlsfx.dialog.Dialogs;

import com.jobbrown.auction_room.exceptions.LotNotFoundException;
import com.jobbrown.auction_room.helpers.BidList;
import com.jobbrown.auction_room.helpers.JavaSpacesLotService;
import com.jobbrown.auction_room.helpers.JavaSpacesNotificationService;
import com.jobbrown.auction_room.models.Bid;
import com.jobbrown.auction_room.models.Lot;
import com.jobbrown.auction_room.models.Notification;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

@SuppressWarnings("deprecation")
public class LotOwnerOptionsController implements Initializable  {
	/**
	 * This contains the lot which is being managed
	 */
	private Lot lot;
	
	/**
	 * This contains a reference to the popover which we are contained within
	 */
	public PopOver po;
	
	@FXML public Label lotName;
	@FXML public TextField lotNameField;
	@FXML public TextArea lotDescription;
	@FXML public Button endBidNoWinner;
	@FXML public Button endBidHighestBidder;
	
	/**
	 * This method is automatically called when this controller is called when the view is initialized by JavaFX
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Set the lot which should be used for this view.
	 * @param t the lot
	 */
	public void setLot(Lot t) {
		this.lot = t;
		this.loadLot();
	}
	
	/**
	 * After a lot has been set, load the specific values out of the lot and display them
	 */
	public void loadLot() {
		this.lotName.setText(this.lot.title);
		this.lotNameField.setText(this.lot.title);
		this.lotDescription.setText(this.lot.description);
	}
	
	/**
	 * Checks whether there are any validation errors on the form. Return boolean if the form is valid
	 * @return are the entered values valid?
	 */
	public boolean isValid() {
		return this.getValidationErrors().size() == 0;
	}
	
	
	/**
	 * Check all the input fields on the form and see if there are any errors.
	 * @return ArrayList of errors (or an arraylist of length 0 if there are none).
	 */
	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		
		// Lot Title
		if(lotNameField.getText().length() < 4) {
			errors.add("Lot Title should be greater than 5 characters long");
		}
		
		// Lot Description
		if(lotDescription.getText().length() < 10) {
			errors.add("Please add a suitable description of the product you are selling");
		}		
		
		return errors;
	}
	
	/**
	 * This method is fired when the end bid with no winner is selected.
	 * It takes care of setting it as not active.
	 */
	@SuppressWarnings("deprecation")
	public void endBidNoWinnerButtonClicked() {
		JavaSpacesLotService ls = new JavaSpacesLotService();
        
		// Get the lot
		Lot searchLot = ls.stripAllProperties(this.lot);
		Lot returnedLot = null;
		
		try {
			returnedLot = (Lot) ls.searchForLot(searchLot);
		} catch (LotNotFoundException e) {
			e.printStackTrace();
		}
		
		if(returnedLot != null) {
			// Set active to false
			returnedLot.active = false;
			
			// Write it back
			if(ls.updateLot(returnedLot)) {
				// Show popup
				Dialogs.create()
					.owner(this.po)
					.title("Lot Finished")
					.masthead("That lot has been ended.")
					.message("That lot has been removed from sale. There is no winning bidder.")
					.showInformation();
				
				// Close the popover
				this.closePopover();
			} else {
				// Show popup
				Dialogs.create()
					.owner(this.po)
					.title("Unknown Error")
					.masthead("Couldn't update Lot")
					.message("We were unable to update your lot. It is still listed as for sale.")
					.showInformation();
			}
		}
	}
	
	/**
	 * This is fired when the End Lot and accept highest bidder button is clicked.
	 * It will set it as no longer active, set the winning bid, and notify the winner. 
	 */
	@SuppressWarnings("deprecation")
	public void endBidHighestBidderButtonClicked() {
		JavaSpacesLotService ls = new JavaSpacesLotService();
		
		// Get the lot
		Lot searchLot = ls.stripAllProperties(this.lot);
		Lot returnedLot = null;
		
		try {
			returnedLot = (Lot) ls.searchForLot(searchLot);
		} catch (LotNotFoundException e) {
			e.printStackTrace();
		}
		
		if(returnedLot == null) {
			System.out.println("Unexcepted error");
		} else {
			// Load the bidlist
			BidList bl = new BidList(returnedLot.bids);
			
			// Check we have some bids
			if(bl.count() == 0) {
				// There aren't any bids
				Dialogs.create()
					.owner(this.po)
					.title("error")
					.masthead("No Bids")
					.message("There are no bids on that lot - you can't accept the highest bid when there isn't one")
					.showError();
			} else {
				// Copy the highest bid into winningBid
				Bid highestBid = bl.sortByLargestBid().one();
				returnedLot.winningBid = highestBid;
				
				// Set active to false
				returnedLot.active = false;
				
				// Write it back
				ls.updateLot(returnedLot);
				
				// Notify the winner
				Notification notification = new Notification("Your bid has just been confirmed as the winning bid for " + returnedLot.title, highestBid.bidder.username);
				
				JavaSpacesNotificationService ns = new JavaSpacesNotificationService();
				ns.channelName = highestBid.bidder.username;
				
				if(ns.addNotification(notification)) {
				} else {
					System.out.println("Failed to add Notification");
				}
				
				// Show message
				Dialogs.create()
					.owner(this.po)
					.title("Lot Completed")
					.masthead("That lot has been ended.")
					.message("That lot has been removed from sale. The winning bidder has been notified")
					.showInformation();
			}
		}
		
		
		
		// Close the popover
		this.closePopover();
	}
	
	/**
	 * This is fired when the Save Changes button is clicked. This will only update the title/description on the lot.
	 */
	@SuppressWarnings("deprecation")
	public void saveChangedButtonClicked() {
		// Write the changes onto the object
		if(this.isValid()) {
			// Load the helper
			JavaSpacesLotService ls = new JavaSpacesLotService();
			
			// Grab an updated copy of the lot
			Lot returnedLot = null;
			try {
				returnedLot = (Lot) ls.searchForLot(this.lot);
			} catch (LotNotFoundException e) {
				System.out.println("This shouldnt happen hopefully");
				Dialogs.create()
					.owner(this.po)
					.title("Error")
					.message("An unexpected error occured, please try again")
					.showError();
			}
			
			if(returnedLot == null) {
				Dialogs.create()
					.owner(this.po)
					.title("Error")
					.message("An unexpected error occured, please try again")
					.showError();
			} else {
				// Save the changes
				returnedLot.title = this.lotNameField.getText();
				returnedLot.description = this.lotDescription.getText();
				
				if(ls.updateLot(returnedLot)) {
					Dialogs.create()
						.owner(this.po)
						.title("Success")
						.message("That lot has been updated")
						.showInformation();
				} else {
					Dialogs.create()
					.owner(this.po)
					.title("Error")
					.message("An unexpected error occured, please try again 1")
					.showError();
				}
				
				// Close the popover
				this.closePopover();
			}			
		} else {
			Dialogs.create()
				.owner(this.po)
				.title("Error")
				.masthead("Validation Errors")
				.message(this.buildErrorString(this.getValidationErrors()))
				.showError();
		}
	}
	
	/**
	 * This function will close the popover which this view is contained within.
	 */
	private void closePopover() {
		this.po.hide();
	}

	/** 
	 * This will build a single string of the errors ready for displaying from an ArrayList.
	 * @param errors the errors
	 * @return String the concatinated errors 
	 */
	private String buildErrorString(ArrayList<String> errors) {
		String builtString = "";
		for(String s : errors)  {
			builtString = builtString + s + "\n\n";
		}
		return builtString;
	}
	
}
