package net.kleditzsch.App.RedisAdmin.View;

/**
 * Created by oliver on 19.07.15.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RedisAdmin extends Application {

    public final static String VERSION = "1.0.0";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("RedisAdmin.fxml"));
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Redis Admin");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
