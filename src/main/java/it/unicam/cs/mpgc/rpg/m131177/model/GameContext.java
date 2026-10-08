package it.unicam.cs.mpgc.rpg.m131177.model;

// Classe che mantiene lo stato condiviso durante la creazione di una partita.
// Segue il pattern "Context": un unico oggetto passato tra le varie schermate
// (menu → difficoltà  → gioco) che contiene i dati scelti dall'utente,
// evitando che i controller si passino informazioni direttamente tra loro.
public class GameContext {

    // --- STATO DELLA PARTITA ---
    // Livello di difficoltà scelto dall'utente
    // (es. "Facile", "Media", "Alta", "Superiore").
    // Al momento è l'unico dato memorizzato nel contesto.
    private String difficulty;

    // --- GETTER E SETTER PER LA DIFFICOLTÀ ---
    // Permettono di leggere e impostare il livello di difficoltà
    // senza accedere direttamente al campo privato.
    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    // --- RESET DEL CONTESTO ---
    // Azzera i dati della partita per permettere di iniziarne una nuova
    // senza che restino tracce della precedente (es. tornando al menu principale).
    public void reset() {
        this.difficulty = null;
    }
}