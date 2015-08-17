package net.kleditzsch.App.RedisAdmin.Backup.Data.Entrys;

import java.lang.String;

/**
 * Created by oliver on 16.08.15.
 */
public class ZSetEntry {

    protected Double key = 0.0;

    protected String value = "";

    public ZSetEntry() {

    }

    public ZSetEntry(Double key, String value) {

        this.key = key;
        this.value = value;
    }

    public Double getKey() {

        return key;
    }

    public void setKey(Double key) {

        this.key = key;
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }
}
