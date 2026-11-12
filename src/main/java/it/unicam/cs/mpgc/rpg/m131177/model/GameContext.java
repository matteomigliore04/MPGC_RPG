package it.unicam.cs.mpgc.rpg.m131177.model;

/**
 * Contesto di gioco: mantiene lo stato condiviso durante la creazione di una partita.
 * Evita che i controller si scambino dati direttamente (pattern Context).
 */
public class GameContext {

    // Livello di difficoltà scelto dall'utente (es. "Facile", "Media", "Alta", "Superiore")
    private String difficulty;

    // Getter e setter per la difficoltà
    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    // Azzera i dati per iniziare una nuova partita pulita
    public void reset() {
        this.difficulty = null;
    }
}