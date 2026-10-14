package it.unicam.cs.mpgc.rpg.m131177.game;

// Import delle classi JavaFX necessarie per disegnare il player
// sul Canvas (GraphicsContext), caricare le immagini (Image)
// e disegnare il fallback (Color)
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// Import per il logging: usiamo java.util.logging (standard di Java)
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rappresenta il giocatore: posizione, dimensioni, velocità, direzione,
 * munizioni (frecce) e immagini per le 8 direzioni (4 cardinali + 4 diagonali),
 * sia in movimento sia in fase di sparo.
 */
public class Player {

    // Logger dedicato alla classe: registra messaggi al posto delle println,
    // permettendo una gestione più robusta e filtrabile degli errori
    private static final Logger LOGGER = Logger.getLogger(Player.class.getName());

    // --- POSIZIONE, DIMENSIONI E VELOCITÀ ---
    // x, y: angolo in alto a sinistra del player (in pixel)
    // width, height: dimensioni del rettangolo/immagine del player
    // speed: velocità in pixel al secondo
    private double x;
    private double y;
    final double width = 80;
    final double height = 70;
    final double speed = 200;

    // --- LIMITI DI MOVIMENTO ---
    // Valori minimi e massimi per x e y, calcolati in base al canvas,
    // per evitare che il player esca dallo schermo
    final double minX, maxX, minY, maxY;

    // --- GESTIONE DELLE FRECCE (MUNIZIONI) ---
    // maxArrows: numero massimo di frecce trasportabili
    // currentArrows: frecce attualmente disponibili
    final int maxArrows = 100;
    private int currentArrows = 100;

    // Fattore di normalizzazione per il movimento diagonale (1/√2 ≈ 0.7071):
    // serve a mantenere la stessa velocità percepita sia in orizzontale/verticale
    // sia in diagonale (altrimenti la diagonale sarebbe √2 volte più veloce)
    private static final double DIAGONAL_FACTOR = 0.70710678118;

    // --- IMMAGINI DI MOVIMENTO ---
    // 4 cardinali + 4 diagonali, caricate da resources/images
    private Image imgUp, imgDown, imgLeft, imgRight;
    private Image imgUpLeft, imgUpRight, imgDownLeft, imgDownRight;

    // --- IMMAGINI DI SPARO ---
    // 4 cardinali + 4 diagonali, mostrate brevemente dopo lo sparo
    private Image imgShootUp, imgShootDown, imgShootLeft, imgShootRight;
    private Image imgShootUpLeft, imgShootUpRight, imgShootDownLeft, imgShootDownRight;

    // --- STATO CORRENTE ---
    // currentImg: sprite attualmente disegnato
    // currentDirection: direzione attuale (per capire quale sprite usare dopo lo sparo)
    // shootingTimer: tempo rimanente dell'animazione di sparo
    private Image currentImg;
    private Direction currentDirection = Direction.DOWN;

    private double shootingTimer = 0;
    private static final double SHOOTING_DURATION = 0.15;

    // --- NUOVI CAMPI ---
    private int maxHealth = 100;
    private int currentHealth = 100;
    private int score = 0;

    // Enumerazione delle 8 direzioni possibili
    public enum Direction {
        UP, DOWN, LEFT, RIGHT,
        UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }

    /**
     * Crea un player al centro del canvas.
     * @param canvasWidth  larghezza del canvas
     * @param canvasHeight altezza del canvas
     * @param hudHeight    altezza dell'HUD (per ora non usata, ma utile in futuro
     *                     per limitare il movimento sotto l'HUD)
     */
    public Player(double canvasWidth, double canvasHeight, double hudHeight) {
        // Posiziona il player al centro del canvas
        this.x = canvasWidth / 2 - width / 2;
        this.y = canvasHeight / 2 - height / 2;

        // Limiti di movimento: il player può arrivare fino ai bordi
        this.minX = 0;
        this.maxX = canvasWidth - width;
        this.minY = 0;                                    // Può andare fino al bordo superiore
        this.maxY = canvasHeight - height;

        this.currentHealth = maxHealth;

        // Carica le immagini e imposta lo sprite iniziale (rivolto verso il basso)
        loadImages();
        this.currentImg = imgDown;
    }

