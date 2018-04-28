package net.kleditzsch.App.RedisAdmin.View.Dialog.Zset;
/**
 * Created by oliver on 02.08.15.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.kleditzsch.Ui.UiDialogHelper;

public class ZSetEditScoreDialogController {

    protected Double score = 0.0;

    protected boolean isSaveButtonClicked = false;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="cancleButton"
    private Button cancleButton; // Value injected by FXMLLoader

    @FXML // fx:id="scoreField"
    private TextField scoreField; // Value injected by FXMLLoader

    @FXML
    void clickCancleButton(ActionEvent event) {

        Stage stage = (Stage) cancleButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickSaveButton(ActionEvent event) {

        String scoreValue = scoreField.getText();

        Double score = 0.0;
        try {

            score = Double.parseDouble(scoreValue);
        } catch (NumberFormatException ex) {

            scoreField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            UiDialogHelper.showErrorDialog("Fehlerhafte Eingaben", null, "Fehlerhafte Eingaben im Formular");
            return;
        }

        //Daten uebernehmen
        this.isSaveButtonClicked = true;
        this.score = score;

        Stage stage = (Stage) cancleButton.getScene().getWindow();
        stage.close();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert scoreField != null : "fx:id=\"scoreField\" was not injected: check your FXML file 'ZSetEditScoreDialog.fxml'.";

        scoreField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                scoreField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                try {

                    Double.parseDouble(newValue);
                    scoreField.setStyle("-fx-border-width: 0px;");
                } catch(NumberFormatException ex) {

                    scoreField.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                }
            }
        });
    }

    public Double getScore() {

        return score;
    }

    public void setScore(Double score) {

        this.score = score;
        scoreField.setText(Double.toString(this.score));
    }

    public boolean isSaveButtonClicked() {

        return isSaveButtonClicked;
    }
}