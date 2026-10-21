package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

// ============================================================
// IMPORT
// ============================================================
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// ============================================================
// SKELETON (Nemico)
// ============================================================
// Nemico intermedio: statistiche bilanciate (velocità e danno medi).
public class Skeleton extends Enemy {

    // ============================================================
    // COSTRUTTORE
    // ============================================================
    // Inizializza le statistiche specifiche dello Skeleton.
    public Skeleton(double x, double y) {
        super(x, y);
        this.maxHealth = 50;
        this.speed = 90;        // Velocità media
        this.damage = 10;       // Danno medio
        this.scoreValue = 15;   // Punti medi
        this.arrowDrop = 3;     // Droppa 3 frecce
    }

    // ============================================================
    // GRAFICA
    // ============================================================
    // Carica lo sprite dello Skeleton; se manca, restituisce null (fallback).
    @Override
    protected void loadSprite() {
        try {
            sprite = new Image(getClass().getResourceAsStream("/images/enemies/skeleton.png"));
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
        return "Skeleton";
    }

    // Colore di fallback se lo sprite non è disponibile (Grigio chiaro).
    @Override
    protected Color getFallbackColor() {
        return Color.LIGHTGRAY;
    }
}