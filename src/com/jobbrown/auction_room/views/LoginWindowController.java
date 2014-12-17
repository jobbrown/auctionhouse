package com.jobbrown.auction_room.views;

import java.io.IOException;

import com.jobbrown.auction_room.exceptions.IncorrectLoginException;
import com.jobbrown.auction_room.helpers.JavaSpacesUserService;
import com.jobbrown.auction_room.interfaces.helpers.UserService;
import com.jobbrown.auction_room.thirdparty.gallen.SpaceUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.jini.space.JavaSpace;

public class LoginWindowController {
    @FXML public TextField loginUsername;
    @FXML public PasswordField loginPassword;
    @FXML public Label loginMessage;
    @FXML public Button loginButton;
    
    @FXML public TextField registerUsername;
    @FXML public PasswordField registerPassword;
    @FXML public TextField registerEmail;
    @FXML public Button registerButton;


    public LoginWindowController() {
    }

    @FXML public boolean registerButtonClicked() {
    	if(validateRegistrationForm()) {
    		// Lets check if the username or e-mail are taken
    		UserService us = JavaSpacesUserService.getInstance();
    		
    		if(us.isUsernameUnique(registerUsername.getText())) {
    			if(us.isEmailUnique(registerEmail.getText())) {
    				// Both username and email aren't taken
    				try {
	    				if(us.createUser(registerUsername.getText(), registerPassword.getText(), registerEmail.getText())) {
	    					loginMessage.setText("That login has been created");
	    					return true;
	    				}
    				} catch (Exception e) {}
    				
    				loginMessage.setText("Register failed");
    				return false;
    			} else {
    				// Email is taken
        			loginMessage.setText("That e-mail is taken");
        			return false;
    			}
    		} else {
    			// Username is taken
        		loginMessage.setText("That username is taken");
        		return false;
    		}   		
    	}
    	
    	return false;
    }
    
    @FXML public void loginButtonClicked(ActionEvent event) {
        if(validateLoginForm()) {
            // Check if the login is value
            UserService us = JavaSpacesUserService.getInstance();
            try {
                if(us.login(loginUsername.getText(), loginPassword.getText())) {
                    // The login was correct
                    // Switch the scene
                	loginMessage.setText("Login successful...");
                	
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
                loginMessage.setText(e.getMessage());
            }
        }
    }
    
    private boolean validateRegistrationForm() {
    	if(registerUsername.getText().length() == 0 || registerPassword.getText().length() == 0 || registerEmail.getText().length() == 0) {
    		loginMessage.setText("Please fill in the whole form");
    		
    		return false;
    	}
    	
    	return true;
    }

    private boolean validateLoginForm() {
        // Check for a blank username or password
        if(loginUsername.getText().length() == 0 || loginPassword.getText().length() == 0) {
            loginMessage.setText("Please enter both username and password");

            return false;
        }

        return true;
    }

}