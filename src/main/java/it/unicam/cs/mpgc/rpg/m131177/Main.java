package it.unicam.cs.mpgc.rpg.m131177;

// Import di progetto
import it.unicam.cs.mpgc.rpg.m131177.ui.SceneFactory;

// Import JavaFX
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Punto di ingresso dell'applicazione JavaFX.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Carica il font retrò (dimensione di default, il CSS la sovrascrive)
        Font.loadFont(Main.class.getResourceAsStream("/fonts/PressStart2P-Regular.ttf"), 10);

        // Carica il file FXML del menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));

        // Costruisce l'albero dei nodi grafici
        Parent root = loader.load();

        // Crea la scena
        Scene scene = SceneFactory.create(root);

        // Configura e mostra la finestra
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}