package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

/**
 * Configurazione dei moltiplicatori di difficoltà per una partita.
 */
public record DifficultySettings(
        double enemyHealthMultiplier,
        double enemySpeedMultiplier,
        double spawnRateMultiplier,
        double rewardMultiplier
) {

    // Configurazione di default (difficoltà Media)
    private static final DifficultySettings MEDIUM = new DifficultySettings(1.0, 1.0, 1.0, 1.0);

    /**
     * Restituisce la configurazione corrispondente alla difficoltà data.
     * Se il valore è nullo o sconosciuto, restituisce MEDIUM.
     */
    public static DifficultySettings from(String difficulty) {
        if (difficulty == null) {
            return MEDIUM;
        }

        return switch (difficulty) {
            case "Easy"    -> new DifficultySettings(0.8, 0.8, 0.8, 0.8);
            case "Hard"    -> new DifficultySettings(1.3, 1.2, 1.3, 1.5);
            case "Extreme" -> new DifficultySettings(1.6, 1.5, 1.6, 2.0);
            default        -> MEDIUM;
        };
    }
}