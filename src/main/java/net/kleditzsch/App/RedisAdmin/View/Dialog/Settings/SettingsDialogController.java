package net.kleditzsch.App.RedisAdmin.View.Dialog.Settings;
/**
 * Created by oliver on 03.08.15.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnection;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionList;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import net.kleditzsch.Ui.UiDialogHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class SettingsDialogController {

    protected int currentSelectedIndex = 0;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="cancleButton"
    private Button cancleButton; // Value injected by FXMLLoader

    @FXML // fx:id="connectionsList"
    private ListView<String> connectionsList; // Value injected by FXMLLoader

    @FXML // fx:id="connectionName"
    private TextField connectionName; // Value injected by FXMLLoader

    @FXML // fx:id="connectionHost"
    private TextField connectionHost; // Value injected by FXMLLoader

    @FXML // fx:id="connectionPort"
    private TextField connectionPort; // Value injected by FXMLLoader

    @FXML // fx:id="connectionTimeout"
    private TextField connectionTimeout; // Value injected by FXMLLoader

    @FXML // fx:id="connectionPassword"
    private TextField connectionPassword; // Value injected by FXMLLoader

    @FXML // fx:id="connectionDatabase"
    private ChoiceBox<String> connectionDatabase; // Value injected by FXMLLoader

    @FXML // fx:id="connectionIsDefault"
    private CheckBox connectionIsDefault; // Value injected by FXMLLoader

    @FXML // fx:id="connectionTestButton"
    private Button connectionTestButton; // Value injected by FXMLLoader

    @FXML // fx:id="connectionTestProgress"
    private ProgressIndicator connectionTestProgress; // Value injected by FXMLLoader

    @FXML // fx:id="messageLabel"
    private Label messageLabel; // Value injected by FXMLLoader

    @FXML // fx:id="connectionDelimiter"
    private TextField connectionDelimiter; // Value injected by FXMLLoader

    @FXML
    void clickCancleButton(ActionEvent event) {

        Stage stage = (Stage) cancleButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickNewConnectionButton(ActionEvent event) {

        //neues Connection Element
        RedisConnectionList redisConnectionList = RedisConnectionManager.getInstance().getConnectionsList();
        redisConnectionList.add(new RedisConnection());

        //Liste vorbereiten
        connectionsList.getItems().clear();
        for(RedisConnection rc : redisConnectionList.getRedisConnection()) {

            connectionsList.getItems().add(rc.getName());
        }
        connectionsList.getSelectionModel().selectLast();
    }

    @FXML
    void clickConnectionTestButton(ActionEvent event) {

        //Label zuruecksetzen
        messageLabel.setText("");

        //Daten holen
        String host = connectionHost.getText();
        String portValue = connectionPort.getText();
        String timeoutValue = connectionTimeout.getText();
        String password = connectionPassword.getText();
        String database = connectionDatabase.getValue();

        //Validieren
        int port = 0;
        try {

            port = Integer.parseInt(portValue);
        } catch (NumberFormatException ex) {

            messageLabel.setText("Der Port " + portValue + " ist ungültig");
            messageLabel.setTextFill(Color.RED);
        }
        final int validPort = port;

        int timeout = 0;
        try {

            timeout = Integer.parseInt(timeoutValue);
        } catch (NumberFormatException ex) {

            messageLabel.setText("Der Timeout " + timeoutValue + " ist ungültig");
            messageLabel.setTextFill(Color.RED);
        }
        final int validTimeout = timeout;

        //Versuch zu verbinden
        Task<Integer> task = new Task<Integer>() {

            @Override
            protected Integer call() throws Exception {

                try {

                    Jedis connection = new Jedis(host, validPort, validTimeout);
                    if(password != null && !password.isEmpty()) {

                        connection.auth(password);
                    }
                    connection.select(Integer.parseInt(database));
                    String ping = connection.ping();

                    //Verbindung trennen
                    if(connection.isConnected()) {

                        connection.close();
                    }

                    if(ping.equals("PONG")) {

                        return 1;
                    } else {

                        return 0;
                    }
                } catch(JedisConnectionException ex) {

                    return 2;
                }
            }
        };
        task.setOnSucceeded((WorkerStateEvent workerEvent) -> {

            //Anzeige vom Ergebnis vorbereiten
            int result = (Integer) workerEvent.getSource().getValue();
            switch(result) {

                case 0:

                    messageLabel.setText("Die Verbindung konnte nicht hergestellt werden");
                    messageLabel.setTextFill(Color.RED);
                    break;
                case 1:

                    messageLabel.setText("Die Verbindung wurde erfolgreich hergestellt");
                    messageLabel.setTextFill(Color.GREEN);
                    break;
                case 2:

                    messageLabel.setText("Die Verbindung konnte nicht hergestellt werden");
                    messageLabel.setTextFill(Color.RED);
                    break;
            }

            //Ergebnis anzeigen
            connectionTestProgress.setVisible(false);
            messageLabel.setVisible(true);
        });


        connectionTestProgress.setVisible(true);
        messageLabel.setVisible(false);

        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    void clickSaveButton(ActionEvent event) {

        //Daten holen
        String name = connectionName.getText();
        String host = connectionHost.getText();
        String portValue = connectionPort.getText();
        String timeoutValue = connectionTimeout.getText();
        String password = connectionPassword.getText();
        String database = connectionDatabase.getValue();
        String delimiter = connectionDelimiter.getText();
        boolean isDefault = connectionIsDefault.isSelected();

        //Validieren
        boolean error = false;
        if(name == null || name.isEmpty()) {

            connectionName.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            error = true;
        }

        if(host == null || host.isEmpty()) {

            connectionHost.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            error = true;
        }

        int port = 0;
        try {

            port = Integer.parseInt(portValue);
        } catch (NumberFormatException ex) {

            messageLabel.setText("Der Port " + portValue + " ist ungültig");
            messageLabel.setTextFill(Color.RED);
            error = true;
        }

        int timeout = 0;
        try {

            timeout = Integer.parseInt(timeoutValue);
        } catch (NumberFormatException ex) {

            messageLabel.setText("Der Timeout " + timeoutValue + " ist ungültig");
            messageLabel.setTextFill(Color.RED);
            error = true;
        }

        if(delimiter == null || delimiter.isEmpty()) {

            connectionDelimiter.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            error = true;
        }

        if(error == true) {

            UiDialogHelper.showErrorDialog("Fehler", null, "Fehlerhafte Eingaben");
            return;
        }

        //Verbindoungsobjekt holen
        RedisConnectionList redisConnectionList = RedisConnectionManager.getInstance().getConnectionsList();
        RedisConnection redisConnection = redisConnectionList.get(currentSelectedIndex);

        //Daten ans Objekt übergeben
        redisConnection.setName(name);
        redisConnection.setHost(host);
        redisConnection.setPort(port);
        redisConnection.setTimeout(timeout);
        redisConnection.setPassword(password);
        redisConnection.setDatabase(Integer.parseInt(database));
        redisConnection.setDelimiter(delimiter);
        redisConnection.setIsDefaultConnection(isDefault);

        //XML schreiben
        RedisConnectionManager.getInstance().saveConnectionList();

        //Liste vorbereiten
        connectionsList.getItems().clear();
        for(RedisConnection rc : redisConnectionList.getRedisConnection()) {

            connectionsList.getItems().add(rc.getName());
        }
        connectionsList.getSelectionModel().selectLast();
    }

    @FXML
    void clickDeleteConnectionButton(ActionEvent event) {

        //Index ermitteln
        RedisConnectionList redisConnectionList = RedisConnectionManager.getInstance().getConnectionsList();
        RedisConnection redisConnection = redisConnectionList.get(currentSelectedIndex);

        //Sicherheitsabfrage
        if(UiDialogHelper.showConfirmDialog("Verbindung löschen", redisConnection.getName(), "soll die verbundung wirklich gelöscht werden?")) {

            //Verbindung entfernen und XML Datei schreiben
            redisConnectionList.remove(currentSelectedIndex);
            RedisConnectionManager.getInstance().saveConnectionList();

            //ListView updaten
            connectionsList.getItems().remove(currentSelectedIndex);
        }

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert cancleButton != null : "fx:id=\"cancleButton\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionsList != null : "fx:id=\"connectionsList\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionName != null : "fx:id=\"connectionName\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionHost != null : "fx:id=\"connectionHost\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionPort != null : "fx:id=\"connectionPort\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionTimeout != null : "fx:id=\"connectionTimeout\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionPassword != null : "fx:id=\"connectionPassword\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionDatabase != null : "fx:id=\"connectionDatabase\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionIsDefault != null : "fx:id=\"connectionIsDefault\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionTestButton != null : "fx:id=\"connectionTestButton\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionTestProgress != null : "fx:id=\"connectionTestProgress\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'SettingsDialog.fxml'.";
        assert connectionDelimiter != null : "fx:id=\"connectionDelimiter\" was not injected: check your FXML file 'SettingsDialog.fxml'.";

        connectionTestProgress.setVisible(false);
        messageLabel.setVisible(false);

        RedisConnectionList redisConnectionList = RedisConnectionManager.getInstance().getConnectionsList();

        //Listener vorbereiten
        connectionsList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {

            currentSelectedIndex = connectionsList.getSelectionModel().getSelectedIndex();
            if(currentSelectedIndex >= 0) {

                RedisConnection redisConnection = redisConnectionList.get(currentSelectedIndex);

                //Eingabefelder setzen
                connectionName.setText(redisConnection.getName());
                connectionHost.setText(redisConnection.getHost());
                connectionPort.setText(Integer.toString(redisConnection.getPort()));
                connectionTimeout.setText(Integer.toString(redisConnection.getTimeout()));
                connectionPassword.setText(redisConnection.getPassword());
                connectionDelimiter.setText(redisConnection.getDelimiter());
                connectionDatabase.getSelectionModel().select(redisConnection.getDatabase());
                connectionIsDefault.setSelected((redisConnection.isDefaultConnection() == 1));
            }
        });

        //Datenbank Liste vorbereiten
        for(int i = 0; i <= 20; i++) {

            connectionDatabase.getItems().add(Integer.toString(i));
        }

        //Liste vorbereiten
        for(RedisConnection rc : redisConnectionList.getRedisConnection()) {

            connectionsList.getItems().add(rc.getName());
        }
        connectionsList.getSelectionModel().selectFirst();

        //Validierungs Listener
        connectionName.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                connectionName.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                connectionName.setStyle("-fx-border-width: 0px;");
            }
        });

        connectionHost.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                connectionHost.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                connectionHost.setStyle("-fx-border-width: 0px;");
            }
        });

        connectionPort.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                connectionPort.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                try {

                    Integer.parseInt(newValue);
                    connectionPort.setStyle("-fx-border-width: 0px;");
                } catch(NumberFormatException ex) {

                    connectionPort.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                }
            }
        });

        connectionTimeout.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                connectionTimeout.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                try {

                    Integer.parseInt(newValue);
                    connectionTimeout.setStyle("-fx-border-width: 0px;");
                } catch(NumberFormatException ex) {

                    connectionTimeout.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                }
            }
        });

        connectionDelimiter.textProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue == null || newValue.isEmpty()) {

                connectionDelimiter.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            } else {

                connectionDelimiter.setStyle("-fx-border-width: 0px;");
            }
        });
    }
}