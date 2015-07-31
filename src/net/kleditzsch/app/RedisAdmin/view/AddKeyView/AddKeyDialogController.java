package net.kleditzsch.app.RedisAdmin.view.AddKeyView;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import net.kleditzsch.ui.UiDialogHelper;

public class AddKeyDialogController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="layout"
    private BorderPane layout; // Value injected by FXMLLoader

    @FXML // fx:id="typeChoiceBox"
    private ChoiceBox<String> typeChoiceBox; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert layout != null : "fx:id=\"layout\" was not injected: check your FXML file 'AddKeyDialog.fxml'.";
        assert typeChoiceBox != null : "fx:id=\"typeChoiceBox\" was not injected: check your FXML file 'AddKeyDialog.fxml'.";

        //Auswahelemente Registrieren
        typeChoiceBox.setItems(FXCollections.observableArrayList("String", "Hash", "List", "Set", "ZSet"));

        //default auswahl
        typeChoiceBox.getSelectionModel().selectFirst();
        Parent defaultRoot = null;
        try {

            defaultRoot = FXMLLoader.load(getClass().getResource("StringForm.fxml"));
            layout.setCenter(defaultRoot);
        } catch (IOException e) {

            UiDialogHelper.showErrorDialog("laden einer FXML Datei Fehlgeschlagen", "StringForm.fxml", "konnte nicht geladen werden");
        }

        //Change Listener registrieren
        typeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            Parent root = null;
            String fxmlFile = "";
            try {

                switch (newValue.toString()) {

                    case "String":

                        //String Formular
                        fxmlFile = "StringForm.fxml";
                        root = FXMLLoader.load(getClass().getResource(fxmlFile));
                        layout.setCenter(root);
                        break;
                    case "Hash":

                        //Hash Formular
                        fxmlFile = "HashForm.fxml";
                        root = FXMLLoader.load(getClass().getResource(fxmlFile));
                        layout.setCenter(root);
                        break;
                    case "List":

                        //List Formular
                        fxmlFile = "ListForm.fxml";
                        root = FXMLLoader.load(getClass().getResource(fxmlFile));
                        layout.setCenter(root);
                        break;
                    case "Set":

                        //Set Formular
                        fxmlFile = "SetForm.fxml";
                        root = FXMLLoader.load(getClass().getResource(fxmlFile));
                        layout.setCenter(root);
                    break;
                    case "ZSet":

                        //ZList Formular
                        fxmlFile = "ZSetForm.fxml";
                        root = FXMLLoader.load(getClass().getResource(fxmlFile));
                        layout.setCenter(root);
                        break;
                }
            } catch (IOException e) {

                UiDialogHelper.showErrorDialog("laden einer FXML Datei Fehlgeschlagen", fxmlFile, "konnte nicht geladen werden");
            }
        });

    }
}
