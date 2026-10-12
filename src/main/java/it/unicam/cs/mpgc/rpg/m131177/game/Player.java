package it.unicam.cs.mpgc.rpg.m131177.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player {

    private double x;
    private double y;
    private double width = 70;
    private double height = 70;
    private double speed = 200;

    private double minX, maxX, minY, maxY;

    // Fattore di normalizzazione per movimento diagonale (1/√2)
    private static final double DIAGONAL_FACTOR = 0.70710678118; // ≈ 1/√2

    // Immagini movimento (4 cardinali + 4 diagonali)
    private Image imgUp, imgDown, imgLeft, imgRight;
    private Image imgUpLeft, imgUpRight, imgDownLeft, imgDownRight;

    // Immagini sparo (4 cardinali + 4 diagonali)
    private Image imgShootUp, imgShootDown, imgShootLeft, imgShootRight;
    private Image imgShootUpLeft, imgShootUpRight, imgShootDownLeft, imgShootDownRight;

    private Image currentImg;
    private Direction currentDirection = Direction.DOWN;

    private double shootingTimer = 0;
    private static final double SHOOTING_DURATION = 0.15;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT,
        UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }

    public Player(double canvasWidth, double canvasHeight, double hudHeight) {
        this.x = canvasWidth / 2 - width / 2;
        this.y = canvasHeight / 2 - height / 2;

        this.minX = 0;
        this.maxX = canvasWidth - width;
        this.minY = hudHeight;
        this.maxY = canvasHeight - height;

        loadImages();
        this.currentImg = imgDown;
    }

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

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("⚠️ Immagine non trovata: " + path);
            return null;
        }
    }

    public void update(double dt, boolean upPressed, boolean downPressed,
                       boolean leftPressed, boolean rightPressed) {

        // Calcola il fattore di velocità (normalizzazione diagonale)
        // Se si muove in diagonale (2 tasti premuti), riduci la velocità di √2
        double speedMultiplier = 1.0;
        int axesPressed = 0;
        if ((upPressed || downPressed)) axesPressed++;
        if ((leftPressed || rightPressed)) axesPressed++;
        if (axesPressed == 2) {
            speedMultiplier = DIAGONAL_FACTOR;
        }

        double movement = speed * dt * speedMultiplier;

        if (shootingTimer > 0) {
            // Durante l'animazione di sparo,允许 il movimento ma non cambiare sprite
            if (upPressed) y -= movement;
            if (downPressed) y += movement;
            if (leftPressed) x -= movement;
            if (rightPressed) x += movement;

            shootingTimer -= dt;
            if (shootingTimer <= 0) {
                revertToNormalSprite();
            }
        } else {
            // Movimento normale con sprite che cambia
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

        // Clamp bordi
        x = Math.max(minX, Math.min(x, maxX));
        y = Math.max(minY, Math.min(y, maxY));
    }

    /**
     * Determina la direzione in base alla combinazione di tasti premuti.
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
     * Crea e restituisce un proiettile nella direzione corrente.
     */
    public Projectile shoot() {
        double startX = x + width / 2 - 10;
        double startY = y + height / 2 - 4;

        double dirX = 0;
        double dirY = 0;

        shootingTimer = SHOOTING_DURATION;

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

    public void render(GraphicsContext gc) {
        if (currentImg != null) {
            gc.drawImage(currentImg, x, y, width, height);
        } else {
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

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}