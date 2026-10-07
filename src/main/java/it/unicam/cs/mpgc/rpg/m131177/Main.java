package it.unicam.cs.mpgc.rpg.m131177;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    // Scegli una delle tre immagini
    private static final String BG_IMAGE_PATH = "/images/skybox_village_v2.png";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));

        // Carica il font nel sistema JavaFX
        Font.loadFont(Main.class.getResourceAsStream("/fonts/PressStart2P-Regular.ttf"), 10);

        Parent root = loader.load();

        applyBackground((Region) root);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void applyBackground(Region root) {
        try {
            Image bgImage = new Image(Main.class.getResourceAsStream(BG_IMAGE_PATH));

            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            BackgroundSize.AUTO, BackgroundSize.AUTO,
                            false, false, true, false
                    )
            );

            root.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.err.println("⚠️ Immagine di sfondo non trovata: " + BG_IMAGE_PATH);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}