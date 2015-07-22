package net.kleditzsch.app.RedisAdmin.view;

/**
 * Created by oliver on 19.07.15.
 */

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import net.kleditzsch.app.RedisAdmin.model.RedisConnectionManager;
import net.kleditzsch.ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RedisAdminController {

    protected static String currentKey = "";

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="connectionChooser"
    private ComboBox<?> connectionChooser; // Value injected by FXMLLoader

    @FXML // fx:id="keyTree"
    private TreeView<String> keyTree; // Value injected by FXMLLoader

    @FXML // fx:id="keyView"
    private BorderPane keyView; // Value injected by FXMLLoader

    @FXML
    void clickAboutMenuItem(ActionEvent event) {

    }

    @FXML
    void clickExitMenuItem(ActionEvent event) {

        System.exit(0);
    }

    @FXML
    void clickSettingsMenuItem(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert connectionChooser != null : "fx:id=\"connectionChooser\" was not injected: check your FXML file 'RedisAdmin.fxml'.";
        assert keyTree != null : "fx:id=\"keyTree\" was not injected: check your FXML file 'RedisAdmin.fxml'.";

        //Schluessel einlesen
        keyTree.setRoot(KeyTreeViewModel.getInstance().getKeyList());

        //SelectionListener anmelden
        keyTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {

                KeyTreeViewModel model = KeyTreeViewModel.getInstance();
                String key = model.getKey(newValue);
                if(model.keyExists(key)) {

                    try {

                        Jedis db = RedisConnectionManager.getInstance().getConnection();

                        Parent root = null;
                        RedisAdminController.setCurrentKey(key);
                        String type = db.type(key);
                        switch(type) {

                            case "string":

                                root = FXMLLoader.load(getClass().getResource("TypePanes/StringPane.fxml"));
                                break;
                            case "hash":

                                root = FXMLLoader.load(getClass().getResource("TypePanes/HashPane.fxml"));
                                break;
                            case "list":

                                root = FXMLLoader.load(getClass().getResource("TypePanes/ListPane.fxml"));
                                break;
                            case "set":

                                root = FXMLLoader.load(getClass().getResource("TypePanes/SetPane.fxml"));
                                break;
                            case "zset":

                                root = FXMLLoader.load(getClass().getResource("TypePanes/ZsetPane.fxml"));
                                break;
                        }

                        if(root != null) {

                            keyView.setCenter(root);
                        }
                    } catch (IOException e) {

                        UiDialogHelper.showExceptionDialog(e);
                        //UiDialogHelper.showErrorDialog("Fehler", null, "Eine FXML Datei konnte nicht gelesen werden");
                    }
                }
            }
        });
    }

    public static String getCurrentKey() {

        return RedisAdminController.currentKey;
    }

    protected static void setCurrentKey(String currentKey) {

        RedisAdminController.currentKey = currentKey;
    }
}