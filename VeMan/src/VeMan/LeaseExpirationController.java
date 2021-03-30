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
import java.text.DecimalFormat;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author 
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
    @FXML private AnchorPane ReportOutputPane;
    @FXML private Label TotalPaymentsDisplay;
    @FXML private Label VehicleCountDisplay;

   
    /****************************************** 
    *  Called when the show vehicle button is pressed 
    ******************************************/    
    @FXML void showVehiclesButtonPressed (ActionEvent event) throws IOException {
        String                  sql;                // String for SQL statment;
        float                   totalPayments = 0;
        int                     vehicleCount = 0;
        ObservableList<Vehicle> reportList = FXCollections.observableArrayList();

        // Get the date that was selected and format it for SQL
        LocalDate   localDate = leaseEndDatePicker.getValue();
        Date        lastDate = Date.valueOf(localDate);

        // Build search information and query the database for matches    
        try {
            // Check if a region name was selected
            String regionName;
            try {
                regionName = regionChoiceBox.getValue().toString().trim();
            } catch (Exception e) {
                // Exception thrown when drop down is blank
                regionName = "ALL";
            }
            
            // Build the Prepared statement for the vehicle query
            PreparedStatement ps;
            int regionId = Region.getRegionNumber(regionName);
            if (regionId == 0) {
                // All regions specified
                sql = "SELECT * FROM VEHICLE WHERE (VEH_LEASE_END <= ?)";
                ps = dbConn.prepareStatement(sql);
                ps.setDate(1, lastDate);
            } else {
                sql = "SELECT * FROM VEHICLE WHERE (VEH_LEASE_END <= ?) AND (REGION_ID = ?)";
                ps = dbConn.prepareStatement(sql);
                ps.setDate(1, lastDate);
                ps.setInt(2, regionId);
            }
            
            // Loop through and add all matching records to the list    
            Vehicle v = new Vehicle();
            int rc = v.getFirstVehicleByQuery(ps);
            while (rc == 0) {
                // Add vehicle to vehicleList;
                reportList.add(v);
                v = new Vehicle();
                rc = v.getNextVehicle();
                
                // Adjust counts and totals
                vehicleCount++;
                totalPayments += v.payment;
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
        
        // Format and display the count and total
        VehicleCountDisplay.setText(String.valueOf(vehicleCount));
        DecimalFormat df = new DecimalFormat("$###,000.00");
        String paymentString = df.format(totalPayments);
        TotalPaymentsDisplay.setText(paymentString);
        
        // Make the report pane visible
        ReportOutputPane.setVisible(true);
    }

    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Hide the report pane
        ReportOutputPane.setVisible(false);

        // Load the regions into the choicebox
        regionChoiceBox.getItems().add("All");
        String regionName = Region.getFirstRegionName();
        int i = 1;
        while (regionName.equals("") ==  false) {
            regionChoiceBox.getItems().add(regionName);
            regionName = Region.getNextRegionName(i);
            i++;   
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
    
    @FXML void menuLeaseExpirationClicked(ActionEvent event)  throws IOException {
        GlobalVar.appMenu.leaseExpiration(event);
    }
    
}

