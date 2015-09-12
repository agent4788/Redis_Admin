package net.kleditzsch.App.RedisAdmin.Controller.Backup.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis Datenbank Representation
 */
@XmlRootElement
public class Database {

    private List<RedisString> strings = new ArrayList<>();

    private List<RedisHash> hashes = new ArrayList<>();

    private List<RedisList> lists = new ArrayList<>();

    private List<RedisSet> sets = new ArrayList<>();

    private List<RedisZSet> zsets = new ArrayList<>();

    public Database() {
    }

    @XmlElement( name = "string")
    public List<RedisString> getStrings() {
        return strings;
    }

    public void setStrings(List<RedisString> strings) {
        this.strings = strings;
    }

    @XmlElement( name = "hash")
    public List<RedisHash> getHashes() {
        return hashes;
    }

    public void setHashes(List<RedisHash> hashes) {
        this.hashes = hashes;
    }

    @XmlElement( name = "list")
    public List<RedisList> getLists() {
        return lists;
    }

    public void setLists(List<RedisList> lists) {
        this.lists = lists;
    }

    @XmlElement( name = "set")
    public List<RedisSet> getSets() {
        return sets;
    }

    public void setSets(List<RedisSet> sets) {
        this.sets = sets;
    }

    @XmlElement( name = "zset")
    public List<RedisZSet> getZsets() {
        return zsets;
    }

    public void setZsets(List<RedisZSet> zsets) {
        this.zsets = zsets;
    }
}
