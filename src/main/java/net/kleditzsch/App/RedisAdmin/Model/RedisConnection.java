package net.kleditzsch.App.RedisAdmin.Model;

/**
 * Created by oliver on 03.08.15.
 */
public class RedisConnection {

    protected String name = "Default";

    protected String host = "127.0.0.1";

    protected int port = 6379;

    protected int timeout = 1000;

    protected String password = "";

    protected int database = 0;

    protected String delimiter = ":";

    protected int defaultConnection = 1;

    public RedisConnection() {

    }

    public RedisConnection(String name, String host, int port, int timeout) {

        this.name = name;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public RedisConnection(String name, String host, int port, int timeout, String password, int database) {

        this.name = name;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.password = password;
        this.database = database;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getHost() {

        return host;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public int getPort() {

        return port;
    }

    public void setPort(int port) {

        this.port = port;
    }

    public int getTimeout() {

        return timeout;
    }

    public void setTimeout(int timeout) {

        this.timeout = timeout;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public int getDatabase() {

        return database;
    }

    public void setDatabase(int database) {

        this.database = database;
    }

    public String getDelimiter() {

        return delimiter;
    }

    public void setDelimiter(String delimiter) {

        this.delimiter = delimiter;
    }

    public int getDefaultConnection() {

        return isDefaultConnection();
    }

    public void setDefaultConnection(boolean isDefaultConnection) {

        setIsDefaultConnection(isDefaultConnection);
    }

    public int isDefaultConnection() {

        return defaultConnection;
    }

    public void setIsDefaultConnection(boolean isDefaultConnection) {

        if(isDefaultConnection == true) {

            this.defaultConnection = 1;
        } else {

            this.defaultConnection = 0;
        }
    }
}
