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

    // Immagini per le 4 direzioni
    private Image imgUp, imgDown, imgLeft, imgRight;
    private Image currentImg;

    // Direzione attuale (di default guarda in basso)
    private Direction currentDirection = Direction.DOWN;

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

        // Aggiorna la direzione e lo sprite in base ai tasti premuti
        // (Priorità: Verticale > Orizzontale, per evitare movimenti diagonali confusi)
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

        // Clamp: mantiene il player dentro i limiti
        x = Math.max(minX, Math.min(x, maxX));
        y = Math.max(minY, Math.min(y, maxY));
    }

    public void render(GraphicsContext gc) {
        // Se le immagini sono state caricate correttamente, disegnale
        if (currentImg != null) {
            gc.drawImage(currentImg, x, y, width, height);
        } else {
            // FALLBACK: Se le immagini mancano, disegna il cerchio verde
            gc.setFill(Color.LIMEGREEN);
            gc.fillOval(x, y, width, height);
            gc.setStroke(Color.DARKGREEN);
            gc.setLineWidth(2);
            gc.strokeOval(x, y, width, height);

            // Occhi di fallback
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