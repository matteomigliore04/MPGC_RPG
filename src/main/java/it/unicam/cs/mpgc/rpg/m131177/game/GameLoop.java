package it.unicam.cs.mpgc.rpg.m131177.game;

import it.unicam.cs.mpgc.rpg.m131177.controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Game Loop principale.
 * Gestisce aggiornamento logica e rendering a 60 FPS.
 */
public class GameLoop extends AnimationTimer {

    private final GraphicsContext gc;
    private final GameController controller;

    private long lastTime = 0;
    private double deltaTime = 0;
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    private Player player;

    // Stato dei tasti
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Altezza dell'HUD (zona proibita in alto)
    private static final double HUD_HEIGHT = 70;

    public GameLoop(GraphicsContext gc, GameController controller) {
        this.gc = gc;
        this.controller = controller;

        // Inizializza il player al centro, con limite HUD
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        this.player = new Player(canvasWidth, canvasHeight, HUD_HEIGHT);
    }

    @Override
    public void handle(long now) {
        // Calcolo Delta Time
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        deltaTime = (now - lastTime) / NANOS_PER_SECOND;
        lastTime = now;

        // Aggiorna logica
        update(deltaTime);

        // Renderizza
        render();
    }

    private void update(double dt) {
        // Aggiorna il player in base ai tasti premuti
        player.update(dt, upPressed, downPressed, leftPressed, rightPressed);
    }

    private void render() {
        // Pulisci il canvas
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Disegna il player
        player.render(gc);

        // Debug: mostra coordinate (opzionale, rimuovi in produzione)
        gc.setFill(Color.WHITE);
        gc.fillText(String.format("Pos: (%.0f, %.0f)", player.getX(), player.getY()), 10, gc.getCanvas().getHeight() - 10);
    }

    // Metodi per gestire gli input (chiamati dal GameController)
    public void setUpPressed(boolean pressed) { this.upPressed = pressed; }
    public void setDownPressed(boolean pressed) { this.downPressed = pressed; }
    public void setLeftPressed(boolean pressed) { this.leftPressed = pressed; }
    public void setRightPressed(boolean pressed) { this.rightPressed = pressed; }
}