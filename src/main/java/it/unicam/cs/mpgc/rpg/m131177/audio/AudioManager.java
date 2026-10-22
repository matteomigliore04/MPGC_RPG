package it.unicam.cs.mpgc.rpg.m131177.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

// Gestore centralizzato per la musica di sottofondo.
// Usa il pattern Singleton: esiste una sola istanza in tutto il gioco.
public class AudioManager {

    private static AudioManager instance;
    private MediaPlayer mediaPlayer;

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // Avvia la musica dei menu (se non è già in riproduzione)
    public void playMenuMusic() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return; // Già in esecuzione, non fare nulla
        }
        try {
            // Assicurati di avere un file .mp3 o .wav nella cartella resources/audio/
            String path = Objects.requireNonNull(getClass().getResource("/audio/Menu_Music.mp3")).toExternalForm();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop infinito
            mediaPlayer.setVolume(0.3); // Volume al 30% (per non coprire i suoni di gioco)
            mediaPlayer.play();

        } catch (Exception e) {
            System.err.println("Impossibile caricare la musica di sottofondo: " + e.getMessage());
        }
    }

    // Ferma la musica (usato quando si avvia la partita)
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}