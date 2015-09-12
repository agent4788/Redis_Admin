package net.kleditzsch.App.RedisAdmin.Controller.Backup.Data;

import net.kleditzsch.App.RedisAdmin.Controller.Backup.Data.Entrys.ZSetEntry;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver on 16.08.15.
 */
public class RedisZSet extends RedisDataType {

    private String key = "";

    private List<ZSetEntry> entrys = new ArrayList<>();

    public RedisZSet() {

    }

    public RedisZSet(String key) {

        this.key = key;
    }

    public RedisZSet(String key, List<ZSetEntry> entrys) {

        this.key = key;
        this.entrys = entrys;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public void add(ZSetEntry entry) {

        entrys.add(entry);
    }

    public ZSetEntry getEntry(int index) {

        return entrys.get(index);
    }

    public int size() {

        return entrys.size();
    }

    @XmlElement( name = "zsetEntry")
    public List<ZSetEntry> getEntrys() {

        return entrys;
    }

    public void setEntrys(List<ZSetEntry> entrys) {

        this.entrys = entrys;
    }
}
