package it.unicam.cs.mpgc.rpg.m131177.game;

/**
 * Costanti globali del gioco (utility class, non istanziabile).
 */
public final class GameConstants {

    // Dimensioni della canvas
    public static final double CANVAS_WIDTH = 900;
    public static final double CANVAS_HEIGHT = 700;

    // Confini della mappa (muri)
    public static final double BORDER_LEFT = 20;
    public static final double BORDER_RIGHT = 880;
    public static final double BORDER_TOP = 50;
    public static final double BORDER_BOTTOM = 650;

    // Area giocabile (limiti per il player)
    public static final double PLAYABLE_MIN_X = BORDER_LEFT;
    public static final double PLAYABLE_MAX_X = BORDER_RIGHT;
    public static final double PLAYABLE_MIN_Y = BORDER_TOP;
    public static final double PLAYABLE_MAX_Y = BORDER_BOTTOM;

    // Zone di spawn (aperture laterali)
    public static final double SPAWN_LEFT_X = 10;
    public static final double SPAWN_RIGHT_X = CANVAS_WIDTH - 50;

    // Apertura centrale per lo spawn (Y e altezza)
    public static final double OPENING_CENTER_Y = 350;
    public static final double OPENING_HEIGHT = 80;

    // Limiti Y dell'apertura di spawn (calcolati)
    public static final double SPAWN_OPENING_MIN_Y = OPENING_CENTER_Y - (OPENING_HEIGHT / 2);
    public static final double SPAWN_OPENING_MAX_Y = OPENING_CENTER_Y + (OPENING_HEIGHT / 2);

    // Costruttore privato: impedisce l'istanziazione
    private GameConstants() {}
}