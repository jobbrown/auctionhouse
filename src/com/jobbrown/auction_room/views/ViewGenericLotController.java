package com.jobbrown.auction_room.views;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;
import org.controlsfx.dialog.Dialogs;

import com.jobbrown.auction_room.helpers.BidList;
import com.jobbrown.auction_room.helpers.JavaSpacesUserService;
import com.jobbrown.auction_room.interfaces.helpers.UserService;
import com.jobbrown.auction_room.models.Bid;
import com.jobbrown.auction_room.models.Lot;
import com.jobbrown.auction_room.models.User;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

@SuppressWarnings("deprecation")
public class ViewGenericLotController implements Initializable  {
	public Lot lot = null;
	public MainWindowController parent = null;
	
	@FXML public AnchorPane view;
	@FXML public Label lotName;
	@FXML public TextArea lotDescription;
	@FXML public Label startingPrice;
	@FXML public Label highestPublicBid;
	@FXML public Label startingTime;
	@FXML public Label endingTime;
	
	@FXML public TableView<Bid> bidsTable;
	@FXML public TableColumn<Bid, String> tcBidder;
	@FXML public TableColumn<Bid, String> tcAmount;
	@FXML public TableColumn<Bid, String> tcDate;
	
	@FXML public Button addBidButton;
	@FXML public Button ownerOptionsButton;
	
	/**
	 * This function will be called while the GUI is initializing.
	 */
	public void preload() {
		this.preloadTable();
	}
	
	/**
	 * This will accept a Lot t and set it to the local class variable of 'lot'
	 * It will then call private function loadLot which takes care of loading the
	 * lots params into the field params. 
	 * @param t The lot to be loaded into the GUI
	 */
	@SuppressWarnings("deprecation")
	public void loadLot(Lot t) {
		if(!t.active) {
			Dialogs.create()
				.owner(view)
				.masthead("Error")
				.message("That auction has been removed from sale.")
				.showError();
			
			this.parent.searchButtonClicked();
		} else {
			this.lot = t;
			this.loadLot();
		}
	}
	
	/**
	 * This allows a class variable of 'lot' has been set containing the lot
	 * which should laoded. It will take the properties from there and then
	 * populate the form field using its data.
	 */
	private void loadLot() {
		// Title of the lot
		this.lotName.setText(this.lot.title);
		
		// Description of the lot
		this.lotDescription.setText(this.lot.description);
		
		// The table of bids
		BidList bl = new BidList(this.lot.bids);
		
		if(bl.count() > 0) {
			ArrayList<Bid> bids = bl.onlyPublic().sortByNewest().get();
			
			final ObservableList<Bid> data = FXCollections.observableArrayList(
			    bids
			);
				
			if(bidsTable != null) {
				bidsTable.setItems(data);
				
			} else { System.out.println("Table is null"); }
		}
		
		// Starting price
		this.startingPrice.setText(this.lot.price + "");
		
		// Highest Public Bid
		bl = new BidList(this.lot.bids);
		Bid highestBid = bl.sortByLargestBid().onlyPublic().one();
		
		if(this.highestPublicBid != null) {
			if(highestBid == null) {
				this.highestPublicBid.setText("No public bids yet!");
			} else {
				this.highestPublicBid.setText(highestBid.amount + "");
			}
		}
		
		// Preload bid table
		if(bidsTable == null) {
			System.out.println("Table was null. WTF");
		} else {
			ObservableList<Bid> data = null;
			
			bl = new BidList(this.lot.bids);			
			
			if(this.lot.bids != null && this.lot.bids.size() > 0) {
				data = FXCollections.observableArrayList(
						bl.onlyPublic().get()
				);
			} else {
				data = FXCollections.observableArrayList(
						new ArrayList<Bid>()
				);
			}
				
			bidsTable.setItems(data);
		}
		
		// Starting time
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:ss");
		this.startingTime.setText(dateFormat.format(this.lot.startTime));
		
		// Ending time
		this.endingTime.setText(dateFormat.format(this.lot.endTime));
		
		// Owner button
		this.prepareOwnerButton();
	}
	
	/**
	 * This method takes care of preparing the table to be viewed. It binds each column
	 * to a property of the Bid class so that Bids can be displayed on the table. 
	 */
	private void preloadTable() {
		
		tcBidder.setCellValueFactory(new Callback<CellDataFeatures<Bid, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Bid, String> c) {
		    		return new SimpleStringProperty(c.getValue().bidder.username);
		     	}
		    }
		);
		
		tcAmount.setCellValueFactory(new Callback<CellDataFeatures<Bid, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Bid, String> c) {
		    		return new SimpleStringProperty(c.getValue().amount + "");
		     	}
		    }
		);
		
		tcDate.setCellValueFactory(new Callback<CellDataFeatures<Bid, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Bid, String> c) {
		    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:ss");
		    		return new SimpleStringProperty(dateFormat.format(c.getValue().date));
		     	}
		    }
		);
	}
	
	/**
	 * This is fired when the add button is clicked. It loads the FXML of AddBid.fxml and then
	 * loads it into a popover which is displayed to the user.
	 */
	public void addBidButtonClicked() {		
		FXMLLoader fxmlLoader = null;
		AddBidController abc = null;
		Pane p = null;
		
		try {
			fxmlLoader = new FXMLLoader();
			
			fxmlLoader.setLocation(ViewGenericLotController.class.getResource("AddBid.fxml"));
			p = fxmlLoader.load();
			
			abc = (AddBidController) fxmlLoader.getController();
			
			abc.setLot(this.lot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PopOver po = new PopOver((Node) p);
		
		abc.popover = po;
		
		po.setDetached(true);
		po.setDetachedTitle("Bidding on " + this.lot.title);
		po.show(this.view);
	}
	
	/**
	 * This is fired when the owner options button is clicked. It takes care of loading the FXML
	 * of "LotOwnerOptions.fxml" and displaying it in a popover. 
	 */
	public void ownerOptionsButtonClicked() {
		FXMLLoader fxmlLoader = null;
		LotOwnerOptionsController looc = null;
		Pane p = null;
		
		try {
			fxmlLoader = new FXMLLoader();
			
			fxmlLoader.setLocation(ViewGenericLotController.class.getResource("LotOwnerOptions.fxml"));
			p = fxmlLoader.load();
			
			looc = (LotOwnerOptionsController) fxmlLoader.getController();
			
			looc.setLot(this.lot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PopOver po = new PopOver((Node) p);
		
		looc.po = po;
		
		po.setDetached(true);
		po.setDetachedTitle("Owner options of: " + this.lot.title);
		po.show(this.view);
	}
	
	/*
	 * This function is used to determine whether the owner options button should be displayed
	 * for the user or not (dependent on if they created it).
	 */
	public void prepareOwnerButton() {
		UserService us = JavaSpacesUserService.getInstance();
		User lotSeller = us.getUserByID(this.lot.seller);
		
		if(lotSeller != null && us.getCurrentUser().id.equals(lotSeller.id)) {
			ownerOptionsButton.setVisible(true);
		} else {
			ownerOptionsButton.setVisible(false);
		}
	}
	
	/**
	 * Default initialize function provided by the Initializable interface. Called as soon
	 * as the view is created.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.preload();
	}
}
