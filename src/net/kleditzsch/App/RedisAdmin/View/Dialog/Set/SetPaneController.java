package net.kleditzsch.App.RedisAdmin.View.Dialog.Set;
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
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.App.RedisAdmin.View.RedisAdmin;
import net.kleditzsch.App.RedisAdmin.View.Dialog.RedisAdmin.RedisAdminController;
import net.kleditzsch.Ui.UiDialogHelper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class SetPaneController {

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

    @FXML // fx:id="setList"
    private ListView<String> setList; // Value injected by FXMLLoader

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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/kleditzsch/App/RedisAdmin/View/Dialog/Set/SetEntryEditDialog.fxml"));
            Parent root = loader.load();
            SetEntryEditDialogController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(RedisAdmin.getPrimaryStage());
            Scene scene = new Scene(root, 500, 400);
            dialog.getIcons().add(new Image(SetPaneController.class.getResourceAsStream("/net/kleditzsch/App/RedisAdmin/View/resource/add.png")));
            dialog.setScene(scene);
            dialog.setTitle("Set Eintrag erstellen");
            dialog.showAndWait();

            if(controller.isSaveButtonClicked()) {

                //neue Daten speichern
                String value = controller.getValue();

                if(db.sadd(key, value) == 1) {

                    //Log Eintrag
                    String val = (value.length() < 20 ? value : value.substring(0, 20) + " ...");
                    RedisAdminController.getInstance().addLogEntry("Eintrag \"" + val + "\" des Set \"" + key + "\" erstellt");
                } else {

                    //Log Eintrag
                    String val = (value.length() < 20 ? value : value.substring(0, 20) + " ...");
                    RedisAdminController.getInstance().addLogEntry("Eintrag \"" + val + "\" ist bereits im Set \"" + key + "\" vorhanden");
                }

                //Hash Daten erneuern
                loadSetData();
                return;
            }
        } catch (IOException ex) {

            UiDialogHelper.showErrorDialog("laden einer FXML Datei Fehlgeschlagen", "SetEntryEditDialogController.fxml", "konnte nicht geladen werden");
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
        String value = setList.getSelectionModel().getSelectedItem();

        //Eintrag loeschen
        db.srem(key, value);

        //Log Eintrag
        String val = (value.length() < 20 ? value : value.substring(0, 20) + " ...");
        RedisAdminController.getInstance().addLogEntry("Eintrag \"" + val + "\" des Set \"" + key + "\" gelöscht");

        //Hash Daten erneuern
        loadSetData();
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
                loadSetData();
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
                RedisAdminController.getInstance().addLogEntry("TTL des Set \"" + key + "\" deaktiviert");
            } else {

                //ablaufzeit setzen
                db.expire(key, newTtl);

                //Label anpassen
                ttlLabel.setText(value + " Sekunden");

                //Log Eintrag
                RedisAdminController.getInstance().addLogEntry("TTL des Set \"" + key + "\" auf " + newTtl + " Sekunden gesetzt");
            }
        } catch (NumberFormatException ex) {

            UiDialogHelper.showErrorDialog("Fehler", null, value + " ist keine Zahl");
            return;
        }
    }

    @FXML
    void clickReloadButton(ActionEvent event) {

        loadSetData();
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
                loadSetData();
            } else {

                UiDialogHelper.showErrorDialog("Fehler", key, "Der Schlüssel konnte nicht umbenannt werden");
            }
        }
    }

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        assert typeLabel != null : "fx:id=\"typeLabel\" was not injected: check your FXML file 'SetPane.fxml'.";
        assert ttlLabel != null : "fx:id=\"ttlLabel\" was not injected: check your FXML file 'SetPane.fxml'.";
        assert encodingLabel != null : "fx:id=\"encodingLabel\" was not injected: check your FXML file 'SetPane.fxml'.";
        assert sizeLabel != null : "fx:id=\"sizeLabel\" was not injected: check your FXML file 'SetPane.fxml'.";
        assert keyLabel != null : "fx:id=\"keyLabel\" was not injected: check your FXML file 'SetPane.fxml'.";
        assert setList != null : "fx:id=\"setList\" was not injected: check your FXML file 'SetPane.fxml'.";
    }

    public void init() {

        loadSetData();
    }

    protected void loadSetData() {

        //Daten ermitteln und setzen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //Schluessel
        String key = this.getKey();

        //Daten der Tabelle uebergeben
        setList.getItems().clear();
        Set<String> set = db.smembers(key);
        for(String entry : set) {

            setList.getItems().add(entry);
        }

        //Schluessel
        keyLabel.setText(key);

        //Typ
        typeLabel.setText("Set");

        //TTL
        long ttl = db.ttl(key);
        ttlLabel.setText((ttl == -1 ? "keine" : Long.toString(ttl)));

        //Encoding
        encodingLabel.setText(db.objectEncoding(key));

        //Size
        sizeLabel.setText(Integer.toString(set.size()) + " Einträge");
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }
}