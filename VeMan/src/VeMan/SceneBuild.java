/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VeMan;

import java.sql.Connection;
import java.sql.DriverManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Rich, Jacob McCloud, Julien Fares
 */
public class SceneBuild extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Connect to the database
        if (DBase.connectToDB() == null) {
            System.out.println("Database Connection failed. Program terminating.\n");
            System.exit(1);
        }
                
        // Setup values for testing
        GlobalVar.curUserID = 99;
        GlobalVar.curUserRegionID = 1;
        
        // Run and test data load routines here
        // LoadTestData test = new LoadTestData();
        // test.loadUsers();
        // test.loadVehicles(2);
        // System.out.println("Test routines complete. Starting Application\n");
        

        // Create the object for the appMenu
        GlobalVar.appMenu = new AppMenu();
        
        // Launch the JavaFX engine
        launch(args);
        
        // Close the database connection
        DBase.closeDB();
    }
        
    @Override
    public void start(Stage stage) throws Exception {


        
        
        // Set the inital scene to to display
        //Parent root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
        // Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("VehicleInv.fxml"));   
        // Parent root = FXMLLoader.load(getClass().getResource("EnterService.fxml"));   

       //Save the stage for scene switching
        GlobalVar.ourStage = stage;
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
    
       
}
