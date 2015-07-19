package net.kleditzsch.ui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Created by oliver on 15.07.15.
 */
public class UiDialogHelper {

    /**
     * zeigt einen Info-Dialog an
     *
     * @param title   Titel
     * @param header  Kopfzeile
     * @param message Meldung
     */
    public static void showInfoDialog(String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * zeigt einen Warnungs-Dialog an
     *
     * @param title   Titel
     * @param header  Kopfzeile
     * @param message Meldung
     */
    public static void showWarningDialog(String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * zeigt einen Fehler-Dialog an
     *
     * @param title   Titel
     * @param header  Kopfzeile
     * @param message Meldung
     */
    public static void showErrorDialog(String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * zeigt einen EIngabe-Dialog an
     *
     * @param title   Titel
     * @param header  Kopfzeile
     * @param message Meldung
     */
    public static boolean showConfirmDialog(String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            return true;
        }
        return false;
    }

    /**
     * zeigt einen Dialog zu einer Ausnahme an
     *
     * @param e
     */
    public static void showExceptionDialog(Throwable e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(e.getLocalizedMessage());
        alert.setHeaderText(e.getLocalizedMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Trace:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setPrefSize(800, 400);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        //Fenstergroese anpassen
        alert.getDialogPane().expandedProperty().addListener((l) -> {
            Platform.runLater(() -> {
                alert.getDialogPane().requestLayout();
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.sizeToScene();
            });
        });

        alert.showAndWait();
    }
}
