package it.unicam.cs.mpgc.rpg.m131177.model;

/**
 * Classe che mantiene lo stato condiviso durante la creazione di una partita.
 * Segue il pattern "Context" per passare informazioni tra le diverse schermate
 * senza accoppiamento diretto tra i controller.
 *
 * In pratica: ogni schermata (menu, difficoltà, mostro) riceve lo stesso
 * GameContext e vi scrive/legge i dati man mano che l'utente avanza,
 * così lo stato della partita non va perso tra un cambio di scena e l'altro.
 */
public class GameContext {

    // --- STATO DELLA PARTITA ---
    // Campi che rappresentano le scelte fatte finora dall'utente.
    // - difficulty: livello scelto (es. "Facile", "Media", "Alta", "Superiore")
    // - monster:    mostro scelto (es. "Troll", "Ragno Gigante", ecc.)
    private String difficulty;
    private String monster;

    // --- GETTER E SETTER PER LA DIFFICOLTÀ ---
    // Permettono di leggere e impostare il livello di difficoltà
    // senza accedere direttamente al campo privato
    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    // --- GETTER E SETTER PER IL MOSTRO ---
    // Permettono di leggere e impostare il mostro scelto
    // senza accedere direttamente al campo privato
    public String getMonster() {
        return monster;
    }

    public void setMonster(String monster) {
        this.monster = monster;
    }

    /**
     * Resetta il contesto per una nuova partita.
     * Azzera entrambi i campi, così il giocatore può ricominciare
     * da zero senza che restino tracce della partita precedente.
     */
    public void reset() {
        this.difficulty = null;
        this.monster = null;
    }
}