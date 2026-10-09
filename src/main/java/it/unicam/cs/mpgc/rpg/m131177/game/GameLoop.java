package it.unicam.cs.mpgc.rpg.m131177.game;

import it.unicam.cs.mpgc.rpg.m131177.controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Game Loop principale.
 * Si occupa di aggiornare la logica di gioco e renderizzare la scena
 * a una frequenza costante (es. 60 FPS).
 */
public class GameLoop extends AnimationTimer {

    private final GraphicsContext gc;
    private final GameController controller;

    private long lastTime = 0;
    private double deltaTime = 0;

    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    public GameLoop(GraphicsContext gc, GameController controller) {
        this.gc = gc;
        this.controller = controller;
    }

    @Override
    public void handle(long now) {
        // 1. Calcolo del Delta Time (tempo trascorso dall'ultimo frame in secondi)
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        deltaTime = (now - lastTime) / NANOS_PER_SECOND;
        lastTime = now;

        // 2. Aggiorna la logica (movimenti, collisioni, spawn nemici)
        update(deltaTime);

        // 3. Disegna la scena
        render();
    }

    private void update(double dt) {
        // TODO: Qui aggiorneremo Player, Nemici, Proiettili
        // Esempio futuro: player.update(dt); enemies.forEach(e -> e.update(dt));
    }

    private void render() {
        // Pulisce il canvas dal frame precedente
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // TODO: Qui disegneremo Player, Nemici, Proiettili

        // --- CODICE TEMPORANEO PER TEST ---
        // Disegna un cerchio verde al centro (la nostra "cima d'erba" provvisoria)
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(380, 280, 40, 40);

        // Mostra il delta time per debug
        gc.setFill(Color.WHITE);
        gc.fillText("Delta Time: " + String.format("%.4f", deltaTime), 10, 30);
        // ----------------------------------
    }
}