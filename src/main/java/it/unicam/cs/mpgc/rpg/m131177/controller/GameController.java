package it.unicam.cs.mpgc.rpg.m131177.controller;

import it.unicam.cs.mpgc.rpg.m131177.game.GameLoop;
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import it.unicam.cs.mpgc.rpg.m131177.Main;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;

public class GameController {

    @FXML private Canvas gameCanvas;
    @FXML private Label lblHealth;
    @FXML private Label lblWave;
    @FXML private Label lblScore;

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
            KeyCode key = event.getCode();

            if (key == KeyCode.W || key == KeyCode.UP) gameLoop.setUpPressed(true);
            if (key == KeyCode.S || key == KeyCode.DOWN) gameLoop.setDownPressed(true);
            if (key == KeyCode.A || key == KeyCode.LEFT) gameLoop.setLeftPressed(true);
            if (key == KeyCode.D || key == KeyCode.RIGHT) gameLoop.setRightPressed(true);

            // Sparo con SPAZIO
            if (key == KeyCode.SPACE) {
                gameLoop.shoot();
                event.consume(); // Evita che lo spazio faccia scroll o altre azioni
            }
        });

        gameCanvas.getScene().setOnKeyReleased(event -> {
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
    public void updateHUD(int health, int wave, int score) {
        lblHealth.setText("❤️ Vita: " + health);
        lblWave.setText("🌊 Ondata: " + wave);
        lblScore.setText("⭐ Punti: " + score);
    }

    /**
     * Ferma il game loop quando si esce dalla schermata.
     */
    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}