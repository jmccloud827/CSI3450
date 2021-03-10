/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Rich Frost, Jacob McCloud, Julien Fares
 */
public class FXMLDocumentController implements Initializable { 
    
    // Identifiers to load User tableView and the Table Columns
    @FXML private TableView<User>           userTableView;      
    @FXML private TableColumn<User, String> userNameCol;
    @FXML private TableColumn<User, String> userRegionCol;
    @FXML private TableColumn<User, String> userPasswordCol;
    
    // Identifiers to reference controls fxml form
    @FXML private TextField     newUserNameTextField;
    @FXML private TextField     newUserPasswordTextField;
    @FXML private ChoiceBox     regionChoiceBox;
    @FXML private VBox          resetPasswordBox;
    @FXML private Button        deleteUserButton;
    @FXML private AnchorPane    addUserBox;
    
    // Identifiers used to update a user's password
    @FXML private TextField     updateUserPasswordTextField;

    
    
    
    /***************************************
     * UpdateUserPasswordButtonPressed()
     * 
     * This method is called to update the User Password.
    ****************************************/
    public void UpdateUserPasswordButtonPressed() {
        // Get the selected row
        ObservableList<User>    selectedRow;
        selectedRow = userTableView.getSelectionModel().getSelectedItems();
        if (selectedRow.isEmpty()) {
            // No row was selected
            return;
        }
        
        // Check if the password field is empty
        String password = updateUserPasswordTextField.getText().trim();
        if (password.isEmpty()) {
            // No password supplied
            return;
        }
        
        // Get the User object and update the password in the database
        User user = selectedRow.get(0);
        int rc = user.updatePasswordInDB (password);
        if (rc != 1) {
            System.out.println("Update password failed: " + rc);
        }

        // Empty and reload the users into the tableview
        userTableView.getItems().clear();
        userTableView.setItems(LoadUsers ());
        
        // Clear the password field
        updateUserPasswordTextField.clear();
    }    
    
    /***************************************
     * DeleteUserButtonPressed()
     * 
     * This method is called when the Delete User button is pushed.
     **************************************/
    public void DeleteUserButtonPressed() {
       
        ObservableList<User>    selectedRow;
        
        // Get the selected row and delete it
        selectedRow = userTableView.getSelectionModel().getSelectedItems();
        for (User user: selectedRow) {
            if (Util.ConfirmBox("User Delete", "Confrim User Delete",
                    "Are you sure you want to delete: " + user.getUserName()) == false) {
                return;
            }
            
            // Delete the user from the database
            System.out.println("In Delete Row: " + user.toString());
            user.deleteFromDB();
            
            // Empty and reload the users into the tableview
            userTableView.getItems().clear();
            userTableView.setItems(LoadUsers ());
        }
    }
    
    /*
    * This method is called when the add User button is pushed.
    */
    public void AddNewUserButtonPressed() {
 
        System.out.println("In AddNewUserButtonPressed");
        String name = newUserNameTextField.getText().trim();
        String password = newUserPasswordTextField.getText().trim();
        String regionName;
        try {
            regionName = regionChoiceBox.getValue().toString().trim();
        } catch (Exception e) {
            regionName = "";
        }
        
        // Validate that all fields are present
        if (name.isEmpty() || regionName.isEmpty() || password.isEmpty()) {
            System.out.println("ERROR -- All fields are required to add a user.");
            return;
        }
        
        // Get the region ID for the region name
        int regionId = Region.getRegionNumber(regionName);
        if (regionId == 0) {
            Util.ErrorBox ("Region Error", "Region name is not valid: " + regionName,
                "Please select Region");
            return;
        }
        
        // Instantsiate new User object - we don't have a UserID yet
        User u = new User (0, name, regionId, password); 
        
        // Insert the User information into the database
        int newId = u.InsertIntoDB();

        // Error if the insert Failed
        if (newId == 0) {
            System.out.println("ERROR -- User Insert failed!");
        }
              
        // Clear the text fields
        newUserNameTextField.clear();
        newUserPasswordTextField.clear();
        regionChoiceBox.setValue("");
        
        // Add the User to the tableview
        userTableView.getItems().add(u);
    }
    
    /* 
    * This method loads the list of users from the database
    */
    public ObservableList<User>  LoadUsers() {
        // Create the ObservableList that is returned
        ObservableList<User> userList = FXCollections.observableArrayList();
        
        // Add users to the list from the database
        User u = new User();
        int rc = u.getFirstUser();
        while (rc == 0) {
            // Add user to userList;
            userList.add(u);
            u = new User();
            rc = u.getNextUser();
        }
        return userList;
    }
 
    /*
    *   Called when the form is iniitalized
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //
        // Setup the User TableView control
        //
        // Setup columns to load fields from User class 
        userNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
        userRegionCol.setCellValueFactory(new PropertyValueFactory<User, String>("regionName"));
        userPasswordCol.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        
        // Load the regions into the choicebox
        String regionName = Region.getFirstRegionName();
        int i = 1;
        while (regionName.equals("1") ==  false) {
            regionChoiceBox.getItems().add(regionName);
            regionName = Region.getNextRegionName(i);
            i++;
        }
        
        // Empty and load the users into the tableview
        userTableView.getItems().clear();
        userTableView.setItems(LoadUsers());
        
        // Clear the text fields
        newUserNameTextField.clear();
        newUserPasswordTextField.clear();
        regionChoiceBox.setValue("");
        
        // Make controls visible for corporate users, hide from others
        if (GlobalVar.curUserRegionID == 1) {
            userPasswordCol.setVisible(true);
            resetPasswordBox.setVisible(true);
            deleteUserButton.setVisible(true);
            addUserBox.setVisible(true);
        } else {    
            userPasswordCol.setVisible(false);
            resetPasswordBox.setVisible(false);
            deleteUserButton.setVisible(false);
            addUserBox.setVisible(false);
        }
    }
    
    
  /****************************************** 
  *   Methods to handle menu bar at top of screen 
  ******************************************/
    @FXML void menuLogoutClicked (ActionEvent event) throws IOException {
        GlobalVar.appMenu.logout(event); 
    }
    @FXML void menuCloseClicked (ActionEvent event) throws IOException {
        GlobalVar.appMenu.close(event); 
    }
    @FXML void menuVehicleInvClicked (ActionEvent event) throws IOException {
        GlobalVar.appMenu.vehicleInv(event); 
    }
    @FXML void menuVehicleServiceHistoryClicked(ActionEvent event) throws IOException {
        GlobalVar.appMenu.serviceHistory(event); 
    }
    @FXML void menuEnterServiceClicked (ActionEvent event) throws IOException {
        GlobalVar.appMenu.enterService(event); 
    }  
    @FXML void menuUserClicked(ActionEvent event)  throws IOException {
        GlobalVar.appMenu.user(event);
    }
   
}
