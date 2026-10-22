package it.unicam.cs.mpgc.rpg.m131177.game;

// ============================================================
// GAME CONSTANTS
// ============================================================
// Classe di utilità per raccogliere tutte le costanti globali del gioco.
// (Costruttore privato per impedire l'istanziazione).
public final class GameConstants {

    // ============================================================
    // DIMENSIONI CANVAS
    // ============================================================
    public static final double CANVAS_WIDTH = 900;
    public static final double CANVAS_HEIGHT = 700;

    // ============================================================
    // CONFINI DELLA MAPPA (muri)
    // ============================================================
    // Margini che delimitano l'area di gioco
    public static final double BORDER_LEFT = 20;
    public static final double BORDER_RIGHT = 880;
    public static final double BORDER_TOP = 50;
    public static final double BORDER_BOTTOM = 650;

    // ============================================================
    // AREA GIOCABILE
    // ============================================================
    // Limiti usati per impedire al player di attraversare i muri
    public static final double PLAYABLE_MIN_X = BORDER_LEFT;
    public static final double PLAYABLE_MAX_X = BORDER_RIGHT;
    public static final double PLAYABLE_MIN_Y = BORDER_TOP;
    public static final double PLAYABLE_MAX_Y = BORDER_BOTTOM;

    // ============================================================
    // ZONE DI SPAWN (aperture laterali)
    // ============================================================
    // Posizioni X dei punti di spawn (sinistra/destra)
    public static final double SPAWN_LEFT_X = 10;
    public static final double SPAWN_RIGHT_X = CANVAS_WIDTH - 50;

    // Apertura centrale per lo spawn (Y e altezza)
    public static final double OPENING_CENTER_Y = 350;
    public static final double OPENING_HEIGHT = 80;

    // Limiti Y dell'apertura di spawn (calcolati)
    public static final double SPAWN_OPENING_MIN_Y = OPENING_CENTER_Y - (OPENING_HEIGHT / 2);
    public static final double SPAWN_OPENING_MAX_Y = OPENING_CENTER_Y + (OPENING_HEIGHT / 2);

    // ============================================================
    // COSTRUTTORE PRIVATO
    // ============================================================
    // Impedisce l'istanziazione della classe (utility class)
    private GameConstants() {}
}