package net.kleditzsch.App.RedisAdmin.Backup.Data.Entrys;

import java.lang.String;

/**
 * Created by oliver on 16.08.15.
 */
public class HashEntry {

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
}
