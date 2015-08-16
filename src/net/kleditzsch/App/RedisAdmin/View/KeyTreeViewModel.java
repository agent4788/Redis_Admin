package net.kleditzsch.App.RedisAdmin.View;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.kleditzsch.App.RedisAdmin.Model.RedisConnectionManager;
import redis.clients.jedis.Jedis;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Created by oliver on 19.07.15.
 */
public class KeyTreeViewModel {

    protected TreeItem<String> root = null;

    HashMap<String, TreeItem<String>> map = new HashMap<>();

    private static KeyTreeViewModel ourInstance = new KeyTreeViewModel();

    public static KeyTreeViewModel getInstance() {

        return ourInstance;
    }

    private KeyTreeViewModel() {

    }

    /**
     * liest alle Schluessel der Datenbank ein und erstellt die Baumstruktur fuer den Treeview
     *
     * @return Root Element
     */
    public TreeItem<String> getKeyList() {

        //Map leeren
        map.clear();

        //Icons
        Node database = new ImageView(new Image(getClass().getResourceAsStream("recource/database.png")));
        Image folderImage = new Image(getClass().getResourceAsStream("recource/folder.png"));
        Image dataImage = new Image(getClass().getResourceAsStream("recource/data.png"));

        //Trenner
        String delimiter = RedisConnectionManager.getInstance().getKeyDelimiter();

        //Root Element erstellen
        root = new TreeItem<>("DB-" + Integer.toString(RedisConnectionManager.getInstance().getDbIndex()), database);
        root.setExpanded(true);

        //Baumstruktur erstellen
        Jedis db = RedisConnectionManager.getInstance().getConnection();
        Set<String> keys = db.keys("*");
        keys = new TreeSet<>(keys);
        for (String key : keys) {

            String[] keyParts = key.split(Pattern.quote(delimiter));
            String keyStack = "";
            TreeItem<String> parentNode = null;
            for(int i = 0; i < keyParts.length; i++) {

                //Root
                if(keyStack.length() == 0) {

                    //Root
                    keyStack += keyParts[i];
                    if(!map.containsKey(keyStack)) {

                        //existiert nicht
                        TreeItem<String> item = null;
                        if(keyParts.length > i + 1) {

                            Node folder = new ImageView(folderImage);
                            item = new TreeItem<>(keyParts[i], folder);
                        } else {

                            Node data = new ImageView(dataImage);
                            item = new TreeItem<>(keyParts[i], data);
                        }
                        root.getChildren().add(item);
                        map.put(keyStack, item);
                    }
                    parentNode = map.get(keyStack);
                } else {

                    //alles unterhalb von Root
                    keyStack += delimiter + keyParts[i];
                    if(!map.containsKey(keyStack)) {

                        //noch kein Konten bekannt
                        TreeItem<String> item = null;
                        if((keyParts.length - 1) != i) {

                            Node folder = new ImageView(folderImage);
                            item = new TreeItem<>(keyParts[i], folder);
                        } else {

                            Node data = new ImageView(dataImage);
                            item = new TreeItem<>(keyParts[i], data);
                        }
                        parentNode.getChildren().add(item);
                        map.put(keyStack, item);
                        parentNode = item;
                    } else {

                        //Konten schon bekannt
                        parentNode = map.get(keyStack);
                    }
                }
            }
        }

        return root;
    }

    /**
     * sucht nach dem uebergebenen TreeItem und gibt dessen Schluessel zurueck
     *
     * @param  newValue ausgewaehltes Item
     * @return Schluessel
     */
    public String getKey(TreeItem<String> newValue) {

        for(String key : map.keySet()) {

            if(map.get(key).equals(newValue)) {

                return key;
            }
        }

        return null;
    }

    public TreeItem<String> getRootElement() {

        return root;
    }

    /**
     * prueft ob ein Schluessel existiert
     *
     * @param key Schluessel
     * @return
     */
    public boolean keyExists(String key) {

        return RedisConnectionManager.getInstance().getConnection().exists(key);
    }
}
