package it.unicam.cs.mpgc.rpg.m131177.model;

/**
 * Classe che mantiene lo stato condiviso durante la creazione di una partita.
 * Segue il pattern "Context" per passare informazioni tra le diverse schermate
 * senza accoppiamento diretto tra i controller.
 */
public class GameContext {

    private String difficulty;
    private String monster;

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getMonster() {
        return monster;
    }

    public void setMonster(String monster) {
        this.monster = monster;
    }

    /**
     * Resetta il contesto per una nuova partita.
     */
    public void reset() {
        this.difficulty = null;
        this.monster = null;
    }
}