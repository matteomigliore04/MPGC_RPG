package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

// Import di progetto (ordinati alfabeticamente)
import it.unicam.cs.mpgc.rpg.m131177.game.GameConstants;
import it.unicam.cs.mpgc.rpg.m131177.game.loots.Loot;
import it.unicam.cs.mpgc.rpg.m131177.game.loots.LootType;
import it.unicam.cs.mpgc.rpg.m131177.game.players.Player;

// Import JavaFX
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// Import Java standard
import java.util.List;

/**
 * Classe astratta base per tutti i nemici.
 * Definisce movimento, danno, loot e rendering comuni.
 */
public abstract class Enemy {

    // --- Posizione e dimensioni ---
    protected double x;
    protected double y;
    protected double width = 40;
    protected double height = 40;

    // --- Statistiche ---
    protected int maxHealth;
    protected int currentHealth;
    protected double speed;
    protected int damage;
    protected int scoreValue;    // Punti dati alla morte
    protected int arrowDrop;     // Frecce droppate alla morte
    protected double lootMultiplier = 1.0;

    // --- Grafica e stato ---
    protected Image sprite;
    protected boolean alive = true;

    /**
     * Inizializza posizione e vita corrente (pari alla vita massima).
     */
    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        loadSprite();
    }

    // --- Metodi astratti (da implementare nelle sottoclassi) ---
    protected abstract void loadSprite();
    public abstract String getName();
    protected abstract Color getFallbackColor();

    // --- Aggiornamento logica ---

    /**
     * Muove il nemico verso il player, evitando sovrapposizioni con altri nemici.
     */
    public void update(double dt, Player player, List<Enemy> otherEnemies) {
        if (!alive) return;

        // 1. Muoviti verso il player
        double dx = (player.getX() + player.getWidth() / 2) - (x + width / 2);
        double dy = (player.getY() + player.getHeight() / 2) - (y + height / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);
        double minDistance = (width / 2) + (player.getWidth() / 2);

        if (distance > minDistance) {
            double moveX = (dx / distance) * speed * dt;
            double moveY = (dy / distance) * speed * dt;
            x += moveX;
            y += moveY;
        }

        // 2. Separazione dagli altri nemici
        for (Enemy other : otherEnemies) {
            if (other != this && other.isAlive()) {
                double distX = (other.x + other.width / 2) - (x + width / 2);
                double distY = (other.y + other.height / 2) - (y + height / 2);
                double dist = Math.sqrt(distX * distX + distY * distY);

                if (dist < 35 && dist > 0) {
                    x -= (distX / dist) * speed * dt * 0.5;
                    y -= (distY / dist) * speed * dt * 0.5;
                }
            }
        }

        // 3. Riporta il nemico nell'area giocabile
        x = Math.clamp(x, GameConstants.PLAYABLE_MIN_X, GameConstants.PLAYABLE_MAX_X - width);
        y = Math.clamp(y, GameConstants.PLAYABLE_MIN_Y, GameConstants.PLAYABLE_MAX_Y - height);
    }

    // --- Combattimento e loot ---

    /**
     * Infligge danno; se il nemico muore, restituisce il loot generato.
     */
    public Loot takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            alive = false;
            return generateLoot();
        }
        return null;
    }

    // --- Rendering ---

    /**
     * Disegna sprite (o fallback) e barra della vita.
     */
    public void render(GraphicsContext gc) {
        if (!alive) return;

        if (sprite != null) {
            gc.drawImage(sprite, x, y, width, height);
        } else {
            gc.setFill(getFallbackColor());
            gc.fillOval(x, y, width, height);
        }

        renderHealthBar(gc);
    }

    // --- Scaling statistiche ---

    /** Aumenta vita e velocità in base ai moltiplicatori dell'ondata. */
    public void scaleStats(double healthMultiplier, double speedMultiplier) {
        this.maxHealth = (int) (this.maxHealth * healthMultiplier);
        this.currentHealth = this.maxHealth;
        this.speed = this.speed * speedMultiplier;
    }

    /** Aumenta punti e moltiplicatore loot. */
    public void scaleRewards(double multiplier) {
        this.scoreValue = (int) (this.scoreValue * multiplier);
        this.lootMultiplier = multiplier;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // --- Getter ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getDamage() { return damage; }
    public int getScoreValue() { return scoreValue; }
    public boolean isAlive() { return alive; }

    // --- Helper privati ---

    /** Genera un loot casuale: 70% frecce, 20% cura, 10% scudo. */
    private Loot generateLoot() {
        double random = Math.random();
        LootType type;
        int value;

        if (random < 0.70) {
            type = LootType.ARROWS;
            int baseArrows = arrowDrop + (int)(Math.random() * 3) + 1;
            value = Math.max(1, (int)(baseArrows * lootMultiplier));
        } else if (random < 0.90) {
            type = LootType.HEALTH_POTION;
            value = Math.max(1, (int)(1 * lootMultiplier));
        } else {
            type = LootType.SHIELD_POTION;
            value = Math.max(1, (int)(1 * lootMultiplier));
        }

        return new Loot(x + width / 2 - 12, y + height / 2 - 12, type, value);
    }

    /** Disegna la barra della vita (sfondo rosso, vita verde). */
    private void renderHealthBar(GraphicsContext gc) {
        double barWidth = width;
        double barHeight = 4;
        double barX = x;
        double barY = y - 8;

        gc.setFill(Color.RED);
        gc.fillRect(barX, barY, barWidth, barHeight);

        double healthPercent = (double) currentHealth / maxHealth;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(barX, barY, barWidth * healthPercent, barHeight);
    }
}