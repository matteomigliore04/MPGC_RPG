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

    private Runnable onGameOver;

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

        // 1. Aggiorna proiettili
        Iterator<Projectile> projIterator = projectiles.iterator();
        while (projIterator.hasNext()) {
            Projectile p = projIterator.next();
            if (!p.update(dt, gc.getCanvas().getWidth(), gc.getCanvas().getHeight())) {
                projIterator.remove();
            }
        }

        // 2. Spawn nemici
        spawnEnemies(dt);

        // 3. Aggiorna nemici
        for (Enemy enemy : enemies) {
            enemy.update(dt, player);
        }

        // 4. COLLISIONI: Frecce → Nemici
        handleProjectileEnemyCollisions();

        // 5. COLLISIONI: Nemici → Player
        handleEnemyPlayerCollisions();

        // 6. Rimuovi nemici morti
        enemies.removeIf(enemy -> !enemy.isAlive());

        // 7. Aggiorna HUD con valori REALI
        controller.updateHUD(
                player.getCurrentHealth(),
                player.getScore(),
                player.getCurrentArrows()
        );
    }

    /**
     * Gestisce le collisioni tra frecce e nemici.
     * Se una freccia colpisce un nemico: il nemico prende danno, la freccia viene rimossa.
     * Se il nemico muore: il player guadagna punti e frecce.
     */
    private void handleProjectileEnemyCollisions() {
        Iterator<Projectile> projIterator = projectiles.iterator();

        while (projIterator.hasNext()) {
            Projectile p = projIterator.next();
            boolean arrowHit = false;

            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && CollisionDetector.isProjectileHitEnemy(p, enemy)) {
                    // La freccia ha colpito il nemico
                    boolean killed = enemy.takeDamage(25); // 25 danni per freccia (regolabile)
                    arrowHit = true;

                    if (killed) {
                        // Nemico ucciso: drop punti e frecce
                        player.addScore(enemy.getScoreValue());
                        player.collectArrows(enemy.getArrowDrop());
                    }
                    break; // Una freccia colpisce un solo nemico
                }
            }

            if (arrowHit) {
                projIterator.remove(); // Rimuovi la freccia che ha colpito
            }
        }
    }

    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    /**
     * Gestisce le collisioni tra nemici e player.
     * Se un nemico tocca il player: il player prende danno e il nemico viene rimosso.
     */
    private void handleEnemyPlayerCollisions() {
        Iterator<Enemy> enemyIterator = enemies.iterator();

        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            if (enemy.isAlive() && CollisionDetector.isEnemyHitPlayer(enemy, player)) {
                // Il nemico ha colpito il player
                boolean playerDied = player.takeDamage(enemy.getDamage());
                enemyIterator.remove(); // Rimuovi il nemico (si "sacrifica" colpendo il player)

                if (playerDied) {
                    this.stop(); // Ferma il game loop
                    if (onGameOver != null) {
                        onGameOver.run(); // Chiama il callback
                    }
                    return;
                }
                break; // Un nemico alla volta
            }
        }
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

    public int getScore() { return player.getScore(); }
    public int getCurrentWave() { return currentWave; } // Lo aggiungeremo nella Parte 2
    public int getEnemiesKilled() { return enemiesKilled; } // Lo aggiungeremo nella Parte 2
}