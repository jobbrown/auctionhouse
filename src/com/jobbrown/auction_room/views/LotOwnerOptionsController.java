package com.jobbrown.auction_room.views;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;

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
	
	public void endBidNoWinnerButtonClicked() {
		// Get the lot
		
		// Set active to false
		
		// Write it back
		
		// Close the popover
		this.closePopover();
	}
	
	public void endBidHighestBidderButtonClicked() {
		// Get the lot
		
		// Copy the highest big into winningBid

		// Set active to false
		
		// Write it back
		
		// Notify the winner
		
		// Close the popover
		this.closePopover();
	}
	
	public void saveChangedButtonClicked() {
		// Write the changes onto the object
		
		// Close the popover
		this.closePopover();
	}
	
	private void closePopover() {
		this.po.hide();
	}
	
	
}
