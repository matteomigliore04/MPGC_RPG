package it.unicam.cs.mpgc.rpg.m131177.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class AudioManager {

    private static AudioManager instance;
    private MediaPlayer mediaPlayer;
    private MediaPlayer effectPlayer;

    // Tiene traccia di cosa sta suonando in questo momento
    private String currentTrack = "";

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // Avvia la musica del menu
    public void playMenuMusic() {
        // Se sta già suonando la musica del menu, non fare nulla
        if ("menu".equals(currentTrack) && mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }
        playTrack("/audio/Menu_Music.mp3", "menu", 0.3);
    }

    // Avvia la musica del gioco
    public void playGameMusic() {
        // Se sta già suonando la musica del gioco, non fare nulla
        if ("game".equals(currentTrack) && mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }
        playTrack("/audio/Game_Music.mp3", "game", 0.3); // Volume leggermente più basso per il gioco
    }

    // Metodo interno per gestire il cambio traccia
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

    // Ferma la musica e libera le risorse
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Libera la memoria
            mediaPlayer = null;
            currentTrack = "";
        }
    }

    // NUOVO: Riproduce il suono di Game Over una volta, poi avvia il loop
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
}