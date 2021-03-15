/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 * FXML Controller class
 *
 * @author Rich Frost, Jacob McCloud, Julien Fares
 */
public class VehicleInvController implements Initializable {

    // Identifiers to load Vehicle tableView and the Table Columns
    @FXML private TableView<Vehicle>            vehicleTableView;
    @FXML private TableColumn<Vehicle, Integer> idCol;
    @FXML private TableColumn<Vehicle, String>  regionCol;
    @FXML private TableColumn<Vehicle, Integer> yearCol;
    @FXML private TableColumn<Vehicle, String>  makeCol;
    @FXML private TableColumn<Vehicle, String>  modelCol;
    @FXML private TableColumn<Vehicle, String>  VINCol;
    @FXML private TableColumn<Vehicle, String>  leaseEndStrCol;
    @FXML private TableColumn<Vehicle, String>  paymentStrCol;
    @FXML private TableColumn<Vehicle, Integer> initMilesCol;
    @FXML private TableColumn<Vehicle, Integer> curMilesCol;
    
    // Identifiers for controls fxml form for adding a new vehicle 
    @FXML private TextField     makeTextField;
    @FXML private TextField     modelTextField;
    @FXML private TextField     yearTextField;
    @FXML private TextField     vinTextField;
    @FXML private TextField     mileageTextField;
    @FXML private TextField     paymentTextField;   
    @FXML private DatePicker    leaseEndDatePicker;
    @FXML private ChoiceBox     regionChoiceBox;
    
    // Identifiers for controls for updating mileage 
    @FXML private TextField     updatedMileageTextField;
 
    // Identifiers for controls for deleting vehicle 
    @FXML private Button        deleteSelectedVehicleButton;
    
    
/********************************************
  * AddVehicleButtonPressed
  * 
  *  Insert vehicle into the database table and updates the Vehicle ID
  *    
  ********************************************/
@FXML public void AddVehicleButtonPressed() {
    System.out.println("In AddVehicleButtonPressed");

    // Validate the data for vehicle
    String ErrorMsg = "These Fields are Invalid:";
    System.out.println("String Length: " + ErrorMsg.length());

    String make = makeTextField.getText().trim();
    if (make.length() < 2) {
        ErrorMsg += " Make,";
    }

    String model = modelTextField.getText().trim();
    if (model.length() < 2) {
        ErrorMsg += " Model,";
    }

    int year = 0;
    try {
        year = Integer.parseInt(yearTextField.getText().trim());
        if ((year<1950) || (year>2050)) {
            ErrorMsg += " Year,";
        }          
    } catch (Exception e) {
        ErrorMsg += " Year,";
    }

    String vin = vinTextField.getText().trim();
    if (vin.length() < 2) {
        ErrorMsg += " VIN,";
    }

    int initMiles = 0;
    try {
        initMiles = Integer.parseInt(mileageTextField.getText().trim());
    } catch (Exception e) {
        ErrorMsg += " Initial Miles,";
    }
    
    // Set current miles to 0
    int curMiles = 0;

    Float payment = 0f;
    try {
        payment = Float.parseFloat(paymentTextField.getText().trim());
    } catch (Exception e) {
        ErrorMsg += " Payment,";
    }

    LocalDate leaseEnd = leaseEndDatePicker.getValue();
    if (leaseEnd == null) {
        ErrorMsg += " Lease Date,";
    }

    String regionName;
    try {
        regionName = regionChoiceBox.getValue().toString().trim();
    } catch (Exception e) {
        regionName = "";
    }
    // Get the region ID for the region name
    int regionId = Region.getRegionNumber(regionName);
    if (regionId == 0) {
        ErrorMsg += " Region,";
    }

    /* Display error and return if all entries are not valid */
    int msgLength = ErrorMsg.length();
    if (msgLength > 26) {
        // Change the final comma to a period
        String newMsg = ErrorMsg.substring(0, msgLength-1) + ".";
        Util.ErrorBox("Unable to Add Vehicle", "Incorrect or missing values.", newMsg);
        return;
    }

    // All information is valid create a new vehicle object and insert it
    Vehicle v = new Vehicle (make, model, year, vin,
            regionId, leaseEnd, payment, initMiles, curMiles);        
    int rc = v.InsertIntoDB();

    // Error if the insert Failed
    if (rc != 0) {
        System.out.println("ERROR -- Vehicle Insert failed, code:" + rc);
    }
            
    // Reset the text fields on the screen
    makeTextField.clear();
    modelTextField.clear();
    yearTextField.clear();
    vinTextField.clear();
    mileageTextField.clear();
    paymentTextField.clear();   
    leaseEndDatePicker.setValue(null); 
    regionChoiceBox.setValue("");
     
    // Add the Vehicle to the tableview
    vehicleTableView.getItems().add(v);
}
    
