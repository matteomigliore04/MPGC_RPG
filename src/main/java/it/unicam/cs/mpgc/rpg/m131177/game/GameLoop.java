package it.unicam.cs.mpgc.rpg.m131177.game;

import it.unicam.cs.mpgc.rpg.m131177.controller.GameController;
import it.unicam.cs.mpgc.rpg.m131177.game.enemies.Enemy;
import it.unicam.cs.mpgc.rpg.m131177.game.enemies.EnemyFactory;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {

    private final GraphicsContext gc;
    private final GameController controller;

    private long lastTime = 0;
    private double deltaTime = 0;
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    private Player player;
    private List<Projectile> projectiles;
    private List<Enemy> enemies;
    private EnemyFactory enemyFactory;

    // Sistema di spawn
    private double spawnTimer = 0;
    private double spawnInterval = 3.0; // Un nemico ogni 3 secondi
    private int enemiesSpawned = 0;
    private int maxEnemiesPerWave = 5;

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private static final double HUD_HEIGHT = 70;

    public GameLoop(GraphicsContext gc, GameController controller) {
        this.gc = gc;
        this.controller = controller;
        this.projectiles = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.enemyFactory = new EnemyFactory();

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

        // Aggiorna proiettili
        Iterator<Projectile> projIterator = projectiles.iterator();
        while (projIterator.hasNext()) {
            Projectile p = projIterator.next();
            if (!p.update(dt, gc.getCanvas().getWidth(), gc.getCanvas().getHeight())) {
                projIterator.remove();
            }
        }

        // Spawn nemici
        spawnEnemies(dt);

        // Aggiorna nemici
        for (Enemy enemy : enemies) {
            enemy.update(dt, player);
        }

        // Rimuovi nemici morti
        enemies.removeIf(enemy -> !enemy.isAlive());

        // Aggiorna HUD
        controller.updateHUD(100, 0, 0, 0, player.getCurrentArrows());
    }

    /**
     * Gestisce lo spawn dei nemici in base al timer.
     */
    private void spawnEnemies(double dt) {
        if (enemiesSpawned >= maxEnemiesPerWave) return;

        spawnTimer += dt;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            Enemy newEnemy = enemyFactory.createEnemy(
                    gc.getCanvas().getWidth(),
                    gc.getCanvas().getHeight(),
                    HUD_HEIGHT,
                    "Germoglio" // Per ora fisso, poi useremo la difficoltà dal context
            );
            enemies.add(newEnemy);
            enemiesSpawned++;
        }
    }

    private void render() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Disegna nemici
        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }

        // Disegna proiettili
        for (Projectile p : projectiles) {
            p.render(gc);
        }

        // Disegna player
        player.render(gc);
    }

    public void shoot() {
        Projectile arrow = player.shoot();
        if (arrow != null) {
            projectiles.add(arrow);
        }
    }

    public void setUpPressed(boolean pressed) { this.upPressed = pressed; }
    public void setDownPressed(boolean pressed) { this.downPressed = pressed; }
    public void setLeftPressed(boolean pressed) { this.leftPressed = pressed; }
    public void setRightPressed(boolean pressed) { this.rightPressed = pressed; }
}