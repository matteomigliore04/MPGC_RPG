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
    private double width = 24;
    private double height = 12;
    private double speed = 500; // pixel al secondo (più veloce del player)

    private double directionX; // -1, 0, 1
    private double directionY; // -1, 0, 1

    private boolean active = true;
    private Image arrowImage;

    private static final double DIAGONAL_FACTOR = 0.70710678118;

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

    public boolean update(double dt, double canvasWidth, double canvasHeight) {
        // Normalizza la velocità per movimento diagonale
        double speedMultiplier = 1.0;
        if (directionX != 0 && directionY != 0) {
            speedMultiplier = DIAGONAL_FACTOR; // ≈ 0.707
        }

        x += directionX * speed * dt * speedMultiplier;
        y += directionY * speed * dt * speedMultiplier;

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
            // Calcola l'angolo in radianti e convertilo in gradi
            // Math.atan2(y, x) calcola l'angolo del vettore direzione
            double angle = Math.toDegrees(Math.atan2(directionY, directionX));

            // Salva lo stato attuale del canvas
            gc.save();

            // Sposta il punto di origine al centro della freccia
            gc.translate(x + width / 2, y + height / 2);

            // Ruota il canvas dell'angolo calcolato
            gc.rotate(angle);

            // Disegna l'immagine centrata sul nuovo punto di origine
            gc.drawImage(arrowImage, -width / 2, -height / 2, width, height);

            // Ripristina lo stato del canvas (annulla rotazione e traslazione)
            gc.restore();
        } else {
            // FALLBACK: Rettangolo giallo se l'immagine non viene trovata
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