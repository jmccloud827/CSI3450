/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Rich
 */
public class LoadTestData {
    static  Random      random;
    
    // Construtor
    LoadTestData () {
        random = new Random();
    }
    
    // User records
    private static String[][] userData = {
        {"Tom",     "Hanks"},
        {"Alice",   "Cooper"},
        {"Mickey",  "Mouse"},
        {"Donald",  "Duck"},
        {"Pluto",   "Dog"},
        {"Minne",   "Mouse"},
        {"Frank",   "Sinatra"}
    };
    
    
    public void loadUsers() {
    
        String      name, password;
        int         region;
        
        for (String[] user : userData) {
         name = user[0];
         password = user[1];
         region = random.nextInt(4) +1;
         
         User u = new User(0, name, region, password);
         u.InsertIntoDB();
        }
    }
    
            
   
    
    private static String[][]   vehicleData = {
        {"Subarau",     "Forrester", "Outback", "Impreza", "Ascent", "Brat"},
        {"Volkswagen",  "Passat", "Jetta", "Beetle", "Golf", "Minivan"},
        {"Jeep",        "Renegade", "Cheerokee", "Warnager", "Gladiator", "Compass"},
        {"Chrysler",    "300", "Pacifica", "Voyager", "Town&Country", "Lebaron"},
        {"Ford",        "F150", "F150", "Escape", "Expedition", "Mustang"},
        {"Chevrolet",   "Malibu", "Silverado", "Taverse", "Trax", "Equinox"},
        {"GMC",         "Acadia", "Terrain", "Sierra", "Yukon", "Hummer"}, 
        {"Porsche",     "Boxser", "911", "Cayman", "Panamera", "Macan"}
    };
    
    public void loadVehicles (int maxVehicles) {
        Vehicle v = new Vehicle();
        
        int vehCount = 0;
        while (vehCount < maxVehicles) {
            // Select a row to work from
            int manufNum = random.nextInt(vehicleData.length); 
            v.setMake (vehicleData[manufNum][0]);

            // Select a model in the row
            int ModelNum = random.nextInt(5) + 1;
            v.setModel (vehicleData[manufNum][ModelNum]);
            
            // Select a region
            v.regionId = random.nextInt(4) + 1;
            
            // Generate a random year 2012-2022
            v.setYear (random.nextInt(10) + 2012);
            
            // Generate lease end 0 - 5 years in the future
            int endYear, endMonth, endDay;
            do {
                endYear = random.nextInt(5) + 2021;
                endMonth = random.nextInt(12) + 1;
                endDay = random.nextInt(30) + 1;
            } while((endYear == 2021) && (endMonth<4));
            if ((endMonth==2) && (endDay>28)) {
                endDay = 28;
            }
            v.setLeaseEnd(LocalDate.of(endYear, endMonth, endDay));
            
            // Generate inital mileage 0-2000
            v.setInitMiles (random.nextInt(1000));
            

            // Generate current mileage InitMiles 0-60000
            v.setCurMiles (random.nextInt(60000) + v.getInitMiles());
            
            // Generate payment 250.00-750.00
            v.setPayment (random.nextFloat() * 500 + 250);
            
            // Generate VIN
            String lettersAndNumbers = "ABCDEFGHIJKLMNOPQRSTUVWZY0123456789";
            String vin = "" + String.valueOf(random.nextInt(10));  
            vin += lettersAndNumbers.charAt(random.nextInt(35));
            vin += v.getMake().substring(0,2);
            vin += lettersAndNumbers.charAt(random.nextInt(35));
            vin += lettersAndNumbers.charAt(random.nextInt(35));
            vin += lettersAndNumbers.charAt(random.nextInt(35));
            vin += lettersAndNumbers.charAt(random.nextInt(35));
            vin += String.format("%04d", random.nextInt(9999));
            v.setVin (vin);            

            // Insert this into the database
            int rc = v.InsertIntoDB();
            System.out.println("Vehicle Insert RC: " + rc + " " + v.year + " " + v.make);

            // Generate service Records
            LoadService (v);
            
            // Increment the count of vehicles we have entered
            vehCount++;
        }
    }
    
    
    private static String[][]   serviceData = {
        {"Oil Change",                  "40",       "7500"},
        {"Transmission Fluid Change",   "270",      "40000"},
        {"Air Conditoning Repair",      "600",      "45000"},
        {"Fluid Check",                 "50",       "12000"},
        {"Radiator Flush",               "400",     "40000"},
        {"Windshield Replacement",      "900",      "60000"},
        {"Tires Rotated",               "50",       "15000"},
        {"Collision Damage",            "4000",     "45000"},
        {"Tires Replaced",              "1200",     "40000"},
        {"Emmision Repair",             "2200",     "35000"}
    };
            

    public void LoadService(Vehicle v) {
        int         insertCount = 0;
        
        // Setup Service record
        ServiceRec s = new ServiceRec();
        s.setVehicleId(v.id);

        // Loop and process each service record
        System.out.println("service Data Len: "+ serviceData.length); 
        for (int serviceIndex=0; serviceIndex<serviceData.length; serviceIndex++) {
            System.out.println("\n Service Rec for:  " + serviceData[serviceIndex][0]);
            
            // Process this until it exceeds the current mileage
            int serviceMile = 0;
            int howOften = Integer.valueOf(serviceData[serviceIndex][2]);
            while (serviceMile < v.curMiles) {

                // calculate the new service mileage for this service
                serviceMile = serviceMile + (howOften/2) + random.nextInt(howOften) + random.nextInt(howOften/3);
                
                // break out of loop if the mileage is too high
                if (serviceMile > v.curMiles) {
                    break;
                }
                
                // Set the mileage for the service
                s.setMiles(serviceMile);
                
                // Generate the price and add 20% randomness
                float price = Float.valueOf(serviceData[serviceIndex][1]);
                price = price + (price * (random.nextFloat()*0.3f) - (random.nextFloat()*0.3f));
                price = (float) ((Math.round(price * 100.0)) / 100.0);
                s.setCost(price);
                    
                // Generate a date
                LocalDate leaseStart = LocalDate.of(v.year, 7, 1);  // Estimate a lease start date
                float totalMiles = ((float)(v.curMiles-v.initMiles));
                float intervalMiles = ((float)(serviceMile-v.initMiles));
                float percentOfMiles = (float)1.0f - ((totalMiles-intervalMiles) / totalMiles);
                float numberOfLeaseDays = (float) (v.leaseEnd.toEpochDay() - leaseStart.toEpochDay());
                float daysBetweenService = (float)numberOfLeaseDays * percentOfMiles;
                LocalDate serviceDate = leaseStart.plusDays((long) daysBetweenService);
                s.setDate(serviceDate);
                
                // Set the description
                s.setDescription(serviceData[serviceIndex][0]);
                
                // Insert the record
                System.out.println("\nService Insert: " + s.toString());
                int rc = s.InsertIntoDB();
                if (rc != 0) {
                     System.out.println("Service RC: " + rc);
                }
                    
                insertCount++;
            }  // While
        } //for

        System.out.println("Returning from Service record load.  # records: " + insertCount);
    }  
        
}


    
    
    