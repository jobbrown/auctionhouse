package com.jobbrown.auction_room.views;


import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.Dialogs;

import javafx.application.Platform;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import com.jobbrown.auction_room.enums.Category;
import com.jobbrown.auction_room.helpers.BidList;
import com.jobbrown.auction_room.helpers.JavaSpacesLotService;
import com.jobbrown.auction_room.helpers.JavaSpacesNotificationService;
import com.jobbrown.auction_room.helpers.JavaSpacesUserService;
import com.jobbrown.auction_room.helpers.LotList;
import com.jobbrown.auction_room.helpers.NotificationList;
import com.jobbrown.auction_room.interfaces.helpers.CallBack;
import com.jobbrown.auction_room.interfaces.helpers.UserService;
import com.jobbrown.auction_room.models.Bid;
import com.jobbrown.auction_room.models.Lot;
import com.jobbrown.auction_room.models.Notification;
import com.jobbrown.auction_room.models.User;
import com.jobbrown.auction_room.thirdparty.gallen.SpaceUtils;

@SuppressWarnings("deprecation")
public class MainWindowController implements Initializable, RemoteEventListener {
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
	
	// Notifications Tab
	@FXML public TableView<Notification> notificationTable;
	@FXML public TableColumn<Notification, String> tcNotificationID;
	@FXML public TableColumn<Notification, String> tcNotificationDateTime;
	@FXML public TableColumn<Notification, String> tcNotificationMessage;
	
	// Controllers
	private CreateGenericLotController createGenericLotController;
	private ViewGenericLotController viewGenericLotController;
	
	// Searching Options
	@FXML public TextField searchLotName;
	@FXML public ComboBox<Category> searchLotCategory;
	

