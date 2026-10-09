package it.unicam.cs.mpgc.rpg.m131177.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rappresenta un proiettile (freccia) sparato dal giocatore.
 * Si muove in linea retta nella direzione in cui è stato sparato.
 */
public class Projectile {

    private double x;
    private double y;
    private double width = 20;
    private double height = 8;
    private double speed = 500; // pixel al secondo (più veloce del player)

    private double directionX; // -1, 0, 1
    private double directionY; // -1, 0, 1

    private boolean active = true;
    private Image arrowImage;

    /**
     * Crea un proiettile alla posizione del player, nella direzione specificata.
     */
    public Projectile(double startX, double startY, double dirX, double dirY) {
        this.x = startX;
        this.y = startY;
        this.directionX = dirX;
        this.directionY = dirY;

        // Carica l'immagine della freccia (con fallback)
        try {
            arrowImage = new Image(getClass().getResourceAsStream("/images/arrow.png"));
        } catch (Exception e) {
            arrowImage = null; // Useremo il rettangolo di fallback
        }
    }

    /**
     * Aggiorna la posizione del proiettile.
     * @return true se il proiettile è ancora attivo (dentro lo schermo)
     */
    public boolean update(double dt, double canvasWidth, double canvasHeight) {
        x += directionX * speed * dt;
        y += directionY * speed * dt;

        // Disattiva se esce dallo schermo
        if (x < -width || x > canvasWidth || y < -height || y > canvasHeight) {
            active = false;
        }

        return active;
    }

    /**
     * Disegna il proiettile sul canvas.
     */
    public void render(GraphicsContext gc) {
        if (arrowImage != null) {
            gc.drawImage(arrowImage, x, y, width, height);
        } else {
            // Fallback: rettangolo giallo (freccia stilizzata)
            gc.setFill(Color.GOLD);
            gc.fillRect(x, y, width, height);
        }
    }

    // Getters per collisioni future
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isActive() { return active; }
}