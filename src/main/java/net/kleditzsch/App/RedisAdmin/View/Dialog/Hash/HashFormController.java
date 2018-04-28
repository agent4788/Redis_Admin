package net.kleditzsch.App.RedisAdmin.View.Dialog.Hash;
/**
 * Created by oliver on 31.07.15.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.App.RedisAdmin.View.Dialog.RedisAdmin.RedisAdminController;
import net.kleditzsch.Ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

public class HashFormController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="keyTextField"
    private TextField keyTextField; // Value injected by FXMLLoader

    @FXML // fx:id="valueTextArea"
    private TextArea valueTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="hashKey"
    private TextField hashKeyField; // Value injected by FXMLLoader

    @FXML
    void clickCancleButton(ActionEvent event) {

        Stage stage = (Stage) keyTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickSaveButton(ActionEvent event) {

        //Daten holen
        String key = keyTextField.getText();
        String hasKey = hashKeyField.getText();
        String value = valueTextArea.getText();

        //validieren
        boolean error = false;

        //Schluessel
        if(key == null || key.isEmpty()) {

            keyTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            error = true;
        }

        //Hash Schluessel
        if(hasKey == null || hasKey.isEmpty()) {

            hashKeyField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            error = true;
        }

        //Wert
        if(value == null || value.isEmpty()) {

            valueTextArea.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            error = true;
        }

        if(error == true) {

            UiDialogHelper.showErrorDialog("Fehlerhafte Eingaben", null, "Fehlerhafte Eingaben im Formular");
            return;
        }

        //Formulardaten speichern
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //pruefen ob der Schluessel schon existiert
        if(db.exists(key)) {

            //Abfrage ob der Schluessel ueberschrieben werden soll
            if(!UiDialogHelper.showConfirmDialog("Schlüssel existiert bereits", key, "soll der Schlüssel überschrieben werden?")) {

                return;
            }

            //alten Schluessel loeschen
            db.del(key);
        }

        //Hash erstellen
        db.hset(key, hasKey, value);

        //Fenster schließen
        Stage stage = (Stage) keyTextField.getScene().getWindow();
        stage.close();

        //Tree zum neu Laden anstosen
        RedisAdminController.getInstance().clickReloadMenuItem(new ActionEvent());

        //Log Eintrag schreiben
        RedisAdminController.getInstance().addLogEntry("Schlüssel \"" + key + "\" als Typ \"Hash\" erstellt");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert keyTextField != null : "fx:id=\"keyTextField\" was not injected: check your FXML file 'HashForm.fxml'.";
        assert valueTextArea != null : "fx:id=\"valueTextArea\" was not injected: check your FXML file 'HashForm.fxml'.";
        assert hashKeyField != null : "fx:id=\"hashKey\" was not injected: check your FXML file 'HashForm.fxml'.";

        keyTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                keyTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                keyTextField.setStyle("-fx-border-width: 0px;");
            }
        });

        hashKeyField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                hashKeyField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                hashKeyField.setStyle("-fx-border-width: 0px;");
            }
        });

        valueTextArea.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                valueTextArea.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                valueTextArea.setStyle("-fx-border-width: 0px;");
            }
        });
    }
}