    /**
     * Carica tutte le immagini del player da resources/images.
     * Se una risorsa manca, loadImage() logga un warning e restituisce null:
     * in quel caso, il render() userà un fallback disegnato a mano.
     */
    private void loadImages() {
        // Cardinali
        imgUp = loadImage("/images/weed_up.png");
        imgDown = loadImage("/images/weed_down.png");
        imgLeft = loadImage("/images/weed_left.png");
        imgRight = loadImage("/images/weed_right.png");

        // Diagonali
        imgUpLeft = loadImage("/images/weed_up_left.png");
        imgUpRight = loadImage("/images/weed_up_right.png");
        imgDownLeft = loadImage("/images/weed_down_left.png");
        imgDownRight = loadImage("/images/weed_down_right.png");

        // Sparo cardinali
        imgShootUp = loadImage("/images/weed_up_shoot.png");
        imgShootDown = loadImage("/images/weed_down_shoot.png");
        imgShootLeft = loadImage("/images/weed_left_shoot.png");
        imgShootRight = loadImage("/images/weed_right_shoot.png");

        // Sparo diagonali
        imgShootUpLeft = loadImage("/images/weed_shoot_up_left.png");
        imgShootUpRight = loadImage("/images/weed_shoot_up_right.png");
        imgShootDownLeft = loadImage("/images/weed_shoot_down_left.png");
        imgShootDownRight = loadImage("/images/weed_shoot_down_right.png");
    }

