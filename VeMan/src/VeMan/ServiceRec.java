/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Rich Frost, Jacob McCloud, Julien Fares
 */
public class ServiceRec {
    //private static ResultSet sqlResult;    // Links SQL calls for the user table
     
    // Information for each vehicle istance
    int             id;
    int             vehicleId;
    LocalDate       date;
    int             miles;
    String          description;
    float           cost;
        
    // Special fields required for TableView display of data
     SimpleStringProperty    dateSSP;
     SimpleStringProperty    descriptionSSP;
     SimpleStringProperty    costSSP;
    
    // Blank Constructor - creates empty vehicle object
    ServiceRec () {
        this.dateSSP        = new SimpleStringProperty("No Date");
        this.descriptionSSP = new SimpleStringProperty("No Desc");
        this.costSSP        = new SimpleStringProperty("No Cost");
    }
    
    // Constructor with values passed and prevalidated
    ServiceRec (int vehicleId, LocalDate date, int miles, 
            String description, float cost) {
        
        // Assign the base variables
        this.vehicleId = vehicleId;
        this.date = date;
        this.miles = miles;
        this.description = description;
        this.cost = cost;
        
        // Create the simpleStrings for the tableview object
        this.dateSSP            = new SimpleStringProperty("");
        this.setDateSSP (date);
        this.descriptionSSP     = new SimpleStringProperty(description);
        this.costSSP            = new SimpleStringProperty("");
        this.setCostSSP (cost);
       
    }

    /********************************************
    * insertIntoDB
    * 
    *  Insert Service Record into the database table and updates the ServiceId
    *    - Returns 0 if successful, error code otherwise;
    ********************************************/
    int InsertIntoDB() {
       int                  numRowsUpdated;
       PreparedStatement    ps;
       
       System.out.println("In ServiceRec.InsertIntoDB");
       Connection dbConn = DBase.connectToDB();
            
        try {
            // Build Java SQL prepared statement for the insert 
            String sql = "INSERT INTO MAINTENANCE (VEHICLE_ID, "
                    + "MAINT_DATE, MAINT_MILEAGE, MAINT_DESCRIPTION, MAINT_COST) " 
                    + "VALUES (?, ?, ?, ?, ?)";
            ps = dbConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt   (1, vehicleId);
            ps.setDate  (2, Date.valueOf(date));
            ps.setInt   (3, miles);
            ps.setString(4, description);
            ps.setFloat (5, cost);

            // Send statement to mySQl to execute.
            numRowsUpdated = ps.executeUpdate();
            System.out.println("executeUpdate complete. Result: " + numRowsUpdated);
       
            // There should be one row modified
            if (numRowsUpdated != 1) {
                System.out.println("ERROR Unexpected # of rows inserted: " + numRowsUpdated);
               return 1;
            } 
        
            // Get the Id of the new row
            ResultSet rs = ps.getGeneratedKeys();
            if(rs != null && rs.next()){
                this.id = rs.getInt(1);
                System.out.println("Generated Service Id: " + this.id);
            }
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage()); return 2;}
        
        return 0;
   }
    
    /****************************************
    * deleteAllVehicleServiceRecordsFromDB()
    * 
    *  Delete all of the service records for a vehicle.
    *   - Returns 0 if successful, error code otherwise
    ***************************************/
   int deleteAllVehicleServiceRecordsFromDB(IntRef numRecordsDeleted) {
       PreparedStatement    ps;
       
        System.out.println("In DelAllVehServRecFromDB DeleteFromDB");
        Connection dbConn = DBase.connectToDB();
        try {
            // Build Java SQL prepared statement for the insert 
            String sql = "DELETE FROM MAINTENANCE WHERE VEHICLE_ID = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setInt(1, this.vehicleId);
            
            // Send statement to mySQl to execute.
            int rowCount = 0;
            rowCount = ps.executeUpdate();
            System.out.println("executeUpdate complete. # Rows Deleted: " + rowCount);
            numRecordsDeleted.setValue(rowCount);
            
        } catch(Exception e){ System.out.println("DB Error Service Delete: " + e.getMessage()); return 2;}
        return 0;
   }


    /********************************************
    * Getter and Setter
    ********************************************/
    public int getId()                      {return id;}
    public void setId(int sId)              {id = sId;}
    public int getVehicleId()               {return vehicleId;}
    public void setVehicleId(int vId)       {vehicleId = vId;}
    public LocalDate getDate()              {return date;}
    public void setDate(LocalDate sdate)    {date = sdate; setDateSSP(date);}
    public int getMiles()                   {return miles;}
    public void setMiles(int sMiles)        {miles = sMiles;}
    public String getDescription()          {return description;}
    public void setDescription(String sDesc){description = sDesc; descriptionSSP.set(sDesc);}
    public float getCost()                  {return cost;}
    public void setCost(float sCost)     {cost = sCost; setCostSSP(sCost);}
  
      
    // Getters and Setter for tableview display values that require formatting
    public String getDateSSP()              {return (dateSSP.get());}
    public String getDescriptionSSP()       {return (descriptionSSP.get());}
    public String getCostSSP()              {return (costSSP.get());}
       
    private void setDateSSP(LocalDate date) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        dateSSP.set (date.format(formatters));  
    }
 
    private void setCostSSP(float pmt) {
        DecimalFormat df = new DecimalFormat("#,000.00");
        costSSP.set(df.format(pmt));
    }
 
    public String toString() {
        String str =  
            "id: "              + id
            + "\nvehicleId: "   + vehicleId
            + "\ndate: "        + date
            + "\nmiles: "       + miles
            + "\ndescription: " + description
            + "\ncost: "        + cost;        
        return str;
    }
       
} // End of class
