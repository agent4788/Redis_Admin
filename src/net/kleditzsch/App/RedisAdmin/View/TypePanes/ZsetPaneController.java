package net.kleditzsch.App.RedisAdmin.View.TypePanes;
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
import net.kleditzsch.App.RedisAdmin.Backup.Data.Entrys.ZSetEntry;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.App.RedisAdmin.View.Dialog.ZSetEditScoreDialogController;
import net.kleditzsch.App.RedisAdmin.View.Dialog.ZSetEntryEditDialogController;
import net.kleditzsch.App.RedisAdmin.View.RedisAdmin;
import net.kleditzsch.App.RedisAdmin.View.RedisAdminController;
import net.kleditzsch.Ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class ZsetPaneController {

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
    private TableView<ZSetEntry> hashTable; // Value injected by FXMLLoader

    @FXML // fx:id="keyColum"
    private TableColumn<ZSetEntry, Double> keyColumn; // Value injected by FXMLLoader

    @FXML // fx:id="valueColum"
    private TableColumn<ZSetEntry, String> valueColumn; // Value injected by FXMLLoader

    @FXML // fx:id="contextMenu"
    private ContextMenu contextMenu; // Value injected by FXMLLoader

    @FXML
    void clickAddMenuItem(ActionEvent event) {

        //Kontextmenü schließen
        contextMenu.hide();

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //FXML Laden
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Dialog/ZSetEntryEditDialog.fxml"));
            Parent root = loader.load();
            ZSetEntryEditDialogController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(RedisAdmin.getPrimaryStage());
            Scene scene = new Scene(root, 500, 400);
            dialog.getIcons().add(new Image(ZsetPaneController.class.getResourceAsStream("./../resource/add.png")));
            dialog.setScene(scene);
            dialog.setTitle("ZSet Eintrag erstellen");
            dialog.showAndWait();

            if(controller.isSaveButtonClicked()) {

                //neue Daten speichern
                Double score = controller.getScore();
                String value = controller.getValue();

                if(db.zadd(key, score, value) == 1) {

                    //Log Eintrag
                    String val = (value.length() < 20 ? value : value.substring(0, 20) + " ...");
                    RedisAdminController.getInstance().addLogEntry("Eintrag \"" + val + "\" des ZSet \"" + key + "\" erstellt");
                } else {

                    //Log Eintrag
                    String val = (value.length() < 20 ? value : value.substring(0, 20) + " ...");
                    RedisAdminController.getInstance().addLogEntry("Eintrag \"" + val + "\" ist bereits im ZSet \"" + key + "\" vorhanden");
                }

                //Hash Daten erneuern
                loadZsetData();
                return;
            }
        } catch (IOException ex) {

            UiDialogHelper.showErrorDialog("laden einer FXML Datei Fehlgeschlagen", "SetEntryEditDialogController.fxml", "konnte nicht geladen werden");
            return;
        }
    }

    @FXML
    void editScoreMenuItem(ActionEvent event) {

        //Kontextmenü schließen
        contextMenu.hide();

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //Selektierten Eintrag abfragen
        ZSetEntry entry = hashTable.getSelectionModel().getSelectedItem();

        //aktuellen score abfragen
        Double currentScore = db.zscore(key, entry.getValue());

        //Eingabedialog oeffnen
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Dialog/ZSetEditScoreDialog.fxml"));
            Parent root = loader.load();
            ZSetEditScoreDialogController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(RedisAdmin.getPrimaryStage());
            Scene scene = new Scene(root, 500, 80);
            dialog.getIcons().add(new Image(ZsetPaneController.class.getResourceAsStream("./../resource/edit.png")));
            dialog.setScene(scene);
            dialog.setTitle("ZSet Eintrag erstellen");
            dialog.showAndWait();

            if(controller.isSaveButtonClicked()) {

                //neue Daten speichern
                Double score = controller.getScore();

                db.zadd(key, score, entry.getValue());

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("Der Score \"" + currentScore + "\" des ZSet \"" + key + "\" wurde in \"" + score + "\" geändert");

                //Hash Daten erneuern
                loadZsetData();
                return;
            }
        } catch (IOException ex) {

            UiDialogHelper.showErrorDialog("laden einer FXML Datei Fehlgeschlagen", "ZSetEditScoreDialog.fxml", "konnte nicht geladen werden");
            return;
        }

    }

    @FXML
    void clickDeleteMenuItem(ActionEvent event) {

        //Schluessel laden
        String key = this.getKey();

        //Datenbankobjekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //Selektierten Eintrag abfragen
        ZSetEntry entry = hashTable.getSelectionModel().getSelectedItem();

        //Eintrag loeschen
        db.srem(key, entry.getValue());

        //Log Eintrag
        String val = (entry.getValue().length() < 20 ? entry.getValue() : entry.getValue().substring(0, 20) + " ...");
        RedisAdminController.getInstance().addLogEntry("Eintrag \"" + val + "\" des ZSet \"" + key + "\" gelöscht");

        //Hash Daten erneuern
        loadZsetData();
        return;
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
                loadZsetData();
            }
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
                RedisAdminController.getInstance().addLogEntry("TTL des ZSet \"" + key + "\" deaktiviert");
            } else {

                //ablaufzeit setzen
                db.expire(key, newTtl);

                //Label anpassen
                ttlLabel.setText(value + " Sekunden");

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("TTL des ZSet \"" + key + "\" auf " + newTtl + " Sekunden gesetzt");
            }
        } catch (NumberFormatException ex) {

            UiDialogHelper.showErrorDialog("Fehler", null, value + " ist keine Zahl");
            return;
        }
    }

    @FXML
    void clickReloadButton(ActionEvent event) {

        loadZsetData();
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
                loadZsetData();
            } else {

                UiDialogHelper.showErrorDialog("Fehler", key, "Der Schlüssel konnte nicht umbenannt werden");
            }
        }
    }

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        assert typeLabel != null : "fx:id=\"typeLabel\" was not injected: check your FXML file 'ZsetPane.fxml'.";
        assert ttlLabel != null : "fx:id=\"ttlLabel\" was not injected: check your FXML file 'ZsetPane.fxml'.";
        assert encodingLabel != null : "fx:id=\"encodingLabel\" was not injected: check your FXML file 'ZsetPane.fxml'.";
        assert sizeLabel != null : "fx:id=\"sizeLabel\" was not injected: check your FXML file 'ZsetPane.fxml'.";
        assert keyLabel != null : "fx:id=\"keyLabel\" was not injected: check your FXML file 'ZsetPane.fxml'.";
        assert hashTable != null : "fx:id=\"hashTable\" was not injected: check your FXML file 'ZsetPane.fxml'.";
        assert keyColumn != null : "fx:id=\"keyColumn\" was not injected: check your FXML file 'ZsetPane.fxml'.";
        assert valueColumn != null : "fx:id=\"valueColumn\" was not injected: check your FXML file 'ZsetPane.fxml'.";

        //Breite der Message Zeile ist immer der zur verfuegung stehende restliche Raum
        this.valueColumn.prefWidthProperty().bind(this.hashTable.widthProperty().subtract(keyColumn.getWidth() + 2));

        //Eigenschaften der Log Eintraege an die Tabellenzeilen binden
        this.keyColumn.setCellValueFactory(
                new PropertyValueFactory<>("key")
        );
        this.valueColumn.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );
    }

    public void init() {

        loadZsetData();
    }

    protected void loadZsetData() {

        //Daten ermitteln und setzen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //Schluessel
        String key = this.getKey();

        //Daten der Tabelle uebergeben
        hashTable.getItems().clear();
        Set<String> value = db.zrange(key, 0L, -1L);
        for(String val : value) {

            hashTable.getItems().add(new ZSetEntry(db.zscore(key, val), val));
        }

        //Schluessel
        keyLabel.setText(key);

        //Typ
        typeLabel.setText("Zset");

        //TTL
        long ttl = db.ttl(key);
        ttlLabel.setText((ttl == -1 ? "keine" : Long.toString(ttl)));

        //Encoding
        encodingLabel.setText(db.objectEncoding(key));

        //Size
        sizeLabel.setText(Integer.toString(value.size()) + " Einträge");
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }
}