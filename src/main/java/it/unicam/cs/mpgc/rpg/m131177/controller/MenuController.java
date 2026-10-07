package it.unicam.cs.mpgc.rpg.m131177.controller;

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

public class MenuController {
    // Logger dedicato alla classe: registra messaggi al posto delle println
    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());

    // Campi FXML: pulsanti del menu, iniettati automaticamente da JavaFX
    @FXML private Button btnIniziaPartita;
    @FXML private Button btnStatistichePersonali;
    @FXML private Button btnStatisticheGlobali;

    // --- GESTIONE AVVIO PARTITA ---
    // Metodo invocato quando si preme "Inizia Partita":
    // crea un nuovo GameContext, carica la schermata di scelta difficoltà
    // e vi passa il contesto e lo stage corrente
    @FXML
    private void handleIniziaPartita() {
        try {
            // Creiamo un nuovo contesto di gioco per la partita corrente
            GameContext context = new GameContext();

            // Carichiamo la schermata FXML della scelta della difficoltà
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/difficulty.fxml"));
            Parent root = loader.load();

            // Otteniamo il controller associato alla schermata difficulty
            // e gli passiamo il contesto di gioco e lo stage corrente,
            // così potrà proseguire con la configurazione della partita
            DifficultyController controller = loader.getController();
            Stage stage = (Stage) btnIniziaPartita.getScene().getWindow();
            controller.setContext(context, stage);

            // Creiamo la nuova scena con dimensioni fisse e applichiamo il CSS,
            // poi la impostiamo sullo stage per effettuare il cambio schermata
            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            // In caso di errore nel caricamento della schermata, logghiamo l'eccezione
            LOGGER.log(Level.SEVERE, "Errore durante l'avvio della partita", e);
        }
    }

    // --- GESTIONE STATISTICHE PERSONALI ---
    // Mostra un avviso informativo: la schermata verrà implementata in futuro.
    // Logghiamo l'apertura a livello INFO per tracciare l'azione dell'utente
    @FXML
    private void handleStatistichePersonali() {
        LOGGER.info("Apertura statistiche personali...");
        showAlert(AlertType.INFORMATION, "Statistiche Personali",
                "La schermata delle statistiche personali verrà implementata in futuro.");
    }

    // --- GESTIONE STATISTICHE GLOBALI ---
    // Mostra un avviso informativo: la classifica verrà implementata in futuro.
    // Logghiamo l'apertura a livello INFO per tracciare l'azione dell'utente
    @FXML
    private void handleStatisticheGlobali() {
        LOGGER.info("Apertura statistiche globali...");
        showAlert(AlertType.INFORMATION, "Statistiche Globali",
                "La classifica degli altri giocatori verrà implementata in futuro.");
    }

    // --- METODO DI SUPPORTO PER GLI AVVISI ---
    // Crea e mostra un Alert JavaFX del tipo indicato,
    // con titolo e testo specificati; attende la chiusura dell'utente
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}