package it.unicam.cs.mpgc.rpg.m131177.controller;

// --- IMPORT ---
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import java.util.logging.Level;
import java.util.logging.Logger;

// Controller del menu principale: gestisce i tre pulsanti
// (Inizia Partita, Statistiche Personali, Statistiche Globali)
// e la navigazione verso la schermata di scelta difficoltà.
public class MenuController {

    // --- LOGGER ---
    // Logger di classe per errori e tracciamento azioni utente
    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());

    // --- CAMPI FXML ---
    // Pulsanti del menu, iniettati da JavaFX tramite fx:id
    @FXML private Button btnIniziaPartita;
    @FXML private Button btnStatistichePersonali;
    @FXML private Button btnStatisticheGlobali;

    // ============================================================
    // GESTIONE AVVIO PARTITA
    // ============================================================

    // Crea un nuovo GameContext, carica difficulty.fxml
    // e inietta contesto + stage nel DifficultyController
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
            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            // Log dell'errore a livello SEVERE con stack trace
            LOGGER.log(Level.SEVERE, "Errore durante l'avvio della partita", e);
        }
    }

    // ============================================================
    // GESTIONE STATISTICHE
    // ============================================================

    // Statistiche personali: per ora mostra un alert placeholder
    @FXML
    private void handleStatistichePersonali() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/statistics.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnIniziaPartita.getScene().getWindow(); // uso btnIniziaPartita per prendere lo stage

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento delle statistiche", e);
        }
    }

    // Statistiche globali: per ora mostra un alert placeholder
    @FXML
    private void handleStatisticheGlobali() {
        LOGGER.info("Apertura statistiche globali...");
        showAlert(AlertType.INFORMATION, "Statistiche Globali",
                "La classifica degli altri giocatori verrà implementata in futuro.");
    }

    // ============================================================
    // METODI DI SUPPORTO
    // ============================================================

    // Crea e mostra un Alert del tipo indicato; attende la chiusura
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);   // niente intestazione, solo titolo + contenuto
        alert.setContentText(content);
        alert.showAndWait();
    }
}