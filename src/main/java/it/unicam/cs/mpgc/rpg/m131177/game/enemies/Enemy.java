package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

// ============================================================
// IMPORT
// ============================================================
import it.unicam.cs.mpgc.rpg.m131177.game.loots.Loot;
import it.unicam.cs.mpgc.rpg.m131177.game.loots.LootType;
import it.unicam.cs.mpgc.rpg.m131177.game.players.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import it.unicam.cs.mpgc.rpg.m131177.game.GameConstants;

// ============================================================
// ENEMY (Classe Astratta Base)
// ============================================================
// Definisce il contratto e la logica comune a tutti i nemici (movimento, danno, loot).
public abstract class Enemy {

    // ============================================================
    // POSIZIONE E DIMENSIONI
    // ============================================================
    protected double x;
    protected double y;
    protected double width = 40;
    protected double height = 40;

    // ============================================================
    // STATISTICHE
    // ============================================================
    protected int maxHealth;
    protected int currentHealth;
    protected double speed;
    protected int damage;
    protected int scoreValue;    // Punti dati alla morte
    protected int arrowDrop;     // Frecce droppate alla morte

    // ============================================================
    // GRAFICA E STATO
    // ============================================================
    protected Image sprite;
    protected boolean alive = true;

    // ============================================================
    // COSTRUTTORE
    // ============================================================
    // Inizializza la posizione e la vita corrente (pari alla vita massima)
    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        loadSprite();
    }

    // ============================================================
    // METODI ASTRATTI (da implementare nelle sottoclassi)
    // ============================================================
    protected abstract void loadSprite();
    public abstract String getName();
    protected abstract Color getFallbackColor();

    // ============================================================
    // AGGIORNAMENTO LOGICA
    // ============================================================
    // Muove il nemico verso il player, limitandolo all'area giocabile.
    public void update(double dt, Player player) {
        if (!alive) return;

        // Calcola la direzione verso il centro del player
        double dx = (player.getX() + player.getWidth() / 2) - (x + width / 2);
        double dy = (player.getY() + player.getHeight() / 2) - (y + height / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            double moveX = (dx / distance) * speed * dt;
            double moveY = (dy / distance) * speed * dt;

            // Calcola posizione futura limitata all'area giocabile (Math.clamp)
            double nextX = Math.clamp(x + moveX, GameConstants.PLAYABLE_MIN_X, GameConstants.PLAYABLE_MAX_X);
            double nextY = Math.clamp(y + moveY, GameConstants.PLAYABLE_MIN_Y, GameConstants.PLAYABLE_MAX_Y);

            x = nextX;
            y = nextY;
        }
    }

    // ============================================================
    // COMBATTIMENTO E LOOT
    // ============================================================
    // Infligge danno; se muore, genera e restituisce un loot.
    public Loot takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            alive = false;
            return generateLoot();
        }
        return null;
    }

    // Genera un loot casuale in base alle probabilità (70% frecce, 20% cura, 10% scudo)
    private Loot generateLoot() {
        double random = Math.random();
        LootType type;
        int value;

        if (random < 0.70) {
            type = LootType.ARROWS;
            // Base + bonus scalato: più frecce nelle ondate avanzate
            value = arrowDrop + (int)(Math.random() * 3) + 1; // +1/+2/+3 extra casuali
        } else if (random < 0.90) {
            type = LootType.HEALTH_POTION;
            value = 1;
        } else {
            type = LootType.SHIELD_POTION;
            value = 1;
        }

        return new Loot(x + width / 2 - 12, y + height / 2 - 12, type, value);
    }

    // ============================================================
    // RENDERING
    // ============================================================
    // Disegna lo sprite (o fallback) e la barra della vita sopra il nemico.
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

    // Disegna la barra della vita (sfondo rosso, vita verde)
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

    // ============================================================
    // SCALING STATISTICHE
    // ============================================================
    // Aumenta vita e velocità del nemico in base al moltiplicatore dell'ondata.
    public void scaleStats(double healthMultiplier, double speedMultiplier) {
        this.maxHealth = (int) (this.maxHealth * healthMultiplier);
        this.currentHealth = this.maxHealth;
        this.speed = this.speed * speedMultiplier;
    }

    // ============================================================
    // GETTER
    // ============================================================
    // I getter di currentHealth, maxHealth e arrowDrop sono stati rimossi
    // perché attualmente non vengono usati all'esterno della classe.
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getDamage() { return damage; }
    public int getScoreValue() { return scoreValue; }
    public boolean isAlive() { return alive; }
}