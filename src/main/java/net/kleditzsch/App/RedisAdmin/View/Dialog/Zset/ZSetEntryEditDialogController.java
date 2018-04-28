package net.kleditzsch.App.RedisAdmin.View.Dialog.Zset;
/**
 * Created by oliver on 02.08.15.
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

public class ZSetEntryEditDialogController {

    protected Double score = 0.0;

    protected String value = "";

    protected boolean isSaveButtonClicked = false;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="cancleButton"
    private Button cancleButton; // Value injected by FXMLLoader

    @FXML // fx:id="scoreTextField"
    private TextField scoreTextField; // Value injected by FXMLLoader

    @FXML // fx:id="valueTextField"
    private TextArea valueTextField; // Value injected by FXMLLoader

    @FXML
    void clickCancleButton(ActionEvent event) {

        Stage stage = (Stage) cancleButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickSaveButton(ActionEvent event) {

        String scoreValue = scoreTextField.getText();
        String value = valueTextField.getText();

        boolean error = false;
        if(value == null && value.isEmpty()) {

            error = true;
            valueTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        }

        Double score = 0.0;
        try {

            score = Double.parseDouble(scoreValue);
        } catch (NumberFormatException ex) {

            error = true;
            scoreTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        }

        if(error == true) {

            UiDialogHelper.showErrorDialog("Fehlerhafte Eingaben", null, "Fehlerhafte Eingaben im Formular");
            return;
        }

        //Daten uebernehmen
        this.isSaveButtonClicked = true;
        this.value = value;
        this.score = score;

        Stage stage = (Stage) cancleButton.getScene().getWindow();
        stage.close();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert cancleButton != null : "fx:id=\"cancleButton\" was not injected: check your FXML file 'ZSetEntryEditDialog.fxml'.";
        assert scoreTextField != null : "fx:id=\"scoreTextField\" was not injected: check your FXML file 'ZSetEntryEditDialog.fxml'.";
        assert valueTextField != null : "fx:id=\"valueTextField\" was not injected: check your FXML file 'ZSetEntryEditDialog.fxml'.";

        scoreTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                scoreTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                try {

                    Double.parseDouble(newValue);
                    scoreTextField.setStyle("-fx-border-width: 0px;");
                } catch(NumberFormatException ex) {

                    scoreTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                }
            }
        });

        valueTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                valueTextField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                valueTextField.setStyle("-fx-border-width: 0px;");
            }
        });
    }

    public Double getScore() {

        return score;
    }

    public void setScore(Double score) {

        this.score = score;
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