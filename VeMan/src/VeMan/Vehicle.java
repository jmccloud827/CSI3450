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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Rich Frost, Jacob McCloud, Julien Fares
 */
public class Vehicle {
    private static ResultSet sqlResult;    // Links SQL calls for the user table
     
    // Information for each vehicle istance
    int             id;
    String          make;
    String          model;
    int             year;
    String          vin;
    int             regionId;
    LocalDate       leaseEnd;
    float           payment;
    int             initMiles;
    int             curMiles;
        
    // Special fields required for TableView display of data
    SimpleStringProperty    makeSSP;
    SimpleStringProperty    modelSSP;
    SimpleStringProperty    vinSSP;
    SimpleStringProperty    leaseEndSSP;
    SimpleStringProperty    paymentSSP;
    SimpleStringProperty    regionSSP;
    
    // Blank Constructor - creates empty vehicle object
    Vehicle () {
        this.makeSSP        = new SimpleStringProperty("No Make");
        this.modelSSP       = new SimpleStringProperty("No model");
        this.vinSSP         = new SimpleStringProperty("No VIN");
        this.leaseEndSSP    = new SimpleStringProperty("No End");
        this.paymentSSP     = new SimpleStringProperty("000.00");
        this.regionSSP      = new SimpleStringProperty("NO REG");
    }
    
    // Constructor with values passed and prevalidated
    Vehicle (String make, String model, int year, String vin,int regionId
        , LocalDate leaseEnd, float payment, int initMiles, int curMiles) {
        
        // Assign the base variables
        this.make   = make;
        this.model  = model;
        this.year   = year;
        this.vin    = vin;
        this.regionId  = regionId;
        this.leaseEnd  = leaseEnd;
        this.payment   = payment;
        this.initMiles = initMiles;
        this.curMiles = curMiles;
        
        // Create the simpleStrings for the tableview object
        this.makeSSP        = new SimpleStringProperty(make);
        this.modelSSP       = new SimpleStringProperty(model);
        this.vinSSP         = new SimpleStringProperty(vin);
        this.leaseEndSSP    = new SimpleStringProperty("");
        this.setLeaseEndSSP (leaseEnd);
        this.paymentSSP     = new SimpleStringProperty("");
        this.setPaymentSSP  (payment);
        this.regionSSP      = new SimpleStringProperty("");
        this.setRegionSSP   (regionId);
       
    }
        

    
  /********************************************
  * insertIntoDB
  * 
  *  Insert vehicle into the database table and updates the Vehicle ID
  *    - Returns 0 if successful, error code otherwise;
  ********************************************/
   int InsertIntoDB() {
       int                  numRowsUpdated;
       PreparedStatement    ps;
       
       System.out.println("InVehicle InsertIntoDB");
       Connection dbConn = DBase.connectToDB();
            
        try {
            // Build Java SQL prepared statement for the insert 
            String sql = "INSERT INTO VEHICLE (REGION_ID, "
                    + "VEH_MAKE, VEH_MODEL, VEH_YEAR, VEH_VIN," 
                    + "VEH_INITIAL_MILEAGE, VEH_CUR_MILEAGE," 
                    + "VEH_LEASE_END, VEH_PAYMENT) " 
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = dbConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt   (1, regionId);
            ps.setString(2, make);
            ps.setString(3, model);
            ps.setInt   (4, year);
            ps.setString(5, vin.toUpperCase());
            ps.setInt   (6, initMiles);
            ps.setInt   (7, curMiles); 
            ps.setDate  (8, Date.valueOf(leaseEnd));
            ps.setFloat (9, payment);

            
            // Send statement to mySQl to execute.
            numRowsUpdated = ps.executeUpdate();
            System.out.println("executeUpdate complete. Result: " + numRowsUpdated);
       
            // There should be one row modified
            if (numRowsUpdated != 1) {
                System.out.println("ERROR Unexpected # of rows inserted: " + numRowsUpdated);
               return 1;
            } 
        
            // Get the VehicleId of the new vehicle
            ResultSet rs = ps.getGeneratedKeys();
            if(rs != null && rs.next()){
                this.id = rs.getInt(1);
                System.out.println("Generated Vehicle Id: " + this.id);
            }
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage());}
        
