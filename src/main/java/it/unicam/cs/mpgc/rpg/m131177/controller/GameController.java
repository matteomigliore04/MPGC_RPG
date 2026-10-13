package it.unicam.cs.mpgc.rpg.m131177.controller;

import it.unicam.cs.mpgc.rpg.m131177.game.GameLoop;
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;          // ✅ AGGIUNGI QUESTO
import javafx.scene.Parent;             // ✅ AGGIUNGI QUESTO
import javafx.scene.Scene;
import javafx.scene.layout.VBox;        // ✅ AGGIUNGI QUESTO (Risolve l'errore principale)

public class GameController {

    @FXML private Canvas gameCanvas;
    @FXML private Label lblHealth;
    @FXML private Label lblWave;
    @FXML private Label lblScore;
    @FXML private Label lblArrows;
    @FXML private VBox pauseMenu;
    @FXML private Button btnResume;
    @FXML private Button btnQuit;

    private boolean isPaused = false;
    private GameContext context;
    private Stage stage;
    private GameLoop gameLoop;

    public void setContext(GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Inizializza e avvia il Game Loop
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gameLoop = new GameLoop(gc, this);
        gameLoop.start();
    }

    public void setupKeyboardInput() {
            gameCanvas.getScene().setOnKeyPressed(event -> {
                // ✅ Se il gioco è in pausa, ignora tutti i tasti tranne ESC
                if (isPaused) {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        togglePause();
                    }
                    return;
                }

                KeyCode key = event.getCode();

                // Movimento
                if (key == KeyCode.W || key == KeyCode.UP) gameLoop.setUpPressed(true);
                if (key == KeyCode.S || key == KeyCode.DOWN) gameLoop.setDownPressed(true);
                if (key == KeyCode.A || key == KeyCode.LEFT) gameLoop.setLeftPressed(true);
                if (key == KeyCode.D || key == KeyCode.RIGHT) gameLoop.setRightPressed(true);

                // Sparo
                if (key == KeyCode.SPACE) {
                    gameLoop.shoot();
                    event.consume();
                }

                // ✅ Tasto Pausa
                if (key == KeyCode.ESCAPE) {
                    togglePause();
                }
            });

            gameCanvas.getScene().setOnKeyReleased(event -> {
                if (isPaused) return; // Ignora rilascio tasti se in pausa

                KeyCode key = event.getCode();
                if (key == KeyCode.W || key == KeyCode.UP) gameLoop.setUpPressed(false);
                if (key == KeyCode.S || key == KeyCode.DOWN) gameLoop.setDownPressed(false);
                if (key == KeyCode.A || key == KeyCode.LEFT) gameLoop.setLeftPressed(false);
                if (key == KeyCode.D || key == KeyCode.RIGHT) gameLoop.setRightPressed(false);
            });

            gameCanvas.requestFocus();
    }

    /**
     * Metodo chiamato dal GameLoop per aggiornare i testi dell'HUD.
     */
    public void updateHUD(int health, int wave, int score, int arrows) {
        lblHealth.setText("Vita: " + health);
        lblArrows.setText("Frecce: " + arrows + " / 100"); // ✅ Nuovo formato
        lblScore.setText("Punti: " + score);
        lblWave.setText("Ondata: " + wave);
    }

    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    /**
     * Alterna lo stato di pausa.
     */
    private void togglePause() {
        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    public void pauseGame() {
        isPaused = true;
        gameLoop.stop(); // Ferma il motore di gioco
        pauseMenu.setVisible(true);
        pauseMenu.setManaged(true); // Lo rende cliccabile
    }

    @FXML
    private void handleResume() {
        resumeGame();
    }

    public void resumeGame() {
        isPaused = false;
        pauseMenu.setVisible(false);
        pauseMenu.setManaged(false);
        gameLoop.start(); // Riavvia il motore di gioco
        gameCanvas.requestFocus(); // Ridà il focus al canvas per la tastiera
    }

    @FXML
    private void handleQuit() {
        gameLoop.stop(); // Ferma definitivamente il loop
        try {
            // Torna al menu principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnQuit.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700); // Ripristina la dimensione del menu
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}