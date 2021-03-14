/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Rich
 */


public class Region {
    private static ResultSet sqlResult;

// Define the region name arrray and limit
static ArrayList<String> regionNames = new ArrayList<String>();
static ArrayList<Integer> regionNums = new ArrayList<Integer>();  // Used for get First/Next

/* 
* Returns the Rigion Name for a given region number
*/
public static String getRegionName(int regionNum) {
    // Check for out of range regions
    if ((regionNum < 0) || (regionNum > regionNames.size())) {
        return ("Reg Err: " + regionNum + "!!!");
    }
    
    // Return the region name
    int index = regionNums.indexOf(regionNum);
    if(index == -1){
        return ("Reg Err: " + regionNum + "!!!");
    }
    return regionNames.get(index);
}
    
/* 
* Returns the Region Number for a given region name
*/
public static int getRegionNumber(String regionName) {
    //Loop through and check for match of the name
    String rname = regionName.trim();
    for (int i=0; i<regionNames.size(); i++) {
        if (rname.equalsIgnoreCase(regionNames.get(i)) == true) {
            return regionNums.get(i);
        }
    }
    
    // There was not match - return null
    System.out.println("ERROR - region not found: " + regionName);
    return 0;
}

/* 
* Used to enumerate the regions, Returns the first Region Name
*/
public static String getFirstRegionName() {
    // Set the static counter to be used by getNext
    regionNames = new ArrayList<String>();
    regionNums = new ArrayList<Integer>();
    System.out.println("In Region.GetFirstRegion.");
        Connection dbConn = DBase.connectToDB();
        
        try {
            // Build Java SQL query statement 
            String sql = "SELECT * FROM REGION"; 
            PreparedStatement ps = dbConn.prepareStatement(sql);
        
            // Send statement to mySQl to execute.
            sqlResult = ps.executeQuery();
            System.out.println("executeQuery complete." + sqlResult);
        } catch(Exception e){ System.out.println("DB Error: " + e.getMessage());}
        return getNextRegionName(0);
}

/* 
* Used to enumerate the regions, Returns the next Region Name or "" if at end
*/
public static String getNextRegionName(int index) {
    System.out.println("In GetNextRegionName");
        try {
            // Read the next record from the database
            if (sqlResult.next() != true) {
                System.out.println("sqlResult empty.  Returning 1");
                return "1";
            }
        
            // Load the data into the Region object
            regionNums.add(sqlResult.getInt("REG_ID"));
            regionNames.add(sqlResult.getString("REG_NAME"));
        } catch(Exception e){ System.out.println("DB Error" + e.getMessage());}
            
        return regionNames.get(index); //success
}
}
