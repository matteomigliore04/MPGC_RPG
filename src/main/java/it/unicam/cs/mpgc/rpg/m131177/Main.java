package it.unicam.cs.mpgc.rpg.m131177;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica il file FXML del menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
        Parent root = loader.load();

        // Configura la scena principale
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // Configura la finestra
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}