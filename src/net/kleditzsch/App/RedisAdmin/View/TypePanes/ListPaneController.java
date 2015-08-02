package net.kleditzsch.App.RedisAdmin.View.TypePanes;
/**
 * Created by oliver on 21.07.15.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.kleditzsch.App.RedisAdmin.View.Dialog.ListEntryAddDialogController;
import net.kleditzsch.App.RedisAdmin.View.Dialog.ListEntryEditDialogController;
import net.kleditzsch.App.RedisAdmin.View.RedisAdminController;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.Ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListPaneController {

    protected class ListEntry {

        protected long key = 0L;

        protected String value = "";

        public ListEntry() {

        }

        public ListEntry(long key, String value) {

            this.key = key;
            this.value = value;
        }

        public long getKey() {

            return key;
        }

        public void setKey(long key) {

            this.key = key;
        }

        public String getValue() {

            return value;
        }

        public void setValue(String value) {

            this.value = value;
        }
    };

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

    @FXML // fx:id="listTable"
    private TableView<ListEntry> listTable; // Value injected by FXMLLoader

    @FXML // fx:id="keyColum"
    private TableColumn<ListEntry, Long> keyColumn; // Value injected by FXMLLoader

    @FXML // fx:id="valueColum"
    private TableColumn<ListEntry, String> valueColumn; // Value injected by FXMLLoader

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
                loadListData();
            }
        }
    }

    @FXML
    void clickAddMenuItem(ActionEvent event) {

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Dialog/ListEntryAddDialog.fxml"));
            Parent root = loader.load();
            ListEntryAddDialogController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(root, 500, 400);
            dialog.setScene(scene);
            dialog.setTitle("Listen Eintrag erstellen");
            dialog.showAndWait();

            if(controller.isSaveButtonClicked()) {

                String value = controller.getValue();
                int pos = controller.getChoosedListPosition();

                if(pos == 1) {

                    //am Anfang hinzufuegen
                    db.lpush(key, value);
                } else {

                    //am Ende hinzufuegen
                    db.rpush(key, value);
                }

                //Log Eintrag
                String val = (value.length() < 20 ? value : value.substring(0, 20) + " ...");
                RedisAdminController.getInstance().addLogEntry("Eintrag \"" + val + "\" der List \"" + key + "\" erstellt");

                //Hash Daten erneuern
                loadListData();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clickDeleteMenuItem(ActionEvent event) {

        //Selektierter Eintrag
        ListEntry entry = listTable.getSelectionModel().getSelectedItem();

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        if(db.lrem(key, 1, entry.getValue()) == 1) {

            //Log Eintrag
            String val = (entry.getValue().length() < 20 ? entry.getValue() : entry.getValue().substring(0, 20) + " ...");
            RedisAdminController.getInstance().addLogEntry("Eintrag \"" + val + "\" der Liste \"" + key + "\" gelöscht");

            //Hash Daten erneuern
            loadListData();
        } else {

            UiDialogHelper.showErrorDialog("Der Eintrag konnte nicht gelöscht werden", null, key + " -> " + entry.getKey());
        }
    }

    @FXML
    void clickEditMenuItem(ActionEvent event) {

        //Schluessel laden
        String key = this.getKey();

        //Selektierter Eintrag
        ListEntry entry = listTable.getSelectionModel().getSelectedItem();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Dialog/ListEntryEditDialog.fxml"));
            Parent root = loader.load();
            ListEntryEditDialogController controller = loader.getController();

            //Contenten initalisieren
            String content = db.lindex(key, entry.getKey());
            controller.setValue(content);

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(root, 500, 400);
            dialog.setScene(scene);
            dialog.setTitle("Listen Eintrag bearbeiten");
            dialog.showAndWait();

            if(controller.isSaveButtonClicked()) {

                String value = controller.getValue();

                db.lset(key, entry.getKey(), value);

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("Eintrag \"" + entry.getKey() + "\" der List \"" + key + "\" bearbeitet");

                //Hash Daten erneuern
                loadListData();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
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

        //Eingabe überpruefen
        try {

            int newTtl = Integer.parseInt(value);

            if(newTtl == -1) {

                //TTL deaktivieren
                db.persist(key);

                //Label anpassen
                ttlLabel.setText("keine");

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("TTL der List \"" + key + "\" deaktiviert");
            } else {

                //ablaufzeit setzen
                db.expire(key, newTtl);

                //Label anpassen
                ttlLabel.setText(value + " Sekunden");

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("TTL der List \"" + key + "\" auf " + newTtl + " Sekunden gesetzt");
            }
        } catch (NumberFormatException ex) {

            UiDialogHelper.showErrorDialog("Fehler", null, value + " ist keine Zahl");
            return;
        }
    }

    @FXML
    void clickReloadButton(ActionEvent event) {

        loadListData();
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
                loadListData();
            } else {

                UiDialogHelper.showErrorDialog("Fehler", key, "Der Schlüssel konnte nicht umbenannt werden");
            }
        }
    }

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        assert typeLabel != null : "fx:id=\"typeLabel\" was not injected: check your FXML file 'ListPane.fxml'.";
        assert ttlLabel != null : "fx:id=\"ttlLabel\" was not injected: check your FXML file 'ListPane.fxml'.";
        assert encodingLabel != null : "fx:id=\"encodingLabel\" was not injected: check your FXML file 'ListPane.fxml'.";
        assert sizeLabel != null : "fx:id=\"sizeLabel\" was not injected: check your FXML file 'ListPane.fxml'.";
        assert keyLabel != null : "fx:id=\"keyLabel\" was not injected: check your FXML file 'ListPane.fxml'.";
        assert listTable != null : "fx:id=\"listTable\" was not injected: check your FXML file 'ListPane.fxml'.";

        //Breite der Message Zeile ist immer der zur verfuegung stehende restliche Raum
        this.valueColumn.prefWidthProperty().bind(this.listTable.widthProperty().subtract(keyColumn.getWidth() + 2));

        //Eigenschaften der Log Eintraege an die Tabellenzeilen binden
        this.keyColumn.setCellValueFactory(
                new PropertyValueFactory<>("key")
        );
        this.valueColumn.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );
    }

    public void init() {

        loadListData();
    }

    protected void loadListData() {

        //Daten ermitteln und setzen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //Schluessel
        String key = this.getKey();

        //Daten der Tabelle uebergeben
        listTable.getItems().clear();
        Long length = db.llen(key);
        for(long i = 0L; i < length; i++) {

            listTable.getItems().add(new ListEntry(i, db.lindex(key, i)));
        }

        //Schluessel
        keyLabel.setText(key);

        //Typ
        typeLabel.setText("List");

        //TTL
        long ttl = db.ttl(key);
        ttlLabel.setText((ttl == -1 ? "keine" : Long.toString(ttl)));

        //Encoding
        encodingLabel.setText(db.objectEncoding(key));

        //Size
        sizeLabel.setText(Long.toString(length) + " Einträge");
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }
}