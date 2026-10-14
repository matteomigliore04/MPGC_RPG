package it.unicam.cs.mpgc.rpg.m131177.controller;

// Import del GameLoop (motore di gioco), del modello (GameContext)
// e delle classi JavaFX necessarie per gestire FXML, Canvas, HUD,
// input da tastiera, pulsanti e navigazione tra schermate
import it.unicam.cs.mpgc.rpg.m131177.game.GameLoop;
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

// Import per il logging: usiamo java.util.logging (standard di Java)
import java.util.logging.Level;
import java.util.logging.Logger;

// Controller della schermata di gioco.
// Gestisce il GameLoop, l'HUD (vita, scudo, frecce, punti, ondata),
// l'input da tastiera (movimento, sparo, pausa) e il menu di pausa.
public class GameController {

    // Logger dedicato alla classe: registra messaggi al posto delle println
    // e degli stack trace, permettendo una gestione più robusta degli errori
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    // --- RIFERIMENTI FXML ---
    // Canvas su cui il GameLoop disegna player, mostri e proiettili
    @FXML private Canvas gameCanvas;
    // Etichette dell'HUD (aggiornate da updateHUD)
    @FXML private Label lblHealth;
    @FXML private Label lblWave;
    @FXML private Label lblScore;
    @FXML private Label lblArrows;
    @FXML private Label lblShield;
    // Menu di pausa.
    // Nota: i pulsanti btnResume e btnQuit NON sono dichiarati come campi:
    // funzionano tramite onAction nel FXML e non servono riferimenti Java.
    // Se in futuro dovessi disabilitarli/cambiarne il testo, andranno reinseriti.
    @FXML private VBox pauseMenu;

    // --- STATO DEL CONTROLLER ---
    // isPaused: true quando il menu di pausa è aperto
    // context: stato condiviso della partita (difficoltà, ecc.)
    // stage: finestra principale per la navigazione
    // gameLoop: motore di gioco (AnimationTimer)
    private boolean isPaused = false;
    private GameContext context;
    private Stage stage;
    private GameLoop gameLoop;

    // Metodo invocato dal DifficultyController dopo il caricamento della schermata:
    // inietta il contesto e lo stage, che non sono forniti da FXML
    public void setContext(GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    // --- INIZIALIZZAZIONE ---
    // initialize() è chiamato automaticamente da JavaFX dopo il caricamento
    // dell'FXML: qui creiamo il GameLoop e lo avviamo.
    // Nota: setupKeyboardInput() viene chiamato separatamente dal
    // DifficultyController, perché ha bisogno che la scena sia già impostata
    @FXML
    public void initialize() {
        // Inizializza e avvia il Game Loop
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gameLoop = new GameLoop(gc, this);

        gameLoop.setOnGameOver(() -> {
            showGameOverScreen();
        });

        gameLoop.start();
    }

    private void showGameOverScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameover.fxml"));
            Parent root = loader.load();

            GameOverController controller = loader.getController();
            controller.setGameData(
                    gameLoop.getScore(),
                    gameLoop.getCurrentWave(),
                    gameLoop.getEnemiesKilled(),
                    context,
                    stage
            );

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle(" Game Over");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- GESTIONE INPUT DA TASTIERA ---
    // Collega i gestori di eventi (pressione/rilascio tasti) alla scena.
    // Va chiamato dopo che la scena è stata impostata sullo stage.
    // - Se il gioco è in pausa, solo ESC è accettato (per riprendere)
    // - Movimento: WASD o frecce direzionali
    // - Sparo: SPAZIO
    // - Pausa: ESC
    public void setupKeyboardInput() {
        gameCanvas.getScene().setOnKeyPressed(event -> {
            // Se il gioco è in pausa, ignora tutti i tasti tranne ESC
            if (isPaused) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }
                return;
            }

            KeyCode key = event.getCode();

            // Movimento
            if (key == KeyCode.W || key == KeyCode.UP)    gameLoop.setUpPressed(true);
            if (key == KeyCode.S || key == KeyCode.DOWN)  gameLoop.setDownPressed(true);
            if (key == KeyCode.A || key == KeyCode.LEFT)  gameLoop.setLeftPressed(true);
            if (key == KeyCode.D || key == KeyCode.RIGHT) gameLoop.setRightPressed(true);

            // Sparo
            if (key == KeyCode.SPACE) {
                gameLoop.shoot();
                event.consume(); // evita che SPAZIO attivi altri controlli
            }

            // Tasto Pausa
            if (key == KeyCode.ESCAPE) {
                togglePause();
            }
        });

