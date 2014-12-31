package com.jobbrown.auction_room.views;


import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Dialogs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import com.jobbrown.auction_room.enums.Category;
import com.jobbrown.auction_room.helpers.BidList;
import com.jobbrown.auction_room.helpers.JavaSpacesLotService;
import com.jobbrown.auction_room.helpers.JavaSpacesUserService;
import com.jobbrown.auction_room.helpers.LotList;
import com.jobbrown.auction_room.interfaces.helpers.LotService;
import com.jobbrown.auction_room.interfaces.helpers.UserService;
import com.jobbrown.auction_room.models.Bid;
import com.jobbrown.auction_room.models.Lot;
import com.jobbrown.auction_room.models.User;

public class MainWindowController implements Initializable {
	// Main Window
	@FXML public TabPane tabPane;
	
	// View Lots Tab
	@FXML public TableView<Lot> lotsTable;
	
	@FXML public TableColumn<Lot, String> tcID;
	@FXML public TableColumn<Lot, String> tcTitle;
	@FXML public TableColumn<Lot, String> tcDescription;
	@FXML public TableColumn<Lot, String> tcEndTime;
	@FXML public TableColumn<Lot, String> tcPrice;
	@FXML public TableColumn<Lot, String> tcCategory;
	@FXML public TableColumn<Lot, String> tcHighestBid;
	@FXML public TableColumn<Lot, String> tcLotSeller;
	
	@FXML public Button clearSearchSettingsButton;
	@FXML public Button searchButton;
	
	@FXML public BorderPane viewLotPane;
	
	// Create Lot Tab
	@FXML public ComboBox<Category> createLotLotType;
	@FXML public BorderPane createLotPane;
	
	// Controllers
	private CreateGenericLotController createGenericLotController;
	private ViewGenericLotController viewGenericLotController;
	
	
	// My Account Tab
	
