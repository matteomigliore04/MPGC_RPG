package it.unicam.cs.mpgc.rpg.m131177.game.players;

// ============================================================
// IMPORT
// ============================================================
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Proiettile (freccia) sparato dal giocatore.
 * Si muove in linea retta nella direzione indicata.
 */
public class Projectile {

    // ============================================================
    // COSTANTI
    // ============================================================
    // Fattore di normalizzazione per il movimento diagonale (1/√2 ≈ 0.7071)
    private static final double DIAGONAL_FACTOR = 0.70710678118;

    // ============================================================
    // POSIZIONE, DIMENSIONI, VELOCITÀ
    // ============================================================
    // x, y: angolo in alto a sinistra (pixel)
    // width, height: dimensioni della freccia
    // speed: velocità in pixel/secondo (più alta del player)
    private double x;
    private double y;
    final double width = 30;
    final double height = 15;
    final double speed = 500; // pixel al secondo (più veloce del player)

    // ============================================================
    // DIREZIONE
    // ============================================================
    // Valori attesi: -1, 0 o 1 per ciascun asse.
    // Sono final perché la direzione non cambia durante la vita del proiettile
    final double directionX; // -1, 0, 1
    final double directionY; // -1, 0, 1

    // ============================================================
    // STATO E GRAFICA
    // ============================================================
    // active: true finché il proiettile è in gioco
    // arrowImage: sprite della freccia (null se manca)
    private boolean active = true;
    private Image arrowImage;

    // ============================================================
    // COSTRUTTORE
    // ============================================================
    // Crea un proiettile alla posizione del player, nella direzione specificata.
    // Carica l'immagine della freccia (con fallback a rettangolo giallo).
    public Projectile(double startX, double startY, double dirX, double dirY) {
        this.x = startX;
        this.y = startY;
        this.directionX = dirX;
        this.directionY = dirY;

        // Carica l'immagine della freccia (con fallback).
        // Se il file non esiste o non è raggiungibile, arrowImage resta null
        // e nel render() verrà disegnato un rettangolo giallo al suo posto
        try {
            arrowImage = new Image(getClass().getResourceAsStream("/images/player/arrow.png"));
        } catch (Exception e) {
            arrowImage = null; // Useremo il rettangolo di fallback
        }
    }

    // ============================================================
    // AGGIORNAMENTO POSIZIONE
    // ============================================================
    // Sposta il proiettile in base a dt e direzione.
    // Restituisce true se è ancora attivo, false se è uscito dai bordi.
    public boolean update(double dt, double canvasWidth, double canvasHeight) {
        // Normalizza la velocità per il movimento diagonale:
        // se ci si muove su entrambi gli assi, si moltiplica per ~0.707
        // così la velocità risultante resta uguale a quella orizzontale/verticale
        double speedMultiplier = 1.0;
        if (directionX != 0 && directionY != 0) {
            speedMultiplier = DIAGONAL_FACTOR; // ≈ 0.707
        }

        // Sposta il proiettile: spazio = velocità × tempo × fattore diagonale
        x += directionX * speed * dt * speedMultiplier;
        y += directionY * speed * dt * speedMultiplier;

        // Se esce completamente dai bordi del canvas (considerando anche
        // le dimensioni del proiettile), lo marchiamo come inattivo
        if (x < -width || x > canvasWidth || y < -height || y > canvasHeight) {
            active = false;
        }

        return active;
    }

    // ============================================================
    // RENDERING
    // ============================================================
    // Disegna la freccia ruotata; se manca l'immagine, disegna un rettangolo giallo.
    public void render(GraphicsContext gc) {
        if (arrowImage != null) {
            // Calcola l'angolo in radianti e convertilo in gradi.
            // Math.atan2(y, x) restituisce l'angolo del vettore (x, y)
            // rispetto all'asse X positivo, in radianti
            double angle = Math.toDegrees(Math.atan2(directionY, directionX));

            // Salva lo stato attuale del canvas (trasformazioni, stili, ecc.)
            gc.save();

            // Sposta il punto di origine al centro della freccia:
            // così la rotazione avviene attorno al centro dell'immagine
            gc.translate(x + width / 2, y + height / 2);

            // Ruota il canvas dell'angolo calcolato
            gc.rotate(angle);

            // Disegna l'immagine centrata sul nuovo punto di origine:
            // -width/2, -height/2 perché il centro è ora in (0, 0)
            gc.drawImage(arrowImage, -width / 2, -height / 2, width, height);

            // Ripristina lo stato del canvas (annulla rotazione e traslazione),
            // fondamentale per non influenzare i disegni successivi
            gc.restore();
        } else {
            // FALLBACK: Rettangolo giallo se l'immagine non viene trovata.
            // Utile in fase di sviluppo o se la risorsa manca
            gc.setFill(Color.GOLD);
            gc.fillRect(x, y, width, height);
        }
    }

    // ============================================================
    // GETTER
    // ============================================================
    // Usati dal GameController per collisioni e per rimuovere i proiettili inattivi
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isActive() { return active; }
}