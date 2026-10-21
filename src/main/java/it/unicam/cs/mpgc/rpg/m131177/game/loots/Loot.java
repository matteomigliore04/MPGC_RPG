package it.unicam.cs.mpgc.rpg.m131177.game.loots;

import it.unicam.cs.mpgc.rpg.m131177.game.players.Player;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * Rappresenta un oggetto raccoglibile a terra (loot).
 * Ogni loot ha un tipo, un valore e una posizione.
 */
public class Loot {

    // ============================================================
    // POSIZIONE E ATTRIBUTI (immutabili)
    // ============================================================
    // x, y: posizione a terra
    // size: dimensione del loot (50x50)
    // type: tipo di loot (frecce, pozione cura, pozione scudo)
    // value: quantità da aggiungere al player
    private final double x;
    private final double y;
    private final double size = 50;
    private final LootType type;
    private final int value;

    // ============================================================
    // STATO E GRAFICA
    // ============================================================
    // collected: true se il loot è già stato raccolto
    // sprite: immagine del loot
    private boolean collected = false;
    private Image sprite;

    // ============================================================
    // ANIMAZIONE (galleggiamento)
    // ============================================================
    private double floatOffset = 0;
    private double floatTimer = 0;

    // ============================================================
    // COSTRUTTORE
    // ============================================================
    // Inizializza il loot e carica il suo sprite
    public Loot(double x, double y, LootType type, int value) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.value = value;
        loadSprite();
    }

    // ============================================================
    // CARICAMENTO SPRITE
    // ============================================================
    // Carica l'immagine in base al tipo di loot.
    // Uso Objects.requireNonNull per risolvere il warning "might be null".
    private void loadSprite() {
        String path = switch (type) {
            case ARROWS -> "/images/loot/loot_arrow.png";
            case HEALTH_POTION -> "/images/loot/loot_health_potion.png";
            case SHIELD_POTION -> "/images/loot/loot_shield_potion.png";
        };
        try {
            sprite = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        } catch (Exception e) {
            sprite = null;
        }
    }

    // ============================================================
    // AGGIORNAMENTO (animazione)
    // ============================================================
    // Aggiorna l'animazione di galleggiamento (fluttuazione verticale)
    public void update(double dt) {
        floatTimer += dt;
        floatOffset = Math.sin(floatTimer * 3) * 3;
    }

    // ============================================================
    // RENDERING
    // ============================================================
    // Disegna lo sprite (o un fallback colorato) con l'animazione applicata
    public void render(GraphicsContext gc) {
        if (collected) return;

        double renderY = y + floatOffset;

        if (sprite != null) {
            gc.drawImage(sprite, x, renderY, size, size);
        } else {
            // Fallback: cerchio colorato con emoji iniziale
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

    // ============================================================
    // RACCOLTA
    // ============================================================
    // Raccoglie il loot e applica l'effetto al player.
    // Restituisce true se la raccolta è avvenuta (e quindi il loot va rimosso).
    // Il warning "return value never used" si può ignorare: è utile per la logica
    // del GameLoop che rimuove i loot raccolti.
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

    // ============================================================
    // GETTER
    // ============================================================
    // I getter di type e value sono stati rimossi perché attualmente
    // inutilizzati nel codice (risoluzione warning).
    // Se in futuro servissero, basterà reinserirli.
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
    public boolean isCollected() { return collected; }
}