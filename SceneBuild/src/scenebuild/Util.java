/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenebuild;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Rich
 */
public class Util {

    /*
    *  Show an error box and wait for user to click OK
    */
    public static void ErrorBox(String Title, String Header, String Context) {
        System.out.println("ERROR BOX");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Title);
        alert.setHeaderText(Header);
        alert.setContentText(Context);
        alert.showAndWait();
        return;
    }
    
    /*
    *  Show an Confirmation box and wait for user to click OK
    *   - return false for Cancel, true for OK. 
    */
    public static boolean ConfirmBox (String Title, String Header, String Context) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        //final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Title);
        alert.setHeaderText(Header);
        alert.setContentText(Context);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {
            return false;
        }
        return true;
    }
            
}

