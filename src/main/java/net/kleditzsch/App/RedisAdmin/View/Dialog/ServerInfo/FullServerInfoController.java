package net.kleditzsch.App.RedisAdmin.View.Dialog.ServerInfo;
/**
 * Created by oliver on 16.08.15.
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;

public class FullServerInfoController {

    public class Info {

        private String name = "";
        private String value = "";

        public Info() {
        }

        public Info(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="closeButton"
    private Button closeButton; // Value injected by FXMLLoader

    @FXML // fx:id="dataTable"
    private TableView<Info> dataTable; // Value injected by FXMLLoader

    @FXML // fx:id="keyColumn"
    private TableColumn<Info, String> keyColumn; // Value injected by FXMLLoader

    @FXML // fx:id="valueColumn"
    private TableColumn<Info, String> valueColumn; // Value injected by FXMLLoader

    @FXML
    void clickCloseButton(ActionEvent event) {

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert closeButton != null : "fx:id=\"closeButton\" was not injected: check your FXML file 'FullServerInfo.fxml'.";
        assert dataTable != null : "fx:id=\"dataTable\" was not injected: check your FXML file 'FullServerInfo.fxml'.";

        Map<String, String> serverInfo = RedisConnectionManager.getInstance().getServerInfoForCurrentConnection();
        List<Info> fullInfo = new ArrayList<>();

        for(String key : serverInfo.keySet()) {

            fullInfo.add(new Info(key, serverInfo.get(key)));
        }

        dataTable.getItems().addAll(fullInfo);
        keyColumn.setCellValueFactory(new PropertyValueFactory<Info, String>("name"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<Info, String>("value"));
    }
}