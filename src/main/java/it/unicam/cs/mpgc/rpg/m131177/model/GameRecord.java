package it.unicam.cs.mpgc.rpg.m131177.model;

// Import JavaFX per le property osservabili
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Record di una partita, esposto tramite JavaFX Properties per la TableView.
 */
public class GameRecord {

    // Campi base della partita
    private final SimpleStringProperty date;
    private final SimpleIntegerProperty score;
    private final SimpleIntegerProperty wave;
    private final SimpleStringProperty difficulty;

    // Campi statistici della partita
    private final SimpleIntegerProperty timePlayed;
    private final SimpleIntegerProperty goblinsKilled;
    private final SimpleIntegerProperty zombiesKilled;
    private final SimpleIntegerProperty skeletonsKilled;

    public GameRecord(String date, int score, int wave, String difficulty,
                      int timePlayed, int goblinsKilled, int zombiesKilled, int skeletonsKilled) {
        this.date = new SimpleStringProperty(date);
        this.score = new SimpleIntegerProperty(score);
        this.wave = new SimpleIntegerProperty(wave);
        this.difficulty = new SimpleStringProperty(difficulty);
        this.timePlayed = new SimpleIntegerProperty(timePlayed);
        this.goblinsKilled = new SimpleIntegerProperty(goblinsKilled);
        this.zombiesKilled = new SimpleIntegerProperty(zombiesKilled);
        this.skeletonsKilled = new SimpleIntegerProperty(skeletonsKilled);
    }

    // Property esposte alla TableView
    public SimpleStringProperty dateProperty() { return date; }
    public SimpleIntegerProperty scoreProperty() { return score; }
    public SimpleIntegerProperty waveProperty() { return wave; }
    public SimpleStringProperty difficultyProperty() { return difficulty; }

    // Getter standard per i calcoli
    public int getTimePlayed() { return timePlayed.get(); }
    public int getGoblinsKilled() { return goblinsKilled.get(); }
    public int getZombiesKilled() { return zombiesKilled.get(); }
    public int getSkeletonsKilled() { return skeletonsKilled.get(); }
}