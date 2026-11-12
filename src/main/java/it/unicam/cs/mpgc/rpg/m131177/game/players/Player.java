package it.unicam.cs.mpgc.rpg.m131177.game.players;

// Import di progetto
import it.unicam.cs.mpgc.rpg.m131177.game.GameConstants;

// Import JavaFX
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// Import Java standard
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Player: posizione, movimento, direzioni, sparo, vita/scudo e pozioni.
 */
public class Player {

    // Logger di classe
    private static final Logger LOGGER = Logger.getLogger(Player.class.getName());

    // Costanti
    private static final double DIAGONAL_FACTOR = 0.70710678118; // 1/√2
    private static final double SHOOTING_DURATION = 0.15;        // secondi
    private static final double INVINCIBILITY_DURATION = 1.0;    // secondi
    private static final int MAX_POTIONS = 10;

    // Posizione, dimensioni e velocità
    private double x;
    private double y;
    final double width = 60;
    final double height = 50;
    final double speed = 200;

    // Limiti di movimento (da GameConstants)
    final double minX, maxX, minY, maxY;

    // Frecce (munizioni)
    final int maxArrows = 150;
    private int currentArrows = 150;

    // Immagini di movimento (4 cardinali + 4 diagonali)
    private Image imgUp, imgDown, imgLeft, imgRight;
    private Image imgUpLeft, imgUpRight, imgDownLeft, imgDownRight;

    // Immagini di sparo (4 cardinali + 4 diagonali)
    private Image imgShootUp, imgShootDown, imgShootLeft, imgShootRight;
    private Image imgShootUpLeft, imgShootUpRight, imgShootDownLeft, imgShootDownRight;

    // Stato corrente
    private Image currentImg;
    private Direction currentDirection = Direction.DOWN;
    private double shootingTimer = 0;

    // Vita, scudo e punteggio
    private final int maxHealth = 100;
    private int currentHealth;
    private final int maxShield = 50;
    private int currentShield = 0;
    private int score = 0;

    // Timer di invincibilità (0 = non invincibile)
    private double invincibilityTimer = 0;

    // Pozioni
    private int healthPotions = 0;
    private int shieldPotions = 0;

    // Direzioni possibili
    public enum Direction {
        UP, DOWN, LEFT, RIGHT,
        UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }

    /**
     * Crea il player al centro del canvas e imposta i limiti di movimento.
     */
    public Player(double canvasWidth, double canvasHeight) {
        this.x = canvasWidth / 2 - width / 2;
        this.y = canvasHeight / 2 - height / 2;
        this.currentHealth = maxHealth;

        this.minX = GameConstants.PLAYABLE_MIN_X;
        this.maxX = GameConstants.PLAYABLE_MAX_X;
        this.minY = GameConstants.PLAYABLE_MIN_Y;
        this.maxY = GameConstants.PLAYABLE_MAX_Y;

        loadImages();
        this.currentImg = imgDown;
    }

    // --- Caricamento immagini ---

    /** Carica tutti gli sprite (movimento e sparo). */
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

    /** Carica una singola immagine; se manca, logga un warning e restituisce null. */
    private Image loadImage(String path) {
        try {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Immagine non trovata: " + path, e);
            return null;
        }
    }

    // --- Aggiornamento ---

    /**
     * Aggiorna posizione, sprite e timer di invincibilità.
     * Durante lo sparo il movimento è consentito ma lo sprite resta bloccato.
     */
    public void update(double dt, boolean upPressed, boolean downPressed,
                       boolean leftPressed, boolean rightPressed) {

        // Normalizzazione diagonale
        double speedMultiplier = 1.0;
        int axesPressed = 0;
        if (upPressed || downPressed) axesPressed++;
        if (leftPressed || rightPressed) axesPressed++;
        if (axesPressed == 2) {
            speedMultiplier = DIAGONAL_FACTOR;
        }

        double movement = speed * dt * speedMultiplier;

        if (shootingTimer > 0) {
            // Durante lo sparo: muoviti senza cambiare sprite
            if (upPressed) y -= movement;
            if (downPressed) y += movement;
            if (leftPressed) x -= movement;
            if (rightPressed) x += movement;

            shootingTimer -= dt;
            if (shootingTimer <= 0) {
                revertToNormalSprite();
            }
        } else {
            // Movimento normale con cambio sprite
            boolean moved = false;

            if (upPressed) { y -= movement; moved = true; }
            if (downPressed) { y += movement; moved = true; }
            if (leftPressed) { x -= movement; moved = true; }
            if (rightPressed) { x += movement; moved = true; }

            if (moved) {
                updateDirection(upPressed, downPressed, leftPressed, rightPressed);
            }
        }

        // Clamp nei limiti giocabili
        x = Math.clamp(x, GameConstants.PLAYABLE_MIN_X, GameConstants.PLAYABLE_MAX_X - width);
        y = Math.clamp(y, GameConstants.PLAYABLE_MIN_Y, GameConstants.PLAYABLE_MAX_Y - height);

        // Decrementa il timer di invincibilità
        if (invincibilityTimer > 0) {
            invincibilityTimer -= dt;
            if (invincibilityTimer < 0) {
                invincibilityTimer = 0;
            }
        }
    }

