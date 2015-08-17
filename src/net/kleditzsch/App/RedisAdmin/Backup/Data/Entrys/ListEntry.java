package net.kleditzsch.App.RedisAdmin.Backup.Data.Entrys;

/**
 * Created by oliver on 16.08.15.
 */
public class ListEntry {

    protected long key = 0L;

    protected String value = "";

    public ListEntry() {

    }

    public ListEntry(long key) {
        this.key = key;
    }

    public ListEntry(long key, String value) {

        this.key = key;
        this.value = value;
    }

    public long getKey() {

        return key;
    }

    public void setKey(long key) {

        this.key = key;
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }
}