	// View Lots
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		preloadLotTypeComboBox();
		preloadTable();
		preloadControllers();
	}
	
	// Methods
	
	
	
	
	
	// View Lots Tab
	
	public void searchButtonClicked() {
		fillTable();
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void preloadTable() {
		// Bind "id" to tcID
		
		tcID.setCellValueFactory(new Callback<CellDataFeatures<Lot, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Lot, String> c) {
		    		return new SimpleStringProperty(c.getValue().id + "");
		     	}
		    }
		);
		
		// Bind "title" to tcTitle
		tcTitle.setCellValueFactory(new Callback<CellDataFeatures<Lot, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Lot, String> c) {
		    		return new SimpleStringProperty(c.getValue().title);
		     	}
		    }
		);
		
		// Bind "description" to tcDescription
		tcDescription.setCellValueFactory(new Callback<CellDataFeatures<Lot, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Lot, String> c) {
		    		return new SimpleStringProperty(c.getValue().description);
		     	}
		    }
		);
		
		// Bind endDate through a callback
		tcEndTime.setCellValueFactory(new Callback<CellDataFeatures<Lot, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Lot, String> c) {
		    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:ss");	 
		      
		    		return new SimpleStringProperty(dateFormat.format(c.getValue().endTime));
		     	}
		    }
		);
		
		// Bind price to the model
		tcPrice.setCellValueFactory(new Callback<CellDataFeatures<Lot, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Lot, String> c) {
		    		return new SimpleStringProperty(c.getValue().price + "");
		     	}
		    }
		);
		
		// Bind Category
		tcCategory.setCellValueFactory(new Callback<CellDataFeatures<Lot, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Lot, String> c) {
		    		return new SimpleStringProperty(c.getValue().category.getTitle());
		     	}
		    }
		);
		
		// Get highest bid
		tcHighestBid.setCellValueFactory(new Callback<CellDataFeatures<Lot, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Lot, String> c) {
			    	BidList bl = new BidList(c.getValue().bids);
			    	Bid bid = bl.sortByLargestBid().onlyPublic().one();
			    	
			    	if(bid == null) {
			    		return new SimpleStringProperty("No public bids yet.");
			    	} else {
			    		return new SimpleStringProperty(bid.toString());
			    	}
		    	}
		    }
		);
		
		tcLotSeller.setCellValueFactory(new Callback<CellDataFeatures<Lot, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Lot, String> c) {
		    	UserService us = JavaSpacesUserService.getInstance();
		    	User owner = us.getUserByID(c.getValue().seller);
		    		return new SimpleStringProperty(owner.username);
		     	}
		    }
		);
		
		
		// When its selected, load the detailed panel of the bids
		lotsTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            //Check whether item is selected and set value of selected item to Label
            if (lotsTable.getSelectionModel().getSelectedItem() != null) {
            	// Load it from the space and display it
            	JavaSpacesLotService ls = new JavaSpacesLotService();
            	
            	Lot returnedLot = null;
            	try {
					returnedLot = (Lot) ls.searchForLot(newValue);
				} catch (Exception e) {
					Dialogs.create()
						.owner(tabPane)
						.masthead("Error")
						.message("That auction has been removed from sale.")
						.showError();
					
					this.searchButtonClicked();
				}
            	
            	if(returnedLot != null) {
            		this.loadDetailedLotPane(returnedLot);
            	}
            }
        });
		
		fillTable();
	}
	
	public void loadDetailedLotPane(Lot t) {		
		switch(t.category.getCode()) {
		case "COLLECTABLES":
			viewLotPane.setCenter(viewGenericLotController.view);
			break;
		case "ELECTRONICS":
			viewLotPane.setCenter(viewGenericLotController.view);
			break;
		case "MOTORING":
			viewLotPane.setCenter(viewGenericLotController.view);
			break;
		case "OTHER":
			viewLotPane.setCenter(viewGenericLotController.view);
			break;
		default:
			viewLotPane.setCenter(createGenericLotController.view);
			break;
		}
		
		if(viewGenericLotController == null) {
			System.out.println("viewgenericlotcontroller is null");
		} else {
			viewGenericLotController.loadLot(t);
		}
	}
	
	public void fillTable() {
		LotService ls = new JavaSpacesLotService();
		
		final ObservableList<Lot> data = FXCollections.observableArrayList(
		    new LotList().active().all()
		);
		
		if(lotsTable != null) {
			lotsTable.setItems(data);
			
		} else { System.out.println("Table is null"); }
		
	}
	
	
	// Create Lot Tab
	
	@FXML public void lotTypeChanged() {
		// Load the correct panel in
		Category selectedCategory = createLotLotType.getSelectionModel().getSelectedItem();
		
		this.loadAddLotForm(selectedCategory);
		
	}
	
	// My Account Tab
	
	/**
	 * Given a category, loads the approapiate view for adding that lot
	 * @param category
	 */
	private void loadAddLotForm(Category category) {
		switch(category.getCode()) {
		case "COLLECTABLES":
			createLotPane.setCenter(createGenericLotController.view);
			break;
		case "ELECTRONICS":
			createLotPane.setCenter(createGenericLotController.view);
			break;
		case "MOTORING":
			createLotPane.setCenter(createGenericLotController.view);
			break;
		case "OTHER":
			createLotPane.setCenter(createGenericLotController.view);
			break;
		default:
			createLotPane.setCenter(createGenericLotController.view);
			break;
		}
		
	}
	
	/**
	 * Preloads the controllers we might need for each lot type
	 */
	private void preloadControllers() {
		FXMLLoader fxmlLoader = null;
		
		Pane p = null;
		
		try {
			// VIEW Controllers
			
			//FXMLLoader loader = new FXMLLoader();
	        //loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
	        //rootLayout = (BorderPane) loader.load();
			
			fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(MainWindowController.class.getResource("ViewGenericLot.fxml"));
			p = fxmlLoader.load();
			this.viewGenericLotController = (ViewGenericLotController) fxmlLoader.getController();
			this.viewGenericLotController.parent = this;
			
			//p = fxmlLoader.load(getClass().getResource("ViewGenericLot.fxml").openStream());
			//this.viewGenericLotController = (ViewGenericLotController) fxmlLoader.getController();
			
			fxmlLoader = new FXMLLoader();
			
			// CREATE Controllers
			p = fxmlLoader.load(getClass().getResource("CreateGenericLot.fxml").openStream());
			this.createGenericLotController = (CreateGenericLotController) fxmlLoader.getController();
			this.createGenericLotController.parent = this;
			
		} catch (IOException e) {
			System.out.println("Caught exception");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Preloads Category options into Combo Box on Add Lot Page
	 */
	private void preloadLotTypeComboBox() {
		ObservableList<Category> options = 
			    FXCollections.observableArrayList(
			    		Category.values()
			    );
		createLotLotType.setItems(options);
	}
}
