package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

import java.util.Random;

/**
 * Factory per la creazione di nemici.
 * Permette di aggiungere nuovi tipi di nemici senza modificare il GameLoop.
 */
public class EnemyFactory {

    private final Random random = new Random();

    /**
     * Crea un nemico casuale in base alla difficoltà.
     * @param difficulty "Germoglio", "Erba Selvatica", "Fumo Denso", "100% THC"
     */
    public Enemy createEnemy(double canvasWidth, double canvasHeight, double hudHeight, String difficulty) {
        int type = random.nextInt(getNumberOfTypes(difficulty));

        return switch (type) {
            case 0 -> new Goblin(canvasWidth, canvasHeight, hudHeight);
            case 1 -> new Zombie(canvasWidth, canvasHeight, hudHeight);
            case 2 -> new Skeleton(canvasWidth, canvasHeight, hudHeight);
            default -> new Goblin(canvasWidth, canvasHeight, hudHeight);
        };
    }

    /**
     * Restituisce il numero di tipi di nemici disponibili in base alla difficoltà.
     * Più alta la difficoltà, più tipi di nemici possono apparire.
     */
    private int getNumberOfTypes(String difficulty) {
        return switch (difficulty) {
            case "Germoglio" -> 1;          // Solo goblin
            case "Erba Selvatica" -> 2;     // Goblin + Zombie
            case "Fumo Denso" -> 3;         // Tutti e 3
            case "100% THC" -> 3;           // Tutti e 3 (più frequenti)
            default -> 1;
        };
    }
}