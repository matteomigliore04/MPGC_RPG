package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Zombie extends Enemy {

    public Zombie(double x, double y) {
        super(x, y);
        this.maxHealth = 80;
        this.speed = 60;        // Lento
        this.damage = 15;       // Molto danno
        this.scoreValue = 25;   // Più punti
        this.arrowDrop = 4;     // Droppa 4 frecce
    }

    @Override
    protected void loadSprite() {
        try {
            sprite = new Image(getClass().getResourceAsStream("/images/enemies/zombie.png"));
        } catch (Exception e) {
            sprite = null;
        }
    }

    @Override
    public String getName() {
        return "Zombie";
    }

    @Override
    protected Color getFallbackColor() {
        return Color.DARKGREEN; // Verde scuro per lo zombie
    }
}