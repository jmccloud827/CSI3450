/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

/**
 *
 * @author Rich
 */


public class Region {

// Define the region name arrray and limit
private static String[]  regionNames = new String[] {
    "null", "Corporate", "North", "South", "Midwest", "Pacific" };
private static int       regionCount = 5;   // Number of regions
private static int       curRegionNum = 1;  // Used for get First/Next

/* 
* Returns the Rigion Name for a given region number
*/
public static String getRegionName(int regionNum) {
    // Check for out of range regions
    if ((regionNum < 1) || (regionNum > regionCount)) {
        return ("Reg Err: " + regionNum + "!!!");
    }
    
    // Return the region name
    return regionNames[regionNum];
}
    
/* 
* Returns the Region Number for a given region name
*/
public static int getRegionNumber(String regionName) {
    //Loop through and check for match of the name
    String rname = regionName.trim();
    for (int i=1; i<=regionCount; i++) {
        if (rname.equalsIgnoreCase(regionNames[i]) == true) {
            return i;
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
    curRegionNum = 1;
    
    // Return the region name
    return regionNames[curRegionNum];
}

/* 
* Used to enumerate the regions, Returns the next Region Name or "" if at end
*/
public static String getNextRegionName() {
    // Set the static counter to be used by getNext
    curRegionNum++;
    if(curRegionNum > regionCount)
        return "";
    
    // Return the region name
    return regionNames[curRegionNum];
}
}
