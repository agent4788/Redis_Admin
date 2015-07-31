package net.kleditzsch.app.RedisAdmin.view.AddKeyView;
/**
 * Created by oliver on 29.07.15.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import net.kleditzsch.app.RedisAdmin.model.RedisConnectionManager;
import net.kleditzsch.app.RedisAdmin.view.RedisAdminController;
import net.kleditzsch.ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

public class StringFormController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="keyTextField"
    private TextField keyTextField; // Value injected by FXMLLoader

    @FXML // fx:id="valueTextArea"
    private TextArea valueTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="encoding"
    private ToggleGroup encoding; // Value injected by FXMLLoader

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
        String encoding = ((RadioButton) this.encoding.getSelectedToggle()).getText();

        //validieren
        boolean error = false;

        //Schluessel
        if(key == null || key.isEmpty()) {

            keyTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
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

        //Format auswaehlen
        if(encoding.equals("Zahl")) {

            //als Zahl
            try {

                int intValue = Integer.parseInt(value);
                if(db.incrBy(key, intValue) != intValue) {

                    UiDialogHelper.showErrorDialog("Fehler", null, "Der Schlüssel \"" + key + "\" konnte nicht erstellt werden");
                    return;
                }
            } catch (NumberFormatException e) {

                UiDialogHelper.showErrorDialog("Fehler", null, value + " ist keine Zahl");
                return;
            }
        } else {

            //Als Text
            System.out.println(db.set(key, value));
            if(!db.set(key, value).equals("OK")) {

                UiDialogHelper.showErrorDialog("Fehler", null, "Der Schlüssel \"" + key + "\" konnte nicht erstellt werden");
                return;
            }
        }

        //Fenster schließen
        Stage stage = (Stage) keyTextField.getScene().getWindow();
        stage.close();

        //Tree zum neu Laden anstosen
        RedisAdminController.getInstance().clickReloadMenuItem(new ActionEvent());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert keyTextField != null : "fx:id=\"keyTextField\" was not injected: check your FXML file 'StringForm.fxml'.";
        assert valueTextArea != null : "fx:id=\"valueTextArea\" was not injected: check your FXML file 'StringForm.fxml'.";
        assert encoding != null : "fx:id=\"encoding\" was not injected: check your FXML file 'StringForm.fxml'.";

        keyTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                keyTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                keyTextField.setStyle("-fx-border-width: 0px;");
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
