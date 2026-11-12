package it.unicam.cs.mpgc.rpg.m131177.game.players;

// Import di progetto
import it.unicam.cs.mpgc.rpg.m131177.game.GameConstants;

// Import JavaFX
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Proiettile (freccia) sparato dal giocatore.
 * Si muove in linea retta nella direzione indicata.
 */
public class Projectile {

    // Fattore di normalizzazione per il movimento diagonale (1/√2)
    private static final double DIAGONAL_FACTOR = 0.70710678118;

    // Posizione (angolo in alto a sinistra), dimensioni e velocità
    private double x;
    private double y;
    final double width = 30;
    final double height = 15;
    final double speed = 500; // pixel al secondo (più veloce del player)

    // Direzione: -1, 0 o 1 per ciascun asse (fissa per tutta la vita del proiettile)
    final double directionX;
    final double directionY;

    // Stato e grafica
    private boolean active = true;
    private Image arrowImage;

    /**
     * Crea un proiettile alla posizione indicata, nella direzione specificata.
     * Se l'immagine non è disponibile, arrowImage resta null (fallback nel render).
     */
    public Projectile(double startX, double startY, double dirX, double dirY) {
        this.x = startX;
        this.y = startY;
        this.directionX = dirX;
        this.directionY = dirY;

        try {
            arrowImage = new Image(getClass().getResourceAsStream("/images/player/arrow.png"));
        } catch (Exception e) {
            arrowImage = null; // Verrà usato il rettangolo di fallback
        }
    }

    /**
     * Aggiorna la posizione del proiettile.
     * Restituisce true se è ancora attivo, false se è uscito dai bordi o ha colpito un muro.
     */
    public boolean update(double dt, double canvasWidth, double canvasHeight) {
        // Normalizza la velocità per il movimento diagonale
        double speedMultiplier = 1.0;
        if (directionX != 0 && directionY != 0) {
            speedMultiplier = DIAGONAL_FACTOR;
        }

        // Sposta il proiettile
        x += directionX * speed * dt * speedMultiplier;
        y += directionY * speed * dt * speedMultiplier;

        // Muri orizzontali (sopra e sotto, completi)
        boolean hitTopBottomWall = (y <= GameConstants.PLAYABLE_MIN_Y) ||
                (y + height >= GameConstants.PLAYABLE_MAX_Y);

        // Muri laterali (sinistra e destra, con apertura centrale)
        boolean hitLeftWall = (x <= GameConstants.PLAYABLE_MIN_X) &&
                (y < GameConstants.SPAWN_OPENING_MIN_Y || y > GameConstants.SPAWN_OPENING_MAX_Y);

        boolean hitRightWall = (x + width >= GameConstants.PLAYABLE_MAX_X) &&
                (y < GameConstants.SPAWN_OPENING_MIN_Y || y > GameConstants.SPAWN_OPENING_MAX_Y);

        // Disattiva la freccia se ha colpito un muro
        if (hitTopBottomWall || hitLeftWall || hitRightWall) {
            active = false;
        }

        // Fuori dallo schermo (es. passato dalle aperture laterali)
        if (x < -width || x > canvasWidth || y < -height || y > canvasHeight) {
            active = false;
        }

        return active;
    }

    /**
     * Disegna la freccia ruotata; se manca l'immagine, disegna un rettangolo giallo.
     */
    public void render(GraphicsContext gc) {
        if (arrowImage != null) {
            // Angolo in gradi del vettore direzione
            double angle = Math.toDegrees(Math.atan2(directionY, directionX));

            // Salva lo stato del canvas
            gc.save();

            // Sposta l'origine al centro della freccia (rotazione attorno al centro)
            gc.translate(x + width / 2, y + height / 2);

            // Ruota il canvas
            gc.rotate(angle);

            // Disegna l'immagine centrata sull'origine
            gc.drawImage(arrowImage, -width / 2, -height / 2, width, height);

            // Ripristina lo stato del canvas
            gc.restore();
        } else {
            // Fallback: rettangolo giallo se l'immagine manca
            gc.setFill(Color.GOLD);
            gc.fillRect(x, y, width, height);
        }
    }

    // Getter usati dal GameLoop per collisioni e rimozione dei proiettili inattivi
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}