        // Al rilascio dei tasti: resettiamo lo stato dei booleani di movimento.
        // Se il gioco è in pausa, ignoriamo il rilascio (tanto il loop è fermo)
        gameCanvas.getScene().setOnKeyReleased(event -> {
            if (isPaused) return; // Ignora rilascio tasti se in pausa

            KeyCode key = event.getCode();
            if (key == KeyCode.W || key == KeyCode.UP)    gameLoop.setUpPressed(false);
            if (key == KeyCode.S || key == KeyCode.DOWN)  gameLoop.setDownPressed(false);
            if (key == KeyCode.A || key == KeyCode.LEFT)  gameLoop.setLeftPressed(false);
            if (key == KeyCode.D || key == KeyCode.RIGHT) gameLoop.setRightPressed(false);
        });

        // Assicura che il canvas riceva gli eventi di tastiera
        gameCanvas.requestFocus();
    }

    // --- AGGIORNAMENTO HUD ---
    // Metodo chiamato dal GameLoop ad ogni frame per aggiornare i testi dell'HUD.
    // I valori arrivano dal GameLoop (che a sua volta li legge dal player
    // o da un model condiviso).
    public void updateHUD(int health, int score, int arrows) {
        lblHealth.setText("❤️ Vita: " + health);
        lblScore.setText("⭐ Punti: " + score);
        lblArrows.setText("🏹 Frecce: " + arrows + "/100");
        // lblWave e lblShield per ora restano con valori fissi o li rimuovi temporaneamente
    }

    // --- GESTIONE DEL GIOCO ---
    // stopGame(): ferma il GameLoop (chiamato quando la partita termina).
    // Il check null evita NullPointerException se initialize() non è ancora
    // stato eseguito o il gameLoop non è stato creato
    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    // togglePause(): alterna pausa/ripresa in base allo stato corrente
    private void togglePause() {
        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    // pauseGame(): ferma il GameLoop e mostra il menu di pausa.
    // setManaged(true) rende il menu cliccabile e occupante spazio nel layout
    public void pauseGame() {
        isPaused = true;
        gameLoop.stop(); // Ferma il motore di gioco
        pauseMenu.setVisible(true);
        pauseMenu.setManaged(true); // Lo rende cliccabile
    }

    // handleResume(): invocato dal pulsante "Riprendi partita"
    @FXML
    private void handleResume() {
        resumeGame();
    }

    // resumeGame(): chiude il menu di pausa e riavvia il GameLoop.
    // Resetta anche i booleani dei tasti, per evitare movimenti indesiderati
    // se l'utente teneva premuto un tasto al momento della pausa.
    // Infine ridà il focus al canvas per far funzionare di nuovo la tastiera
    public void resumeGame() {
        isPaused = false;
        pauseMenu.setVisible(false);
        pauseMenu.setManaged(false);

        // Reset dello stato dei tasti per evitare movimenti "fantasma"
        gameLoop.setUpPressed(false);
        gameLoop.setDownPressed(false);
        gameLoop.setLeftPressed(false);
        gameLoop.setRightPressed(false);

        gameLoop.start(); // Riavvia il motore di gioco
        gameCanvas.requestFocus(); // Ridà il focus al canvas per la tastiera
    }

    // handleQuit(): invocato dal pulsante "Termina partita".
    // Ferma il GameLoop e torna al menu principale.
    // In caso di errore nel caricamento del menu, logghiamo l'eccezione
    // a livello SEVERE invece di stampare lo stack trace su console.
    // Lo stage viene preso dal campo iniettato con setContext(),
    // così non serve un riferimento diretto al pulsante btnQuit
    @FXML
    private void handleQuit() {
        stopGame(); // Ferma il motore di gioco (con check null)
        try {
            // Torna al menu principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 700); // Ripristina la dimensione del menu
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            // Logging robusto: livello SEVERE, messaggio contestuale,
            // eccezione passata per avere lo stack trace completo nel log
            LOGGER.log(Level.SEVERE, "Errore nel ritorno al menu principale", e);
        }
    }
}