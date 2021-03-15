package VeMan;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ServiceHistoryController implements Initializable {
    @FXML private TableView<Vehicle> vehicleTableView;
    @FXML private TableColumn<Vehicle, Integer> idCol;
    @FXML private TableColumn<Vehicle, String>  regionCol;
    @FXML private TableColumn<Vehicle, Integer> yearCol;
    @FXML private TableColumn<Vehicle, String>  makeCol;
    @FXML private TableColumn<Vehicle, String>  modelCol;
    @FXML private TableColumn<Vehicle, String>  VINCol;
    @FXML private TableColumn<Vehicle, Integer> curMilesCol;

    @FXML private ChoiceBox<String> regionChoiceBox;
    
    @FXML private TableView<Vehicle> reportTableView;
    @FXML private TableColumn<Vehicle, LocalDate> dateCol;
    @FXML private TableColumn<Vehicle, String> serviceCol;
    @FXML private TableColumn<Vehicle, Float> costCol;
    @FXML private TableColumn<Vehicle, Integer> millageCol;

 /*   @FXML private MenuItem          menuLogout;
    @FXML private MenuItem          menuFileCloseClick;
    @FXML private MenuItem          menuUserMaint;
    @FXML private Menu              menuVehicleClick;
    @FXML private Menu              menuEnterServiceClicked;
    @FXML private MenuItem          menuHelpAboutClick;
*/
    @FXML void regionReportButtonPressed(ActionEvent event) {   

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
    
    @FXML void reportButtonPressed(ActionEvent event) {

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
@Override public void initialize(URL url, ResourceBundle rb) {
        
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
}
