package it.unicam.cs.mpgc.rpg.m131177.controller;

// ============================================================
// IMPORT
// ============================================================
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
import javafx.scene.control.Button;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.unicam.cs.mpgc.rpg.m131177.model.StatsManager; // Aggiungi l'import in cima
import it.unicam.cs.mpgc.rpg.m131177.audio.AudioManager;

// Controller della schermata di gioco.
// Gestisce GameLoop, HUD, input tastiera e menu di pausa/game over.
public class GameController {

    // ============================================================
    // LOGGER
    // ============================================================
    // Logger di classe per errori, info e tracciamento eventi
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    // ============================================================
    // RIFERIMENTI FXML — GIOCO E HUD
    // ============================================================
    // Canvas su cui il GameLoop disegna player, mostri e proiettili
    @FXML private Canvas gameCanvas;
    // Etichette dell'HUD
    @FXML private Label lblHealth;
    @FXML private Label lblWave;
    @FXML private Label lblScore;
    @FXML private Label lblArrows;
    @FXML private Label lblShield;
    @FXML private Label lblHealthPotions;
    @FXML private Label lblShieldPotions;

    // ============================================================
    // RIFERIMENTI FXML — MENU DI PAUSA E GAME OVER
    // ============================================================
    // Menu di pausa (btnResume/btnQuit funzionano via onAction, non servono campi)
    @FXML private VBox pauseMenu;
    // Menu di game over
    @FXML private VBox gameOverMenu;
    @FXML private Label lblFinalScore;
    @FXML private Button btnRestart;
    @FXML private Button btnMainMenu;

    // ============================================================
    // STATO DEL CONTROLLER
    // ============================================================
    // isPaused: true quando il menu di pausa è aperto
    // context: stato condiviso della partita
    // stage: finestra principale per la navigazione
    // gameLoop: motore di gioco (AnimationTimer)
    private boolean isPaused = false;
    private GameContext context;
    private Stage stage;
    private GameLoop gameLoop;

    // ============================================================
    // INIEZIONE CONTESTO E STAGE
    // ============================================================
    // Chiamato dal DifficultyController dopo il caricamento FXML
    public void setContext(GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;

        // Crea e avvia il Game Loop passando il contesto (che contiene la difficoltà)
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gameLoop = new GameLoop(gc, this, context);
        gameLoop.start();
    }

    // ============================================================
    // INIZIALIZZAZIONE
    // ============================================================
    @FXML
    public void initialize() {
        // Avvia la musica della partita
        AudioManager.getInstance().playGameMusic();
    }

