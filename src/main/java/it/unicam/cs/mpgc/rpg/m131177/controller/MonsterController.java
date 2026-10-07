package it.unicam.cs.mpgc.rpg.m131177.controller;

import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonsterController {

    // Logger dedicato alla classe: registra messaggi al posto delle println
    // e degli stack trace, permettendo una gestione più robusta degli errori
    private static final Logger LOGGER = Logger.getLogger(MonsterController.class.getName());

    // Campi FXML: pulsanti dei mostri e pulsante "Indietro",
    // iniettati automaticamente da JavaFX
    @FXML private Button btnTroll;
    @FXML private Button btnRagno;
    @FXML private Button btnCavaliere;
    @FXML private Button btnDragone;
    @FXML private Button btnIndietro;

    // Riferimenti condivisi con le altre schermate:
    // - context: contiene i dati della partita in corso
    // - stage: finestra principale su cui cambiare scena
    private GameContext context;
    private Stage stage;

    // Metodo invocato dal DifficultyController dopo il caricamento della schermata:
    // serve a iniettare il contesto e lo stage, che non sono forniti da FXML
    public void setContext(GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    // --- GESTORI DEI PULSANTI DEI MOSTRI ---
    // Ogni metodo è collegato a un pulsante FXML e chiama selectMonster()
    // passando il nome del mostro corrispondente, così da registrarlo nel contesto
    @FXML private void handleTroll() {
        selectMonster("Troll");
    }

    @FXML private void handleRagno() {
        selectMonster("Ragno Gigante");
    }

    @FXML private void handleCavaliere() {
        selectMonster("Cavaliere Nero");
    }

    @FXML private void handleDragone() {
        selectMonster("Dragone");
    }

    // --- GESTIONE PULSANTE "INDIETRO" ---
    // Riporta l'utente alla schermata di scelta difficoltà.
    // In caso di errore nel caricamento, logghiamo l'eccezione
    // a livello SEVERE invece di stampare lo stack trace su console.
    @FXML
    private void handleIndietro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/difficulty.fxml"));
            Parent root = loader.load();

            // Passiamo contesto e stage al DifficultyController,
            // così la navigazione può proseguire senza perdere lo stato
            DifficultyController controller = loader.getController();
            controller.setContext(context, stage);

            // Creiamo la nuova scena con dimensioni fisse, applichiamo il CSS
            // e la impostiamo sullo stage per effettuare il cambio schermata
            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel ritorno alla schermata di scelta difficoltà", e);
        }
    }

    // --- SELEZIONE DEL MOSTRO ---
    // Salva il mostro scelto nel GameContext e mostra un avviso riepilogativo
    // con difficoltà corrente e mostro selezionato.
    // Logghiamo la scelta a livello INFO per tracciare l'azione dell'utente.
    private void selectMonster(String monster) {
        context.setMonster(monster);
        LOGGER.info("Mostro selezionato: " + monster
                + " (difficoltà: " + context.getDifficulty() + ")");
        showAlert(
                "Partita Pronta!",
                "Stai per affrontare un " + monster + " in difficoltà " + context.getDifficulty() + "!\n\n" +
                        "La schermata di gioco verrà implementata nella prossima release."
        );
    }

    // --- METODO DI SUPPORTO PER GLI AVVISI ---
    // Crea e mostra un Alert JavaFX di tipo INFORMATION,
    // con titolo e testo specificati; attende la chiusura dell'utente
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}