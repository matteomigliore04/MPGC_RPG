package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Goblin extends Enemy {

    public Goblin(double x, double y) {
        super(x, y);
        this.maxHealth = 30;
        this.speed = 120;       // Veloce
        this.damage = 5;        // Poco danno
        this.scoreValue = 10;   // Pochi punti
        this.arrowDrop = 2;     // Droppa 2 frecce
    }

    @Override
    protected void loadSprite() {
        try {
            sprite = new Image(getClass().getResourceAsStream("/images/enemies/goblin.png"));
        } catch (Exception e) {
            sprite = null;
        }
    }

    @Override
    public String getName() {
        return "Goblin";
    }

    @Override
    protected Color getFallbackColor() {
        return Color.LIMEGREEN; // Verde per il goblin
    }
}