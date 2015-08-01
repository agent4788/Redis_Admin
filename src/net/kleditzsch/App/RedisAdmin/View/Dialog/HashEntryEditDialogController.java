package net.kleditzsch.App.RedisAdmin.View.Dialog;
/**
 * Created by oliver on 01.08.15.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.kleditzsch.Ui.UiDialogHelper;

public class HashEntryEditDialogController {

    protected static String hashKey = "";

    protected static String value = "";

    protected static boolean isSaveButtonClicked = false;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="cancleButton"
    private Button cancleButton; // Value injected by FXMLLoader

    @FXML // fx:id="hashKeyTextField"
    private TextField hashKeyTextField; // Value injected by FXMLLoader

    @FXML // fx:id="valueTextField"
    private TextArea valueTextField; // Value injected by FXMLLoader

    @FXML
    void clickCancleButton(ActionEvent event) {

        Stage stage = (Stage) cancleButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickSaveButton(ActionEvent event) {

        String key = hashKeyTextField.getText();
        String value = valueTextField.getText();

        if(key != null && !key.isEmpty()) {

            HashEntryEditDialogController.isSaveButtonClicked = true;
            HashEntryEditDialogController.hashKey = key;
            HashEntryEditDialogController.value = value;
        } else {

            UiDialogHelper.showErrorDialog("Fehlerhafte Eingaben", null, "Fehlerhafte Eingaben im Formular");
            return;
        }

        Stage stage = (Stage) cancleButton.getScene().getWindow();
        stage.close();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert cancleButton != null : "fx:id=\"cancleButton\" was not injected: check your FXML file 'HashEntryEditDialog.fxml'.";
        assert hashKeyTextField != null : "fx:id=\"hashKeyTextField\" was not injected: check your FXML file 'HashEntryEditDialog.fxml'.";
        assert valueTextField != null : "fx:id=\"valueTextField\" was not injected: check your FXML file 'HashEntryEditDialog.fxml'.";

        hashKeyTextField.setText(HashEntryEditDialogController.hashKey);
        valueTextField.setText(HashEntryEditDialogController.value);
        HashEntryEditDialogController.isSaveButtonClicked = false;

        hashKeyTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                hashKeyTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                hashKeyTextField.setStyle("-fx-border-width: 0px;");
            }
        });
    }

    public static String getHashKey() {

        return hashKey;
    }

    public static void setHashKey(String hashKey) {

        HashEntryEditDialogController.hashKey = hashKey;
    }

    public static String getValue() {

        return value;
    }

    public static void setValue(String value) {

        HashEntryEditDialogController.value = value;
    }

    public static boolean isSaveButtonClicked() {

        return isSaveButtonClicked;
    }
}