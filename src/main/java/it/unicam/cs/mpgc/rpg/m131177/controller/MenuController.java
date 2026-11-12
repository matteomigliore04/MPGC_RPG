package it.unicam.cs.mpgc.rpg.m131177.controller;

// Import di progetto
import it.unicam.cs.mpgc.rpg.m131177.audio.AudioManager;
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import it.unicam.cs.mpgc.rpg.m131177.ui.SceneFactory;

// Import JavaFX
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

// Import Java standard
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller del menu principale: gestisce i tre pulsanti
 * (Inizia Partita, Statistiche Personali, Statistiche Globali)
 * e la navigazione verso la schermata di scelta difficoltà.
 */
public class MenuController {

    // Logger di classe
    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());

    // Pulsanti iniettati da JavaFX
    @FXML private Button btnIniziaPartita;
    @FXML private Button btnStatistichePersonali;
    @FXML private Button btnStatisticheGlobali;

    /**
     * Inizializza il menu avviando la musica.
     */
    @FXML
    public void initialize() {
        AudioManager.getInstance().playMenuMusic();
    }

    // --- Gestione avvio partita ---

    /**
     * Crea un nuovo GameContext, carica difficulty.fxml
     * e inietta contesto + stage nel DifficultyController.
     */
    @FXML
    private void handleIniziaPartita() {
        try {
            // Nuovo contesto di gioco per la partita corrente
            GameContext context = new GameContext();

            // Carica la schermata di scelta difficoltà
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/difficulty.fxml"));
            Parent root = loader.load();

            // Inietta contesto e stage nel controller della difficoltà
            DifficultyController controller = loader.getController();
            Stage stage = (Stage) btnIniziaPartita.getScene().getWindow();
            controller.setContext(context, stage);

            // Crea la nuova scena, applica il CSS e cambia schermata
            stage.setScene(SceneFactory.create(root));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'avvio della partita", e);
        }
    }

    // --- Gestione statistiche ---

    /**
     * Apre la schermata delle statistiche personali.
     */
    @FXML
    private void handleStatistichePersonali() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/statistics.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnIniziaPartita.getScene().getWindow(); // uso btnIniziaPartita per prendere lo stage

            stage.setScene(SceneFactory.create(root));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento delle statistiche", e);
        }
    }

    /**
     * Mostra un alert placeholder per le statistiche globali.
     */
    @FXML
    private void handleStatisticheGlobali() {
        LOGGER.info("Apertura statistiche globali...");
        showAlert(AlertType.INFORMATION, "Statistiche Globali",
                "La classifica degli altri giocatori verrà implementata in futuro.");
    }

    // --- Metodi di supporto ---

    /**
     * Crea e mostra un Alert del tipo indicato; attende la chiusura.
     */
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);   // niente intestazione, solo titolo + contenuto
        alert.setContentText(content);
        alert.showAndWait();
    }
}