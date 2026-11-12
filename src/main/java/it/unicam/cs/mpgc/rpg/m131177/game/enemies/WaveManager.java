package it.unicam.cs.mpgc.rpg.m131177.game.enemies;

// Import per il logging standard di Java
import java.util.logging.Logger;

/**
 * Gestisce la logica delle ondate: numero di nemici, tempi di spawn e progressione.
 */
public class WaveManager {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(WaveManager.class.getName());

    // Costanti
    private static final double TRANSITION_DURATION = 3.0; // Pausa tra ondate (secondi)
    private static final int ARROW_BONUS_PER_WAVE = 15;    // Frecce bonus per ondata completata

    // Stato dell'ondata
    private int currentWave = 1;
    private int enemiesToSpawn; // Nemici totali da spawnare nell'ondata
    private int enemiesSpawned; // Nemici già spawnati
    private int enemiesAlive;   // Nemici ancora vivi in gioco

    // Timing e stato di spawn/transizione
    private double spawnTimer = 0;
    private double spawnInterval; // Secondi tra uno spawn e l'altro
    private double spawnRateMultiplier;
    private boolean waveInProgress = false;
    private boolean waveTransition = false; // True tra un'ondata e l'altra
    private boolean bonusPending = false;
    private double transitionTimer = 0;

    public WaveManager(double spawnRateMultiplier) {
        this.spawnRateMultiplier = spawnRateMultiplier;
        configureWave(currentWave);
    }

    /**
     * Aggiorna la logica delle ondate.
     * Restituisce true se è il momento di spawnare un nemico.
     */
    public boolean update(double dt) {
        // Pausa tra un'ondata e l'altra
        if (waveTransition) {
            transitionTimer -= dt;
            if (transitionTimer <= 0) {
                currentWave++;
                bonusPending = true; // Segnala che c'è un bonus da dare
                configureWave(currentWave);
            }
            return false;
        }

        if (!waveInProgress) return false;

        // Tutti i nemici spawnati e morti → ondata completata
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

    /** Chiamato quando un nemico viene rimosso (morto). */
    public void onEnemyKilled() {
        if (enemiesAlive > 0) {
            enemiesAlive--;
        }
    }

    /** Consuma il bonus pendente, se presente. */
    public boolean consumeBonusPending() {
        if (bonusPending) {
            bonusPending = false;
            return true;
        }
        return false;
    }

    // Getter
    public int getCurrentWave() { return currentWave; }

    public int getArrowWaveBonus() { return ARROW_BONUS_PER_WAVE; }

    /**
     * Configura i parametri dell'ondata corrente.
     * La difficoltà scala progressivamente.
     */
    private void configureWave(int wave) {
        this.enemiesToSpawn = 3 + (wave * 2);
        this.enemiesSpawned = 0;
        this.enemiesAlive = 0;
        this.waveInProgress = true;
        this.waveTransition = false;

        // Intervallo base in base all'ondata
        double baseInterval = Math.max(0.8, 2.5 - (wave * 0.1));

        // Più alto è il moltiplicatore, più l'intervallo diminuisce (spawn più veloci)
        this.spawnInterval = baseInterval / spawnRateMultiplier;

        this.spawnTimer = 0;
        LOGGER.info("🌊 ONDATA " + wave + " INIZIATA - Nemici: " + enemiesToSpawn);
    }

    /** Avvia la pausa tra un'ondata e l'altra. */
    private void startTransition() {
        waveInProgress = false;
        waveTransition = true;
        transitionTimer = TRANSITION_DURATION;
        LOGGER.info("✅ ONDATA " + currentWave + " COMPLETATA! Bonus frecce: +" + ARROW_BONUS_PER_WAVE);
    }
}