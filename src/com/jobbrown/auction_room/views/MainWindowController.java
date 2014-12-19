package com.jobbrown.auction_room.views;


import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import com.jobbrown.auction_room.enums.Category;
import com.jobbrown.auction_room.helpers.JavaSpacesLotService;
import com.jobbrown.auction_room.interfaces.helpers.LotService;
import com.jobbrown.auction_room.models.Lot;

public class MainWindowController implements Initializable {
	// View Lots Tab
	@FXML public TableView<Lot> lotsTable;
	
	@FXML public TableColumn<Lot, String> tcID;
	@FXML public TableColumn<Lot, String> tcTitle;
	@FXML public TableColumn<Lot, String> tcDescription;
	@FXML public TableColumn<Lot, String> tcEndTime;
	@FXML public TableColumn<Lot, String> tcPrice;
	@FXML public TableColumn<Lot, String> tcCategory;
	
	@FXML public Button clearSearchSettingsButton;
	@FXML public Button searchButton;
	
	// Create Lot Tab
	@FXML public ComboBox<Category> createLotLotType;
	@FXML public BorderPane createLotPane;
	
	private CreateGenericLotController createGenericLotController;
	
	
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
	
	@SuppressWarnings("unchecked")
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
		
		
		// When its selected, load the detailed panel of the bids
		lotsTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            //Check whether item is selected and set value of selected item to Label
            if (lotsTable.getSelectionModel().getSelectedItem() != null) {
            	this.loadDetailedLotPane(newValue);
            }
        });
		fillTable();
		
	}
	
	public void loadDetailedLotPane(Lot t) {
		
	}
	
	public void fillTable() {
		LotService ls = new JavaSpacesLotService();
		
		final ObservableList<Lot> data = FXCollections.observableArrayList(
		    ls.getAllLots()
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
			if(createGenericLotController == null) {
				System.out.println("controller was null");
			}
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
		FXMLLoader fxmlLoader = new FXMLLoader();
		
		Pane p = null;
		
		try {
			
			// Load generic controller
			p = fxmlLoader.load(getClass().getResource("CreateGenericLot.fxml").openStream());
			
			this.createGenericLotController = (CreateGenericLotController) fxmlLoader.getController();
			
			
		} catch (IOException e) {
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
