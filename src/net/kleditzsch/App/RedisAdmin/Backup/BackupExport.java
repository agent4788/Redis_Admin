package net.kleditzsch.App.RedisAdmin.Backup;

import net.kleditzsch.App.RedisAdmin.Backup.Data.*;
import net.kleditzsch.App.RedisAdmin.Backup.Data.Entrys.HashEntry;
import net.kleditzsch.App.RedisAdmin.Backup.Data.Entrys.ListEntry;
import net.kleditzsch.App.RedisAdmin.Backup.Data.Entrys.ZSetEntry;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import redis.clients.jedis.Jedis;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Backups erstellen
 */
public abstract class BackupExport {

    /**
     * erstellt ein Backup in die angegebene Datei
     *
     * @param targetFile
     * @return
     */
    public static boolean exportBackup(Path targetFile) {

        //DB Objekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();

        //Datenbank speicher vorbereiten
        Database database = new Database();

        //alle Schluessel einlesen
        Set<String> keys = db.keys("*");
        for (String key : keys) {

            String type = db.type(key);
            switch (type) {

                case "string":

                    RedisString string = new RedisString(key, db.get(key));
                    database.getStrings().add(string);
                    break;
                case "hash":

                    RedisHash hash = new RedisHash(key);
                    Map<String, String> value = db.hgetAll(key);
                    for(String hashKey : value.keySet()) {

                        hash.add(new HashEntry(hashKey, value.get(hashKey)));
                    }
                    database.getHashes().add(hash);
                    break;
                case "list":

                    RedisList list = new RedisList(key);
                    long len = db.llen(key);
                    for(long i = 0; i < len; i++){

                        list.add(new ListEntry(i, db.lindex(key, i)));
                    }
                    database.getLists().add(list);
                    break;
                case "set":

                    RedisSet set = new RedisSet(key);
                    Set<String> entrys = db.smembers(key);
                    for(String entry : entrys) {

                        set.add(entry);
                    }
                    database.getSets().add(set);
                    break;
                case "zset":

                    RedisZSet zset = new RedisZSet(key);
                    Set<String> value1 = db.zrange(key, 0L, -1L);
                    for(String val : value1) {

                        zset.add(new ZSetEntry(db.zscore(key, val), val));
                    }
                    database.getZsets().add(zset);
                    break;
            }
        }

        //Datenstruktur in XML Schreiben
        try(Writer out = Files.newBufferedWriter(targetFile, StandardCharsets.UTF_8)) {

            JAXB.marshal(database, out);
        } catch (IOException ex) {

            return false;
        }

        return true;
    }
}
