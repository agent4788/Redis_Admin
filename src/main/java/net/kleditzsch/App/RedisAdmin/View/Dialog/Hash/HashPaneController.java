package net.kleditzsch.App.RedisAdmin.View.Dialog.Hash;
/**
 * Created by oliver on 21.07.15.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.kleditzsch.App.RedisAdmin.Controller.Backup.Data.Entrys.HashEntry;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.App.RedisAdmin.View.RedisAdmin;
import net.kleditzsch.App.RedisAdmin.View.Dialog.RedisAdmin.RedisAdminController;
import net.kleditzsch.Ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class HashPaneController {


    protected String key = "";

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="typeLabel"
    private Label typeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="ttlLabel"
    private Label ttlLabel; // Value injected by FXMLLoader

    @FXML // fx:id="encodingLabel"
    private Label encodingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="sizeLabel"
    private Label sizeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="keyLabel"
    private Label keyLabel; // Value injected by FXMLLoader

    @FXML // fx:id="hashTable"
    private TableView<HashEntry> hashTable; // Value injected by FXMLLoader

    @FXML // fx:id="keyColum"
    private TableColumn<HashEntry, String> keyColumn; // Value injected by FXMLLoader

    @FXML // fx:id="valueColum"
    private TableColumn<HashEntry, String> valueColumn; // Value injected by FXMLLoader

    @FXML // fx:id="contextMenu"
    private ContextMenu contextMenu; // Value injected by FXMLLoader

    public void init() {

        loadHashData();
    }

    protected void loadHashData() {

        //Breite der Message Zeile ist immer der zur verfuegung stehende restliche Raum
        this.valueColumn.prefWidthProperty().bind(this.hashTable.widthProperty().subtract(keyColumn.getWidth() + 2));

        //Eigenschaften der Log Eintraege an die Tabellenzeilen binden
        this.keyColumn.setCellValueFactory(
                new PropertyValueFactory<>("key")
        );
        this.valueColumn.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );

        //Daten ermitteln und setzen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //Schluessel
        String key = this.getKey();

        //Daten der Tabelle uebergeben
        hashTable.getItems().clear();
        Map<String, String> value = db.hgetAll(key);
        for(String hashKey : value.keySet()) {

            hashTable.getItems().add(new HashEntry(hashKey, value.get(hashKey)));
        }

        //Schluessel
        keyLabel.setText(key);

        //Typ
        typeLabel.setText("Hash");

        //TTL
        long ttl = db.ttl(key);
        ttlLabel.setText((ttl == -1 ? "keine" : Long.toString(ttl) + " Sekunden"));

        //Encoding
        encodingLabel.setText(db.objectEncoding(key));

        //Size
        sizeLabel.setText(Integer.toString(value.size()) + " Einträge");
    }

    @FXML
    void clickDeleteButton(ActionEvent event) {

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        if(key != null && key != "" && db.exists(key)) {

            //Sicherheitsabfrage
            if(UiDialogHelper.showConfirmDialog("Schlüssel löschen?", key, "willst du den Schlüssel wirklich löschen?")) {

                //loeschen
                db.del(key);

                //Tree aktualisieren
                RedisAdminController.getInstance().clickReloadMenuItem(new ActionEvent());

                //Log Eintrag schreiben
                RedisAdminController.getInstance().addLogEntry("Schlüssel \"" + key + "\" gelöscht");

                //Hash Daten erneuern
                loadHashData();
            }
        }
    }

    @FXML
    void clickRenameButton(ActionEvent event) {

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        String newKey = UiDialogHelper.showTextInputDialog("Schlüssel umbenennen", key, "neuer Schlüssel: ", key);
        if(newKey != null && !newKey.isEmpty()) {

            //umbenennen
            if(db.rename(key, newKey).equals("OK")) {

                RedisAdminController.getInstance().clickReloadMenuItem(new ActionEvent());

                //Log Eintrag schreiben
                RedisAdminController.getInstance().addLogEntry("Schlüssel \"" + key + "\" in \"" + newKey + "\" umbenannt");

                //Hash Daten erneuern
                loadHashData();
            } else {

                UiDialogHelper.showErrorDialog("Fehler", key, "Der Schlüssel konnte nicht umbenannt werden");
            }
        }
    }

    @FXML
    void clickAddMenuItem(ActionEvent event) {

        //Kontextmenü schließen
        contextMenu.hide();

        //Selektierter Eintrag
        HashEntry entry = hashTable.getSelectionModel().getSelectedItem();

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //FXML Laden
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HashEntryEditDialog.fxml"));
            Parent root = loader.load();
            HashEntryEditDialogController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(RedisAdmin.getPrimaryStage());
            Scene scene = new Scene(root, 500, 400);
            dialog.getIcons().add(new Image(HashPaneController.class.getResourceAsStream("/icon/add.png")));
            dialog.setScene(scene);
            dialog.setTitle("Hash Eintrag bearbeiten");
            dialog.showAndWait();

            if(controller.isSaveButtonClicked()) {

                //neue Daten speichern
                String hashKey = controller.getHashKey();
                String hashValue = controller.getValue();

                db.hset(key, hashKey, hashValue);

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("Eintrag \"" + hashKey + "\" des Hash \"" + key + "\" erstellt");

                //Hash Daten erneuern
                loadHashData();
                return;
            }
        } catch (IOException ex) {

            UiDialogHelper.showErrorDialog("laden einer FXML Datei Fehlgeschlagen", "HashEntryEditDialog.fxml", "konnte nicht geladen werden");
            return;
        }
    }

    @FXML
    void clickDeleteMenuItem(ActionEvent event) {

        //Selektierter Eintrag
        HashEntry entry = hashTable.getSelectionModel().getSelectedItem();

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        if(db.hdel(key, entry.getKey()) == 1) {

            //Log Eintrag
            RedisAdminController.getInstance().addLogEntry("Eintrag \"" + entry.getKey() + "\" des Hash \"" + key + "\" gelöscht");

            //Hash Daten erneuern
            loadHashData();
        } else {

            UiDialogHelper.showErrorDialog("Der Eintrag konnte nicht gelöscht werden", null, key + " -> " + entry.getKey());
        }
    }

    @FXML
    void clickEditMenuItem(ActionEvent event) {

        //Kontextmenü schließen
        contextMenu.hide();

        //Selektierter Eintrag
        HashEntry entry = hashTable.getSelectionModel().getSelectedItem();

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        String value = db.hget(key, entry.getKey());

        //FXML Laden
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/HashEntryEditDialog.fxml"));
            Parent root = loader.load();
            HashEntryEditDialogController controller = loader.getController();

            //Controller vorbereiten
            controller.setHashKey(entry.getKey());
            controller.setValue(value);

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(RedisAdmin.getPrimaryStage());
            Scene scene = new Scene(root, 500, 400);
            dialog.getIcons().add(new Image(HashPaneController.class.getResourceAsStream("/icon/edit.png")));
            dialog.setScene(scene);
            dialog.setTitle("Hash Eintrag bearbeiten");
            dialog.showAndWait();

            //vor dem Schliesen noch speichern
            if(controller.isSaveButtonClicked()) {

                //neue Daten speichern
                String hashKey = controller.getHashKey();
                String hashValue = controller.getValue();

                db.hset(key, hashKey, hashValue);

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("Eintrag \"" + hashKey + "\" des Hash \"" + key + "\" bearbeitet");

                //Hash Daten erneuern
                loadHashData();
                return;
            }
        } catch (IOException ex) {

            UiDialogHelper.showErrorDialog("laden einer FXML Datei Fehlgeschlagen", "HashEntryEditDialog.fxml", "konnte nicht geladen werden");
            return;
        }
    }

    @FXML
    void clickEditTtlButton(ActionEvent event) {

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //aktuelle TTL laden
        Long ttl = db.ttl(key);

        //Dialog anzeigen
        String value = UiDialogHelper.showTextInputDialog("TTL bearbeiten", "-1 um die TTL zu deaktivieren", "TTL:", Long.toString(ttl));

        //Abbruch
        if(value.equals(Long.toString(ttl))) {

            return;
        }

        //Eingabe überpruefen
        try {

            int newTtl = Integer.parseInt(value);

            if(newTtl == -1) {

                //TTL deaktivieren
                db.persist(key);

                //Label anpassen
                ttlLabel.setText("keine");

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("TTL des Hash \"" + key + "\" deaktiviert");
            } else {

                //ablaufzeit setzen
                db.expire(key, newTtl);

                //Label anpassen
                ttlLabel.setText(value + " Sekunden");

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("TTL des Hash \"" + key + "\" auf " + newTtl + " Sekunden gesetzt");
            }
        } catch (NumberFormatException ex) {

            UiDialogHelper.showErrorDialog("Fehler", null, value + " ist keine Zahl");
            return;
        }
    }

    @FXML
    void clickReloadButton(ActionEvent event) {

        loadHashData();
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        assert typeLabel != null : "fx:id=\"typeLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert ttlLabel != null : "fx:id=\"ttlLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert encodingLabel != null : "fx:id=\"encodingLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert sizeLabel != null : "fx:id=\"sizeLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert keyLabel != null : "fx:id=\"keyLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert hashTable != null : "fx:id=\"hashTable\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert keyColumn != null : "fx:id=\"keyColumn\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert valueColumn != null : "fx:id=\"valueColumn\" was not injected: check your FXML file 'HashPane.fxml'.";
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }
}