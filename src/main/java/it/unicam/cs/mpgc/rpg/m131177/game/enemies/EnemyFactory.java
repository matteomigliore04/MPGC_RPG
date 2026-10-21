package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

// ============================================================
// IMPORT
// ============================================================
import it.unicam.cs.mpgc.rpg.m131177.game.GameConstants;
import java.util.Random;

// ============================================================
// ENEMY FACTORY
// ============================================================
// Factory per la creazione di nemici.
// Permette di aggiungere nuovi tipi senza modificare il GameLoop.
public class EnemyFactory {

    // ============================================================
    // STATO
    // ============================================================
    // Generatore di numeri casuali per spawn e scelta del tipo
    private final Random random = new Random();

    // ============================================================
    // CREAZIONE NEMICO
    // ============================================================
    // Istanzia un nemico casuale, lo posiziona a sinistra o a destra
    // e ne scala le statistiche in base all'ondata corrente.
    public Enemy createEnemy(int wave) {
        // Sceglie un tipo in base alle probabilità dell'ondata
        int type = random.nextInt(getNumberOfTypes(wave));

        // Calcola la posizione Y all'interno dell'apertura laterale
        double spawnY = GameConstants.SPAWN_OPENING_MIN_Y +
                (random.nextDouble() * (GameConstants.SPAWN_OPENING_MAX_Y - GameConstants.SPAWN_OPENING_MIN_Y));

        // Sceglie il lato di spawn (50% sinistra, 50% destra)
        double spawnX;
        if (random.nextBoolean()) {
            spawnX = GameConstants.SPAWN_LEFT_X;  // Apertura sinistra
        } else {
            spawnX = GameConstants.SPAWN_RIGHT_X; // Apertura destra
        }

        // Istanzia il nemico corretto tramite switch expression
        Enemy enemy = switch (type) {
            case 1 -> new Zombie(spawnX, spawnY);
            case 2 -> new Skeleton(spawnX, spawnY);
            default -> new Goblin(spawnX, spawnY);
        };

        // Applica lo scaling in base all'ondata
        double healthMultiplier = 1.0 + ((wave - 1) * 0.10);
        double speedMultiplier = 1.0 + ((wave - 1) * 0.05);
        enemy.scaleStats(healthMultiplier, speedMultiplier);

        return enemy;
    }

    // ============================================================
    // CONFIGURAZIONE TIPI PER ONDATA
    // ============================================================
    // Restituisce quanti tipi di nemici possono spawnare in base all'ondata
    private int getNumberOfTypes(int wave) {
        return switch (wave) {
            case 1 -> 1;          // Solo goblin
            case 2, 3 -> 2;       // Goblin + Zombie
            default -> 3;         // Tutti e 3 dall'ondata 4 in poi
        };
    }
}