    /***************************************
     *  DeleteVehicleButtonPressed()
     * 
     * This method is called when the Delete Vehicle button is pushed.
    ****************************************/
    @FXML public void DeleteVehicleButtonPressed() {
       
        ObservableList<Vehicle>    selectedRow;
        
        // Get the selected row and delete it
        selectedRow = vehicleTableView.getSelectionModel().getSelectedItems();
        for (Vehicle v: selectedRow) {
            
            if (Util.ConfirmBox("Vehicle Delete", "Confrim Deletion of Vehicle.",
                    "Confirm deletion of Viehicle ID: " + 
                    v.getId() +", "+ v.getMake() +", "+ v.getModel()) == false) {
                return;
            }
            
            /************************************************
             *  Delete all service records, then Delete the Vehicle Record
             **************************************************/
            IntRef serviceDeleteCount = new IntRef(0);
            int rc = v.deleteVehicleAndServiceFromDB(serviceDeleteCount);
            if (rc == 0) {
                Util.InformationBox("Vehicle Deleted",
                        "Vehicle # " + v.id + ", " + v.make + " " +v.model + " was deleted.",
                        serviceDeleteCount + " service record(s) for the vehicle were deleted.");
            } else {
                Util.ErrorBox("Unable to Delete Vehicle",
                        "The system was unable to delete the vehicle.",
                        "Vehicle # " + v.id + " " + v.make + " " + v.model);
            }
            
            // Empty and reload the vehicle into the tableview
            vehicleTableView.getItems().clear();
            vehicleTableView.setItems(loadVehicles ());
        }
    }
    
    /***************************************
     *  UpdateMileageButtonPressed()
     * 
     * This method is called to update the current mileage of a vehicle
    ****************************************/
@FXML  public void updateMileageButtonPressed(ActionEvent event) {
    // Get the selected row
    ObservableList<Vehicle>    selectedRow;
    selectedRow = vehicleTableView.getSelectionModel().getSelectedItems();
    if (selectedRow.isEmpty()) {
        // No row was selected
        return;
    }
        
    // Check if the mileage field is empty or invalid
    String ErrorMsg = "";
    int newMileage = 0;
    try {
        String mileageStr = updatedMileageTextField.getText().trim();
        newMileage = Integer.parseInt(mileageStr);
        if ((newMileage<1) || (newMileage>300000)) {
            ErrorMsg += "Mileage must be between 1 and 300,000";
        }          
    } catch (Exception e) {
        ErrorMsg += "Invalid Mileage entered";
    }
        
    /* Display error and return if all entries are not valid */
    int msgLength = ErrorMsg.length();
    if (msgLength > 0) {
        // Change the final comma to a period
        //String newMsg = ErrorMsg.substring(0, msgLength-1) + ".";
        Util.ErrorBox("Unable to Update Mileage", "Mileage entry is invaild.", ErrorMsg);
        return;
    }
        
    // Get the Vehicle object and update mileage in the database
    Vehicle v = selectedRow.get(0);
    int rc = v.updateCurMileageInDB (newMileage);
    if (rc != 0) {
        System.out.println("Update mileage failed: " + rc);
    }

    // Empty and reload the vehicles into the tableview
    vehicleTableView.getItems().clear();
    vehicleTableView.setItems(loadVehicles ());
        
    // Clear the Mileage field
    updatedMileageTextField.clear();
}    

    
    
/*************************************** 
* loadVehicles()
* 
* This method loads the list of vehicles from the database
***************************************/
@FXML public ObservableList<Vehicle>  loadVehicles() {
    // Create the ObservableList that is returned
    ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
        
    // Add vehicless to the list from the database
    Vehicle v = new Vehicle();
    int rc = v.getFirstVehicle();
    while (rc == 0) {
        // Add vehicle to vehicleList;
        vehicleList.add(v);
        v = new Vehicle();
        rc = v.getNextVehicle();
    }
    return vehicleList;
}
 
    
/**
 * Initializes the controller class.
 */
@Override public void initialize(URL url, ResourceBundle rb) {
    
    // Load the regions into the choicebox
    String regionName = Region.getFirstRegionName();
    int i = 1;
    while (regionName.equals("") ==  false) {
        regionChoiceBox.getItems().add(regionName);
        regionName = Region.getNextRegionName(i);
        i++;
    }
        
    // Setup the vehicle TableView control and columns
    System.out.println("In Vehicle Scene Initialize.");
    idCol.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("id"));
    regionCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("regionSSP"));
    makeCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("makeSSP"));
    modelCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("modelSSP"));
    yearCol.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("year"));
    VINCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("vinSSP"));
    leaseEndStrCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("leaseEndSSP"));
    paymentStrCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("paymentSSP"));
    initMilesCol.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("initMiles")); 
    curMilesCol.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("curMiles"));
         
    // Empty and load the vehicles into the tableview
    vehicleTableView.getItems().clear();
    vehicleTableView.setItems(loadVehicles());
     
    // Make controls visible for corporate users, hide from others
    if (GlobalVar.curUserRegionID == 1) {
        deleteSelectedVehicleButton.setVisible(true);
    } else {    
        deleteSelectedVehicleButton.setVisible(false);
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
 
}  // End of Class
