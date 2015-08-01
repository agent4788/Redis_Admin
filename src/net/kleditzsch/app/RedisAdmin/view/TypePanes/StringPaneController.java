package net.kleditzsch.app.RedisAdmin.view.TypePanes;
/**
 * Created by oliver on 20.07.15.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import net.kleditzsch.app.RedisAdmin.model.RedisConnectionManager;
import net.kleditzsch.app.RedisAdmin.view.KeyTreeViewModel;
import net.kleditzsch.app.RedisAdmin.view.RedisAdminController;
import net.kleditzsch.ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

import java.net.URL;
import java.util.ResourceBundle;

public class StringPaneController {

    protected boolean editState = false;

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

    @FXML // fx:id="keyLabelTooltip"
    private Tooltip keyLabelTooltip; // Value injected by FXMLLoader

    @FXML // fx:id="editButton"
    private Button editButton; // Value injected by FXMLLoader

    @FXML
    void clickDeleteButton(ActionEvent event) {

        //Sluessel laden
        String key = RedisAdminController.getCurrentKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        if(key != null && key != "" && db.exists(key)) {

            //Sicherheitsabfrage
            if(UiDialogHelper.showConfirmDialog("Schlüssel löschen?", key, "willst du den Schlüssel wirklich löschen?")) {

                //loeschen
                db.del(key);

                //Tree aktualisieren
                RedisAdminController.getInstance().clickReloadMenuItem(new ActionEvent());
            }
        }
    }

    @FXML
    void clickEditButton(ActionEvent event) {

        if(editState == false) {

            //bearbeiten starten
            contentTextArea.setEditable(true);
            editButton.setText("speichern");
            editState = true;
        } else {

            //bearbeiten beenden
            contentTextArea.setEditable(false);
            editButton.setText("bearbeiten");
            editState = false;

            //Speichern

            //Sluessel laden
            String key = RedisAdminController.getCurrentKey();

            //Datenbankobjekt holen
            Jedis db = RedisConnectionManager.getInstance().getConnection();
            if(db.set(key, contentTextArea.getText()).equals("OK")) {

                //Size
                sizeLabel.setText(Integer.toString(contentTextArea.getText().length()) + " Zeichen");
            } else {

                UiDialogHelper.showErrorDialog("Bearbeiten Fehlgeschlagen", key, "Der Wert des Schlüssels konnte nicht gespeichert werden");
            }
        }
    }

    @FXML
    void clickRenameButton(ActionEvent event) {

        //Sluessel laden
        String key = RedisAdminController.getCurrentKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        String newKey = UiDialogHelper.showTextInputDialog("Schlüssel umbenennen", key, "neuer Schlüssel: ", key);
        if(newKey != null && !newKey.isEmpty()) {

            //umbenennen
            if(db.rename(key, newKey).equals("OK")) {

                RedisAdminController.getInstance().clickReloadMenuItem(new ActionEvent());
            } else {

                UiDialogHelper.showErrorDialog("Fehler", key, "Der Schlüssel konnte nicht umbenannt werden");
            }
        }
    }

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
        keyLabelTooltip.setText(key);

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