    // ============================================================
    // INPUT DA TASTIERA
    // ============================================================
    // Collega i gestori di eventi alla scena.
    // - In pausa: solo ESC accettato
    // - Q/E: pozioni cura/scudo
    // - WASD/frecce: movimento
    // - SPAZIO: sparo
    // - ESC: pausa
    public void setupKeyboardInput() {
        gameCanvas.getScene().setOnKeyPressed(event -> {
            // Se in pausa, solo ESC riprende il gioco
            if (isPaused) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }
                return;
            }

            KeyCode key = event.getCode();

            // Pozione cura con Q
            if (key == KeyCode.Q) {
                if (gameLoop.getPlayer().useHealthPotion()) {
                    // Log dell'uso della pozione cura (sostituisce System.out.println)
                    LOGGER.info("❤️ Pozione cura usata!");
                }
            }

            // Pozione scudo con E
            if (key == KeyCode.E) {
                if (gameLoop.getPlayer().useShieldPotion()) {
                    // Log dell'uso della pozione scudo (sostituisce System.out.println)
                    LOGGER.info("🛡️ Pozione scudo usata!");
                }
            }

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

            // Pausa
            if (key == KeyCode.ESCAPE) {
                togglePause();
            }
        });

        // Rilascio tasti: reset dei flag di movimento (ignorato se in pausa)
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

    // ============================================================
    // AGGIORNAMENTO HUD
    // ============================================================
    // Chiamato dal GameLoop ad ogni frame per aggiornare le etichette
    public void updateHUD(int health, int shield, int wave, int score,
                          int arrows, int healthPotions, int shieldPotions) {
        lblHealth.setText("Vita: " + health + "/100");
        lblShield.setText("Scudo: " + shield + "/50");
        lblWave.setText("Ondata: " + wave);
        lblScore.setText("Punti: " + score);
        lblArrows.setText("Frecce: " + arrows + "/150");
        lblHealthPotions.setText("Cure: " + healthPotions + "/10");
        lblShieldPotions.setText("Scudi: " + shieldPotions + "/10");
    }

    // ============================================================
    // GESTIONE DEL GIOCO
    // ============================================================
    // stopGame(): ferma il GameLoop (con check null)
    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    // togglePause(): alterna pausa/ripresa
    private void togglePause() {
        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    // pauseGame(): ferma il loop, resetta i flag di movimento
    // (per evitare il "ghost input") e mostra il menu di pausa
    public void pauseGame() {
        isPaused = true;
        gameLoop.stop();

        // Reset flag di movimento per evitare "ghost input" alla ripresa
        gameLoop.setUpPressed(false);
        gameLoop.setDownPressed(false);
        gameLoop.setLeftPressed(false);
        gameLoop.setRightPressed(false);

        pauseMenu.setVisible(true);
        pauseMenu.setManaged(true);
    }

    // handleResume(): invocato dal pulsante "Riprendi partita"
    @FXML
    private void handleResume() {
        resumeGame();
    }

    // resumeGame(): chiude il menu, resetta il delta timer
    // e riavvia il GameLoop
    public void resumeGame() {
        isPaused = false;
        pauseMenu.setVisible(false);
        pauseMenu.setManaged(false);

        gameLoop.resetDelta();   // Resetta il timer prima di ripartire
        gameLoop.start();        // Riavvia il motore di gioco
        gameCanvas.requestFocus();
    }

    // ============================================================
    // GESTIONE GAME OVER
    // ============================================================
    // showGameOver(): ferma il gioco e mostra il menu di game over
    // con il punteggio finale
    public void showGameOver(int finalScore, int finalWave, int timePlayed, boolean isWin,
                             int goblins, int zombies, int skeletons) {
        isPaused = true;
        gameLoop.stop();

        // Ferma la musica di gioco al Game Over (o potresti metterne una di game over)
        AudioManager.getInstance().stopMusic();
        AudioManager.getInstance().playGameOver();

        String difficulty = (context != null && context.getDifficulty() != null) ? context.getDifficulty() : "Sconosciuta";

        // Salva tutto su file
        StatsManager.saveRecord(finalScore, finalWave, difficulty, timePlayed, isWin, goblins, zombies, skeletons);

        lblFinalScore.setText("Punti Finali: " + finalScore);
        gameOverMenu.setVisible(true);
        gameOverMenu.setManaged(true);
    }

    // handleRestart(): ricarica game.fxml per resettare lo stato
    // e reinizializza il nuovo controller con il context esistente
    @FXML
    private void handleRestart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            Parent root = loader.load();

            GameController newController = loader.getController();
            Stage currentStage = (Stage) btnMainMenu.getScene().getWindow();

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            currentStage.setScene(scene);

            // Inizializza il nuovo controller con il context esistente
            newController.setContext(this.context, currentStage);
            newController.setupKeyboardInput();

        } catch (Exception e) {
            // Logging robusto invece di printStackTrace()
            LOGGER.log(Level.SEVERE, "Errore durante il restart della partita", e);
        }
    }

    // ============================================================
    // GESTIONE RITORNO AL MENU
    // ============================================================
    // handleQuit(): ferma il gioco e torna al menu principale
    @FXML
    private void handleQuit() {
        stopGame(); // Ferma il motore di gioco (con check null)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel ritorno al menu principale", e);
        }
    }

    // handleMainMenu(): ferma il gioco e torna al menu principale
    @FXML
    private void handleMainMenu() {
        stopGame(); // Ferma il motore di gioco (con check null)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) btnMainMenu.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            currentStage.setScene(scene);
        } catch (Exception e) {
            // Logging robusto invece di printStackTrace()
            LOGGER.log(Level.SEVERE, "Errore nel ritorno al menu principale", e);
        }
    }
}