/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author Rich Frost, Jacob McCloud, Julien Fares
 */
public class LoginSceneController implements Initializable {

    // Variables are used to add a new user
    @FXML private TextField     loginNameTextField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button        loginButton;
    
    /**
     * This is called when the login button is pressed
    */
    public void loginButtonPressed(ActionEvent event) throws IOException {
        String name = loginNameTextField.getText().trim();
        String password = loginPasswordField.getText().trim();
        
        // Validate that all fields are present
        if (name.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Empty Fields");
            alert.setContentText("All fields required to login");
            alert.showAndWait();
            return;
        }
        
        // Instantsiate new User object - we don't have a UserID or RegionID yet
        User u = new User (0, name, 0, password); 
        
        // Validate the User information
        int rc = u.validate();
        if (rc != 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Incorrect Credentials");
            alert.setContentText("The Username and Password combination is incorrect");
            alert.showAndWait();
            return;
        }
        
        // Login was sucessful. Set the glabal user and region ID
        GlobalVar.curUserID = u.getUserId();
        GlobalVar.curUserRegionID = u.getRegionId();
        
        // Switch to vehicle screen after the login
                
        // Switch display to User scene
        Parent loginSceneParent = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene  loginScene = new Scene (loginSceneParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene (loginScene);
        window.show();
  
    }
              
 
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
