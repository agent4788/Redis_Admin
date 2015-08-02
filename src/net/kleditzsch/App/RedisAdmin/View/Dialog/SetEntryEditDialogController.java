package net.kleditzsch.App.RedisAdmin.View.Dialog;
/**
 * Created by oliver on 02.08.15.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import net.kleditzsch.Ui.UiDialogHelper;

public class SetEntryEditDialogController {

    protected String value = "";

    protected boolean isSaveButtonClicked = false;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="valueTextField"
    private TextArea valueTextField; // Value injected by FXMLLoader

    @FXML // fx:id="cancleButton"
    private Button cancleButton; // Value injected by FXMLLoader

    @FXML
    void clickCancleButton(ActionEvent event) {

        Stage stage = (Stage) cancleButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickSaveButton(ActionEvent event) {

        String value = valueTextField.getText();

        if(value != null && !value.isEmpty()) {

            this.isSaveButtonClicked = true;
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
        assert valueTextField != null : "fx:id=\"valueTextField\" was not injected: check your FXML file 'SetEntryEditDialogController.fxml'.";
        assert cancleButton != null : "fx:id=\"cancleButton\" was not injected: check your FXML file 'SetEntryEditDialogController.fxml'.";

        valueTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                valueTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                valueTextField.setStyle("-fx-border-width: 0px;");
            }
        });
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }

    public boolean isSaveButtonClicked() {

        return isSaveButtonClicked;
    }
}