package it.unicam.cs.mpgc.rpg.m131177.game;

// Import di progetto
import it.unicam.cs.mpgc.rpg.m131177.game.enemies.*;
import it.unicam.cs.mpgc.rpg.m131177.game.loots.Loot;
import it.unicam.cs.mpgc.rpg.m131177.game.players.CollisionDetector;
import it.unicam.cs.mpgc.rpg.m131177.game.players.Player;
import it.unicam.cs.mpgc.rpg.m131177.game.players.Projectile;
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;

// Import JavaFX
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

// Import Java standard
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ciclo principale del gioco (estende AnimationTimer).
 * Gestisce aggiornamento logico e rendering ad ogni frame.
 */
public class GameLoop extends AnimationTimer {

    // Logger e costanti
    private static final Logger LOGGER = Logger.getLogger(GameLoop.class.getName());
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    // Dipendenze e collezioni (immutabili dopo la costruzione)
    private final GraphicsContext gc;
    private final GameLoopObserver observer;
    private final List<Loot> loots;
    private final Player player;
    private final List<Projectile> projectiles;
    private final List<Enemy> enemies;
    private final EnemyFactory enemyFactory;
    private final WaveManager waveManager;
    private final DifficultySettings difficultySettings;

    // Contatori uccisioni e tempo totale
    private int goblinsKilled = 0;
    private int zombiesKilled = 0;
    private int skeletonsKilled = 0;
    private double totalTimePlayed = 0;

    // Stato del loop e input
    private long lastTime = 0;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public GameLoop(GraphicsContext gc, GameLoopObserver observer, GameContext context) {
        this.gc = gc;
        this.observer = observer;
        this.projectiles = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.loots = new ArrayList<>();
        this.enemyFactory = new EnemyFactory();

        this.difficultySettings = DifficultySettings.from(context.getDifficulty());
        this.waveManager = new WaveManager(difficultySettings.spawnRateMultiplier());

        this.player = new Player(GameConstants.CANVAS_WIDTH, GameConstants.CANVAS_HEIGHT);
    }

    // Loop principale JavaFX: calcola deltaTime e aggiorna/disegna
    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        double deltaTime = (now - lastTime) / NANOS_PER_SECOND;
        lastTime = now;

