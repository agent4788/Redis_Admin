package net.kleditzsch.App.RedisAdmin.View.Dialog;
/**
 * Created by oliver on 14.08.15.
 */

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import net.kleditzsch.App.RedisAdmin.View.RedisAdmin;
import net.kleditzsch.Ui.UiDialogHelper;

public class AboutDialogController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="versionLabel"
    private Label versionLabel; // Value injected by FXMLLoader

    @FXML // fx:id="closeButton"
    private Button cancleButton; // Value injected by FXMLLoader

    @FXML
    void clickCloseButton(ActionEvent event) {

        Stage stage = (Stage) cancleButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickLinkJedisLibaryPage(ActionEvent event) {

        try {

            Desktop.getDesktop().browse(new URI("https://github.com/agent4788/Redis_Admin"));

        } catch (URISyntaxException | IOException ex) {

            UiDialogHelper.showExceptionDialog(ex);
        }
    }

    @FXML
    void clickLinkProjectPage(ActionEvent event) {

        try {

            Desktop.getDesktop().browse(new URI("https://github.com/xetorthio/jedis"));

        } catch (URISyntaxException | IOException ex) {

            UiDialogHelper.showExceptionDialog(ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert versionLabel != null : "fx:id=\"versionLabel\" was not injected: check your FXML file 'AboutDialog.fxml'.";
        assert cancleButton != null : "fx:id=\"closeButton\" was not injected: check your FXML file 'AboutDialog.fxml'.";

        versionLabel.setText(RedisAdmin.VERSION);
    }
}
