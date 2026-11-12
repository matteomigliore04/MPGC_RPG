package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

// Import JavaFX
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Goblin: nemico base veloce, con pochi punti vita e danni contenuti.
 */
public class Goblin extends Enemy {

    /**
     * Inizializza le statistiche specifiche del Goblin.
     * Nota: maxHealth è impostato dopo super(), quindi currentHealth
     * nella superclasse resta 0 finché non viene aggiornato qui.
     */
    public Goblin(double x, double y) {
        super(x, y);
        this.maxHealth = 30;
        this.speed = 120;       // Veloce
        this.damage = 5;        // Poco danno
        this.scoreValue = 10;   // Pochi punti
        this.arrowDrop = 2;     // Droppa 2 frecce
    }

    /**
     * Carica lo sprite del Goblin; se manca, sprite resta null (fallback).
     */
    @Override
    protected void loadSprite() {
        try {
            sprite = new Image(getClass().getResourceAsStream("/images/enemies/goblin.png"));
        } catch (Exception e) {
            sprite = null;
        }
    }

    /**
     * Restituisce il nome visualizzato/loggato del nemico.
     */
    @Override
    public String getName() {
        return "Goblin";
    }

    /**
     * Colore di fallback se lo sprite non è disponibile (verde).
     */
    @Override
    protected Color getFallbackColor() {
        return Color.LIMEGREEN;
    }
}