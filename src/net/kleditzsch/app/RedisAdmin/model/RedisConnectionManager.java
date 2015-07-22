package net.kleditzsch.app.RedisAdmin.model;

import redis.clients.jedis.Jedis;

/**
 * Created by oliver on 19.07.15.
 */
public class RedisConnectionManager {

    protected Jedis currentConnection = null;

    protected String keyDelimiter = ":";

    protected int dbIndex = 0;

    private static RedisConnectionManager ourInstance = new RedisConnectionManager();

    public static RedisConnectionManager getInstance() {

        return ourInstance;
    }

    private RedisConnectionManager() {

    }

    public Jedis getConnection(/* String hash */) {

        if(currentConnection == null) {

            currentConnection = new Jedis("localhost", 6379, 1000);
            currentConnection.select(dbIndex);
        }
        return currentConnection;
    }

    public String getKeyDelimiter() {

        return keyDelimiter;
    }

    public int getDbIndex() {

        return dbIndex;
    }
}
