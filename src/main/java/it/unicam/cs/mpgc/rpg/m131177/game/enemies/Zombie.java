package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

// Import JavaFX
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Zombie: nemico lento ma resistente, con alta vita e alto danno.
 */
public class Zombie extends Enemy {

    /**
     * Inizializza le statistiche specifiche dello Zombie.
     */
    public Zombie(double x, double y) {
        super(x, y);
        this.maxHealth = 80;
        this.speed = 60;        // Lento
        this.damage = 15;       // Alto danno
        this.scoreValue = 25;   // Più punti
        this.arrowDrop = 4;     // Droppa 4 frecce
    }

    /**
     * Carica lo sprite dello Zombie; se manca, sprite resta null (fallback).
     */
    @Override
    protected void loadSprite() {
        try {
            sprite = new Image(getClass().getResourceAsStream("/images/enemies/zombie.png"));
        } catch (Exception e) {
            sprite = null;
        }
    }

    /**
     * Restituisce il nome visualizzato/loggato del nemico.
     */
    @Override
    public String getName() {
        return "Zombie";
    }

    /**
     * Colore di fallback se lo sprite non è disponibile (verde scuro).
     */
    @Override
    protected Color getFallbackColor() {
        return Color.DARKGREEN;
    }
}