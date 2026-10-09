package it.unicam.cs.mpgc.rpg.m131177.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player {

    private double x;
    private double y;
    private double width = 80;
    private double height = 80;
    private double speed = 200;

    private double minX, maxX, minY, maxY;

    // Immagini per le 4 direzioni
    private Image imgUp, imgDown, imgLeft, imgRight;
    private Image currentImg;

    // Direzione attuale (di default guarda in basso)
    private Direction currentDirection = Direction.DOWN;
    // Immagini sparo (NUOVE)
    private Image imgShootUp, imgShootDown, imgShootLeft, imgShootRight;

    // Timer per l'animazione di sparo
    private double shootingTimer = 0;
    private static final double SHOOTING_DURATION = 0.15; // Durata in secondi della posa di sparo

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Player(double canvasWidth, double canvasHeight, double hudHeight) {
        this.x = canvasWidth / 2 - width / 2;
        this.y = canvasHeight / 2 - height / 2;

        this.minX = 0;
        this.maxX = canvasWidth - width;
        this.minY = hudHeight;
        this.maxY = canvasHeight - height;

        // Carica le immagini
        loadImages();
        this.currentImg = imgDown; // Sprite iniziale
    }

    /**
     * Carica le immagini in modo sicuro.
     * Se un'immagine non viene trovata, resta null (useremo il cerchio verde di fallback).
     */
    private void loadImages() {
        imgUp = loadImage("/images/weed_up.png");
        imgDown = loadImage("/images/weed_down.png");
        imgLeft = loadImage("/images/weed_left.png");
        imgRight = loadImage("/images/weed_right.png");

        // Sparo
        imgShootUp = loadImage("/images/weed_up_shoot.png");
        imgShootDown = loadImage("/images/weed_down_shoot.png");
        imgShootLeft = loadImage("/images/weed_left_shoot.png");
        imgShootRight = loadImage("/images/weed_right_shoot.png");
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("⚠️ Immagine non trovata: " + path + ". Verrà usato il cerchio verde.");
            return null;
        }
    }

    public void update(double dt, boolean upPressed, boolean downPressed,
                       boolean leftPressed, boolean rightPressed) {

        double movement = speed * dt;
// Se il timer di sparo è attivo, non cambiare l'immagine di movimento
        if (shootingTimer > 0) {
            shootingTimer -= dt; // Decrementa il timer
            if (shootingTimer <= 0) {
                // Tempo scaduto: torna alla posa normale
                revertToNormalSprite();
            }
        } else {
            // Logica di movimento normale
            if (upPressed) {
                y -= movement;
                currentDirection = Direction.UP;
                currentImg = imgUp;
            } else if (downPressed) {
                y += movement;
                currentDirection = Direction.DOWN;
                currentImg = imgDown;
            } else if (leftPressed) {
                x -= movement;
                currentDirection = Direction.LEFT;
                currentImg = imgLeft;
            } else if (rightPressed) {
                x += movement;
                currentDirection = Direction.RIGHT;
                currentImg = imgRight;
            }
        }

        // Clamp bordi
        x = Math.max(minX, Math.min(x, maxX));
        y = Math.max(minY, Math.min(y, maxY));
    }

    /**
     * Ripristina l'immagine di movimento in base alla direzione attuale.
     */
    private void revertToNormalSprite() {
        switch (currentDirection) {
            case UP: currentImg = imgUp; break;
            case DOWN: currentImg = imgDown; break;
            case LEFT: currentImg = imgLeft; break;
            case RIGHT: currentImg = imgRight; break;
        }
    }

    // Aggiungi questo metodo alla classe Player

    /**
     * Crea e restituisce un proiettile (freccia) nella direzione corrente del player.
     * La freccia parte dal centro del player.
     */
    public Projectile shoot() {
        double startX = x + width / 2 - 10;
        double startY = y + height / 2 - 4;

        double dirX = 0;
        double dirY = 0;

        // Attiva il timer di sparo e cambia l'immagine
        shootingTimer = SHOOTING_DURATION;

        switch (currentDirection) {
            case UP:
                dirY = -1;
                startY = y - 10;
                currentImg = imgShootUp; // Cambia sprite
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
        }

        return new Projectile(startX, startY, dirX, dirY);
    }

    public void render(GraphicsContext gc) {
        if (currentImg != null) {
            gc.drawImage(currentImg, x, y, width, height);
        } else {
            // Fallback cerchio verde
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

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}