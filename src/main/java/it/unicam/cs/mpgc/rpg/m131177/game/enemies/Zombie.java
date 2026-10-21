package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

// ============================================================
// IMPORT
// ============================================================
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// ============================================================
// ZOMBIE (Nemico)
// ============================================================
// Nemico lento ma resistente: alta vita e alto danno.
public class Zombie extends Enemy {

    // ============================================================
    // COSTRUTTORE
    // ============================================================
    // Inizializza le statistiche specifiche dello Zombie.
    public Zombie(double x, double y) {
        super(x, y);
        this.maxHealth = 80;
        this.speed = 60;        // Lento
        this.damage = 15;       // Molto danno
        this.scoreValue = 25;   // Più punti
        this.arrowDrop = 4;     // Droppa 4 frecce
    }

    // ============================================================
    // GRAFICA
    // ============================================================
    // Carica lo sprite dello Zombie; se manca, restituisce null (fallback).
    @Override
    protected void loadSprite() {
        try {
            sprite = new Image(getClass().getResourceAsStream("/images/enemies/zombie.png"));
        } catch (Exception e) {
            sprite = null;
        }
    }

    // ============================================================
    // IDENTIFICAZIONE E FALLBACK
    // ============================================================
    // Restituisce il nome visualizzato/loggato del nemico.
    @Override
    public String getName() {
        return "Zombie";
    }

    // Colore di fallback se lo sprite non è disponibile (Verde scuro).
    @Override
    protected Color getFallbackColor() {
        return Color.DARKGREEN;
    }
}