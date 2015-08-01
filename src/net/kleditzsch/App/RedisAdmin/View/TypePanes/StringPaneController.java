package net.kleditzsch.App.RedisAdmin.View.TypePanes;
/**
 * Created by oliver on 20.07.15.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.App.RedisAdmin.View.RedisAdminController;
import net.kleditzsch.Ui.UiDialogHelper;
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

    @FXML // fx:id="editMenuItem"
    private MenuItem editMenuItem; // Value injected by FXMLLoader

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

                //Log Eintrag schreiben
                RedisAdminController.getInstance().addLogEntry("Schlüssel \"" + key + "\" gelöscht");
            }
        }
    }

    @FXML
    void clickEditMenuItem(ActionEvent event) {

        if(editState == false) {

            //bearbeiten starten
            contentTextArea.setEditable(true);
            editMenuItem.setText("speichern");
            editState = true;
        } else {

            //bearbeiten beenden
            contentTextArea.setEditable(false);
            editMenuItem.setText("bearbeiten");
            editState = false;

            //Speichern

            //Sluessel laden
            String key = RedisAdminController.getCurrentKey();

            //Datenbankobjekt holen
            Jedis db = RedisConnectionManager.getInstance().getConnection();
            if(db.set(key, contentTextArea.getText()).equals("OK")) {

                //Size
                sizeLabel.setText(Integer.toString(contentTextArea.getText().length()) + " Zeichen");

                //Log Eintrag schreiben
                RedisAdminController.getInstance().addLogEntry("Schlüssel \"" + key + "\" bearbeitet");
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

                //Log Eintrag schreiben
                RedisAdminController.getInstance().addLogEntry("Schlüssel \"" + key + "\" in \"" + newKey + "\" umbenannt");
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