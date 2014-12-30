package com.jobbrown.auction_room.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Dialogs;

import com.jobbrown.auction_room.enums.Category;
import com.jobbrown.auction_room.helpers.JavaSpacesLotService;
import com.jobbrown.auction_room.helpers.JavaSpacesUserService;
import com.jobbrown.auction_room.interfaces.helpers.LotService;
import com.jobbrown.auction_room.interfaces.helpers.UserService;
import com.jobbrown.auction_room.models.Lot;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CreateGenericLotController implements Initializable  {

	@FXML public GridPane view;
	@FXML public TextField lotTitle;
	@FXML public TextArea lotDescription;
	@FXML public Slider lotLength;
	@FXML public TextField lotStartingPrice;
	@FXML public Button lotSubmit;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		preload();
		
	}
	
	/**
	 * Function for calling individual methods for loading the form
	 */
	public void preload() {
	}
	
	/**
	 * Valid the completion of the add form
	 * @return if the form is valid
	 */
	public boolean isValid() {
		return this.getValidationErrors().size() == 0;
	}
	
	/**
	 * Checks the form to see if it's valid, returns an ArrayList of errors.
	 * Empty list means a valid form.
	 * @return ArrayList of errors
	 */
	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		
		// Lot Title
		if(lotTitle.getText().length() < 4) {
			errors.add("Lot Title should be greater than 5 characters long");
		}
		
		// Lot Description
		if(lotDescription.getText().length() < 10) {
			errors.add("Please add a suitable description of the product you are selling");
		}
		
		// Starting Price
		// Check if they added a value before trying to validate it
		if(lotStartingPrice.getText() != null && lotStartingPrice.getText().length() > 0) {
			Double parsedStartingPrice = null;
			try {
				parsedStartingPrice = Double.parseDouble(lotStartingPrice.getText());	
				
				if(parsedStartingPrice < 0) {
					errors.add("Starting price should be greater than 0");
				}
			} catch (NumberFormatException e) {
				errors.add("That starting price was not a recognised format");
			}
		}		
		
		return errors;
	}
	
	/**
	 * Event for when submit button of create lot form is clicked
	 */
	@SuppressWarnings("deprecation")
	public void submitButtonClicked() {
		if(this.isValid()) {
			// Lets try and create that lot
			Lot newLot = new Lot();
			
			// Set the title
			newLot.title = lotTitle.getText();
			
			// Set the description
			newLot.description = lotDescription.getText();
			
			// Set the start time to NOW
			newLot.startTime = new Date();
			
			// Set the end date to now + length
			newLot.endTime = new Date();
			newLot.endTime.setTime(newLot.endTime.getTime() +  (long) lotLength.getValue() * (1000 * 60 * 60 * 24));
			
			// Load the active user and set that as the seller
			UserService us = JavaSpacesUserService.getInstance();
			newLot.seller = us.getCurrentUser().id;
			
			// Set the category
			newLot.category = Category.OTHER;
			
			// Set the price
			if(lotStartingPrice.getText() != null && lotStartingPrice.getText().length() > 0) {
				newLot.price = Double.parseDouble(lotStartingPrice.getText()); 
			} else {
				newLot.price = 0.01;
			}
			
			LotService ls = new JavaSpacesLotService();
			
			if(ls.addLot(newLot)) {
				Dialogs.create()
				.owner(view)
				.masthead("Success")
				.message("That lot has been added to the system. TODO notify and change tab")
				.showInformation();
			} else {
				Dialogs.create()
				.owner(view)
				.masthead("Error")
				.message("Failed to add to space. Unknown reason.")
				.showError();
			}
			
		} else {
			Dialogs.create()
			.owner(view)
			.masthead("Validation Error")
			.message(this.buildErrorString(this.getValidationErrors()))
			.showError();
			
		}
	}
	
	private String buildErrorString(ArrayList<String> errors) {
		String builtString = "";
		for(String s : errors)  {
			builtString = builtString + s + "\n\n";
		}
		return builtString;
	}
}
