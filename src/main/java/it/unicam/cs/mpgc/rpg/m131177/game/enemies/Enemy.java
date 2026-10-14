package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

import it.unicam.cs.mpgc.rpg.m131177.game.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Classe base astratta per tutti i nemici del gioco.
 * Definisce il contratto comune e la logica di movimento verso il player.
 */
public abstract class Enemy {

    protected double x;
    protected double y;
    protected double width = 40;
    protected double height = 40;

    protected int maxHealth;
    protected int currentHealth;
    protected double speed;
    protected int damage;
    protected int scoreValue;    // Punti dati alla morte
    protected int arrowDrop;     // Frecce droppate alla morte

    protected Image sprite;
    protected boolean alive = true;

    /**
     * Costruisce un nemico in una posizione casuale ai bordi del canvas.
     */
    public Enemy(double canvasWidth, double canvasHeight, double hudHeight) {
        this.x = randomEdgePosition(canvasWidth, canvasHeight, hudHeight);
        this.y = randomEdgePosition(canvasWidth, canvasHeight, hudHeight);
        this.currentHealth = maxHealth;
        loadSprite();
    }

    /**
     * Carica lo sprite specifico del nemico.
     * Da implementare nelle sottoclassi.
     */
    protected abstract void loadSprite();

    /**
     * Restituisce il nome del nemico (per debug/log).
     */
    public abstract String getName();

    /**
     * Aggiorna la posizione del nemico, facendolo muovere verso il player.
     */
    public void update(double dt, Player player) {
        if (!alive) return;

        // Calcola la direzione verso il player
        double dx = (player.getX() + player.getWidth() / 2) - (x + width / 2);
        double dy = (player.getY() + player.getHeight() / 2) - (y + height / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Normalizza e muovi verso il player
        if (distance > 0) {
            x += (dx / distance) * speed * dt;
            y += (dy / distance) * speed * dt;
        }
    }

    /**
     * Infligge danno al nemico. Se la vita arriva a 0, muore.
     * @return true se il nemico è morto
     */
    public boolean takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            alive = false;
            return true;
        }
        return false;
    }

    /**
     * Disegna il nemico sul canvas.
     */
    public void render(GraphicsContext gc) {
        if (!alive) return;

        if (sprite != null) {
            gc.drawImage(sprite, x, y, width, height);
        } else {
            // Fallback: cerchio colorato
            gc.setFill(getFallbackColor());
            gc.fillOval(x, y, width, height);
        }

        // Barra della vita sopra il nemico
        renderHealthBar(gc);
    }

    /**
     * Disegna una piccola barra della vita sopra il nemico.
     */
    private void renderHealthBar(GraphicsContext gc) {
        double barWidth = width;
        double barHeight = 4;
        double barX = x;
        double barY = y - 8;

        // Sfondo rosso (vita mancante)
        gc.setFill(Color.RED);
        gc.fillRect(barX, barY, barWidth, barHeight);

        // Vita rimanente verde
        double healthPercent = (double) currentHealth / maxHealth;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(barX, barY, barWidth * healthPercent, barHeight);
    }

    /**
     * Genera una posizione casuale lungo i bordi del canvas (fuori dalla vista iniziale).
     */
    private double randomEdgePosition(double canvasWidth, double canvasHeight, double hudHeight) {
        // Per ora posiziona casualmente dentro l'area di gioco
        // In futuro potremo farli spawnare dai bordi
        return Math.random() * (canvasWidth - width);
    }

    /**
     * Colore di fallback se lo sprite non è caricato.
     */
    protected abstract Color getFallbackColor();

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    public int getScoreValue() { return scoreValue; }
    public int getArrowDrop() { return arrowDrop; }
    public boolean isAlive() { return alive; }
}