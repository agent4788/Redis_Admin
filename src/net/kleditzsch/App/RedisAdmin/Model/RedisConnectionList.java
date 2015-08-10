package net.kleditzsch.App.RedisAdmin.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by oliver on 03.08.15.
 */
@XmlRootElement
public class RedisConnectionList {

    protected ObservableList<RedisConnection> connectionList = FXCollections.observableArrayList();

    public void add(RedisConnection rc) {

        connectionList.add(rc);
    }

    public RedisConnection get(int index) {

        return connectionList.get(index);
    }

    public void remove(RedisConnection rc) {

        connectionList.remove(rc);
    }

    public void remove(int index) {

        connectionList.remove(index);
    }

    public int size() {

        return connectionList.size();
    }

    @XmlElement( name = "connection" )
    public ObservableList<RedisConnection> getRedisConnection() {

        return connectionList;
    }

    public void setRedisConnection(ObservableList<RedisConnection> rc) {

        connectionList = rc;
    }
}
