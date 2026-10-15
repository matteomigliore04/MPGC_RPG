package it.unicam.cs.mpgc.rpg.m131177.game.player;

// Import delle classi JavaFX necessarie per disegnare il proiettile
// sul Canvas (GraphicsContext), caricare l'immagine della freccia (Image)
// e disegnare un rettangolo di fallback (Color)
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rappresenta un proiettile (freccia) sparato dal giocatore.
 * Si muove in linea retta nella direzione in cui è stato sparato.
 * La direzione è espressa come coppia (directionX, directionY) con valori
 * tipici -1, 0, 1: ad esempio (1, 0) = destra, (0, -1) = su,
 * (1, 1) = diagonale in basso a destra.
 */
public class Projectile {

    // --- POSIZIONE E DIMENSIONI ---
    // x, y: angolo in alto a sinistra del proiettile (in pixel)
    // width, height: dimensioni del rettangolo/immagine della freccia
    // speed: velocità in pixel al secondo (più alta del player per renderla visibile)
    private double x;
    private double y;
    final double width = 24;
    final double height = 12;
    final double speed = 500; // pixel al secondo (più veloce del player)

    // --- DIREZIONE DI MOVIMENTO ---
    // Valori attesi: -1, 0 o 1 per ciascun asse.
    // Sono final perché la direzione non cambia durante la vita del proiettile
    final double directionX; // -1, 0, 1
    final double directionY; // -1, 0, 1

    // --- STATO ---
    // active: true finché il proiettile è in gioco, false quando esce dallo schermo
    // arrowImage: sprite della freccia caricato da resources/images/arrow.png
    private boolean active = true;
    private Image arrowImage;

    // Fattore di normalizzazione per il movimento diagonale.
    // 1/√2 ≈ 0.7071: serve a mantenere la stessa velocità percepita
    // sia in orizzontale/verticale sia in diagonale (altrimenti la diagonale
    // sarebbe più veloce di √2 volte)
    private static final double DIAGONAL_FACTOR = 0.70710678118;

    /**
     * Crea un proiettile alla posizione del player, nella direzione specificata.
     *
     * @param startX posizione X iniziale (es. centro del player)
     * @param startY posizione Y iniziale
     * @param dirX   componente X della direzione (-1, 0, 1)
     * @param dirY   componente Y della direzione (-1, 0, 1)
     */
    public Projectile(double startX, double startY, double dirX, double dirY) {
        this.x = startX;
        this.y = startY;
        this.directionX = dirX;
        this.directionY = dirY;

        // Carica l'immagine della freccia (con fallback).
        // Se il file non esiste o non è raggiungibile, arrowImage resta null
        // e nel render() verrà disegnato un rettangolo giallo al suo posto
        try {
            arrowImage = new Image(getClass().getResourceAsStream("/images/arrow.png"));
        } catch (Exception e) {
            arrowImage = null; // Useremo il rettangolo di fallback
        }
    }

    /**
     * Aggiorna la posizione del proiettile in base al tempo trascorso (dt).
     * Restituisce true se il proiettile è ancora attivo, false se è uscito
     * dai limiti del canvas (e quindi va rimosso dalla lista dei proiettili).
     *
     * @param dt           delta time in secondi (es. 0.016 per 60 FPS)
     * @param canvasWidth  larghezza del canvas
     * @param canvasHeight altezza del canvas
     * @return true se il proiettile è ancora in gioco
     */
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

    /**
     * Disegna il proiettile sul canvas.
     * Se l'immagine è disponibile, la ruota in base alla direzione;
     * altrimenti disegna un rettangolo giallo come fallback.
     */
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

    // --- GETTER PER COLLISIONI E LOGICA DI GIOCO ---
    // Usati dal GameController per rilevare collisioni con i mostri
    // e per rimuovere i proiettili inattivi dalla lista
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isActive() { return active; }
}