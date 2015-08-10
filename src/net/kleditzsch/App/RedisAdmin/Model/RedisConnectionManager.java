package net.kleditzsch.App.RedisAdmin.Model;

import net.kleditzsch.Ui.UiDialogHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * Created by oliver on 19.07.15.
 */
public class RedisConnectionManager {

    protected Jedis currentConnection = null;

    protected String keyDelimiter = ":";

    protected int dbIndex = 0;

    protected String message = "";

    protected RedisConnectionList redisConnectionList = null;

    private static RedisConnectionManager ourInstance = new RedisConnectionManager();

    public static RedisConnectionManager getInstance() {

        return ourInstance;
    }

    private RedisConnectionManager() {

        Path configurationFile = Paths.get(System.getProperty("user.home"), ".RedisAdmin", "connections.xml");
        if(Files.exists(configurationFile)) {

            //XML Datei lesen
            redisConnectionList = JAXB.unmarshal(configurationFile.toUri(), RedisConnectionList.class);
        } else {

            //Default einstellungen initalisieren
            RedisConnection defaultConnection = new RedisConnection();
            redisConnectionList = new RedisConnectionList();
            redisConnectionList.add(defaultConnection);

            //XML Datei erstellen
            try {

                //Attribute
                Set<PosixFilePermission> permsDir = PosixFilePermissions.fromString("rwxrwxrwx");
                FileAttribute<Set<PosixFilePermission>> attrDir = PosixFilePermissions.asFileAttribute(permsDir);

                Set<PosixFilePermission> permsFile = PosixFilePermissions.fromString("rw-rw-rw-");
                FileAttribute<Set<PosixFilePermission>> attrFile = PosixFilePermissions.asFileAttribute(permsFile);

                //Ordner und Datei erstellen
                Files.createDirectories(configurationFile.getParent(), attrDir);
                Files.createFile(configurationFile, attrFile);

                //XML Datei erstellen
                JAXB.marshal(redisConnectionList, configurationFile.toFile());
            } catch (IOException e) {

                UiDialogHelper.showErrorDialog("Fehler", "Die Einstellungen konnten nicht gespeichert werden", e.getLocalizedMessage());
                System.exit(1);
            }
        }
    }

    public boolean switchConnection(RedisConnection rc) {

        //alte Verbindung trennen
        if(currentConnection != null && currentConnection.isConnected()) {

            currentConnection.close();
        }

        //neue Verbindung aufbauen
        try {

            currentConnection = new Jedis(rc.getHost(), rc.getPort(), rc.getTimeout());
            currentConnection.select(rc.getDatabase());
            keyDelimiter = rc.getDelimiter();
            dbIndex = rc.getDatabase();
            return true;
        } catch (JedisConnectionException ex) {

            message = ex.getLocalizedMessage();
            return false;
        }
    }

    public Jedis getConnection() {

        if(currentConnection == null) {

            currentConnection = new Jedis("localhost", 6379, 1000);
            currentConnection.select(dbIndex);
        }
        return currentConnection;
    }

    public void saveConnectionList() {

        if(redisConnectionList != null) {

            //XML Datei speichern
            Path configurationFile = Paths.get(System.getProperty("user.home"), ".RedisAdmin", "connections.xml");
            if(!Files.exists(configurationFile)) {

                //Datei existiert noch nicht
                try {

                    //Attribute
                    Set<PosixFilePermission> permsDir = PosixFilePermissions.fromString("rwxrwxrwx");
                    FileAttribute<Set<PosixFilePermission>> attrDir = PosixFilePermissions.asFileAttribute(permsDir);

                    Set<PosixFilePermission> permsFile = PosixFilePermissions.fromString("rw-rw-rw-");
                    FileAttribute<Set<PosixFilePermission>> attrFile = PosixFilePermissions.asFileAttribute(permsFile);

                    //Ordner und Datei erstellen
                    Files.createDirectories(configurationFile.getParent(), attrDir);
                    Files.createFile(configurationFile, attrFile);

                } catch (IOException e) {

                    UiDialogHelper.showErrorDialog("Fehler", "Die Einstellungen konnten nicht gespeichert werden", e.getLocalizedMessage());
                    System.exit(1);
                }
            }
            JAXB.marshal(redisConnectionList, configurationFile.toFile());
        }
    }

    public RedisConnectionList getConnectionsList() {

        return redisConnectionList;
    }

    public String getCurrentConnectedHost() {

        return "localhost";
    }

    public int getCurrentConnectedPort() {

        return 6379;
    }

    public int getCurrentConnectedDatabase() {

        return dbIndex;
    }

    public String getKeyDelimiter() {

        return keyDelimiter;
    }

    public int getDbIndex() {

        return dbIndex;
    }

    public String getMessage() {

        return message;
    }
}
