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
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.kleditzsch.app.RedisAdmin.model.RedisConnectionManager;
import net.kleditzsch.ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RedisAdminController {

    protected static String currentKey = "";

    protected static RedisAdminController rac = null;

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

    @FXML // fx:id="deleteTreeContextMenuItem"
    private MenuItem deleteTreeContextMenuItem; // Value injected by FXMLLoader

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

        //Referenz auf eigenes objekt speichern
        rac = this;

        //Schluessel einlesen
        keyTree.setRoot(KeyTreeViewModel.getInstance().getKeyList());

        //SelectionListener anmelden
        keyTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {

                KeyTreeViewModel model = KeyTreeViewModel.getInstance();
                String key = model.getKey(newValue);
                if (key != null && model.keyExists(key)) {

                    //loeschen verfuegbar machen
                    deleteTreeContextMenuItem.setDisable(false);

                    try {

                        Jedis db = RedisConnectionManager.getInstance().getConnection();

                        Parent root = null;
                        RedisAdminController.setCurrentKey(key);
                        String type = db.type(key);
                        switch (type) {

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

                        if (root != null) {

                            keyView.setCenter(root);
                        }
                    } catch (IOException e) {

                        UiDialogHelper.showExceptionDialog(e);
                        //UiDialogHelper.showErrorDialog("Fehler", null, "Eine FXML Datei konnte nicht gelesen werden");
                    }
                } else {

                    //kein existierender Schluessel -> loeschen deaktivieren
                    deleteTreeContextMenuItem.setDisable(true);
                }
            }
        });
    }

    @FXML
    public void clickReloadMenuItem(ActionEvent event) {

        //Schluessel einlesen
        keyTree.setRoot(KeyTreeViewModel.getInstance().getKeyList());
    }

    @FXML
    void clickDeleteTreeItem(ActionEvent event) {

        //Schluessel finden
        String key = KeyTreeViewModel.getInstance().getKey(keyTree.getSelectionModel().getSelectedItem());

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        if(key != null && key != "" && db.exists(key)) {

            //Sicherheitsabfrage
            if(UiDialogHelper.showConfirmDialog("Schlüssel löschen?", key, "willst du den Schlüssel wirklich löschen?")) {

                //loeschen
                db.del(key);

                //Tree aktualisieren
                keyTree.setRoot(KeyTreeViewModel.getInstance().getKeyList());
            }
        }
    }

    @FXML
    void clickAddKeyMenuItem(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("AddKeyView/AddKeyDialog.fxml"));

        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(root, 500, 500);
        dialog.setScene(scene);
        dialog.setTitle("neuer Schlüssel");
        dialog.showAndWait();

    }

    public static String getCurrentKey() {

        return RedisAdminController.currentKey;
    }

    protected static void setCurrentKey(String currentKey) {

        RedisAdminController.currentKey = currentKey;
    }

    public static RedisAdminController getInstance() {

        return rac;
    }
}