package it.unicam.cs.mpgc.rpg.m131177.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

/**
 * Gestore audio singleton per musica di sottofondo ed effetti.
 */
public class AudioManager {

    // Istanza singleton
    private static AudioManager instance;

    // Player per musica ed effetti
    private MediaPlayer mediaPlayer;
    private MediaPlayer effectPlayer;

    // Traccia attualmente in riproduzione ("menu", "game", ecc.)
    private String currentTrack = "";

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // --- Metodi pubblici ---

    /** Avvia la musica del menu (se non è già in riproduzione). */
    public void playMenuMusic() {
        if ("menu".equals(currentTrack) && mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }
        playTrack("/audio/Menu_Music.mp3", "menu", 0.3);
    }

    /** Avvia la musica del gioco (se non è già in riproduzione). */
    public void playGameMusic() {
        if ("game".equals(currentTrack) && mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }
        playTrack("/audio/Game_Music.mp3", "game", 0.3); // Volume leggermente più basso per il gioco
    }

    /** Ferma la musica e libera le risorse. */
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Libera la memoria
            mediaPlayer = null;
            currentTrack = "";
        }
    }

    /** Riproduce il suono di Game Over una volta. */
    public void playGameOver() {
        stopMusic();
        try {
            String effectUrl = Objects.requireNonNull(getClass().getResource("/audio/Game_Over.mp3")).toExternalForm();
            effectPlayer = new MediaPlayer(new Media(effectUrl));

            // Rallenta il suono singolo (es. a 0.8x)
            effectPlayer.setRate(0.8);

            effectPlayer.play();
        } catch (Exception e) {
            System.err.println("Errore nel caricamento del suono Game Over: " + e.getMessage());
        }
    }

    // --- Helper privato ---

    /** Ferma la traccia corrente e avvia quella indicata in loop. */
    private void playTrack(String path, String trackName, double volume) {
        stopMusic(); // Ferma e pulisce la traccia precedente
        try {
            String url = Objects.requireNonNull(getClass().getResource(path)).toExternalForm();
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop infinito
            mediaPlayer.setVolume(volume);
            mediaPlayer.play();

            currentTrack = trackName; // Segna quale traccia è attiva

        } catch (Exception e) {
            System.err.println("Impossibile caricare la traccia audio: " + path + " - " + e.getMessage());
        }
    }
}