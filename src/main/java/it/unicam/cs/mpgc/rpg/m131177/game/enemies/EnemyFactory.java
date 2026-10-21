package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

import java.util.Random;

/**
 * Factory per la creazione di nemici.
 * Permette di aggiungere nuovi tipi di nemici senza modificare il GameLoop.
 */
public class EnemyFactory {

    private final Random random = new Random();

    public Enemy createEnemy(double canvasWidth, double canvasHeight, double hudHeight, int wave) {
        int type = random.nextInt(getNumberOfTypes(wave));

        Enemy enemy = switch (type) {
            case 0 -> new Goblin(canvasWidth, canvasHeight, hudHeight);
            case 1 -> new Zombie(canvasWidth, canvasHeight, hudHeight);
            case 2 -> new Skeleton(canvasWidth, canvasHeight, hudHeight);
            default -> new Goblin(canvasWidth, canvasHeight, hudHeight);
        };

        // ✅ SCALING: Aumenta vita e velocità in base all'ondata (+10% vita e +5% velocità per ondata)
        double healthMultiplier = 1.0 + ((wave - 1) * 0.10);
        double speedMultiplier = 1.0 + ((wave - 1) * 0.05);

        enemy.scaleStats(healthMultiplier, speedMultiplier);

        return enemy;
    }

    private int getNumberOfTypes(int wave) {
        return switch (wave) {
            case 1 -> 1;          // Solo goblin
            case 2, 3 -> 2;       // Goblin + Zombie
            default -> 3;         // Tutti e 3 dall'ondata 4 in poi
        };
    }
}