package it.unicam.cs.mpgc.rpg.m131177.ui;

// Import di progetto
import it.unicam.cs.mpgc.rpg.m131177.game.GameConstants;

// Import JavaFX
import javafx.scene.Parent;
import javafx.scene.Scene;

// Import Java standard
import java.util.Objects;

/**
 * Factory per la creazione delle scene JavaFX con stylesheet globale applicato.
 */
public final class SceneFactory {

    // Percorso del foglio di stile globale
    private static final String STYLESHEET_PATH = "/css/style.css";

    // Costruttore privato: classe di utilità, non istanziabile
    private SceneFactory() {
    }

    /**
     * Crea una scena con dimensioni standard e stylesheet applicato.
     */
    public static Scene create(Parent root) {
        // Recupera l'URL del foglio di stile (errore esplicito se assente)
        String stylesheet = Objects.requireNonNull(
                SceneFactory.class.getResource(STYLESHEET_PATH),
                "Stylesheet not found: " + STYLESHEET_PATH
        ).toExternalForm();

        // Crea la scena con le dimensioni del canvas
        Scene scene = new Scene(root, GameConstants.CANVAS_WIDTH, GameConstants.CANVAS_HEIGHT);

        // Applica lo stylesheet e restituisce la scena
        scene.getStylesheets().add(stylesheet);
        return scene;
    }
}