package com.jobbrown.auction_room.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import net.jini.core.transaction.Transaction;

import org.controlsfx.control.PopOver;
import org.controlsfx.dialog.Dialogs;

import com.jobbrown.auction_room.exceptions.LotNotFoundException;
import com.jobbrown.auction_room.helpers.JavaSpacesLotService;
import com.jobbrown.auction_room.helpers.JavaSpacesTransactionService;
import com.jobbrown.auction_room.interfaces.helpers.TransactionService;
import com.jobbrown.auction_room.models.Lot;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class LotOwnerOptionsController implements Initializable  {
	private Lot lot;
	public PopOver po;
	
	@FXML public Label lotName;
	@FXML public TextField lotNameField;
	@FXML public TextArea lotDescription;
	@FXML public Button endBidNoWinner;
	@FXML public Button endBidHighestBidder;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void setLot(Lot t) {
		this.lot = t;
		this.loadLot();
	}
	
	public void loadLot() {
		this.lotName.setText(this.lot.title);
		this.lotNameField.setText(this.lot.title);
		this.lotDescription.setText(this.lot.description);
	}
	
	public boolean isValid() {
		return this.getValidationErrors().size() == 0;
	}
	
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
	
	@SuppressWarnings("deprecation")
	public void endBidHighestBidderButtonClicked() {
		// Get the lot
		
		// Copy the highest bid into winningBid

		// Set active to false
		
		// Write it back
		
		// Notify the winner
		
		// Show message
		Dialogs.create()
			.owner(this.po)
			.title("Lot Completed")
			.masthead("That lot has been ended.")
			.message("That lot has been removed from sale. The winning bidder has been notified")
			.showInformation();
		
		// Close the popover
		this.closePopover();
	}
	
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
	
	private void closePopover() {
		this.po.hide();
	}

	private String buildErrorString(ArrayList<String> errors) {
		String builtString = "";
		for(String s : errors)  {
			builtString = builtString + s + "\n\n";
		}
		return builtString;
	}
	
}
