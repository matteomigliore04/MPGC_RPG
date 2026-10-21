package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

// ============================================================
// IMPORT
// ============================================================
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// ============================================================
// GOBLIN (Nemico)
// ============================================================
// Nemico base: veloce, con pochi punti vita e danni contenuti.
public class Goblin extends Enemy {

    // ============================================================
    // COSTRUTTORE
    // ============================================================
    // Inizializza le statistiche specifiche del Goblin.
    // (Nota: maxHealth viene impostato DOPO super(), quindi currentHealth
    // nel costruttore della superclasse sarà 0 finché non viene aggiornato qui).
    public Goblin(double x, double y) {
        super(x, y);
        this.maxHealth = 30;
        this.speed = 120;       // Veloce
        this.damage = 5;        // Poco danno
        this.scoreValue = 10;   // Pochi punti
        this.arrowDrop = 2;     // Droppa 2 frecce
    }

    // ============================================================
    // GRAFICA
    // ============================================================
    // Carica lo sprite del Goblin; se manca, restituisce null (fallback).
    @Override
    protected void loadSprite() {
        try {
            sprite = new Image(getClass().getResourceAsStream("/images/enemies/goblin.png"));
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
        return "Goblin";
    }

    // Colore di fallback se lo sprite non è disponibile (Verde).
    @Override
    protected Color getFallbackColor() {
        return Color.LIMEGREEN;
    }
}