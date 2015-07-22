package net.kleditzsch.app.RedisAdmin.view.TypePanes;
/**
 * Created by oliver on 20.07.15.
 */

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import net.kleditzsch.app.RedisAdmin.model.RedisConnectionManager;
import net.kleditzsch.app.RedisAdmin.view.RedisAdminController;
import redis.clients.jedis.Jedis;

import java.net.URL;
import java.util.ResourceBundle;

public class StringPaneController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="typeLabel"
    private Label typeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="ttlLabel"
    private Label ttlLabel; // Value injected by FXMLLoader

    @FXML // fx:id="encodingLabel"
    private Label encodingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="sizeLabel"
    private Label sizeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="contentTextArea"
    private TextArea contentTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="keyLabel"
    private Label keyLabel; // Value injected by FXMLLoader

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        assert typeLabel != null : "fx:id=\"typeLabel\" was not injected: check your FXML file 'StringPane.fxml'.";
        assert ttlLabel != null : "fx:id=\"ttlLabel\" was not injected: check your FXML file 'StringPane.fxml'.";
        assert encodingLabel != null : "fx:id=\"encodingLabel\" was not injected: check your FXML file 'StringPane.fxml'.";
        assert sizeLabel != null : "fx:id=\"sizeLabel\" was not injected: check your FXML file 'StringPane.fxml'.";
        assert contentTextArea != null : "fx:id=\"contentTextArea\" was not injected: check your FXML file 'StringPane.fxml'.";
        assert keyLabel != null : "fx:id=\"keyLabel\" was not injected: check your FXML file 'StringPane.fxml'.";

        //Daten ermitteln und setzen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //Schluessel
        String key = RedisAdminController.getCurrentKey();
        String value = db.get(key);

        //Schluessel
        keyLabel.setText(key);

        //Typ
        typeLabel.setText("String");

        //TTL
        long ttl = db.ttl(key);
        ttlLabel.setText((ttl == -1 ? "keine" : Long.toString(ttl)));

        //Encoding
        encodingLabel.setText(db.objectEncoding(key));

        //Size
        sizeLabel.setText(Integer.toString(value.length()) + " Zeichen");

        //Content
        contentTextArea.setText(value);
    }
}