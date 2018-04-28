package net.kleditzsch.App.RedisAdmin.Controller.Backup.Data;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver on 16.08.15.
 */
public class RedisSet extends RedisDataType {

    private String key = "";

    private long ttl = 0;

    private List<String> entrys = new ArrayList<>();

    public RedisSet() {

    }

    public RedisSet(String key) {

        this.key = key;
    }

    public RedisSet(String key, List<String> entrys) {

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

    public void add(String entry) {

        entrys.add(entry);
    }

    public String getEntry(int index) {

        return entrys.get(index);
    }

    public int size() {

        return entrys.size();
    }

    @XmlElement( name = "setEntry")
    public List<String> getEntrys() {

        return entrys;
    }

    public void setEntrys(List<String> entrys) {

        this.entrys = entrys;
    }
}
