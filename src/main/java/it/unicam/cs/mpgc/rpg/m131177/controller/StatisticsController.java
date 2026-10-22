package it.unicam.cs.mpgc.rpg.m131177.controller;

import it.unicam.cs.mpgc.rpg.m131177.model.GameRecord;
import it.unicam.cs.mpgc.rpg.m131177.model.StatsManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsController {

    @FXML private TableView<GameRecord> statsTable;
    @FXML private TableColumn<GameRecord, String> colDate;
    @FXML private TableColumn<GameRecord, Integer> colScore;
    @FXML private TableColumn<GameRecord, Integer> colWave;
    @FXML private TableColumn<GameRecord, String> colDifficulty;

    @FXML private Label lblTotalTime;
    @FXML private Label lblWinRate;
    @FXML private Label lblGoblins;
    @FXML private Label lblZombies;
    @FXML private Label lblSkeletons;

    @FXML
    public void initialize() {
        // Imposta le colonne in modo che occupino tutta la larghezza
        statsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        List<GameRecord> allRecords = StatsManager.loadRecords();

        // 1. Calcola Statistiche Generali
        calculateGlobalStats(allRecords);

        // 2. Popola la Top 5 (ordinata per punteggio decrescente)
        List<GameRecord> top5 = allRecords.stream()
                .sorted(Comparator.comparingInt(r -> -getScore(r))) // Ordine decrescente
                .limit(5)
                .collect(Collectors.toList());

        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        colScore.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());
        colWave.setCellValueFactory(cellData -> cellData.getValue().waveProperty().asObject());
        colDifficulty.setCellValueFactory(cellData -> cellData.getValue().difficultyProperty());

        statsTable.setItems(FXCollections.observableArrayList(top5));
    }

    private int getScore(GameRecord r) {
        // Helper per estrarre il punteggio (dato che il getter potrebbe non esserci)
        return r.scoreProperty().get();
    }

    // Rimuovi questa riga:
    // @FXML private Label lblWinRate;

    private void calculateGlobalStats(List<GameRecord> records) {
        if (records.isEmpty()) return;

        int totalTimeSeconds = 0;
        int totalGoblins = 0, totalZombies = 0, totalSkeletons = 0;

        for (GameRecord r : records) {
            totalTimeSeconds += r.getTimePlayed();
            // Rimuovi il conteggio delle vittorie
            totalGoblins += r.getGoblinsKilled();
            totalZombies += r.getZombiesKilled();
            totalSkeletons += r.getSkeletonsKilled();
        }

        // Formatta il tempo
        int hours = totalTimeSeconds / 3600;
        int minutes = (totalTimeSeconds % 3600) / 60;
        lblTotalTime.setText(hours + "h " + minutes + "m");

        // Rimuovi il calcolo di winRate e lblWinRate.setText(...)

        // Uccisioni
        lblGoblins.setText("Goblin: " + totalGoblins);
        lblZombies.setText("Zombie: " + totalZombies);
        lblSkeletons.setText("Scheletri: " + totalSkeletons);
    }

    @FXML
    private void handleIndietro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) statsTable.getScene().getWindow();

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}