package it.unicam.cs.mpgc.rpg.m131177.controller;

// ============================================================
// IMPORT
// ============================================================
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;

// Controller della schermata di scelta difficoltà.
// Mantiene GameContext (stato partita) e Stage (finestra corrente).
public class DifficultyController {

    // ============================================================
    // LOGGER
    // ============================================================
    // Logger di classe per errori e stack trace
    private static final Logger LOGGER = Logger.getLogger(DifficultyController.class.getName());

    // ============================================================
    // STATO CONDIVISO
    // ============================================================
    // - context: dati della partita in corso
    // - stage: finestra principale per cambiare scena
    private GameContext context;
    private Stage stage;

    // Inietta contesto e stage (chiamato dal MenuController dopo il load FXML)
    public void setContext(GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    // ============================================================
    // CAMPI FXML
    // ============================================================
    // Pulsanti dichiarati ma non letti nel codice: funzionano via onAction.
    // Possono essere rimossi (evita warning "unused field").
    // Servono solo se in futuro vorrai disabilitarli/cambiarne il testo.
    @FXML private Button btnFacile;
    @FXML private Button btnMedia;
    @FXML private Button btnAlta;
    @FXML private Button btnSuperiore;
    @FXML private Button btnIndietro;

    // ============================================================
    // GESTORI PULSANTI DIFFICOLTÀ
    // ============================================================
    // Ogni metodo chiama selectDifficulty() con la stringa del livello.
    // Ricevono ActionEvent per ricavare lo stage dal pulsante sorgente.
    @FXML private void handleFacile(javafx.event.ActionEvent event) {
        selectDifficulty("Germoglio", event);
    }
    @FXML private void handleMedia(javafx.event.ActionEvent event) {
        selectDifficulty("Erba Selvatica", event);
    }
    @FXML private void handleAlta(javafx.event.ActionEvent event) {
        selectDifficulty("Fumo Denso", event);
    }
    @FXML private void handleSuperiore(javafx.event.ActionEvent event) {
        selectDifficulty("100% THC", event);
    }

    // ============================================================
    // GESTIONE PULSANTE "INDIETRO"
    // ============================================================
    // Torna al menu principale tramite navigateTo().
    // In caso di errore: log SEVERE con stack trace.
    @FXML
    private void handleIndietro() {
        try {
            navigateTo("/fxml/menu.fxml", "Menu Principale");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel ritorno al menu principale", e);
        }
    }

    // ============================================================
    // SELEZIONE DIFFICOLTÀ E AVVIO PARTITA
    // ============================================================
    // Salva la difficoltà, carica game.fxml, inietta contesto+stage
    // nel GameController, imposta la scena e attiva la tastiera.
    //
    // Nota: loader.load() chiama initialize() del GameController
    // PRIMA di setContext(): initialize() non deve usare context/stage.
    private void selectDifficulty(String difficulty, javafx.event.ActionEvent event) {
        context.setDifficulty(difficulty);
        try {
            // Carica la schermata di gioco (initialize() viene chiamato qui)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            Parent root = loader.load(); // <--- Qui viene chiamato initialize() (context è ancora null, ma non lo usiamo più)

            // Ottiene il controller e ricava lo stage dal pulsante sorgente
            GameController controller = loader.getController();
            Stage stage = (Stage) ((javafx.scene.control.Button) event.getSource()).getScene().getWindow();

            // Inietta contesto e stage nel GameController (aggiorna HUD)
            controller.setContext(context, stage); // <--- Qui impostiamo il context e aggiorniamo l'HUD

            // Crea la nuova scena, applica il CSS e cambia schermata
            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);

            // Collega gli eventi di tastiera DOPO stage.setScene(scene)
            // (gameCanvas.getScene() esiste solo dopo aver impostato la scena)
            controller.setupKeyboardInput();
        } catch (Exception e) {
            // Log SEVERE con difficoltà scelta e stack trace completo
            LOGGER.log(Level.SEVERE,
                    "Errore nel caricamento della schermata di gioco (difficoltà: " + difficulty + ")", e);
        }
    }

    // ============================================================
    // NAVIGAZIONE GENERICA TRA SCHERMATE
    // ============================================================
    // Carica l'FXML indicato, applica il CSS e lo imposta sullo stage.
    // Se la destinazione è il menu, resetta il GameContext.
    //
    // Nota: il parametro "title" non è usato.
    // Potresti usarlo per stage.setTitle(title) o rimuoverlo.
    private void navigateTo(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Se si torna al menu, azzera lo stato della partita
        if (fxmlPath.contains("menu")) {
            context.reset();
        }

        // Crea la nuova scena, applica il CSS e cambia schermata
        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
    }
}