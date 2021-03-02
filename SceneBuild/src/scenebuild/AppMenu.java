/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenebuild;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Rich
 */
public class AppMenu {
    /* 
    *   This method is called when the logout menu is selected
    */
    public void logout (ActionEvent event) throws IOException  {
        // Clear the user information
        GlobalVar.curUserID = 0;
        GlobalVar.curUserRegionID = 0;
        
        // Switch display to login scene
        Parent parent = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
        Scene  scene = new Scene (parent);
        Stage window = GlobalVar.ourStage;  //(Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene (scene);
        window.show();
    }

     /* 
    *   This method is called when the File->Close menu is selected
    */
    public void close (ActionEvent event) throws IOException  {
        // Close the database connection
        DBase.closeDB();
        Platform.exit();
    }


    /* 
    *   This method is called to move to the primary vehicle screen
    */
    public void vehicleInv (ActionEvent event) throws IOException  {
        
        // Switch display to the vehicle scene
        System.out.println("In Vehicle Inventory scene load");
        Parent parent = FXMLLoader.load(getClass().getResource("VehicleInv.fxml"));
        Scene  scene = new Scene (parent);
        Stage window = GlobalVar.ourStage;  //(Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene (scene);
        window.show();
    }

    /* 
    *   This method is called to move to the primary service screen
    */
    public void service (ActionEvent event) throws IOException  {
        // Switch display to the proper scene
        Parent parent = FXMLLoader.load(getClass().getResource("ServiceScene.fxml"));
        Scene  scene = new Scene (parent);
        Stage window = GlobalVar.ourStage; 
        window.setScene (scene);
        window.show();
    }
    
    /* 
    *   This method is called to move to the User screen
    */
    public void user (ActionEvent event) throws IOException  {
        // Switch display to the proper scene
        Parent parent = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene  scene = new Scene (parent);
        Stage window = GlobalVar.ourStage; 
        window.setScene (scene);
        window.show();
    }
}
