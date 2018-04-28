package net.kleditzsch.App.RedisAdmin.View.Dialog.Hash;
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

    protected String hashKey = "";

    protected String value = "";

    protected boolean isSaveButtonClicked = false;

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

            this.isSaveButtonClicked = true;
            this.hashKey = key;
            this.value = value;
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

        hashKeyTextField.setText(this.hashKey);
        valueTextField.setText(this.value);
        this.isSaveButtonClicked = false;

        hashKeyTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                hashKeyTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                hashKeyTextField.setStyle("-fx-border-width: 0px;");
            }
        });
    }

    public String getHashKey() {

        return hashKey;
    }

    public void setHashKey(String hashKey) {

        this.hashKey = hashKey;
        this.hashKeyTextField.setText(this.hashKey);
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
        this.valueTextField.setText(this.value);
    }

    public boolean isSaveButtonClicked() {

        return isSaveButtonClicked;
    }
}