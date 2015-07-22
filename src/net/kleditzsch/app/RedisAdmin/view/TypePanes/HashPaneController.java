package net.kleditzsch.app.RedisAdmin.view.TypePanes;
/**
 * Created by oliver on 21.07.15.
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.kleditzsch.app.RedisAdmin.model.RedisConnectionManager;
import net.kleditzsch.app.RedisAdmin.view.RedisAdminController;
import redis.clients.jedis.Jedis;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class HashPaneController {

    protected class HashEntry {

        protected String key = "";

        protected String value = "";

        public HashEntry() {

        }

        public HashEntry(String key, String value) {

            this.key = key;
            this.value = value;
        }

        public String getKey() {

            return key;
        }

        public void setKey(String key) {

            this.key = key;
        }

        public String getValue() {

            return value;
        }

        public void setValue(String value) {

            this.value = value;
        }
    };

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

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        assert typeLabel != null : "fx:id=\"typeLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert ttlLabel != null : "fx:id=\"ttlLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert encodingLabel != null : "fx:id=\"encodingLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert sizeLabel != null : "fx:id=\"sizeLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert keyLabel != null : "fx:id=\"keyLabel\" was not injected: check your FXML file 'HashPane.fxml'.";
        assert hashTable != null : "fx:id=\"hashTable\" was not injected: check your FXML file 'HashPane.fxml'.";

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
        String key = RedisAdminController.getCurrentKey();

        //Daten der Tabelle uebergeben
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
        ttlLabel.setText((ttl == -1 ? "keine" : Long.toString(ttl)));

        //Encoding
        encodingLabel.setText(db.objectEncoding(key));

        //Size
        sizeLabel.setText(Integer.toString(value.size()) + " Einträge");
    }
}