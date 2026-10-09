package it.unicam.cs.mpgc.rpg.m131177.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rappresenta il giocatore (la cima d'erba) nel gioco.
 * Gestisce posizione, movimento e rendering del personaggio.
 */
public class Player {

    private double x;
    private double y;
    private double width = 40;
    private double height = 40;
    private double speed = 200; // pixel al secondo

    // Limiti di movimento
    private double minX;
    private double maxX;
    private double minY; // Limite superiore (sotto l'HUD)
    private double maxY;

    /**
     * Costruisce il player posizionato al centro della schermata.
     *
     * @param canvasWidth  larghezza del canvas
     * @param canvasHeight altezza del canvas
     * @param hudHeight    altezza dell'HUD (zona proibita in alto)
     */
    public Player(double canvasWidth, double canvasHeight, double hudHeight) {
        this.x = canvasWidth / 2 - width / 2;
        this.y = canvasHeight / 2 - height / 2;

        this.minX = 0;
        this.maxX = canvasWidth - width;
        this.minY = hudHeight; // Non può salire oltre l'HUD
        this.maxY = canvasHeight - height;
    }

    /**
     * Aggiorna la posizione del player in base ai tasti premuti.
     * Usa il delta time per movimento fluido indipendente dai FPS.
     *
     * @param dt          tempo trascorso dall'ultimo frame (in secondi)
     * @param upPressed   true se il tasto su è premuto
     * @param downPressed true se il tasto giù è premuto
     * @param leftPressed true se il tasto sinistro è premuto
     * @param rightPressed true se il tasto destro è premuto
     */
    public void update(double dt, boolean upPressed, boolean downPressed,
                       boolean leftPressed, boolean rightPressed) {

        double movement = speed * dt;

        if (upPressed) {
            y -= movement;
        }
        if (downPressed) {
            y += movement;
        }
        if (leftPressed) {
            x -= movement;
        }
        if (rightPressed) {
            x += movement;
        }

        // Clamp: mantiene il player dentro i limiti
        x = Math.max(minX, Math.min(x, maxX));
        y = Math.max(minY, Math.min(y, maxY));
    }

    /**
     * Disegna il player sul canvas.
     * Per ora è un cerchio verde (la cima d'erba).
     */
    public void render(GraphicsContext gc) {
        // Corpo del player (cerchio verde)
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(x, y, width, height);

        // Bordo più scuro per definizione
        gc.setStroke(Color.DARKGREEN);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, width, height);

        // "Occhi" per dare un po' di carattere
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 10, y + 12, 6, 6);
        gc.fillOval(x + 24, y + 12, 6, 6);
    }

    // Getters per eventuali collisioni future
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}