	/**
	 * This function will be called automatically when the controller is initialized by JavaFX
	 * Its used to preload some of the GUI options (such as tables and dropdown options)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		preloadLotTypeComboBox();
		preloadLotSearchCategoryComboBox();
		preloadTable();
		preloadNotificationTable();
		preloadControllers();
		registerNotificationHandler();
		
		/*
		JavaSpacesUserService us = JavaSpacesUserService.getInstance();
		if(!us.getCurrentUser().username.equals("a")) {
			System.out.println("Adding notification for a");
			
			Notification not = new Notification("message for ya a", "a");
			
			JavaSpacesNotificationService ns = new JavaSpacesNotificationService();
			ns.addNotification(not);
		}
		*/
	}
	
	
	// View Lots Tab
	
	/**
	 * This function is fired when the search button is clicked, determine which values have been changed
	 * then pass it to the fillTable method to filter the table
	 */
	public void searchButtonClicked() {
		
		String lotName = null;
		Category lotCategory = null;
		
		if(searchLotName.getText().length() > 0){
			lotName = searchLotName.getText();
		}
		
		if(searchLotCategory.getSelectionModel().getSelectedItem() != null) {
			lotCategory = searchLotCategory.getSelectionModel().getSelectedItem();
		}
		
		fillTable(lotName, lotCategory);
	}
	
	/**
	 * This function is fired when the reset button is clicked. Unset the values on the search form and then
	 * fire the fillTable method with null values to reload everything
	 */
	public void searchResetButtonClicked() {
		searchLotName.setText("");
		searchLotCategory.getSelectionModel().select(-1);
		
		fillTable(null, null);
	}
	
	
	/**
	 * This function takes care of binding the table columns to specific attributes on the Lot class
	 * At the end of the function, it also fires the fillTable method to load the table
	 */
	@SuppressWarnings({ "deprecation" })
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
		    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");	 
		      
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
			lotTableSelectionChanged();
        });
		
		lotsTable.setRowFactory( tv -> {
		    TableRow<Lot> row = new TableRow<Lot>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		           this.lotTableSelectionChanged();
		        }
		    });
		    return row ;
		});
		
		this.searchButtonClicked();
	}
	
	public void lotTableSelectionChanged() {
		if (lotsTable.getSelectionModel().getSelectedItem() != null) {
        	// Load it from the space and display it
        	JavaSpacesLotService ls = new JavaSpacesLotService();
        	
        	Lot returnedLot = null;
        	try {
				returnedLot = (Lot) ls.searchForLot(lotsTable.getSelectionModel().getSelectedItem());
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
	}
	/**
	 * This function takes care of binding the table columns to specific attributes on the Lot class
	 * At the end of the function, it also fires the fillTable method to load the table
	 */
	@SuppressWarnings({ "deprecation" })
	public void preloadNotificationTable() {
		// Bind "id"
		tcNotificationID.setCellValueFactory(new Callback<CellDataFeatures<Notification, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Notification, String> c) {
		    		return new SimpleStringProperty(c.getValue().id + "");
		     	}
		    }
		);
		
		// Bind Date Time
		tcNotificationDateTime.setCellValueFactory(new Callback<CellDataFeatures<Notification, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Notification, String> c) {
		    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");	 
		      
		    		return new SimpleStringProperty(dateFormat.format(c.getValue().datetime));
		     	}
		    }
		);
		
		// Bind Message
		tcNotificationMessage.setCellValueFactory(new Callback<CellDataFeatures<Notification, String>, ObservableValue<String>>() {
		    @Override
		    public ObservableValue<String> call(
		    	CellDataFeatures<Notification, String> c) {
		    		return new SimpleStringProperty(c.getValue().message);
		     	}
		    }
		);
		
		// When its selected mark it as read
		/*
		notificationTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            //Check whether item is selected and set value of selected item to Label
            if (notificationTable.getSelectionModel().getSelectedItem() != null) {
            	if(!newValue.read) {
            		// Lets change it to read
            		
            		JavaSpacesNotificationService ns = new JavaSpacesNotificationService();
            		ns.markNofificationAsRead(newValue);
            	}
            }
        });
        */
		
		this.fillNotificationTable();
	}
	
	/**
	 * This method is fired when a lot is selected. First we must determine what TYPE the lot is, and then
	 * load the approapiate view for that Lot. Finally pass it the lot to load.
	 * @param t the lot to load
	 */
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
			viewLotPane.setCenter(viewGenericLotController.view);
			break;
		}
		
		if(viewGenericLotController == null) {
			System.out.println("viewgenericlotcontroller is null");
		} else {
			viewGenericLotController.loadLot(t);
		}
	}
	
	public void fillNotificationTable() {
		NotificationList notifications = new NotificationList();

		notifications = notifications.sortByNewest();
		
		final ObservableList<Notification> data = FXCollections.observableArrayList(
				notifications.all()
		);
		
		notificationTable.setItems(data);
	}

	/**
	 * Fills the tables with lots. Can also accept params to filter the table, this is how the search is implemented.
	 * @param lotName filter the table by this value on "title" of the lot
	 * @param category filter the table by this value on "category" of the lot
	 */
	public void fillTable(String lotName, Category category) {
		LotList lots = new LotList().active();
		
		if(lotName != null) {
			lots = lots.filterByLotName(lotName);
		}
		
		if(category != null) {
			lots = lots.filterByLotCategory(category);
		}
		
		final ObservableList<Lot> data = FXCollections.observableArrayList(
		    lots.all()
		);
		
		if(lotsTable != null) {
			lotsTable.setItems(data);
			
		} else { System.out.println("Table is null"); }
		
	}
	
	
	// Create Lot Tab
	/**
	 * This method is fired when the lot type dropdown is changed on the Create Lot Tab.
	 * It gets the Category selected and then fires the loadAddLotForm method.
	 */
	@FXML public void lotTypeChanged() {
		// Load the correct panel in
		Category selectedCategory = createLotLotType.getSelectionModel().getSelectedItem();
		
		this.loadAddLotForm(selectedCategory);
		
	}
	
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
		
		try {
			// VIEW Controllers
			
			fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(MainWindowController.class.getResource("ViewGenericLot.fxml"));
			fxmlLoader.load();
			this.viewGenericLotController = (ViewGenericLotController) fxmlLoader.getController();
			this.viewGenericLotController.parent = this;
			
			
			fxmlLoader = new FXMLLoader();
			
			// CREATE Controllers
			fxmlLoader.load(getClass().getResource("CreateGenericLot.fxml").openStream());
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
	
	/**
	 * Preloads Category optiosn into the Combo Box on the search Pane on the main window
	 */
	private void preloadLotSearchCategoryComboBox() {
		ObservableList<Category> options = 
			    FXCollections.observableArrayList(
			    		Category.values()
			    );
		searchLotCategory.setItems(options);
	}
	
	/*
	private void registerNotificationHandler() {
		JavaSpacesNotificationService ns = new JavaSpacesNotificationService();
		JavaSpacesUserService us = JavaSpacesUserService.getInstance();
	
		CallBack myCallBack = new CallBack() {
			@Override
			public void notified() {
				System.out.println("Triggered");
				MainWindowController.this.receivedNotification();
			}
		};
		
		// Make a quick template
		Notification template = new Notification();
		template.owner = us.getCurrentUser().username;
		
		System.out.println("Registering to receive notifications on:\n");
		System.out.println(template);
		System.out.println("\n\n");
		
		// Register the callback
		ns.createCallBack(template, myCallBack);
	}
	*/
	
	private void registerNotificationHandler() {
		JavaSpacesNotificationService ns = new JavaSpacesNotificationService();
		JavaSpacesUserService us = JavaSpacesUserService.getInstance();
		
		// Make a quick template
		Notification template = new Notification();
		template.owner = us.getCurrentUser().username;
		
		// Create an exporter
		Exporter myDefaultExporter = 
				new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						new BasicILFactory(), false, true);
				
		try {
			RemoteEventListener relE = (RemoteEventListener) myDefaultExporter.export(this);
			
			SpaceUtils.getSpace().notify(template, null, relE, Lease.FOREVER, null);
			
			System.out.println("Finished registering");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void notify(RemoteEvent arg0) throws UnknownEventException, RemoteException {
		System.out.println("Triggered");
		
		// Get the newest notification
		NotificationList nl = new NotificationList();
		Notification notification = nl.sortByNewest().one();
		
		this.fillNotificationTable();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Notifications.create()
					.owner(tabPane)
			        .title("Received notification")
			        .text(notification.toString())
			        .show();
			}
		});
		
		
		
	}
}
