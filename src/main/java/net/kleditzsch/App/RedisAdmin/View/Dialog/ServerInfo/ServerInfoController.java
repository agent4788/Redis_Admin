package net.kleditzsch.App.RedisAdmin.View.Dialog.ServerInfo;
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
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.App.RedisAdmin.View.Dialog.RedisAdmin.RedisAdminController;
import net.kleditzsch.App.RedisAdmin.View.RedisAdmin;
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

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/FullServerInfo.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(root, 600, 500);
            dialog.setScene(scene);
            dialog.setTitle("Server Info");
            dialog.initOwner(RedisAdmin.getPrimaryStage());
            dialog.initModality(Modality.APPLICATION_MODAL);
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

        Map<String, String> serverInfo = RedisConnectionManager.getInstance().getServerInfoForCurrentConnection();

        //OS
        if(serverInfo.containsKey("os")) {

            String os = serverInfo.get("os");
            osLabel.setText(os);
        }

        //Redis Version
        if(serverInfo.containsKey("redis_version")) {

            String version = serverInfo.get("redis_version");
            versionLabel.setText(version);
        }

        //Redis Modus
        if(serverInfo.containsKey("redis_mode")) {

            String mode = serverInfo.get("redis_mode");
            modusLabel.setText(mode);
        } else if(serverInfo.containsKey("role")) {

            String mode = serverInfo.get("role");
            modusLabel.setText(mode);
        }

        //Redis Uptime
        if(serverInfo.containsKey("uptime_in_seconds")) {

            String uptime = serverInfo.get("uptime_in_seconds");
            runtimeLabel.setText(TimeUtil.formatSeconds(Long.parseLong(uptime), false));
        }

        //Redis Uptime
        if(serverInfo.containsKey("config_file")) {

            String configFile = serverInfo.get("config_file");
            configFileLabel.setText(configFile);
        }

        //Redis Config Datei
        if(serverInfo.containsKey("config_file")) {

            String configFile = serverInfo.get("config_file");
            configFileLabel.setText(configFile);
        }

        //Redis Speicherbedarf
        if(serverInfo.containsKey("used_memory")) {

            String memoryUsage = serverInfo.get("used_memory");
            memoryUsageLabel.setText(FileUtil.formatFilezizeBinary(Integer.parseInt(memoryUsage)));
        }

        //Redis Speicherbedarf spitze
        if(serverInfo.containsKey("used_memory_peak")) {

            String memoryUsagePeak = serverInfo.get("used_memory_peak");
            peakMemoryUsageLabel.setText(FileUtil.formatFilezizeBinary(Integer.parseInt(memoryUsagePeak)));
        }

        //Redis letzte Speicherung
        if(serverInfo.containsKey("rdb_last_save_time")) {

            String lastSaveTime = serverInfo.get("rdb_last_save_time");
            Instant lastSave = TimeUtil.getInstantOfEpoch(Long.parseLong(lastSaveTime));
            Duration diff = Duration.between(Instant.now(), lastSave);
            lastSaveLabel.setText(TimeUtil.formatDuration(diff, false));
        }

        //Redis letzter Speicherstatus
        if(serverInfo.containsKey("rdb_last_bgsave_status")) {

            String lastSaveState = serverInfo.get("rdb_last_bgsave_status");
            saveStateLabel.setText(lastSaveState);
        }

        //Redis letzter Speicherstatus
        if(serverInfo.containsKey("total_net_input_bytes")) {

            String received = serverInfo.get("total_net_input_bytes");
            recivedLabel.setText(FileUtil.formatFilezizeBinary(Integer.parseInt(received)));
        }

        //Redis letzter Speicherstatus
        if(serverInfo.containsKey("total_net_output_bytes")) {

            String transmitted = serverInfo.get("total_net_output_bytes");
            sendLabel.setText(FileUtil.formatFilezizeBinary(Integer.parseInt(transmitted)));
        }

        //Datenbanken
        databasesList.getItems().clear();
        Map<String, String> keyspace = RedisConnectionManager.getInstance().getDatabaseListForCurrentConnection();
        for(String key : keyspace.keySet()) {

            databasesList.getItems().add("DB-" + key.substring(2) + " - " + keyspace.get(key));
        }

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                Thread.sleep(30000);
                return null;
            }
        };
        task.setOnSucceeded((WorkerStateEvent event) -> {

           this.loadServerInfo();
        });
        Thread thread = new Thread(task);
        thread.start();
    }
}