    // --- Direzione e sprite ---

    /** Aggiorna direzione e sprite in base ai tasti premuti (priorità alle diagonali). */
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

    /** Ripristina lo sprite di movimento in base alla direzione corrente. */
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

    // --- Frecce ---

    /** Aggiunge frecce rispettando il massimo; restituisce quante ne ha aggiunte. */
    public int collectArrows(int amount) {
        int spaceLeft = maxArrows - currentArrows;
        int arrowsAdded = Math.min(amount, spaceLeft);
        currentArrows += arrowsAdded;
        return arrowsAdded;
    }

    public int getCurrentArrows() {
        return currentArrows;
    }

    // --- Sparo ---

    /**
     * Crea un proiettile nella direzione corrente.
     * Restituisce null se non ci sono frecce.
     */
    public Projectile shoot() {
        if (currentArrows <= 0) {
            return null;
        }
        currentArrows--;

        // Posizione di partenza di default (centro del player)
        double startX = x + width / 2 - 10;
        double startY = y + height / 2 - 4;

        double dirX = 0;
        double dirY = 0;

        // Attiva l'animazione di sparo
        shootingTimer = SHOOTING_DURATION;

        // Imposta direzione, posizione di partenza e sprite di sparo
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

    // --- Rendering ---

    /** Disegna lo sprite corrente; se manca, disegna un fallback verde. */
    public void render(GraphicsContext gc) {
        if (currentImg != null) {
            gc.drawImage(currentImg, x, y, width, height);
        } else {
            // Fallback: cerchio verde con occhi neri
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

    // --- Vita e danno ---

    /**
     * Infligge danno: lo scudo assorbe per primo, poi la vita.
     * Imposta invincibilità temporanea. Restituisce true se il player è morto.
     */
    public boolean takeDamage(int damage) {
        if (invincibilityTimer > 0) {
            return false;
        }

        // Lo scudo assorbe il danno per primo
        if (currentShield > 0) {
            int shieldDamage = Math.min(currentShield, damage);
            currentShield -= shieldDamage;
            damage -= shieldDamage;
        }

        if (damage > 0) {
            currentHealth -= damage;
        }

        invincibilityTimer = INVINCIBILITY_DURATION;

        if (currentHealth <= 0) {
            invincibilityTimer = 0;
            currentHealth = 0;
            return true;
        }
        return false;
    }

    // --- Punteggio ---

    /** Aggiunge punti al punteggio. */
    public void addScore(int points) {
        score += points;
    }

    // --- Pozioni ---

    /** Aggiunge pozioni di cura (max MAX_POTIONS). */
    public void addHealthPotion(int amount) {
        this.healthPotions = Math.min(this.healthPotions + amount, MAX_POTIONS);
    }

    /** Aggiunge pozioni di scudo (max MAX_POTIONS). */
    public void addShieldPotion(int amount) {
        this.shieldPotions = Math.min(this.shieldPotions + amount, MAX_POTIONS);
    }

    /** Usa una pozione di cura: +30 vita, solo se disponibile e non full. */
    public boolean useHealthPotion() {
        if (healthPotions > 0 && currentHealth < maxHealth) {
            healthPotions--;
            currentHealth = Math.min(maxHealth, currentHealth + 30);
            return true;
        }
        return false;
    }

    /** Usa una pozione di scudo: +25 scudo, solo se disponibile e non full. */
    public boolean useShieldPotion() {
        if (shieldPotions > 0 && currentShield < maxShield) {
            shieldPotions--;
            currentShield = Math.min(maxShield, currentShield + 25);
            return true;
        }
        return false;
    }

    // --- Getter ---

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public int getCurrentHealth() { return currentHealth; }
    public int getScore() { return score; }
    public int getHealthPotions() { return healthPotions; }
    public int getShieldPotions() { return shieldPotions; }
    public int getCurrentShield() { return currentShield; }
}