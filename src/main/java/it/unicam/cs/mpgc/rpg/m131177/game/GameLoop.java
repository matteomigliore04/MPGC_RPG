package it.unicam.cs.mpgc.rpg.m131177.game;

import it.unicam.cs.mpgc.rpg.m131177.controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private List<Projectile> projectiles; // Lista di frecce attive

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
        this.projectiles = new ArrayList<>();

        // Inizializza il player al centro, con limite HUD
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        this.player = new Player(canvasWidth, canvasHeight, HUD_HEIGHT);
    }

    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        deltaTime = (now - lastTime) / NANOS_PER_SECOND;
        lastTime = now;

        update(deltaTime);
        render();
    }

    private void update(double dt) {
        player.update(dt, upPressed, downPressed, leftPressed, rightPressed);

        // Aggiorna proiettili...
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            if (!p.update(dt, gc.getCanvas().getWidth(), gc.getCanvas().getHeight())) {
                iterator.remove();
            }
        }

        // ✅ AGGIORNAMENTO HUD CON LE FRECCE
        controller.updateHUD(100, 1, 0, player.getCurrentArrows());
    }

    private void render() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Disegna i proiettili (sotto il player)
        for (Projectile p : projectiles) {
            p.render(gc);
        }

        // Disegna il player
        player.render(gc);
    }

    /**
     * Metodo chiamato dal controller quando il giocatore preme SPAZIO.
     * Crea una nuova freccia e la aggiunge alla lista.
     */
    public void shoot() {
        Projectile arrow = player.shoot();
        if (arrow != null) { // ✅ Aggiunge la freccia solo se il player ne aveva una
            projectiles.add(arrow);
        }
    }

    // Metodi per gestire gli input (chiamati dal GameController)
    public void setUpPressed(boolean pressed) { this.upPressed = pressed; }
    public void setDownPressed(boolean pressed) { this.downPressed = pressed; }
    public void setLeftPressed(boolean pressed) { this.leftPressed = pressed; }
    public void setRightPressed(boolean pressed) { this.rightPressed = pressed; }
}