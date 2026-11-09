package it.unicam.cs.mpgc.rpg.m131177.game;

public record DifficultySettings(
        double enemyHealthMultiplier,
        double enemySpeedMultiplier,
        double spawnRateMultiplier,
        double rewardMultiplier
) {

    private static final DifficultySettings MEDIUM = new DifficultySettings(1.0, 1.0, 1.0, 1.0);

    public static DifficultySettings from(String difficulty) {
        if (difficulty == null) {
            return MEDIUM;
        }

        return switch (difficulty) {
            case "Easy" -> new DifficultySettings(0.8, 0.8, 0.8, 0.8);
            case "Hard" -> new DifficultySettings(1.3, 1.2, 1.3, 1.5);
            case "Extreme" -> new DifficultySettings(1.6, 1.5, 1.6, 2.0);
            case "Medium" -> MEDIUM;
            default -> MEDIUM;
        };
    }
}
