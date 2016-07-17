package net.kleditzsch.App.RedisAdmin.Controller.Backup.Data;

import net.kleditzsch.App.RedisAdmin.Controller.Backup.Data.Entrys.ListEntry;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver on 16.08.15.
 */
public class RedisList extends RedisDataType {

    private String key = "";

    private long ttl = 0;

    private List<ListEntry> entrys = new ArrayList<>();

    public RedisList() {

    }

    public RedisList(String key) {

        this.key = key;
    }

    public RedisList(String key, List<ListEntry> entrys) {

        this.key = key;
        this.entrys = entrys;
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

    public void add(ListEntry entry) {

        entrys.add(entry);
    }

    public ListEntry getEntry(int index) {

        return entrys.get(index);
    }

    public int size() {

        return entrys.size();
    }

    @XmlElement( name = "listEntry")
    public List<ListEntry> getEntrys() {

        return entrys;
    }

    public void setEntrys(List<ListEntry> entrys) {

        this.entrys = entrys;
    }
}
