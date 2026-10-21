package it.unicam.cs.mpgc.rpg.m131177.game.loots;

// ============================================================
// ENUM DEI TIPI DI LOOT
// ============================================================
// Definisce i possibili oggetti raccoglibili nel gioco,
// associando a ciascuno un nome visualizzabile e un colore (esadecimale).
public enum LootType {

    // ============================================================
    // COSTANTI
    // ============================================================
    // Formato: NOME("displayName", "#colorHex")
    ARROWS("Frecce", "#8BC34A"),
    HEALTH_POTION("Pozione Cura", "#ff4444"),
    SHIELD_POTION("Pozione Scudo", "#44aaff");

    // ============================================================
    // CAMPI
    // ============================================================
    // displayName: nome leggibile da mostrare nella UI
    // colorHex: colore associato al loot (per fallback grafici)
    private final String displayName;
    private final String colorHex;

    // ============================================================
    // COSTRUTTORE
    // ============================================================
    // Inizializza i campi di ogni costante enum
    LootType(String displayName, String colorHex) {
        this.displayName = displayName;
        this.colorHex = colorHex;
    }

    // ============================================================
    // GETTER
    // ============================================================
    // Restituisce il nome visualizzabile del loot
    public String getDisplayName() { return displayName; }

    // Restituisce il colore esadecimale associato al loot
    public String getColorHex() { return colorHex; }
}