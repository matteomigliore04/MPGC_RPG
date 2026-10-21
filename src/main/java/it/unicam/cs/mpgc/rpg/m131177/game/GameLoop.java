package it.unicam.cs.mpgc.rpg.m131177.game;

// ============================================================
// IMPORT
// ============================================================
import it.unicam.cs.mpgc.rpg.m131177.game.enemies.WaveManager;
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
import java.util.logging.Level;
import java.util.logging.Logger;

// ============================================================
// GAME LOOP
// ============================================================
// Ciclo principale del gioco (eredita da AnimationTimer).
// Gestisce aggiornamento logico e rendering ad ogni frame.
public class GameLoop extends AnimationTimer {

    // ============================================================
    // LOGGER
    // ============================================================
    private static final Logger LOGGER = Logger.getLogger(GameLoop.class.getName());

    // ============================================================
    // COSTANTI
    // ============================================================
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    // ============================================================
    // DIPENDENZE E COLLEZIONI (final)
    // ============================================================
    private final GraphicsContext gc;
    private final GameController controller;

    private final List<Loot> loots;
    private final Player player;
    private final List<Projectile> projectiles;
    private final List<Enemy> enemies;
    private final EnemyFactory enemyFactory;
    private final WaveManager waveManager;

    private double enemyHealthMult = 1.0;
    private double enemySpeedMult = 1.0;
    private double spawnRateMult = 1.0;

    private double rewardMultiplier = 1.0;

    // ============================================================
    // STATO DEL LOOP E INPUT
    // ============================================================
    private long lastTime = 0;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // ============================================================
    // COSTRUTTORE
    // ============================================================
    public GameLoop(GraphicsContext gc, GameController controller, it.unicam.cs.mpgc.rpg.m131177.model.GameContext context) {
        this.gc = gc;
        this.controller = controller;
        this.projectiles = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.loots = new ArrayList<>();
        this.enemyFactory = new EnemyFactory();

        // Calcola la difficoltà prima di creare il WaveManager
        applyDifficulty(context.getDifficulty());

        // Ora passa il moltiplicatore al WaveManager
        this.waveManager = new WaveManager(spawnRateMult);

        this.player = new Player(GameConstants.CANVAS_WIDTH, GameConstants.CANVAS_HEIGHT);
    }

    private void applyDifficulty(String difficulty) {
        if (difficulty == null) return;

        switch (difficulty) {
            case "Easy":
                enemyHealthMult = 0.8;
                enemySpeedMult = 0.8;
                spawnRateMult = 0.8;
                rewardMultiplier = 0.8; // Meno punti e loot
                break;
            case "Hard":
                enemyHealthMult = 1.3;
                enemySpeedMult = 1.2;
                spawnRateMult = 1.3;
                rewardMultiplier = 1.5; // +50% punti e loot
                break;
            case "Extreme":
                enemyHealthMult = 1.6;
                enemySpeedMult = 1.5;
                spawnRateMult = 1.6;
                rewardMultiplier = 2.0; // Raddoppia punti e loot
                break;
            case "Medium":
            default:
                rewardMultiplier = 1.0;
                break;
        }
    }

    // ============================================================
    // HANDLE (loop principale di JavaFX)
    // ============================================================
    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        // Sostituito il campo 'deltaTime' con una variabile locale
        double deltaTime = (now - lastTime) / NANOS_PER_SECOND;
        lastTime = now;

