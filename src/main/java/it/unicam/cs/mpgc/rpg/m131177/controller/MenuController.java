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

/**
 * Controller della schermata del menu iniziale.
 * Gestisce le azioni dei tre bottoni principali del gioco.
 */
public class MenuController {

    @FXML
    private Button btnIniziaPartita;

    @FXML
    private Button btnStatistichePersonali;

    @FXML
    private Button btnStatisticheGlobali;

    /**
     * Metodo chiamato automaticamente dopo il caricamento dell'FXML.
     * Può essere usato per inizializzare componenti.
     */
    @FXML
    public void initialize() {
        System.out.println("Menu iniziale caricato correttamente.");
    }

    /**
     * Gestisce il click sul bottone "Inizia Partita".
     * In futuro caricherà la schermata di gioco.
     */
    @FXML
    private void handleIniziaPartita() {
        try {
            // Crea il contesto condiviso per la nuova partita
            GameContext context = new GameContext();

            // Carica la schermata della difficoltà
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/difficulty.fxml"));
            Parent root = loader.load();

            // Passa il contesto e lo stage al controller
            DifficultyController controller = loader.getController();
            Stage stage = (Stage) btnIniziaPartita.getScene().getWindow();
            controller.setContext(context, stage);

            // Cambia la scena
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestisce il click sul bottone "Statistiche Personali".
     * In futuro caricherà la schermata con lo storico delle partite.
     */
    @FXML
    private void handleStatistichePersonali() {
        System.out.println("Apertura statistiche personali...");
        showAlert(AlertType.INFORMATION, "Statistiche Personali",
                "La schermata delle statistiche personali verrà implementata in futuro.");
    }

    /**
     * Gestisce il click sul bottone "Statistiche Globali".
     * In futuro caricherà la classifica degli altri giocatori dal DB.
     */
    @FXML
    private void handleStatisticheGlobali() {
        System.out.println("Apertura statistiche globali...");
        showAlert(AlertType.INFORMATION, "Statistiche Globali",
                "La classifica degli altri giocatori verrà implementata in futuro.");
    }

    /**
     * Mostra una finestra di dialogo informativa.
     * Utile come placeholder per le schermate non ancora implementate.
     */
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}