/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

/**
 * FXML Controller class
 *
 * @author Julien Fares, Ric Frost, Jacob McCloud
 */
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class EnterServiceController implements Initializable {

    @FXML private TableView<Vehicle> vehicleTableView;
    @FXML private TableColumn<Vehicle, Integer> idCol;
    @FXML private TableColumn<Vehicle, String>  regionCol;
    @FXML private TableColumn<Vehicle, Integer> yearCol;
    @FXML private TableColumn<Vehicle, String>  makeCol;
    @FXML private TableColumn<Vehicle, String>  modelCol;
    @FXML private TableColumn<Vehicle, String>  VINCol;
    @FXML private TableColumn<Vehicle, Integer> curMilesCol;
    
    @FXML private DatePicker        serviceDatePicker;
    @FXML private TextField         mileageTextField;
    @FXML private TextField         descriptionTextField;
    @FXML private TextField         costTextField;
    @FXML private ChoiceBox<String> regionChoiceBox;
    
 /*   @FXML private MenuItem          menuLogout;
    @FXML private MenuItem          menuFileCloseClick;
    @FXML private MenuItem          menuUserMaint;
    @FXML private Menu              menuVehicleClick;
    @FXML private Menu              menuEnterServiceClicked;
    @FXML private MenuItem          menuHelpAboutClick;
*/
    

    
/********************************************
  * AddServceButtonPressed
  * 
  *  Called when the AddSevice button is pressed.  
  *  - It reads the field values and inserts a service record fro the selected vehicle.  
  *  - It also updates the current mileage if the service mileage is more stored mileage.
  * 
  ********************************************/
 @FXML void addServiceButtonPressed(ActionEvent event) {   
    System.out.println("In AddServiceButtonPressed");

    // Get the vehicle object for the selected row
    ObservableList<Vehicle>    selectedRow;
    selectedRow = vehicleTableView.getSelectionModel().getSelectedItems();
    if (selectedRow.isEmpty()) {
        // No row was selected
        Util.ErrorBox("No Vehicle Selected", "Please select a vehicle for Service entry.", "Click on row to select"); 
        return;
    }
    Vehicle v = selectedRow.get(0);
    
    
    // Validate the data for Service
    String ErrorMsg = "These Fields are Invalid:";
    System.out.println("String Length: " + ErrorMsg.length());

    
    String description = descriptionTextField.getText().trim();
    if (description.length() < 2) {
        ErrorMsg += " Description,";
    }

    int curMiles = 0;
    try {
        curMiles = Integer.parseInt(mileageTextField.getText().trim());
    } catch (Exception e) {
        ErrorMsg += " Current Miles,";
    }

    Float cost = 0f;
    try {
        cost = Float.parseFloat(costTextField.getText().trim());
    } catch (Exception e) {
        ErrorMsg += " Cost,";
    }

    LocalDate serviceDate = serviceDatePicker.getValue();
    if (serviceDate == null) {
        ErrorMsg += " Service Date,";
    }
        
    /* Display error and return if all entries are not valid */
    int msgLength = ErrorMsg.length();
    if (msgLength > 26) {
        // Change the final comma to a period
        String newMsg = ErrorMsg.substring(0, msgLength-1) + ".";
        Util.ErrorBox("Unable to record Service", "Incorrect or missing service information.", newMsg);
        return;
    }
    
   // All information is valid create a new Service object and insert it
    ServiceRec s = new ServiceRec (v.id, serviceDate, curMiles, description, cost);        
    int rc = s.InsertIntoDB();

    // Error if the insert Failed
    if (rc != 0) {
        System.out.println("ERROR -- Vehicle Insert failed, code:" + rc);
    }

    // Update the vehicle current mileage if necessary
    Boolean refreshTableviewNeeded = false;
    if (curMiles > v.getCurMiles()) {
        rc = v.updateCurMileageInDB (curMiles);
        if (rc != 0) {
            System.out.println("Update mileage failed: " + rc);
        }
        refreshTableviewNeeded = true;
    }

    // clear the entry fields on the screen
    
    // Reload the tableview to get the updated mileage by simulating region selection
    if (refreshTableviewNeeded) {
        ActionEvent dummyEvent = null;
        regionServiceButtonPressed(dummyEvent);
    }
}
 
 
/********************************************
  * regionServiceButtonPressed
  * 
  *  Called when the AddSevice button is pressed.  
  *  - It filters the tableview to the region in the drop down list
  * 
  ********************************************/ 
@FXML void regionServiceButtonPressed(ActionEvent event) {   

    String regionName;
    try {
        regionName = regionChoiceBox.getValue().toString().trim();
    } catch (Exception e) {
        regionName = "ALL";
    }
    
    int regionId;
    // Get the region ID for the region name
    if (regionName.equalsIgnoreCase("ALL")) {
        regionId = 0;
    } else {
        regionId = Region.getRegionNumber(regionName);
    }
    
    // Clear and relod the vehicle tableview
    vehicleTableView.getItems().clear();
    vehicleTableView.setItems(loadVehiclesInRegion(regionId));
 }
 
 /*************************************** 
     * loadVehiclesInRegion(int regionID)
     * 
     * This method loads the vehicles from the database for the region
     * It loads vehicles for all regions if the region id is 0.
     ***************************************/
public ObservableList<Vehicle>  loadVehiclesInRegion(int regionId) {
    // Create the ObservableList that is returned
    ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
        
    // Add users to the list from the database
    Vehicle v = new Vehicle();
    int rc = v.getFirstVehicleInRegion(regionId);
    while (rc == 0) {
        // Add user to userList;
        vehicleList.add(v);
        v = new Vehicle();
        rc = v.getNextVehicle();
    }
    return vehicleList;
}
    
        
/**
 * Initializes the controller class.
 */
@Override  public void initialize(URL url, ResourceBundle rb) {
        
    // Load the regions into the choicebox
    regionChoiceBox.getItems().add("All");
    String regionName = Region.getFirstRegionName();
    int i = 1;
    while (regionName.equals("") ==  false) {
        regionChoiceBox.getItems().add(regionName);
        regionName = Region.getNextRegionName(i);
        i++;   
    }

               
        
    // Setup the vehicle TableView control and columns
    idCol.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("id"));
    regionCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("regionSSP"));
    makeCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("makeSSP"));
    modelCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("modelSSP"));
    yearCol.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("year"));
    VINCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("vinSSP"));
    curMilesCol.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("curMiles"));
        
    // Empty and load the vehicles into the tableview
    vehicleTableView.getItems().clear();
    vehicleTableView.setItems(loadVehiclesInRegion(0));
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
@FXML void menuServiceHistoryClicked(ActionEvent event) throws IOException {
     GlobalVar.appMenu.serviceHistory(event); 
}
@FXML void menuEnterServiceClicked (ActionEvent event) throws IOException {
     GlobalVar.appMenu.enterService(event); 
}  
@FXML void menuUserClicked(ActionEvent event)  throws IOException {
    GlobalVar.appMenu.user(event);
}
@FXML void menuAboutClicked(ActionEvent event) throws IOException {
    GlobalVar.appMenu.about(event);
}
 @FXML void menuLeaseExpirationClicked(ActionEvent event) throws IOException {
    GlobalVar.appMenu.leaseExpiration(event);
}
 
}  // End of class