        update(deltaTime);
        render();
    }

    // Aggiornamento della logica di gioco
    private void update(double dt) {
        totalTimePlayed += dt;

        // 1. Muovi il player
        player.update(dt, upPressed, downPressed, leftPressed, rightPressed);

        // 2. Risolvi collisioni Player-Nemico spingendo i nemici fuori (Push-Out)
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && CollisionDetector.checkCollision(
                    player.getX(), player.getY(), player.getWidth(), player.getHeight(),
                    enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight())) {

                // Calcola i centri
                double pCenterX = player.getX() + player.getWidth() / 2;
                double pCenterY = player.getY() + player.getHeight() / 2;
                double eCenterX = enemy.getX() + enemy.getWidth() / 2;
                double eCenterY = enemy.getY() + enemy.getHeight() / 2;

                // Vettore dal player verso il nemico
                double dx = eCenterX - pCenterX;
                double dy = eCenterY - pCenterY;
                double dist = Math.sqrt(dx * dx + dy * dy);

                // Direzione di default se perfettamente sovrapposti
                if (dist == 0) {
                    dx = 1; dy = 0; dist = 1;
                }

                // Distanza minima + margine di 5 px per creare il "varco"
                double minDist = (player.getWidth() / 2) + (enemy.getWidth() / 2) + 5;

                // Nuova posizione del nemico, fuori dal player
                double newEx = pCenterX + (dx / dist) * minDist - enemy.getWidth() / 2;
                double newEy = pCenterY + (dy / dist) * minDist - enemy.getHeight() / 2;

                // Applica la spinta al nemico (clamp nei limiti giocabili)
                enemy.setPosition(
                        Math.clamp(newEx, GameConstants.PLAYABLE_MIN_X, GameConstants.PLAYABLE_MAX_X),
                        Math.clamp(newEy, GameConstants.PLAYABLE_MIN_Y, GameConstants.PLAYABLE_MAX_Y)
                );
            }
        }

        // 2. Muovi le frecce (rimuovi quelle fuori schermo)
        projectiles.removeIf(p -> !p.update(dt, gc.getCanvas().getWidth(), gc.getCanvas().getHeight()));

        // 3. Aggiorna i loot
        for (Loot loot : loots) {
            loot.update(dt);
        }

        // 4. Spawn dei nemici
        if (waveManager.update(dt)) {
            Enemy newEnemy = enemyFactory.createEnemy(
                    waveManager.getCurrentWave(),
                    difficultySettings.enemyHealthMultiplier(),
                    difficultySettings.enemySpeedMultiplier(),
                    difficultySettings.rewardMultiplier()
            );
            enemies.add(newEnemy);
        }

        // 5. Bonus frecce a fine ondata
        if (waveManager.consumeBonusPending()) {
            int bonus = waveManager.getArrowWaveBonus();
            player.collectArrows(bonus);
            LOGGER.info("Bonus ondata: +" + bonus + " frecce!");
        }

        // 6. Muovi i nemici (passando la lista per la separazione)
        for (Enemy enemy : enemies) {
            enemy.update(dt, player, enemies);
        }

        // 7. Gestione collisioni
        handleProjectileEnemyCollisions();
        handleEnemyPlayerCollisions();
        handleLootCollection();

        // 8. Rimuovi nemici morti e conta le uccisioni per tipo
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (!enemy.isAlive()) {
                waveManager.onEnemyKilled();

                switch (enemy) {
                    case Goblin g -> goblinsKilled++;
                    case Zombie z -> zombiesKilled++;
                    case Skeleton s -> skeletonsKilled++;
                    default -> { }
                }

                enemyIterator.remove();
            }
        }

        // 9. Aggiorna HUD
        observer.updateHUD(
                player.getCurrentHealth(),
                player.getCurrentShield(),
                waveManager.getCurrentWave(),
                player.getScore(),
                player.getCurrentArrows(),
                player.getHealthPotions(),
                player.getShieldPotions()
        );
    }

    // Collisioni Nemico vs Player
    private void handleEnemyPlayerCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && CollisionDetector.isEnemyAttackingPlayer(enemy, player)) {
                LOGGER.info("Collisione rilevata con: " + enemy.getName());

                boolean isDead = player.takeDamage(enemy.getDamage());
                LOGGER.info("Risultato di takeDamage: " + isDead);

                if (isDead) {
                    LOGGER.info("Eseguo showGameOver e stop del loop!");
                    try {
                        // Vittoria se si supera l'ondata 10
                        boolean isWin = waveManager.getCurrentWave() >= 10;
                        observer.showGameOver(player.getScore(), waveManager.getCurrentWave(),
                                (int) totalTimePlayed, isWin, goblinsKilled, zombiesKilled, skeletonsKilled);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Errore critico in showGameOver", e);
                    }
                    this.stop();
                }
                break;
            }
        }
    }

    // Collisioni Proiettile vs Nemico
    private void handleProjectileEnemyCollisions() {
        Iterator<Projectile> projIterator = projectiles.iterator();

        while (projIterator.hasNext()) {
            Projectile p = projIterator.next();
            boolean arrowHit = false;

            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && CollisionDetector.isProjectileHitEnemy(p, enemy)) {
                    Loot droppedLoot = enemy.takeDamage(25); // 25 danni per freccia

                    if (droppedLoot != null) {
                        loots.add(droppedLoot);
                        player.addScore(enemy.getScoreValue());
                    }

                    arrowHit = true;
                    break;
                }
            }

            if (arrowHit) {
                projIterator.remove();
            }
        }
    }

    // Collisioni Loot vs Player
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

    // Rendering: loot sotto, entità ordinate per Y, proiettili sopra
    private void render() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // 1. Disegna loot
        for (Loot loot : loots) {
            loot.render(gc);
        }

        // 2. Unisci nemici e player per ordinarli
        List<Object> entitiesToDraw = new ArrayList<>(enemies);
        entitiesToDraw.add(player);

        // 3. Ordina per coordinata Y (Y più alta = disegnata dopo)
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

        // 5. Disegna proiettili
        for (Projectile p : projectiles) {
            p.render(gc);
        }
    }

    // Azioni esterne: sparo, reset delta, getter e setter input

    public void shoot() {
        Projectile arrow = player.shoot();
        if (arrow != null) {
            projectiles.add(arrow);
        }
    }

    // Resetta il timer per evitare deltaTime enormi dopo una pausa
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