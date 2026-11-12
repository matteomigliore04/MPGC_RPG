package it.unicam.cs.mpgc.rpg.m131177.model;

// Import Gson
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

// Import Java standard
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestisce salvataggio e caricamento delle statistiche di gioco su file JSON.
 */
public class StatsManager {

    // Logger, percorso file, Gson e tipo della lista di record
    private static final Logger LOGGER = Logger.getLogger(StatsManager.class.getName());
    private static final Path FILE_PATH = Path.of("stats.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type RECORD_LIST_TYPE = new TypeToken<List<RecordData>>() {}.getType();

    // Costruttore privato: classe di utilità, non istanziabile
    private StatsManager() {
    }

    /**
     * Salva un nuovo record di partita nel file JSON.
     */
    public static void saveRecord(int score, int wave, String difficulty,
                                  int timePlayed, boolean isWin,
                                  int goblins, int zombies, int skeletons) {
        try {
            // Legge i record esistenti
            List<RecordData> records = readStoredRecords();

            // Crea il nuovo record con data corrente
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            records.add(new RecordData(date, score, wave, difficulty, timePlayed,
                    isWin, goblins, zombies, skeletons));

            // Scrive la lista aggiornata su file
            Files.writeString(FILE_PATH, GSON.toJson(records));
        } catch (IOException | JsonParseException e) {
            LOGGER.log(Level.SEVERE, "Impossibile salvare le statistiche della partita", e);
        }
    }

    /**
     * Carica tutti i record salvati e li restituisce ordinati per tempo giocato.
     */
    public static List<GameRecord> loadRecords() {
        List<GameRecord> records = new ArrayList<>();
        try {
            // Converte i record salvati in GameRecord
            for (RecordData record : readStoredRecords()) {
                records.add(new GameRecord(
                        record.date,
                        record.score,
                        record.wave,
                        record.difficulty,
                        record.timePlayed,
                        record.goblins,
                        record.zombies,
                        record.skeletons
                ));
            }
        } catch (IOException | JsonParseException e) {
            LOGGER.log(Level.SEVERE, "Impossibile caricare le statistiche delle partite", e);
        }

        // Ordina per tempo giocato decrescente
        records.sort(Comparator.comparingInt(GameRecord::getTimePlayed).reversed());
        return records;
    }

    /**
     * Legge i record dal file JSON; se il file non esiste restituisce una lista vuota.
     */
    private static List<RecordData> readStoredRecords() throws IOException {
        if (Files.notExists(FILE_PATH)) {
            return new ArrayList<>();
        }

        List<RecordData> records = GSON.fromJson(Files.readString(FILE_PATH), RECORD_LIST_TYPE);
        return records == null ? new ArrayList<>() : records;
    }

    /**
     * Struttura dati usata per la serializzazione JSON.
     */
    private static class RecordData {
        private String date;
        private int score;
        private int wave;
        private String difficulty;
        private int timePlayed;
        private int goblins;
        private int zombies;
        private int skeletons;

        private RecordData(String date, int score, int wave, String difficulty,
                           int timePlayed, boolean isWin,
                           int goblins, int zombies, int skeletons) {
            this.date = date;
            this.score = score;
            this.wave = wave;
            this.difficulty = difficulty;
            this.timePlayed = timePlayed;
            this.goblins = goblins;
            this.zombies = zombies;
            this.skeletons = skeletons;
        }
    }
}