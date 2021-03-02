/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenebuild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Rich Frost, Jacob McCloud, Julien Fares
 */
public class User {
    private static ResultSet sqlResult;    // Links SQL calls for the user table
    
    // Data for each user instance
    int                   userId;
    int                   regionId;
    SimpleStringProperty  userName;
    SimpleStringProperty  regionName;
    SimpleStringProperty  password;

     
    // Test Constructor used during testing to pass test strings 
    User(String uName, String uRegionName, String uPassword) {
        this.userName   = new SimpleStringProperty(uName);
        this.regionName = new SimpleStringProperty(uRegionName);
        this.password   = new SimpleStringProperty(uPassword);
    }
    
    // Constructor for User with all informtion
    User(int uID, String uName, int uRegionID, String uPassword) {
        this.userId     = uID;
        this.userName   = new SimpleStringProperty(uName);
        this.regionId   = uRegionID;
        this.regionName = new SimpleStringProperty(Region.getRegionName(uRegionID));
        this.password   = new SimpleStringProperty(uPassword);
    }
    
    // Blank Constructor - creates empty user objeect
    User() {
        this.userName   = new SimpleStringProperty("No Name");
        this.regionName = new SimpleStringProperty("No Region");
        this.password   = new SimpleStringProperty("No Pass");
    }
 
    
    /*
    *   Validate the user name and password
    *   The only fields in the user object are Username and Password
    *   -Returns 0 if sucessfull, error code otherwise
    */
    public int validate(){
        Connection dbConn = DBase.connectToDB();
        System.out.println("In User.validate()");
        
        // The user name is not case sensitive so convert it to upper case
        String upperUserName = this.getUserName().toUpperCase();
        try {
            // Build Java SQL query statement 
            String sql = "SELECT * FROM USER WHERE UPPER(USER_NAME) = ?"; 
            PreparedStatement ps = dbConn.prepareStatement(sql);
            ps.setString(1, upperUserName);
            
            // Send statement to mySQl to execute.
            sqlResult = ps.executeQuery();
  
            // Read the next record from the database
            if (sqlResult.next() != true) {
                System.out.println("sqlResult empty.  Returning 1");
                return 1;
            }
        
            // Read and compare the password
            String storedPassword = sqlResult.getString("USER_PASSWORD");
            String enteredPassword = this.getPassword();            
            if (storedPassword.equals(enteredPassword) == false) {
                System.out.println("Password mismatch.  Returning 2");
                return 2;
            }
                
            // Password matched.  Load the data into the User object
            setUserId (sqlResult.getInt("USER_ID"));
            setUserName (sqlResult.getString("USER_NAME"));
            setPassword (sqlResult.getString("USER_PASSWORD"));
            setRegionId (sqlResult.getInt("REGION_ID"));
            setRegionName(Region.getRegionName(regionId));
        } catch(Exception e){System.out.println("DB Error: " + e.getMessage());}
        
        return 0;
    }
    
    /*
    *  Read the first User from the database
    */
    int getFirstUser() {
        System.out.println("In User.GetFirstUser.");
        Connection dbConn = DBase.connectToDB();
        
        try {
            // Build Java SQL query statement 
            String sql = "SELECT * FROM USER"; 
            PreparedStatement ps = dbConn.prepareStatement(sql);
        
            // Send statement to mySQl to execute.
            sqlResult = ps.executeQuery();
            System.out.println("executeQuery complete." + sqlResult);
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage());}
        
