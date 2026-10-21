package it.unicam.cs.mpgc.rpg.m131177.model;

// ============================================================
// GAME CONTEXT
// ============================================================
// Mantiene lo stato condiviso durante la creazione di una partita
// (pattern "Context": evita che i controller si passino dati a vicenda).
public class GameContext {

    // ============================================================
    // STATO DELLA PARTITA
    // ============================================================
    // Livello di difficoltà scelto dall'utente
    // (es. "Facile", "Media", "Alta", "Superiore").
    private String difficulty;

    // ============================================================
    // GETTER E SETTER — DIFFICOLTÀ
    // ============================================================
    // Legge e imposta il livello di difficoltà in modo incapsulato.
    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    // ============================================================
    // RESET
    // ============================================================
    // Azzera i dati per iniziare una nuova partita pulita
    // (es. quando si torna al menu principale).
    public void reset() {
        this.difficulty = null;
    }
}