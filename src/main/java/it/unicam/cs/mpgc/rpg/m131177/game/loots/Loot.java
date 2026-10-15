package it.unicam.cs.mpgc.rpg.m131177.game.loots;
import it.unicam.cs.mpgc.rpg.m131177.game.players.Player;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rappresenta un oggetto raccoglibile a terra (loot).
 * Ogni loot ha un tipo, un valore e una posizione.
 */
public class Loot {

    private double x;
    private double y;
    private double size = 24;
    private LootType type;
    private int value;
    private Image sprite;
    private boolean collected = false;

    // Animazione di "galleggiamento"
    private double floatOffset = 0;
    private double floatTimer = 0;

    public Loot(double x, double y, LootType type, int value) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.value = value;
        loadSprite();
    }

    private void loadSprite() {
        String path = switch (type) {
            case ARROWS -> "/images/loot/loot_arrow.png";
            case HEALTH_POTION -> "/images/loot/loot_health_potion.png";
            case SHIELD_POTION -> "/images/loot/loot_shield_potion.png";
        };
        try {
            sprite = new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            sprite = null;
        }
    }

    public void update(double dt) {
        // Animazione di galleggiamento
        floatTimer += dt;
        floatOffset = Math.sin(floatTimer * 3) * 3;
    }

    public void render(GraphicsContext gc) {
        if (collected) return;

        double renderY = y + floatOffset;

        if (sprite != null) {
            gc.drawImage(sprite, x, renderY, size, size);
        } else {
            // Fallback: cerchio colorato con iniziale
            gc.setFill(Color.web(type.getColorHex()));
            gc.fillOval(x, renderY, size, size);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeOval(x, renderY, size, size);

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(12));
            String initial = switch (type) {
                case ARROWS -> "🏹";
                case HEALTH_POTION -> "❤️";
                case SHIELD_POTION -> "🛡️";
            };
            gc.fillText(initial, x + 4, renderY + 16);
        }
    }

    /**
     * Raccoglie il loot. Restituisce true se la raccolta è avvenuta.
     */
    public boolean collect(Player player) {
        if (collected) return false;
        collected = true;

        switch (type) {
            case ARROWS -> player.collectArrows(value);
            case HEALTH_POTION -> player.addHealthPotion(value);
            case SHIELD_POTION -> player.addShieldPotion(value);
        }
        return true;
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
    public LootType getType() { return type; }
    public int getValue() { return value; }
    public boolean isCollected() { return collected; }
}