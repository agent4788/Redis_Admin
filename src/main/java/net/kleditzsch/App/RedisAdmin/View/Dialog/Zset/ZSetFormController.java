package net.kleditzsch.App.RedisAdmin.View.Dialog.Zset;
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

public class ZSetFormController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="keyTextField"
    private TextField keyTextField; // Value injected by FXMLLoader

    @FXML // fx:id="valueTextArea"
    private TextArea valueTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="scoreField"
    private TextField scoreField; // Value injected by FXMLLoader

    @FXML
    void clickCancleButton(ActionEvent event) {

        Stage stage = (Stage) keyTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickSaveButton(ActionEvent event) {

        //Daten holen
        String key = keyTextField.getText();
        String value = valueTextArea.getText();
        String score = scoreField.getText();
        Double scoreValue = 0.0;

        //validieren
        boolean error = false;

        //Schluessel
        if(key == null || key.isEmpty()) {

            keyTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            error = true;
        }

        //Sortierung
        try {

            if(score != null && !score.isEmpty()) {

                scoreValue = Double.parseDouble(score);
            } else {

                scoreField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                error = true;
            }

        } catch(NumberFormatException ex) {

            scoreField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
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

        //ZSet erstellen
        if(db.zadd(key, scoreValue, value) == 1) {

            //Fenster schließen
            Stage stage = (Stage) keyTextField.getScene().getWindow();
            stage.close();

            //Tree zum neu Laden anstosen
            RedisAdminController.getInstance().clickReloadMenuItem(new ActionEvent());

            //Log Eintrag schreiben
            RedisAdminController.getInstance().addLogEntry("Schlüssel \"" + key + "\" als Typ \"ZSet\" erstellt");
        } else {

            UiDialogHelper.showErrorDialog("Fehler beim erstellen des ZSet", null, "Fehler beim erstellen des ZSet");
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert keyTextField != null : "fx:id=\"keyTextField\" was not injected: check your FXML file 'ZSetForm.fxml'.";
        assert valueTextArea != null : "fx:id=\"valueTextArea\" was not injected: check your FXML file 'ZSetForm.fxml'.";
        assert scoreField != null : "fx:id=\"scoreField\" was not injected: check your FXML file 'ZSetForm.fxml'.";

    }
}