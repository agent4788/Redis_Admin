package net.kleditzsch.App.RedisAdmin.View.ServerInfo;
/**
 * Created by oliver on 15.08.15.
 */

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.App.RedisAdmin.View.RedisAdminController;
import net.kleditzsch.Ui.UiDialogHelper;
import net.kleditzsch.Util.FileUtil;
import net.kleditzsch.Util.TimeUtil;
import redis.clients.jedis.Jedis;

public class ServerInfoController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="versionLabel"
    private Label versionLabel; // Value injected by FXMLLoader

    @FXML // fx:id="modusLabel"
    private Label modusLabel; // Value injected by FXMLLoader

    @FXML // fx:id="runtimeLabel"
    private Label runtimeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="configFileLabel"
    private Label configFileLabel; // Value injected by FXMLLoader

    @FXML // fx:id="memoryUsageLabel"
    private Label memoryUsageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="peakMemoryUsageLabel"
    private Label peakMemoryUsageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="lastSaveLabel"
    private Label lastSaveLabel; // Value injected by FXMLLoader

    @FXML // fx:id="saveStateLabel"
    private Label saveStateLabel; // Value injected by FXMLLoader

    @FXML // fx:id="recivedLabel"
    private Label recivedLabel; // Value injected by FXMLLoader

    @FXML // fx:id="sendLabel"
    private Label sendLabel; // Value injected by FXMLLoader

    @FXML // fx:id="databasesList"
    private ListView<String> databasesList; // Value injected by FXMLLoader

    @FXML // fx:id="osLabel"
    private Label osLabel; // Value injected by FXMLLoader

    @FXML
    void clickDumpDatabaseButton(ActionEvent event) {

        Jedis db = RedisConnectionManager.getInstance().getConnection();
        db.bgsave();
        RedisAdminController.getInstance().addLogEntry("Datenbanken von " + RedisConnectionManager.getInstance().getCurrentConnectedHost() + " gspeichert");
        loadServerInfo();
    }

    @FXML
    void clickReloadButton(ActionEvent event) {

        loadServerInfo();
    }

    @FXML
    void showFullServerInfoButton(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("FullServerInfo.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(root, 600, 500);
            dialog.setScene(scene);
            dialog.setTitle("Server Info");
            dialog.showAndWait();
        } catch (IOException ex) {

            UiDialogHelper.showErrorDialog("Fehler", null, "Eine FXML Datei konnte nicht gelesen werden");
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert versionLabel != null : "fx:id=\"versionLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert modusLabel != null : "fx:id=\"modusLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert runtimeLabel != null : "fx:id=\"runtimeLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert configFileLabel != null : "fx:id=\"configFileLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert memoryUsageLabel != null : "fx:id=\"memoryUsageLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert peakMemoryUsageLabel != null : "fx:id=\"peakMemoryUsageLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert lastSaveLabel != null : "fx:id=\"lastSaveLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert saveStateLabel != null : "fx:id=\"saveStateLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert recivedLabel != null : "fx:id=\"recivedLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert sendLabel != null : "fx:id=\"sendLabel\" was not injected: check your FXML file 'ServerInfo.fxml'.";
        assert databasesList != null : "fx:id=\"databasesList\" was not injected: check your FXML file 'ServerInfo.fxml'.";

        loadServerInfo();
    }

    protected void loadServerInfo() {

        Map<String, Map<String, String>> serverInfo = RedisConnectionManager.getInstance().getServerInfoForCurrentConnection();
        String os = serverInfo.get("Server").get("os");
        String version = serverInfo.get("Server").get("redis_version");
        String mode = serverInfo.get("Server").get("redis_mode");
        String uptime = serverInfo.get("Server").get("uptime_in_seconds");
        String configFile = serverInfo.get("Server").get("config_file");

        String memoryUsage = serverInfo.get("Memory").get("used_memory");
        String memoryUsagePeak = serverInfo.get("Memory").get("used_memory_peak");

        String lastSaveTime = serverInfo.get("Persistence").get("rdb_last_save_time");
        String lastSaveState = serverInfo.get("Persistence").get("rdb_last_bgsave_status");

        String received = serverInfo.get("Stats").get("total_net_input_bytes");
        String transmitted = serverInfo.get("Stats").get("total_net_output_bytes");

        //Daten Anzeigen
        osLabel.setText(os);
        versionLabel.setText(version);
        modusLabel.setText(mode);
        runtimeLabel.setText(TimeUtil.formatSeconds(Long.parseLong(uptime), false));
        configFileLabel.setText(configFile);

        memoryUsageLabel.setText(FileUtil.formatFilezizeBinary(Integer.parseInt(memoryUsage)));
        peakMemoryUsageLabel.setText(FileUtil.formatFilezizeBinary(Integer.parseInt(memoryUsagePeak)));

        Instant lastSave = TimeUtil.getInstantOfEpoch(Long.parseLong(lastSaveTime));
        Duration diff = Duration.between(Instant.now(), lastSave);
        lastSaveLabel.setText(TimeUtil.formatDuration(diff, false));
        saveStateLabel.setText(lastSaveState);

        recivedLabel.setText(FileUtil.formatFilezizeBinary(Integer.parseInt(received)));
        sendLabel.setText(FileUtil.formatFilezizeBinary(Integer.parseInt(transmitted)));

        //Datenbanken
        databasesList.getItems().clear();
        Map<String, String> keyspace = serverInfo.get("Keyspace");
        for(String key : keyspace.keySet()) {

            databasesList.getItems().add("DB-" + key.substring(2) + " - " + keyspace.get(key));
        }
    }
}