        return 0;
   }

   /****************************************
    * deleteFromDB()
    * 
    *  Delete a vehicle record from the database table
    *   - All maintenance records for vehicle must be deleted before calling.
    *   - Returns 0 if successful, error code otherwise
    ***************************************/
   int deleteFromDB() {
       int                  numRowsUpdated = 0;
       PreparedStatement    ps;
       
        System.out.println("In Vehicle DeleteFromDB");
        Connection dbConn = DBase.connectToDB();
        try {
            // Build Java SQL prepared statement for the insert 
            String sql = "DELETE FROM VEHICLE WHERE VEH_ID = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setInt(1, this.id);
            
            // Send statement to mySQl to execute.
            numRowsUpdated = ps.executeUpdate();
            System.out.println("executeUpdate complete. Result: " + numRowsUpdated);
       
            // There should be one row modified
            if (numRowsUpdated != 1) {
                System.out.println("ERROR Unexpected # of rows deleted: " + numRowsUpdated);
               return 1;
            } 
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage());}
        return 0;
   }

   
   /****************************************
    * deleteVehicleAndServiceFromDB()
    * 
    *  Delete a vehicle record and all associated service records from the the database
    *   - Returns 0 if successful, error code otherwise
    ***************************************/
   int deleteVehicleAndServiceFromDB(IntRef serviceDeleteCount) {
        System.out.println("In Vehicle DeleteVehicleAenServiceFromDB");
        int returnCode = 0;
        serviceDeleteCount.setValue(0);
        Connection dbConn = DBase.connectToDB();
        
        try {
            // Turn off auto commint so this can be handled as a transaction
            dbConn.setAutoCommit(false);

            // First delete the service records for the vehicle
            ServiceRec s = new ServiceRec();
            s.vehicleId = this.id;
            s.deleteAllVehicleServiceRecordsFromDB(serviceDeleteCount);
            
            // Delete the vehicle from the database
            this.deleteFromDB();
                
            // Commit the chages to the database
            System.out.println("Commiting changes to DB.");
            dbConn.commit();
        
        // An error occured rollback any DB changes 
        } catch(Exception e){ 
            System.out.println("DB Error: " + e.getMessage());
            returnCode = 1;
            try {
                dbConn.rollback();
            } catch (Exception ex){ System.out.println("DB Rollback Error: " + ex.getMessage());}

        // Set the auto commit status back to true           
        } finally {
            try {
                dbConn.setAutoCommit(true);
            } catch (Exception ex){ System.out.println("DB AutoCommit Error: " + ex.getMessage());}
        }
        //serviceDeleteCount = 46; return 0;
        return returnCode;
    }
    
   
     /******************************************
     * updateCurMileageInDB()
     * 
     *  Update the current mileage for a vehicle in the database table
     *  -Returns 0 if successful, error code otherwise
     *****************************************/
   int updateCurMileageInDB(int newMileage) {
       int                  numRowsUpdated = 0;
       PreparedStatement    ps;
       
       System.out.println("In UpdateCurMileageInDB");
       Connection dbConn = DBase.connectToDB();
            
        try {
            // Build Java SQL prepared statement for the update 
            String sql = "UPDATE VEHICLE SET VEH_CUR_MILEAGE = ? WHERE VEH_ID =?";
            ps = dbConn.prepareStatement(sql);
            ps.setInt(1, newMileage);
            ps.setInt(2, this.id);
            
            // Send statement to mySQl to execute.
            numRowsUpdated = ps.executeUpdate();
            System.out.println("executeUpdate complete. Result: " + numRowsUpdated);
       
            // There should be one row modified
            if (numRowsUpdated != 1) {
                System.out.println("ERROR Unexpected # of rows updated: " + numRowsUpdated);
                return 1;
            }
            
            // Set the current Mileage in the vehicle object
            this.setCurMiles(newMileage);
            
        } catch (Exception e){ System.out.println("DB Error: " + e.getMessage());}
        return 0;
   }

   
   /****************************************
    *   getFirstVehicleInRegion()
    * 
    * Reads the first Vehicle from the database for a Region.
    * It loads vehicles for all regions if the region id is 0.
    ****************************************/
    int getFirstVehicleInRegion(int regionId) {
        System.out.println("In Vehicle.getFirstVehicleInRegion.");
        Connection dbConn = DBase.connectToDB();
        
        // If  region ID  is zero then select all the vehicles
        if (regionId == 0) 
                return getFirstVehicle();
        
        try {
            // Build Java SQL query statement 
            String sql = "SELECT * FROM VEHICLE where REGION_ID=?"; 
            PreparedStatement ps = dbConn.prepareStatement(sql);
            ps.setInt(1, regionId);
        
            // Send statement to mySQl to execute.
            sqlResult = ps.executeQuery();
            System.out.println("executeQuery complete." + sqlResult);
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage()); return 2;}
        
        // Get the first user in the list
        return getNextVehicle();
    }
    
   
    /****************************************
    *   getFirstVehicle()
    * 
    * Read the first Vehicle from the database
    ****************************************/
    int getFirstVehicle() {
        System.out.println("In Vehicle.getFirstVehicle.");
        Connection dbConn = DBase.connectToDB();
        
        try {
            // Build Java SQL query statement 
            String sql = "SELECT * FROM VEHICLE"; 
            PreparedStatement ps = dbConn.prepareStatement(sql);
        
            // Send statement to mySQl to execute.
            sqlResult = ps.executeQuery();
            System.out.println("executeQuery complete." + sqlResult);
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage()); return 2;}
        
        // Get the first user in the list
        return getNextVehicle();
    }

    
    /****************************************
    *   getFirstVehicleByQuery()
    * 
    * Passed Prepared statement query and the first Vehicle from the database
    * based on the query
    ****************************************/
    int getFirstVehicleByQuery(PreparedStatement ps) {
        System.out.println("In Vehicle.getFirstVehicleByQuery.");
        Connection dbConn = DBase.connectToDB();
        
        try {
            // Send statement to mySQl to execute.
            sqlResult = ps.executeQuery();
            System.out.println("executeQuery complete." + sqlResult);
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage()); return 2;}
        
        // Get the first user in the list
        return getNextVehicle();
    }
    
        
    /***************************************
     * getNextVehicle()
     * 
     *  Read the next Vehicle from the database
     *   - returns 0 if success, error code otherwise
    ***************************************/
    int getNextVehicle() {
        try {
            // Read the next record from the database
            if (sqlResult.next() != true) {
                System.out.println("Vehicle sqlResult empty.  Returning 1");
                return 1;
            }
        
            // Load the data into the Vehicle object
            this.id         = sqlResult.getInt("VEH_ID");
            this.regionId   = sqlResult.getInt("REGION_ID");
            this.make       = sqlResult.getString("VEH_MAKE");
            this.model      = sqlResult.getString("VEH_MODEL");
            this.year       = sqlResult.getInt("VEH_YEAR");
            this.vin        = sqlResult.getString("VEH_VIN");
            this.initMiles  = sqlResult.getInt("VEH_INITIAL_MILEAGE");
            Date date       = sqlResult.getDate("VEH_LEASE_END");
            this.leaseEnd   = date.toLocalDate();
            this.payment    = sqlResult.getFloat("VEH_PAYMENT");
            this.curMiles   = sqlResult.getInt("VEH_CUR_MILEAGE");;
            
            // Load the displayable strings for tableview
            this.makeSSP.set(this.make);
            this.modelSSP.set(this.model);
            this.vinSSP.set(this.vin);
            setRegionSSP (this.regionId);
            setLeaseEndSSP (this.leaseEnd);
            setPaymentSSP(this.payment);
                        
        } catch(Exception e){ System.out.println("DB Error" + e.getMessage());}
            
        return 0;  // Success
    }  


    /********************************************
    * Getter and Setter
    ********************************************/
    public int getId()                      {return id;}
    public void setId(int vId)              {id = vId;}
    public String getMake()                 {return make;}
    public void setMake(String vmake)       {make = vmake; makeSSP.set(vmake);}
    public String getModel()                {return model;}
    public void setModel(String vmodel)     {model = vmodel; modelSSP.set(vmodel);}
    public int getYear()                    {return year;}
    public void setYear(int vyear)          {year = vyear;}
    public int getRegionId()                {return regionId;}
    public void setRegionId (int vregionId) {regionId = vregionId;}
    public String getVin()                  {return vin;}
    public void setVin(String vVin)         {vin = vVin; vinSSP.set(vVin);}
    public int getInitMiles()               {return initMiles;}
    public void setInitMiles(int vMiles)    {initMiles = vMiles;}
    public int getCurMiles()                {return curMiles;}
    public void setCurMiles(int vMiles)     {curMiles = vMiles;}
    public LocalDate getLeaseEnd()          {return leaseEnd;}
    public void setLeaseEnd(LocalDate date) {leaseEnd = date; setLeaseEndSSP(date);}
    public float getPayment()               {return payment;}
    public void setPayment (float pmt)      {payment = pmt; setPaymentSSP(pmt);}
  
      
    // Getters and Setter for tableview display values that require formatting
    public String getMakeSSP()              {return (makeSSP.get());}
    public String getModelSSP()             {return (modelSSP.get());}
    public String getVinSSP()               {return (vinSSP.get());}
    public String getLeaseEndSSP()          {return (leaseEndSSP.get());}
    public String getPaymentSSP()           {return (paymentSSP.get());}
    public String getRegionSSP()            {return (regionSSP.get());}
       
    
    private void setLeaseEndSSP(LocalDate date) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("MM/dd/uuuu");
        leaseEndSSP.set (date.format(formatters));
    }
 
    private void setPaymentSSP(float pmt) {
        DecimalFormat df = new DecimalFormat("#,000.00");
        paymentSSP.set(df.format(pmt));
    }

    private void setRegionSSP(int regionId) {
        String  regionName = Region.getRegionName(regionId);
        regionSSP.set(regionName);
    }
 
    public String toString() {
        String str =  
            "id: "          + id
            + "\nmake: "    + make
            + "\nmodel: "   + model
            + "\nyear: "    + year
            + "\nvin: "     + vin
            + "\nregionId: "+ regionId
            + "\npayment: " + payment
            + "\ninitMiles: "+initMiles
            + "\ncurMiles: "+ curMiles;
        return str;
    }
}

