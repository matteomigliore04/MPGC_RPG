package it.unicam.cs.mpgc.rpg.m131177.game.loots;

/**
 * Tipi di loot raccoglibili nel gioco.
 */
public enum LootType {
    ARROWS("Frecce", "#8BC34A"),
    HEALTH_POTION("Pozione Cura", "#ff4444"),
    SHIELD_POTION("Pozione Scudo", "#44aaff");

    private final String displayName;
    private final String colorHex;

    LootType(String displayName, String colorHex) {
        this.displayName = displayName;
        this.colorHex = colorHex;
    }

    public String getDisplayName() { return displayName; }
    public String getColorHex() { return colorHex; }
}