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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author jay2j
 */
public class LeaseExpirationController implements Initializable {
     @FXML private ChoiceBox     regionChoiceBox;
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

    /**
     * Initializes the controller class.
     */
        /*************************************** 
     * loadVehicles()
     * 
     * This method loads the list of users from the database
     ***************************************/
    public ObservableList<Vehicle>  loadVehicles() {
        // Create the ObservableList that is returned
        ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
        
        // Add users to the list from the database
        Vehicle v = new Vehicle();
        int rc = v.getFirstVehicle();
        while (rc == 0) {
            // Add user to userList;
            vehicleList.add(v);
            v = new Vehicle();
            rc = v.getNextVehicle();
        }
        return vehicleList;
        
        
    }
    
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
    @Override
    public void initialize(URL url, ResourceBundle rb) {
           // Load the regions into the choicebox
            regionChoiceBox.getItems().add("All");
        String regionName = Region.getFirstRegionName();
        while (regionName.equals("") ==  false) {
            regionChoiceBox.getItems().add(regionName);
            regionName = Region.getNextRegionName();
        }
        
        
     
        // TODO
    }    
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
    
    @FXML void menuLeaseExpirationClicked(ActionEvent event)  throws IOException {
        GlobalVar.appMenu.leaseExpiration(event);
    }
     @FXML void showVehiclesButtonPressed (ActionEvent event) throws IOException {
   
        // Setup the vehicle TableView control and columns
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
    }
}
