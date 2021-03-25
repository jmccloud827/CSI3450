/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

import static VeMan.DBase.dbConn;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
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
    @FXML private DatePicker leaseEndDatePicker;

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
        int i = 1;
        while (regionName.equals("") ==  false) {
            regionChoiceBox.getItems().add(regionName);
            regionName = Region.getNextRegionName(i);
            i++;   
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
        LocalDate localDate = leaseEndDatePicker.getValue();
        Date date = Date.valueOf(localDate);
        String dateString = date.toString();
        dateString = dateString.replace("-", "");
        ObservableList<Vehicle> reportList = FXCollections.observableArrayList();
        String regionName = regionChoiceBox.getValue().toString().trim();
        try {
            // Build Java SQL query statement 
            String sql = "SELECT * FROM VEHICLE WHERE VEH_LEASE_END <= ?"; 
            PreparedStatement ps = dbConn.prepareStatement(sql);
            ps.setString(1, dateString);
            ResultSet sqlResult;
            
            // Send statement to mySQl to execute.
            sqlResult = ps.executeQuery();
            System.out.println("executeQuery complete." + sqlResult);
            
            Vehicle v;
            int totalCost = 0;
            if (sqlResult.next() == false) {
                System.out.println("Result is Empty");
            } else {
                do {
                    Date dateTemp = sqlResult.getDate("VEH_LEASE_END");
                    LocalDate temp = dateTemp.toLocalDate();
                    v = new Vehicle(sqlResult.getString("VEH_MAKE"), sqlResult.getString("VEH_MODEL"), sqlResult.getInt("VEH_YEAR"), sqlResult.getString("VEH_VIN"), sqlResult.getInt("REGION_ID"), temp, sqlResult.getFloat("VEH_PAYMENT"), sqlResult.getInt("VEH_INITIAL_MILEAGE"), sqlResult.getInt("VEH_CUR_MILEAGE"));
                    v.id = sqlResult.getInt("VEH_ID");
                    if (regionName.equals("All")) {
                    reportList.add(v);
                    } else {
                        if (v.regionId == Region.getRegionNumber(regionName)) {
                            reportList.add(v);
                        }
                    }
                } while (sqlResult.next());
            }
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage());}
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
        vehicleTableView.setItems(reportList);
    }
}
