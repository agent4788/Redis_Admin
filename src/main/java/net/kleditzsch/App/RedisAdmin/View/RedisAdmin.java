package net.kleditzsch.App.RedisAdmin.View;

/**
 * Created by oliver on 19.07.15.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class RedisAdmin extends Application {

    public final static String VERSION = "1.0.1";

    private static Stage primaryStage = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        RedisAdmin.primaryStage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/RedisAdmin.fxml"));
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.getIcons().add(new Image(RedisAdmin.class.getResourceAsStream("/icon/app.png")));
        primaryStage.setTitle("Redis Admin");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {

        return primaryStage;
    }
}
