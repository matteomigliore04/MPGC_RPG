package it.unicam.cs.mpgc.rpg.m131177.controller;

import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

// Controller della schermata di scelta difficoltà.
// Mantiene il riferimento al GameContext (stato della partita)
// e allo Stage (finestra corrente), così da poter cambiare schermata.
public class DifficultyController {

    // Logger dedicato alla classe: registra messaggi al posto delle println
    // e degli stack trace, permettendo una gestione più robusta degli errori
    private static final Logger LOGGER = Logger.getLogger(DifficultyController.class.getName());

    // Riferimenti condivisi con le altre schermate:
    // - context: contiene i dati della partita in corso
    // - stage: finestra principale su cui cambiare scena
    private GameContext context;
    private Stage stage;

    // Metodo invocato dal MenuController dopo il caricamento della schermata:
    // serve a iniettare il contesto e lo stage, che non sono forniti da FXML
    public void setContext(GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    // --- GESTORI DEI PULSANTI DI DIFFICOLTÀ ---
    // Ogni metodo è collegato a un pulsante FXML e chiama selectDifficulty()
    // passando la stringa corrispondente al livello scelto.
    @FXML private void handleFacile() {
        selectDifficulty("Facile");
    }
    @FXML private void handleMedia() {
        selectDifficulty("Media");
    }
    @FXML private void handleAlta() {
        selectDifficulty("Alta");
    }
    @FXML private void handleSuperiore() {
        selectDifficulty("Superiore");
    }

    // --- GESTIONE PULSANTE "INDIETRO" ---
    // Riporta l'utente al menu principale tramite navigateTo().
    // In caso di errore nel caricamento, logghiamo l'eccezione
    // a livello SEVERE invece di stampare lo stack trace su console.
    @FXML
    private void handleIndietro() {
        try {
            navigateTo("/fxml/menu.fxml", "Menu Principale");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel ritorno al menu principale", e);
        }
    }

    // --- SELEZIONE DIFFICOLTÀ E PASSAGGIO ALLA SCHERMATA MOSTRI ---
    // Salva la difficoltà nel GameContext, poi carica la schermata monster.fxml
    // e vi passa il contesto e lo stage, così la partita può proseguire.
    // Infine crea la nuova scena, applica il CSS e la imposta sullo stage.
    // In caso di errore, logghiamo l'eccezione con il livello di difficoltà scelto.
    private void selectDifficulty(String difficulty) {
        context.setDifficulty(difficulty);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/monster.fxml"));
            Parent root = loader.load();

            MonsterController controller = loader.getController();
            controller.setContext(context, stage);

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "Errore nel caricamento della schermata mostri (difficoltà: " + difficulty + ")", e);
        }
    }

    // --- NAVIGAZIONE GENERICA TRA SCHERMATE ---
    // Carica l'FXML indicato, applica il CSS e lo imposta sullo stage.
    // Caso particolare: se si torna al menu, resetta il GameContext
    // per iniziare una nuova partita pulita.
    private void navigateTo(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Se stiamo tornando al menu principale, azzeriamo lo stato della partita
        if (fxmlPath.contains("menu")) {
            context.reset();
        }

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
    }
}