        update(deltaTime);
        render();
    }

    // ============================================================
    // AGGIORNAMENTO LOGICA
    // ============================================================
    private void update(double dt) {
        // 1. Muovi il player
        player.update(dt, upPressed, downPressed, leftPressed, rightPressed);

        // 2. Risolvi le collisioni Player-Nemico spingendo il player fuori (Push-Out)
        // Risolvi collisioni Player-Nemico spingendo i NEMICI via
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && CollisionDetector.checkCollision(
                    player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                    enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight())) {

                // Calcola i centri
                double pCenterX = player.getX() + player.getWidth() / 2;
                double pCenterY = player.getY() + player.getHeight() / 2;
                double eCenterX = enemy.getX() + enemy.getWidth() / 2;
                double eCenterY = enemy.getY() + enemy.getHeight() / 2;

                // Vettore dal Player verso il Nemico
                double dx = eCenterX - pCenterX;
                double dy = eCenterY - pCenterY;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist == 0) {
                    dx = 1; dy = 0; dist = 1; // Direzione di default se perfettamente sovrapposti
                }

                // Distanza minima + un piccolo margine di 5 pixel per creare il "varco"
                double minDist = (player.getWidth() / 2) + (enemy.getWidth() / 2) + 5;

                // Calcola la nuova posizione del NEMICO, fuori dal player
                double newEx = pCenterX + (dx / dist) * minDist - enemy.getWidth() / 2;
                double newEy = pCenterY + (dy / dist) * minDist - enemy.getHeight() / 2;

                // Applica la spinta al nemico
                enemy.setPosition(
                        Math.clamp(newEx, GameConstants.PLAYABLE_MIN_X, GameConstants.PLAYABLE_MAX_X),
                        Math.clamp(newEy, GameConstants.PLAYABLE_MIN_Y, GameConstants.PLAYABLE_MAX_Y)
                );
            }
        }

        // 2. MUOVI LE FRECCE
        projectiles.removeIf(p -> !p.update(dt, gc.getCanvas().getWidth(), gc.getCanvas().getHeight()));

        // 3. AGGIORNA I LOOT
        for (Loot loot : loots) {
            loot.update(dt);
        }

        // 4. SPAWN DEI NEMICI (se mancava, ecco perché non apparivano!)
        if (waveManager.update(dt)) {
            Enemy newEnemy = enemyFactory.createEnemy(waveManager.getCurrentWave(), enemyHealthMult, enemySpeedMult, rewardMultiplier);
            enemies.add(newEnemy);
        }

        // 5. BONUS FRECCE
        if (waveManager.consumeBonusPending()) {
            int bonus = waveManager.getArrowWaveBonus();
            player.collectArrows(bonus);
            LOGGER.info("Bonus ondata: +" + bonus + " frecce!");
        }

        // 6. MUOVI I NEMICI (passando la lista per la separazione)
        for (Enemy enemy : enemies) {
            enemy.update(dt, player, enemies);
        }

        // 7. GESTIONE COLLISIONI
        handleProjectileEnemyCollisions();
        handleEnemyPlayerCollisions();
        handleLootCollection();

        // 8. RIMUOVI NEMICI MORTI
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (!enemy.isAlive()) {
                waveManager.onEnemyKilled();
                enemyIterator.remove();
            }
        }

        // 9. AGGIORNA HUD
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

    // ============================================================
    // COLLISIONI — NEMICO vs PLAYER
    // ============================================================
    private void handleEnemyPlayerCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && CollisionDetector.isEnemyAttackingPlayer(enemy, player)) {
                LOGGER.info("Collisione rilevata con: " + enemy.getName());

                boolean isDead = player.takeDamage(enemy.getDamage());
                LOGGER.info("Risultato di takeDamage: " + isDead);

                if (isDead) {
                    LOGGER.info("Eseguo showGameOver e stop del loop!");
                    try {
                        controller.showGameOver(player.getScore());
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Errore critico in showGameOver", e);
                    }
                    this.stop();
                }
                break;
            }
        }
    }

    // ============================================================
    // COLLISIONI — PROIETTILE vs NEMICO
    // ============================================================
    private void handleProjectileEnemyCollisions() {
        Iterator<Projectile> projIterator = projectiles.iterator();

        while (projIterator.hasNext()) {
            Projectile p = projIterator.next();
            boolean arrowHit = false;

            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && CollisionDetector.isProjectileHitEnemy(p, enemy)) {
                    Loot droppedLoot = enemy.takeDamage(25); // 25 danni per freccia

                    if (droppedLoot != null) {
                        loots.add(droppedLoot); // Aggiungi il loot a terra
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

    // ============================================================
    // COLLISIONI — LOOT vs PLAYER
    // ============================================================
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

    // ============================================================
    // RENDERING
    // ============================================================
    private void render() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // 1. Disegna loot (sempre sotto a tutto)
        for (Loot loot : loots) {
            loot.render(gc);
        }

        // 2. Unisci nemici e player in una lista temporanea per ordinarli
        List<Object> entitiesToDraw = new ArrayList<>(enemies);
        entitiesToDraw.add(player);

        // 3. Ordina per coordinata Y (chi ha Y più alta viene disegnato dopo, quindi sta sopra)
        entitiesToDraw.sort((e1, e2) -> {
            double y1 = (e1 instanceof Enemy) ? ((Enemy) e1).getY() : ((Player) e1).getY();
            double y2 = (e2 instanceof Enemy) ? ((Enemy) e2).getY() : ((Player) e2).getY();
            return Double.compare(y1, y2);
        });

        // 4. Disegna entità in ordine di profondità
        for (Object entity : entitiesToDraw) {
            if (entity instanceof Enemy) {
                ((Enemy) entity).render(gc);
            } else if (entity instanceof Player) {
                ((Player) entity).render(gc);
            }
        }

        // 5. Disegna proiettili (sempre sopra a tutti)
        for (Projectile p : projectiles) {
            p.render(gc);
        }
    }

    // ============================================================
    // AZIONI ESTERNE (input e utility)
    // ============================================================
    public void shoot() {
        Projectile arrow = player.shoot();
        if (arrow != null) {
            projectiles.add(arrow);
        }
    }

    // Resetta il timer interno per evitare deltaTime enormi dopo una pausa
    public void resetDelta() {
        lastTime = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void setUpPressed(boolean pressed) { this.upPressed = pressed; }
    public void setDownPressed(boolean pressed) { this.downPressed = pressed; }
    public void setLeftPressed(boolean pressed) { this.leftPressed = pressed; }
    public void setRightPressed(boolean pressed) { this.rightPressed = pressed; }
}