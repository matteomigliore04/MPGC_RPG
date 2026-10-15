package it.unicam.cs.mpgc.rpg.m131177.game;
import it.unicam.cs.mpgc.rpg.m131177.game.loots.Loot;


import it.unicam.cs.mpgc.rpg.m131177.controller.GameController;
import it.unicam.cs.mpgc.rpg.m131177.game.enemies.Enemy;
import it.unicam.cs.mpgc.rpg.m131177.game.enemies.EnemyFactory;
import it.unicam.cs.mpgc.rpg.m131177.game.players.Player;
import it.unicam.cs.mpgc.rpg.m131177.game.players.Projectile;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {

    private List<Loot> loots;

    private final GraphicsContext gc;
    private final GameController controller;

    private long lastTime = 0;
    private double deltaTime = 0;
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    private Player player;
    private List<Projectile> projectiles;
    private List<Enemy> enemies;
    private EnemyFactory enemyFactory;

    // Aggiungi questa:
    private WaveManager waveManager;

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
        this.waveManager = new WaveManager();

        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        this.player = new Player(canvasWidth, canvasHeight, HUD_HEIGHT);

        // Nel costruttore:
        this.loots = new ArrayList<>();
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

        // Aggiorna loot
        for (Loot loot : loots) {
            loot.update(dt);
        }

        // ✅ NUOVO: Aggiorna il WaveManager
        if (waveManager.update(dt)) {
            // Il manager ha segnalato di spawnare un nemico
            Enemy newEnemy = enemyFactory.createEnemy(
                    gc.getCanvas().getWidth(),
                    gc.getCanvas().getHeight(),
                    HUD_HEIGHT,
                    waveManager.getCurrentWave()
            );
            enemies.add(newEnemy);
        }

        // Aggiorna nemici
        for (Enemy enemy : enemies) {
            enemy.update(dt, player);
        }

        // Collisioni
        handleProjectileEnemyCollisions();
        handleEnemyPlayerCollisions();
        handleLootCollection(); // ✅ Nuova collisione

        // Rimuovi nemici morti e aggiorna il WaveManager
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (!enemy.isAlive()) {
                waveManager.onEnemyKilled(); // ✅ Notifica al manager
                enemyIterator.remove();
            }
        }

        // Aggiorna HUD
        controller.updateHUD(
                player.getCurrentHealth(),
                player.getCurrentShield(),
                waveManager.getCurrentWave(),
                player.getScore(),
                player.getCurrentArrows(),
                player.getHealthPotions(),
                player.getShieldPotions()
        );
    }

    private void handleEnemyPlayerCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && CollisionDetector.isEnemyAttackingPlayer(enemy, player)) {
                System.out.println("[DEBUG GAMELOOP] Collisione rilevata con: " + enemy.getName());

                boolean isDead = player.takeDamage(enemy.getDamage());
                System.out.println("[DEBUG GAMELOOP] Risultato di takeDamage: " + isDead);

                if (isDead) {
                    System.out.println("[DEBUG GAMELOOP] ESEGUO showGameOver e stop del loop!");
                    try {
                        controller.showGameOver(player.getScore());
                    } catch (Exception e) {
                        System.out.println("[DEBUG GAMELOOP] ERRORE CRITICO in showGameOver: " + e.getMessage());
                        e.printStackTrace();
                    }
                    this.stop();
                }
                break;
            }
        }
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
                    Loot droppedLoot = enemy.takeDamage(25); // 25 danni per freccia

                    if (droppedLoot != null) {
                        loots.add(droppedLoot); // ✅ Aggiungi il loot a terra
                        player.addScore(enemy.getScoreValue());
                    }

                    arrowHit = true;
                    break;
                }
            }

            if (arrowHit) {
                projIterator.remove(); // Rimuovi la freccia che ha colpito
            }
        }
    }

    // Aggiungi questo metodo per la raccolta dei loot:
    private void handleLootCollection() {
        Iterator<Loot> lootIterator = loots.iterator();
        while (lootIterator.hasNext()) {
            Loot loot = lootIterator.next();

            if (!loot.isCollected() && CollisionDetector.checkCollision(
                    loot.getX(), loot.getY(), loot.getSize(), loot.getSize(),
                    player.getX(), player.getY(), player.getWidth(), player.getHeight())) {

                loot.collect(player);
                lootIterator.remove();
            }
        }
    }

    private void render() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Disegna loot (sotto i nemici)
        for (Loot loot : loots) {
            loot.render(gc);
        }

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

    public Player getPlayer() {
        return player;
    }

    public void setUpPressed(boolean pressed) { this.upPressed = pressed; }
    public void setDownPressed(boolean pressed) { this.downPressed = pressed; }
    public void setLeftPressed(boolean pressed) { this.leftPressed = pressed; }
    public void setRightPressed(boolean pressed) { this.rightPressed = pressed; }
}