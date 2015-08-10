package net.kleditzsch.App.RedisAdmin.View;

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
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnection;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionList;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.App.RedisAdmin.View.Dialog.SettingsDialogController;
import net.kleditzsch.App.RedisAdmin.View.TypePanes.*;
import net.kleditzsch.Ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RedisAdminController {

    protected static String currentKey = "";

    protected static RedisAdminController rac = null;

    protected DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="connectionChooser"
    private ComboBox<String> connectionChooser; // Value injected by FXMLLoader

    @FXML // fx:id="keyTree"
    private TreeView<String> keyTree; // Value injected by FXMLLoader

    @FXML // fx:id="keyView"
    private BorderPane keyView; // Value injected by FXMLLoader

    @FXML // fx:id="deleteTreeContextMenuItem"
    private MenuItem deleteTreeContextMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="logList"
    private ListView<String> logList; // Value injected by FXMLLoader

    @FXML
    void clickAboutMenuItem(ActionEvent event) {

    }

    @FXML
    void clickExitMenuItem(ActionEvent event) {

        System.exit(0);
    }

    @FXML
    void clickSettingsMenuItem(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dialog/SettingsDialog.fxml"));
            Parent root = loader.load();
            SettingsDialogController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(root, 800, 600);
            dialog.setScene(scene);
            dialog.setTitle("Einstellungen");
            dialog.showAndWait();

            //Auswahlöliste der Verbindungen initalisieren
            RedisConnectionList redisConnectionList = RedisConnectionManager.getInstance().getConnectionsList();
            connectionChooser.getItems().clear();
            for(RedisConnection rc : redisConnectionList.getRedisConnection()) {

                connectionChooser.getItems().add(rc.getName());
            }
            connectionChooser.getSelectionModel().selectFirst();
        } catch(IOException ex) {

            UiDialogHelper.showErrorDialog("Fehler", null, "Die FXML Datei \"SettingsDialog.fxml\" konnte nicht geladen werden");
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert connectionChooser != null : "fx:id=\"connectionChooser\" was not injected: check your FXML file 'RedisAdmin.fxml'.";
        assert keyTree != null : "fx:id=\"keyTree\" was not injected: check your FXML file 'RedisAdmin.fxml'.";

        //Referenz auf eigenes objekt speichern
        rac = this;

        //Vebindungs Log schreiben
        String host = RedisConnectionManager.getInstance().getCurrentConnectedHost();
        int port = RedisConnectionManager.getInstance().getCurrentConnectedPort();
        int dbIndex = RedisConnectionManager.getInstance().getCurrentConnectedDatabase();
        this.addLogEntry("Verbindung mit " + host + ":" + port + " hergestellt, Datenbank " + dbIndex + " selektiert");

        //Change Listener anbinden
        connectionChooser.getSelectionModel().selectedIndexProperty().addListener((ov, oldIndex, newIndex) -> {

            //Verbindung aufbauen
            if(newIndex.intValue() >= 0) {

                RedisConnection rc = RedisConnectionManager.getInstance().getConnectionsList().get(newIndex.intValue());
                if(!RedisConnectionManager.getInstance().switchConnection(rc)) {

                    UiDialogHelper.showErrorDialog("Fehler", null, "Verbindung zu \"" + rc.getName() + "\" nicht möglich");
                    return;
                }

                //Baumaktualisieren
                keyTree.setRoot(KeyTreeViewModel.getInstance().getKeyList());
            }
        });

        //Auswahlöliste der Verbindungen initalisieren
        RedisConnectionList redisConnectionList = RedisConnectionManager.getInstance().getConnectionsList();
        connectionChooser.getItems().clear();
        for(RedisConnection rc : redisConnectionList.getRedisConnection()) {

            connectionChooser.getItems().add(rc.getName());
        }
        connectionChooser.getSelectionModel().selectFirst();

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

                        FXMLLoader loader = null;
                        Parent root = null;
                        RedisAdminController.setCurrentKey(key);
                        String type = db.type(key);
                        switch (type) {

                            case "string":

                                loader = new FXMLLoader(getClass().getResource("TypePanes/StringPane.fxml"));
                                root = loader.load();

                                //Key Setzen
                                StringPaneController controller1 = loader.getController();
                                controller1.setKey(key);
                                controller1.init();;
                                break;
                            case "hash":

                                loader = new FXMLLoader(getClass().getResource("TypePanes/HashPane.fxml"));
                                root = loader.load();

                                //Key Setzen
                                HashPaneController controller2 = loader.getController();
                                controller2.setKey(key);
                                controller2.init();
                                break;
                            case "list":

                                loader = new FXMLLoader(getClass().getResource("TypePanes/ListPane.fxml"));
                                root = loader.load();

                                //Key Setzen
                                ListPaneController controller3 = loader.getController();
                                controller3.setKey(key);
                                controller3.init();
                                break;
                            case "set":

                                loader = new FXMLLoader(getClass().getResource("TypePanes/SetPane.fxml"));
                                root = loader.load();

                                //Key Setzen
                                SetPaneController controller4 = loader.getController();
                                controller4.setKey(key);
                                controller4.init();
                                break;
                            case "zset":

                                loader = new FXMLLoader(getClass().getResource("TypePanes/ZsetPane.fxml"));
                                root = loader.load();

                                //Key Setzen
                                ZsetPaneController controller5 = loader.getController();
                                controller5.setKey(key);
                                controller5.init();
                                break;
                        }

                        if (root != null) {

                            keyView.setCenter(root);
                        }

                        //Shluessel setzen
                    } catch (IOException e) {

                        UiDialogHelper.showErrorDialog("Fehler", null, "Eine FXML Datei konnte nicht gelesen werden");
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

                //Log Eintrag schreiben
                this.addLogEntry("Schlüssel \"" + key + "\" gelöscht");
            }
        }
    }

    @FXML
    void clickAddKeyMenuItem(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("Dialog/AddKeyView/AddKeyDialog.fxml"));

        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(root, 500, 500);
        dialog.setScene(scene);
        dialog.setTitle("neuer Schlüssel");
        dialog.showAndWait();
    }

    public void addLogEntry(String content) {

        logList.getItems().add(LocalTime.now().format(format) + ": " + content);
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