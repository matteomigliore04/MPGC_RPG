package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

import it.unicam.cs.mpgc.rpg.m131177.game.GameConstants;
import java.util.Random;

/**
 * Factory per la creazione di nemici.
 * Permette di aggiungere nuovi tipi di nemici senza modificare il GameLoop.
 */
public class EnemyFactory {

    private final Random random = new Random();

    public Enemy createEnemy(int wave) {
        int type = random.nextInt(getNumberOfTypes(wave));

        // ✅ Spawn dalle aperture laterali (sinistra o destra)
        double spawnX;
        double spawnY = GameConstants.SPAWN_OPENING_MIN_Y +
                (random.nextDouble() * (GameConstants.SPAWN_OPENING_MAX_Y - GameConstants.SPAWN_OPENING_MIN_Y));

        // 50% probabilità di spawnare da sinistra o destra
        if (random.nextBoolean()) {
            spawnX = GameConstants.SPAWN_LEFT_X;  // Apertura sinistra
        } else {
            spawnX = GameConstants.SPAWN_RIGHT_X; // Apertura destra
        }

        Enemy enemy = switch (type) {
            case 0 -> new Goblin(spawnX, spawnY);
            case 1 -> new Zombie(spawnX, spawnY);
            case 2 -> new Skeleton(spawnX, spawnY);
            default -> new Goblin(spawnX, spawnY);
        };

        // Scaling difficoltà
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