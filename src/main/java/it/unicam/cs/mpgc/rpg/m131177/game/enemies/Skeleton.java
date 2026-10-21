package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Skeleton extends Enemy {

    public Skeleton(double canvasWidth, double canvasHeight, double hudHeight) {
        super(canvasWidth, canvasHeight, hudHeight);
        this.maxHealth = 50;
        this.speed = 90;        // Velocità media
        this.damage = 10;       // Danno medio
        this.scoreValue = 15;   // Punti medi
        this.arrowDrop = 3;     // Droppa 3 frecce
    }

    @Override
    protected void loadSprite() {
        try {
            sprite = new Image(getClass().getResourceAsStream("/images/enemies/skeleton.png"));
        } catch (Exception e) {
            sprite = null;
        }
    }

    @Override
    public String getName() {
        return "Skeleton";
    }

    @Override
    protected Color getFallbackColor() {
        return Color.LIGHTGRAY; // Grigio per lo scheletro
    }
}