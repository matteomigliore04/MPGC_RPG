package it.unicam.cs.mpgc.rpg.m131177.controller;

import it.unicam.cs.mpgc.rpg.m131177.Main;
import it.unicam.cs.mpgc.rpg.m131177.game.GameLoop;
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

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