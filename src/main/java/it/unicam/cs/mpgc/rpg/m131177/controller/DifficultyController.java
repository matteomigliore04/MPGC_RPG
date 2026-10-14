package it.unicam.cs.mpgc.rpg.m131177.controller;

// Import del modello di gioco e delle classi JavaFX necessarie
// per gestire FXML, scena, finestra, pulsanti e navigazione
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Import per il logging: usiamo java.util.logging (standard di Java)
import java.util.logging.Level;
import java.util.logging.Logger;

// Import del controllo Button (usato per il cast in selectDifficulty)
import javafx.scene.control.Button;

// Controller della schermata di scelta difficoltà.
// Mantiene il riferimento al GameContext (stato della partita)
// e allo Stage (finestra corrente), così da poter cambiare schermata.
public class DifficultyController {

    // Logger dedicato alla classe: registra messaggi al posto delle println
    // e degli stack trace, per una gestione più robusta degli errori
    private static final Logger LOGGER = Logger.getLogger(DifficultyController.class.getName());

    // Riferimenti condivisi con le altre schermate:
    // - context: contiene i dati della partita in corso
    // - stage: finestra principale su cui cambiare scena
    private GameContext context;
    private Stage stage;

    // Metodo invocato dal MenuController dopo il caricamento della schermata:
    // inietta il contesto e lo stage, che non sono forniti da FXML
    public void setContext(GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    // DICHIARAZIONE DEI BOTTONI (necessarie per collegare l'FXML).
    // Nota: questi campi non vengono mai letti direttamente nel codice:
    // i pulsanti funzionano tramite onAction nel FXML.
    // Possono quindi essere rimossi senza problemi (evita warning "unused field").
    // Vanno reinseriti solo se in futuro servisse disabilitarli/cambiarne il testo.
    @FXML private Button btnFacile;
    @FXML private Button btnMedia;
    @FXML private Button btnAlta;
    @FXML private Button btnSuperiore;
    @FXML private Button btnIndietro;

    // --- GESTORI DEI PULSANTI DI DIFFICOLTÀ ---
    // Ogni metodo è collegato a un pulsante FXML e chiama selectDifficulty()
    // passando la stringa corrispondente al livello scelto.
    // Ricevono l'ActionEvent, usato per ricavare lo stage dal pulsante sorgente
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

    // --- GESTIONE PULSANTE "INDIETRO" ---
    // Riporta l'utente al menu principale tramite navigateTo().
    // In caso di errore nel caricamento, logghiamo l'eccezione
    // a livello SEVERE invece di stampare lo stack trace su console
    @FXML
    private void handleIndietro() {
        try {
            navigateTo("/fxml/menu.fxml", "Menu Principale");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel ritorno al menu principale", e);
        }
    }

    // --- SELEZIONE DELLA DIFFICOLTÀ E AVVIO DELLA PARTITA ---
    // Salva la difficoltà scelta nel GameContext, poi carica la schermata di gioco
    // (game.fxml), inietta contesto e stage nel GameController, imposta la scena
    // e attiva la gestione della tastiera.
    // In caso di errore, logghiamo l'eccezione con la difficoltà scelta, così il
    // log è più informativo e non finisce su System.err con printStackTrace().
    //
    // Nota importante: loader.load() chiama initialize() del GameController
    // PRIMA che setContext() venga invocato. Per questo initialize() non deve
    // usare context/stage: setupKeyboardInput() viene chiamato dopo.
    private void selectDifficulty(String difficulty, javafx.event.ActionEvent event) {
        context.setDifficulty(difficulty);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            Parent root = loader.load(); // <--- Qui viene chiamato initialize() (context è ancora null, ma non lo usiamo più)

            GameController controller = loader.getController();
            Stage stage = (Stage) ((javafx.scene.control.Button) event.getSource()).getScene().getWindow();

            controller.setContext(context, stage); // <--- Qui impostiamo il context e aggiorniamo l'HUD

            // Creiamo la nuova scena con dimensioni fisse, applichiamo il CSS
            // e la impostiamo sullo stage per effettuare il cambio schermata
            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);

            // Colleghiamo gli eventi di tastiera alla scena appena impostata.
            // Va fatto DOPO stage.setScene(scene), perché gameCanvas.getScene()
            // esiste solo dopo che la scena è stata creata e impostata
            controller.setupKeyboardInput();
        } catch (Exception e) {
            // Logging robusto: livello SEVERE, messaggio contestuale con la difficoltà,
            // eccezione passata come ultimo argomento per avere lo stack trace completo
            LOGGER.log(Level.SEVERE,
                    "Errore nel caricamento della schermata di gioco (difficoltà: " + difficulty + ")", e);
        }
    }

    // --- NAVIGAZIONE GENERICA TRA SCHERMATE ---
    // Carica l'FXML indicato, applica il CSS e lo imposta sullo stage.
    // Caso particolare: se si torna al menu, resetta il GameContext
    // per iniziare una nuova partita pulita.
    //
    // Nota: il parametro "title" non viene attualmente usato.
    // Potresti usarlo per stage.setTitle(title) o rimuoverlo del tutto
    private void navigateTo(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Se stiamo tornando al menu principale, azzeriamo lo stato della partita
        if (fxmlPath.contains("menu")) {
            context.reset();
        }

        // Creiamo la nuova scena con dimensioni fisse, applichiamo il CSS
        // e la impostiamo sullo stage per effettuare il cambio schermata
        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
    }
}