package it.unicam.cs.mpgc.rpg.m131177.model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatsManager {
    private static final String FILE_PATH = "stats.csv";

    public static void saveRecord(int score, int wave, String difficulty,
                                  int timePlayed, boolean isWin,
                                  int goblins, int zombies, int skeletons) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            // Formato: data,punteggio,ondata,difficoltà,tempo,vittoria,goblin,zombie,scheletri
            writer.write(String.format("%s,%d,%d,%s,%d,%b,%d,%d,%d",
                    date, score, wave, difficulty, timePlayed, isWin, goblins, zombies, skeletons));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<GameRecord> loadRecords() {
        List<GameRecord> records = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return records;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9) { // Aggiornato a 9 campi
                    records.add(new GameRecord(
                            parts[0],
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            parts[3],
                            Integer.parseInt(parts[4]),
                            Boolean.parseBoolean(parts[5]),
                            Integer.parseInt(parts[6]),
                            Integer.parseInt(parts[7]),
                            Integer.parseInt(parts[8])
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Ordina per punteggio decrescente (per la Top 5)
        records.sort(Comparator.comparingInt(GameRecord::getTimePlayed).reversed()); // Fallback
        return records;
    }
}