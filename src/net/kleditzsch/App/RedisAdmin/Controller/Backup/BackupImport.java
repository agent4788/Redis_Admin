package net.kleditzsch.App.RedisAdmin.Controller.Backup;

import net.kleditzsch.App.RedisAdmin.Controller.Backup.Data.*;
import net.kleditzsch.App.RedisAdmin.Controller.Backup.Data.Entrys.HashEntry;
import net.kleditzsch.App.RedisAdmin.Controller.Backup.Data.Entrys.ListEntry;
import net.kleditzsch.App.RedisAdmin.Controller.Backup.Data.Entrys.ZSetEntry;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import redis.clients.jedis.Jedis;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver on 16.08.15.
 */
public abstract class BackupImport {

    /**
     * stellt ei Backup aus der angebenen Datei wieder her
     *
     * @param srcFile
     * @return
     */
    public static boolean importBackup(Path srcFile) {

        //DB Objekt holen
        Jedis db = RedisConnectionManager.getInstance().getConnection();
        Database database = null;

        //XML Daten laden
        try(Reader in = Files.newBufferedReader(srcFile, StandardCharsets.UTF_8)) {

            database = JAXB.unmarshal(in, Database.class);
        } catch (IOException ex) {

            return false;
        }

        //wiederherstellen
        if(database != null) {

            List<String> errorKeys = new ArrayList<>();

            //Datenbank leeren
            db.flushDB();

            //Datensaetze wiederherstellen
            //Strings
            for(RedisString string : database.getStrings()) {

                if(!db.set(string.getKey(), string.getValue()).equals("OK")) {

                    errorKeys.add(string.getKey());
                }
            }

            //Hashes
            for(RedisHash hash : database.getHashes()) {

                for(HashEntry entry : hash.getEntrys()) {

                    if(db.hset(hash.getKey(), entry.getKey(), entry.getValue()) == 0) {

                        errorKeys.add(hash.getKey() + " - " + entry.getKey());
                    }
                }
            }

            //Listen
            for(RedisList list : database.getLists()) {

                for(ListEntry entry : list.getEntrys()) {

                    db.rpush(list.getKey(), entry.getValue());
                }
            }

            //Set
            for(RedisSet set : database.getSets()) {

                for(String entry : set.getEntrys()) {

                    if(db.sadd(set.getKey(), entry) != 1) {

                        errorKeys.add(set.getKey() + " - " + entry);
                    }
                }
            }

            //sortiertes Set
            for(RedisZSet zset : database.getZsets()) {

                for(ZSetEntry entry : zset.getEntrys()) {

                    if(db.zadd(zset.getKey(), entry.getKey(), entry.getValue()) != 1) {

                        errorKeys.add(zset.getKey() + " - " + entry.getValue());
                    }
                }
            }

            errorKeys.stream().forEach(System.out::println);

            return true;
        }
        return false;
    }
}
