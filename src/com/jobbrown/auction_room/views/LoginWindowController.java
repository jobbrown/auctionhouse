package com.jobbrown.auction_room.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.controlsfx.dialog.Dialogs;

import com.jobbrown.auction_room.exceptions.IncorrectLoginException;
import com.jobbrown.auction_room.helpers.JavaSpacesUserService;
import com.jobbrown.auction_room.interfaces.helpers.UserService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class LoginWindowController {
	@FXML public HBox view;
	
    @FXML public TextField loginUsername;
    @FXML public PasswordField loginPassword;
    @FXML public Button loginButton;
    
    @FXML public TextField registerUsername;
    @FXML public PasswordField registerPassword;
    @FXML public TextField registerEmail;
    @FXML public Button registerButton;


    public LoginWindowController() {
    }

    /**
     * This handles registering someone for the application.  After checking fields are unique it 
     * creates the login and informs the user they can login.
     * @return boolean success
     */
    @FXML public boolean registerButtonClicked() {
    	if(isRegistrationValid()) {
    		// Lets check if the username or e-mail are taken
    		UserService us = JavaSpacesUserService.getInstance();
    		
    		if(us.isUsernameUnique(registerUsername.getText())) {
    			if(us.isEmailUnique(registerEmail.getText())) {
    				// Both username and email aren't taken
    				try {
	    				if(us.createUser(registerUsername.getText(), registerPassword.getText(), registerEmail.getText())) {
	    					
	    					Dialogs.create()
	        				.owner(view)
	        				.masthead("Success")
	        				.message("That login has been created. Please log in.")
	        				.showInformation();
	    					
	    					return true;
	    				}
    				} catch (Exception e) {}
    				
    				Dialogs.create()
    				.owner(view)
    				.masthead("Error logging in")
    				.message("Registration failed. Unknown error.")
    				.showError();
    				
    				return false;
    			} else {
    				// Email is taken
    				Dialogs.create()
    				.owner(view)
    				.masthead("Error logging in")
    				.message("That e-mail is already taken.")
    				.showError();
    				
        			return false;
    			}
    		} else {
    			// Username is taken
    			Dialogs.create()
				.owner(view)
				.masthead("Error logging in")
				.message("That username is already taken.")
				.showError();
    			
        		return false;
    		}   		
    	} else {
    		Dialogs.create()
			.owner(view)
			.masthead("Registration Validation Errors")
			.message(this.buildErrorString(this.getRegististrationValidationErrors()))
			.showError();
    	}
    	
    	return false;
    }
    
    /**
     * This is fired when the login button is clicked. It validates the login and takes care of
     * opening the main window view.
     * @param event
     */
    @FXML public void loginButtonClicked(ActionEvent event) {
        if(validateLoginForm()) {
            // Check if the login is value
            UserService us = JavaSpacesUserService.getInstance();
            try {
                if(us.login(loginUsername.getText(), loginPassword.getText())) {
                    // The login was correct
                    // Switch the scene
                	Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    Scene scene = stage.getScene();
                    
                    
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
                    Parent root = null;
					try {
						root = (Parent) fxmlLoader.load();
					} catch (IOException e) {
						e.printStackTrace();
					}
                    scene.setRoot(root);
                    stage.sizeToScene();
                }
            } catch (IncorrectLoginException e) {
            	Dialogs.create()
    			.owner(view)
    			.masthead("Validation Error")
    			.message(e.getMessage())
    			.showError();
            }
        }
    }
    
    private boolean isRegistrationValid() {
    	return getRegististrationValidationErrors().size() == 0;
    }
    
    private ArrayList<String> getRegististrationValidationErrors() {
    	ArrayList<String> errors = new ArrayList<String>();
    	
    	if(registerUsername.getText().length() < 3) {
    		errors.add("Username should be atleast 3 characters long");
    	}
    	
    	if(registerPassword.getText().length() < 4) {
    		errors.add("Password should be atleast 4 characters long");
    	}
    	
    	// Email Validation
    	Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
    			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    	
    	Matcher matcher = pattern.matcher(registerEmail.getText());
    	
    	if(!matcher.matches()) {
    		errors.add("Please enter a valid email address");
    	}
    	
    	return errors;
    }
    
    /**
     * This validates the registration form (ensuring fields are set) 
     * @return boolean valid or not
     */
    private boolean validateRegistrationForm() {
    	if(registerUsername.getText().length() == 0 || registerPassword.getText().length() == 0 || registerEmail.getText().length() == 0) {
    		Dialogs.create()
			.owner(view)
			.masthead("Validation Error")
			.message("Please complete the full form before attempting to register an account.")
			.showError();
    		
    		return false;
    	}
    	
    	return true;
    }

    /**
     * This validates the login form (ensuring both fields are set)
     * @return boolean valid or not
     */
    private boolean validateLoginForm() {
        // Check for a blank username or password
        if(loginUsername.getText().length() == 0 || loginPassword.getText().length() == 0) {
        	Dialogs.create()
			.owner(view)
			.masthead("Validation Error")
			.message("Please enter both a username and password before logging in")
			.showError();

            return false;
        }

        return true;
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