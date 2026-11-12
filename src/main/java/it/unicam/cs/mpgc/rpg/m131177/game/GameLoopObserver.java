package it.unicam.cs.mpgc.rpg.m131177.game;

/** Observer del GameLoop: riceve notifiche per aggiornare la UI di gioco. */
public interface GameLoopObserver {

    // Aggiorna i valori mostrati nell'HUD
    void updateHUD(int health, int shield, int wave, int score,
                   int arrows, int healthPotions, int shieldPotions);

    // Mostra la schermata di game over con le statistiche finali
    void showGameOver(int finalScore, int finalWave, int timePlayed, boolean isWin,
                      int goblins, int zombies, int skeletons);
}