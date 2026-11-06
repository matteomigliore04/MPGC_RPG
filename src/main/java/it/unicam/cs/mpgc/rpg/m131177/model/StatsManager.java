package it.unicam.cs.mpgc.rpg.m131177.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatsManager {
    private static final Path FILE_PATH = Path.of("stats.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type RECORD_LIST_TYPE = new TypeToken<List<RecordData>>() {}.getType();

    public static void saveRecord(int score, int wave, String difficulty,
                                  int timePlayed, boolean isWin,
                                  int goblins, int zombies, int skeletons) {
        try {
            List<RecordData> records = readStoredRecords();
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            records.add(new RecordData(date, score, wave, difficulty, timePlayed,
                    isWin, goblins, zombies, skeletons));
            Files.writeString(FILE_PATH, GSON.toJson(records));
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        }
    }

    public static List<GameRecord> loadRecords() {
        List<GameRecord> records = new ArrayList<>();
        try {
            for (RecordData record : readStoredRecords()) {
                records.add(new GameRecord(
                        record.date,
                        record.score,
                        record.wave,
                        record.difficulty,
                        record.timePlayed,
                        record.isWin,
                        record.goblins,
                        record.zombies,
                        record.skeletons
                ));
            }
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        }
        records.sort(Comparator.comparingInt(GameRecord::getTimePlayed).reversed());
        return records;
    }

    private static List<RecordData> readStoredRecords() throws IOException {
        if (Files.notExists(FILE_PATH)) {
            return new ArrayList<>();
        }
        List<RecordData> records = GSON.fromJson(Files.readString(FILE_PATH), RECORD_LIST_TYPE);
        return records == null ? new ArrayList<>() : records;
    }

    private static class RecordData {
        private String date;
        private int score;
        private int wave;
        private String difficulty;
        private int timePlayed;
        private boolean isWin;
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
            this.isWin = isWin;
            this.goblins = goblins;
            this.zombies = zombies;
            this.skeletons = skeletons;
        }
    }
}