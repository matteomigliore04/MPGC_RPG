package it.unicam.cs.mpgc.rpg.m131177.game;

public interface GameLoopObserver {

    void updateHUD(int health, int shield, int wave, int score,
                   int arrows, int healthPotions, int shieldPotions);

    void showGameOver(int finalScore, int finalWave, int timePlayed, boolean isWin,
                      int goblins, int zombies, int skeletons);
}
