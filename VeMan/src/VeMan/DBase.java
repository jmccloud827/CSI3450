/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Rich, Jacob McCloud, Julien Fares
 */
public class DBase {
    
    static Connection dbConn = null;
    
    /*
    * This method opens the connection to the MySql database.
    */
    public static Connection connectToDB () {
        
        // If we are already connected, then return the Connection information
        if (dbConn != null) {
            System.out.println("In ConnectToDB, already open, returning dbConn."); 
            return dbConn;
        }
        
        // Set the database name, user name, and password
        String jdbcURL = "jdbc:mysql://localhost:3306/CSI3450";
        String dbUser = "root";
        String dbPassword = "Jm21630#18";
        
        // Use the MySQL JDBC drive to conect to the data base 
        System.out.println("In ConnectToDB. Resolving mySQL jdbc driver.");
        try {
           Class.forName("com.mysql.cj.jdbc.Driver");
           System.out.println("Driver resolution completed. \n");
        
            System.out.println("Connecting to database: " + jdbcURL);
            dbConn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
            if (dbConn == null) {
                System.out.println("ERROR: DB Connection failed.\n");
                return null;
            }
        } catch(Exception e){ System.out.println("DB Error" + e);} 
        
        System.out.println("DB Connection completed.\n");
        
        return dbConn;
    }
    
    /*
    *  Close the database connection ();
    */
    static void closeDB () {
        // Close connecton
        if (dbConn != null) {
            try {
                dbConn.close();
            } catch(Exception e){ System.out.println("DB Close Error" + e.getMessage());}
            dbConn = null;
        }
    }
    
    
}