    /**
     * Carica una singola immagine.
     * Se la risorsa non esiste o non è raggiungibile, logga un warning
     * (invece di stampare su console con System.out.println) e restituisce null.
     */
    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Immagine non trovata: " + path, e);
            return null;
        }
    }

    /**
     * Aggiorna la posizione del player in base ai tasti premuti e al tempo trascorso.
     * Gestisce anche l'animazione di sparo: durante lo sparo il player può muoversi,
     * ma lo sprite resta quello di sparo finché il timer non scade.
     */
    public void update(double dt, boolean upPressed, boolean downPressed,
                       boolean leftPressed, boolean rightPressed) {

        // Calcola il fattore di velocità (normalizzazione diagonale):
        // se si muove su entrambi gli assi, riduce la velocità di √2
        double speedMultiplier = 1.0;
        int axesPressed = 0;
        if ((upPressed || downPressed)) axesPressed++;
        if ((leftPressed || rightPressed)) axesPressed++;
        if (axesPressed == 2) {
            speedMultiplier = DIAGONAL_FACTOR;
        }

        double movement = speed * dt * speedMultiplier;

        if (shootingTimer > 0) {
            // Durante l'animazione di sparo, consenti il movimento ma non cambiare sprite
            if (upPressed) y -= movement;
            if (downPressed) y += movement;
            if (leftPressed) x -= movement;
            if (rightPressed) x += movement;

            shootingTimer -= dt;
            if (shootingTimer <= 0) {
                revertToNormalSprite();
            }
        } else {
            // Movimento normale con sprite che cambia in base alla direzione
            boolean moved = false;

            if (upPressed) { y -= movement; moved = true; }
            if (downPressed) { y += movement; moved = true; }
            if (leftPressed) { x -= movement; moved = true; }
            if (rightPressed) { x += movement; moved = true; }

            // Aggiorna la direzione e lo sprite in base alla combinazione di tasti
            if (moved) {
                updateDirection(upPressed, downPressed, leftPressed, rightPressed);
            }
        }

        // Clamp bordi: impedisce al player di uscire dal canvas
        x = Math.max(minX, Math.min(x, maxX));
        y = Math.max(minY, Math.min(y, maxY));
    }

    /**
     * Determina la direzione in base alla combinazione di tasti premuti.
     * La priorità è data alle diagonali, poi ai singoli assi.
     */
    private void updateDirection(boolean up, boolean down, boolean left, boolean right) {
        if (up && left) {
            currentDirection = Direction.UP_LEFT;
            currentImg = imgUpLeft;
        } else if (up && right) {
            currentDirection = Direction.UP_RIGHT;
            currentImg = imgUpRight;
        } else if (down && left) {
            currentDirection = Direction.DOWN_LEFT;
            currentImg = imgDownLeft;
        } else if (down && right) {
            currentDirection = Direction.DOWN_RIGHT;
            currentImg = imgDownRight;
        } else if (up) {
            currentDirection = Direction.UP;
            currentImg = imgUp;
        } else if (down) {
            currentDirection = Direction.DOWN;
            currentImg = imgDown;
        } else if (left) {
            currentDirection = Direction.LEFT;
            currentImg = imgLeft;
        } else if (right) {
            currentDirection = Direction.RIGHT;
            currentImg = imgRight;
        }
    }

    /**
     * Ripristina lo sprite di movimento normale in base alla direzione corrente.
     * Chiamato quando l'animazione di sparo termina.
     */
    private void revertToNormalSprite() {
        switch (currentDirection) {
            case UP: currentImg = imgUp; break;
            case DOWN: currentImg = imgDown; break;
            case LEFT: currentImg = imgLeft; break;
            case RIGHT: currentImg = imgRight; break;
            case UP_LEFT: currentImg = imgUpLeft; break;
            case UP_RIGHT: currentImg = imgUpRight; break;
            case DOWN_LEFT: currentImg = imgDownLeft; break;
            case DOWN_RIGHT: currentImg = imgDownRight; break;
        }
    }

    /**
     * Metodo per raccogliere frecce (da usare quando i nemici verranno uccisi).
     * @param amount Numero di frecce da aggiungere
     * @return Numero effettivo di frecce aggiunte (potrebbe essere meno se si supera il massimo)
     */
    public int collectArrows(int amount) {
        int spaceLeft = maxArrows - currentArrows;
        int arrowsAdded = Math.min(amount, spaceLeft);
        currentArrows += arrowsAdded;
        return arrowsAdded;
    }

    /** Getter per il numero attuale di frecce. */
    public int getCurrentArrows() {
        return currentArrows;
    }

    /** Getter per il numero massimo di frecce. */
    public int getMaxArrows() {
        return maxArrows;
    }

    /**
     * Crea e restituisce un proiettile nella direzione corrente.
     * Se non ci sono frecce disponibili, restituisce null (nessuno sparo).
     * Consuma una freccia e imposta l'animazione di sparo.
     */
    public Projectile shoot() {
        // Controllo munizioni
        if (currentArrows <= 0) {
            return null; // Non spara se non ha frecce
        }
        currentArrows--; // Consuma una freccia

        // Posizione di partenza di default (centro del player, con piccolo offset)
        double startX = x + width / 2 - 10;
        double startY = y + height / 2 - 4;

        double dirX = 0;
        double dirY = 0;

        // Attiva l'animazione di sparo
        shootingTimer = SHOOTING_DURATION;

        // In base alla direzione corrente, imposta:
        // - dirX, dirY: vettore direzione del proiettile
        // - startX, startY: posizione di partenza ottimale (per evitare che la freccia
        //   parta dentro il player)
        // - currentImg: sprite di sparo corrispondente
        switch (currentDirection) {
            case UP:
                dirY = -1;
                startY = y - 10;
                currentImg = imgShootUp;
                break;
            case DOWN:
                dirY = 1;
                startY = y + height;
                currentImg = imgShootDown;
                break;
            case LEFT:
                dirX = -1;
                startX = x - 20;
                currentImg = imgShootLeft;
                break;
            case RIGHT:
                dirX = 1;
                startX = x + width;
                currentImg = imgShootRight;
                break;
            case UP_LEFT:
                dirX = -1; dirY = -1;
                startX = x - 15; startY = y - 10;
                currentImg = imgShootUpLeft;
                break;
            case UP_RIGHT:
                dirX = 1; dirY = -1;
                startX = x + width; startY = y - 10;
                currentImg = imgShootUpRight;
                break;
            case DOWN_LEFT:
                dirX = -1; dirY = 1;
                startX = x - 15; startY = y + height;
                currentImg = imgShootDownLeft;
                break;
            case DOWN_RIGHT:
                dirX = 1; dirY = 1;
                startX = x + width; startY = y + height;
                currentImg = imgShootDownRight;
                break;
        }

        return new Projectile(startX, startY, dirX, dirY);
    }

    /**
     * Disegna il player sul canvas.
     * Se l'immagine corrente è disponibile, la disegna;
     * altrimenti disegna un fallback stilizzato (cerchio verde con occhi).
     */
    public void render(GraphicsContext gc) {
        if (currentImg != null) {
            gc.drawImage(currentImg, x, y, width, height);
        } else {
            // FALLBACK: cerchio verde con occhi neri, se l'immagine non è disponibile
            gc.setFill(Color.LIMEGREEN);
            gc.fillOval(x, y, width, height);
            gc.setStroke(Color.DARKGREEN);
            gc.setLineWidth(2);
            gc.strokeOval(x, y, width, height);
            gc.setFill(Color.BLACK);
            gc.fillOval(x + 10, y + 12, 6, 6);
            gc.fillOval(x + 24, y + 12, 6, 6);
        }
    }

    /**
     * Infligge danno al player.
     * @return true se il player è morto
     */
    public boolean takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            return true; // Player morto
        }
        return false;
    }

    /**
     * Aggiunge punti al punteggio del player.
     */
    public void addScore(int points) {
        score += points;
    }

    // --- GETTER PER POSIZIONE E DIMENSIONI ---
    // Usati dal GameController per collisioni e logica di gioco
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getScore() { return score; }
}