package net.kleditzsch.App.RedisAdmin.Controller.Backup.Data;

import net.kleditzsch.App.RedisAdmin.Controller.Backup.Data.Entrys.HashEntry;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis Hash Datentyp
 */
public class RedisHash extends RedisDataType {

    private String key = "";

    private long ttl = 0;

    private List<HashEntry> entrys = new ArrayList<>();

    public RedisHash() {

    }

    public RedisHash(String key) {

        this.key = key;
    }

    public RedisHash(String key, List<HashEntry> entrys) {

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

    public void add(HashEntry entry) {

        entrys.add(entry);
    }

    public HashEntry getEntry(int index) {

        return entrys.get(index);
    }

    public int size() {

        return entrys.size();
    }

    @XmlElement( name = "hashEntry")
    public List<HashEntry> getEntrys() {

        return entrys;
    }

    public void setEntrys(List<HashEntry> entrys) {

        this.entrys = entrys;
    }
}
