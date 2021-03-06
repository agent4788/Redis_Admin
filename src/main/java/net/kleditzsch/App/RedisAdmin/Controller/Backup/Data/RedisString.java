package net.kleditzsch.App.RedisAdmin.Controller.Backup.Data;

/**
 * Created by oliver on 16.08.15.
 */
public class RedisString extends RedisDataType {

    private String key = "";

    private long ttl = 0;

    private String value = "";

    public RedisString() {
    }

    public RedisString(String key) {

        this.key = key;
    }

    public RedisString(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public long getTtl() {

        return ttl;
    }

    public void setTtl(long ttl) {

        this.ttl = ttl;
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }
}