        // Get the first user in the list
        return getNextUser();
    }
    
    /*
    *  Read the next User from the database
    *   - returns 0 if sucess, error code otherwise
    */
    int getNextUser() {
        System.out.println("In GetNextUser");

        try {
            // Read the next record from the database
            if (sqlResult.next() != true) {
                System.out.println("sqlResult empty.  Returning 1");
                return 1;
            }
        
            // Load the data into the User object
            setUserId (sqlResult.getInt("USER_ID"));
            setUserName (sqlResult.getString("USER_NAME"));
            setPassword (sqlResult.getString("USER_PASSWORD"));
            setRegionId  (sqlResult.getInt("REGION_ID"));
            setRegionName(Region.getRegionName(regionId));
        } catch(Exception e){ System.out.println("DB Error" + e.getMessage());}
            
        return 0;  // Success
    }  

   /*
   *  Insert user into the database table
   *    - Returns the ID of the new user or 0 if error;
   */
   int InsertIntoDB() {
       int                  numRowsUpdated;
       PreparedStatement    ps;
       
       System.out.println("In User InsertIntoDB");
       Connection dbConn = DBase.connectToDB();
            
        try {
            // Build Java SQL prepared statement for the insert 
            String sql = "INSERT INTO USER (USER_NAME, USER_PASSWORD, REGION_ID) VALUES(?,?,?)";
            ps = dbConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int rID = this.getRegionId();
            ps.setString(1, this.getUserName());
            ps.setString(2, this.getPassword());
            ps.setInt(3, rID);
            
            // Send statement to mySQl to execute.
            numRowsUpdated = ps.executeUpdate();
            System.out.println("executeUpdate complete. Result: " + numRowsUpdated);
       
            // There should be one row modified
            if (numRowsUpdated != 1) {
                System.out.println("ERROR Unexpected # of rows inserted: " + numRowsUpdated);
               return 0;
            } 
        
            // Get the UserID of the new user
            ResultSet rs = ps.getGeneratedKeys();
            if(rs != null && rs.next()){
                this.userId = rs.getInt(1);
                System.out.println("Generated User Id: " + this.userId);
            }
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage());}
        
        return this.userId;
       
   }


   /****************************************
    * deleteFromDB()
    * 
    *  Delete a user record from the database table
    *   - Returns 0 if successful, error code otherwise
    ***************************************/
   int deleteFromDB() {
        int                  numRowsUpdated = 0;
        PreparedStatement    ps;
       
        System.out.println("In User DeleteFromDB");
        Connection dbConn = DBase.connectToDB();
        try {
            // Build Java SQL prepared statement for the insert 
            String sql = "DELETE FROM USER WHERE USER_ID = ?";
            ps = dbConn.prepareStatement(sql);
            ps.setInt(1, this.userId);
            
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

    /******************************************
     * updatePasswordInDB()
     * 
     *  Update a user's password in the database table
     *    - Returns the number of rows updated or 0 if error;
     *****************************************/
   int updatePasswordInDB(String password) {
       int                  numRowsUpdated = 0;
       PreparedStatement    ps;
       
       System.out.println("In User UpdatePasswordInDB");
       Connection dbConn = DBase.connectToDB();
            
        try {
            // Build Java SQL prepared statement for the update 
            String sql = "UPDATE USER SET USER_PASSWORD = ? WHERE USER_ID =?";
            ps = dbConn.prepareStatement(sql);
            ps.setString(1, password);
            ps.setInt(2, this.userId);
            
            // Send statement to mySQl to execute.
            numRowsUpdated = ps.executeUpdate();
            System.out.println("executeUpdate complete. Result: " + numRowsUpdated);
       
            // There should be one row modified
            if (numRowsUpdated != 1) {
                System.out.println("ERROR Unexpected # of rows updated: " + numRowsUpdated);
               return 0;
            } 
        } catch (Exception e){ System.out.println("DB Error: " + e.getMessage());}
        
        return numRowsUpdated;
   }
   
    /********************************************
    * Getter and Setter
    ********************************************/
    public String getUserName() {
        return userName.get();
    }
    public void setUserName(String name) {
        userName.set(name);
    }
        
    public String getRegionName() {
        return regionName.get();
    }
    public void setRegionName(String rname) {
        regionName.set(rname);
    }
    
   
    public String getPassword() {
        return password.get();
    }
    public void setPassword(String pw) {
        password.set(pw);
    }
    
    public int getUserId() {
        return userId;
    }
    public void setUserId (int userId) { 
        this.userId = userId;
    }

    public int getRegionId() {
        return regionId;
    }public void setRegionId(int regionId) {    
        this.regionId = regionId;
    }

    // toString
    public String toString() {
        String str =
          "USER_ID: " + userId +
          " USER_NAME: " + userName +
          " PASSWORD: " + password +
          " REGION_ID" + regionId +
          " REGION_NAME" + regionName;
        return str;
    }    
}
