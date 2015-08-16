package net.kleditzsch.App.RedisAdmin.View.ServerInfo;
/**
 * Created by oliver on 16.08.15.
 */

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;

public class FullServerInfoController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="closeButton"
    private Button closeButton; // Value injected by FXMLLoader

    @FXML // fx:id="dataTextArea"
    private TextArea dataTextArea; // Value injected by FXMLLoader

    @FXML
    void clickCloseButton(ActionEvent event) {

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert closeButton != null : "fx:id=\"closeButton\" was not injected: check your FXML file 'FullServerInfo.fxml'.";
        assert dataTextArea != null : "fx:id=\"dataTextArea\" was not injected: check your FXML file 'FullServerInfo.fxml'.";

        StringWriter buffer = new StringWriter();
        PrintWriter pw = new PrintWriter(buffer);
        Map<String, Map<String, String>> serverInfo = RedisConnectionManager.getInstance().getServerInfoForCurrentConnection();

        boolean first = true;
        for(String section : serverInfo.keySet()) {

            //Leerzeile von den Sektionen
            if(first == true) {
                first = false;
            } else {
                pw.println();
            }

            pw.println(" # " + section);
            for(String key : serverInfo.get(section).keySet()) {

                pw.println("\t" + key + " : " + serverInfo.get(section).get(key));
            }
        }

        dataTextArea.setText(buffer.toString());
    }
}