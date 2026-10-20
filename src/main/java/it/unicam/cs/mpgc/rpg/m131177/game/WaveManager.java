package it.unicam.cs.mpgc.rpg.m131177.game;

/**
 * Gestisce la logica delle ondate: numero di nemici, tempi di spawn e progressione.
 */
public class WaveManager {

    private int currentWave = 1;
    private int enemiesToSpawn;        // Nemici totali da spawnare in questa ondata
    private int enemiesSpawned;        // Nemici già spawnati
    private int enemiesAlive;          // Nemici ancora vivi in gioco

    private double spawnTimer = 0;
    private double spawnInterval;      // Secondi tra uno spawn e l'altro
    private boolean waveInProgress = false;
    private boolean waveTransition = false; // True tra un'ondata e l'altra
    private boolean bonusPending = false;
    private double transitionTimer = 0;
    private static final double TRANSITION_DURATION = 3.0; // 3 secondi di pausa tra ondate
    private static final int ARROW_BONUS_PER_WAVE = 15; // Frecce bonus per ondata completata

    public WaveManager() {
        configureWave(currentWave);
    }

    /**
     * Configura i parametri dell'ondata corrente.
     * La difficoltà scala progressivamente.
     */
    private void configureWave(int wave) {
        // Formula: 3 nemici base + 2 per ogni ondata
        this.enemiesToSpawn = 3 + (wave * 2);
        this.enemiesSpawned = 0;
        this.enemiesAlive = 0;
        this.waveInProgress = true;
        this.waveTransition = false;

        // Lo spawn accelera leggermente ogni ondata (minimo 0.8 secondi)
        this.spawnInterval = Math.max(0.8, 2.5 - (wave * 0.1));
        this.spawnTimer = 0;

        System.out.println("🌊 ONDATA " + wave + " INIZIATA - Nemici: " + enemiesToSpawn);
    }

    /**
     * Aggiorna il manager. Restituisce true se è il momento di spawnare un nemico.
     */
    public boolean update(double dt) {
        // Pausa tra un'ondata e l'altra
        if (waveTransition) {
            transitionTimer -= dt;
            if (transitionTimer <= 0) {
                currentWave++;
                bonusPending = true; // ✅ Segnala che c'è un bonus da dare
                configureWave(currentWave);
            }
            return false;
        }

        if (!waveInProgress) return false;

        // Tutti i nemici dell'ondata sono stati spawnati E sono morti → ondata completata
        if (enemiesSpawned >= enemiesToSpawn && enemiesAlive == 0) {
            startTransition();
            return false;
        }

        // Logica di spawn
        if (enemiesSpawned < enemiesToSpawn) {
            spawnTimer += dt;
            if (spawnTimer >= spawnInterval) {
                spawnTimer = 0;
                enemiesSpawned++;
                enemiesAlive++;
                return true; // Segnala al GameLoop di creare un nemico
            }
        }
        return false;
    }

    /**
     * Chiamato quando un nemico viene rimosso (morto).
     */
    public void onEnemyKilled() {
        if (enemiesAlive > 0) {
            enemiesAlive--;
        }
    }

    /**
     * Avvia la pausa tra un'ondata e l'altra.
     */
    private void startTransition() {
        waveInProgress = false;
        waveTransition = true;
        transitionTimer = TRANSITION_DURATION;
        System.out.println("✅ ONDATA " + currentWave + " COMPLETATA! Bonus frecce: +" + ARROW_BONUS_PER_WAVE);
    }

    public boolean consumeBonusPending() {
        if (bonusPending) {
            bonusPending = false;
            return true;
        }
        return false;
    }

    // Getters
    public int getCurrentWave() { return currentWave; }
    public int getEnemiesAlive() { return enemiesAlive; }
    public int getEnemiesRemaining() { return enemiesToSpawn - enemiesSpawned; }
    public boolean isWaveInProgress() { return waveInProgress; }
    public boolean isTransition() { return waveTransition; }
    public double getTransitionTimer() { return transitionTimer; }
    // Aggiungi questo getter (serve al GameLoop per dare il bonus)
    public int getArrowWaveBonus() {return ARROW_BONUS_PER_WAVE;}
}