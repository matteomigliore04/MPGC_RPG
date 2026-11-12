package it.unicam.cs.mpgc.rpg.m131177.game.loots;

/**
 * Tipi di loot raccoglibili nel gioco.
 * Ogni costante ha un nome visualizzabile e un colore esadecimale (per fallback grafici).
 */
public enum LootType {

    // Costanti: NOME("displayName", "#colorHex")
    ARROWS("Frecce", "#8BC34A"),
    HEALTH_POTION("Pozione Cura", "#ff4444"),
    SHIELD_POTION("Pozione Scudo", "#44aaff");

    // Campi
    private final String displayName; // Nome leggibile per la UI
    private final String colorHex;    // Colore associato al loot

    // Costruttore: inizializza i campi di ogni costante
    LootType(String displayName, String colorHex) {
        this.displayName = displayName;
        this.colorHex = colorHex;
    }

    // Getter

    /** Restituisce il nome visualizzabile del loot. */
    public String getDisplayName() { return displayName; }

    /** Restituisce il colore esadecimale associato al loot. */
    public String getColorHex() { return colorHex; }
}