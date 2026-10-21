package it.unicam.cs.mpgc.rpg.m131177.game;

public final class GameConstants {

    // Dimensioni canvas
    public static final double CANVAS_WIDTH = 900;
    public static final double CANVAS_HEIGHT = 700;

    // ============================================
    // CONFINI DELLA MAPPA (MODIFICA QUESTI VALORI)
    // ============================================

    // Margine sinistro (muro sinistro)
    public static final double BORDER_LEFT = 20;

    // Margine destro (muro destro)
    public static final double BORDER_RIGHT = 820;

    // Margine superiore (muro in alto)
    public static final double BORDER_TOP = 50;

    // Margine inferiore (muro in basso)
    public static final double BORDER_BOTTOM = 600;

    // Area giocabile (usata per limitare movimento)
    public static final double PLAYABLE_MIN_X = BORDER_LEFT;
    public static final double PLAYABLE_MAX_X = BORDER_RIGHT;
    public static final double PLAYABLE_MIN_Y = BORDER_TOP;
    public static final double PLAYABLE_MAX_Y = BORDER_BOTTOM;

    // Zone di spawn dalle aperture laterali
    public static final double SPAWN_LEFT_X = 10;
    public static final double SPAWN_RIGHT_X = CANVAS_WIDTH - 50;
    public static final double OPENING_CENTER_Y = 350;
    public static final double OPENING_HEIGHT = 80;
    public static final double SPAWN_OPENING_MIN_Y = OPENING_CENTER_Y - (OPENING_HEIGHT / 2);
    public static final double SPAWN_OPENING_MAX_Y = OPENING_CENTER_Y + (OPENING_HEIGHT / 2);

    private GameConstants() {}
}