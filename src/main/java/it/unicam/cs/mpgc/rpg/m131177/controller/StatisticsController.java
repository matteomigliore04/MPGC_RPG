package it.unicam.cs.mpgc.rpg.m131177.controller;

// Import di progetto
import it.unicam.cs.mpgc.rpg.m131177.audio.AudioManager;
import it.unicam.cs.mpgc.rpg.m131177.model.GameRecord;
import it.unicam.cs.mpgc.rpg.m131177.model.StatsManager;
import it.unicam.cs.mpgc.rpg.m131177.ui.SceneFactory;

// Import JavaFX
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

// Import Java standard
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller della schermata statistiche: carica i record, calcola i totali
 * e popola la classifica Top 5.
 */
public class StatisticsController {

    private static final Logger LOGGER = Logger.getLogger(StatisticsController.class.getName());

    // Componenti UI
    @FXML private TableView<GameRecord> statsTable;
    @FXML private TableColumn<GameRecord, String> colDate;
    @FXML private TableColumn<GameRecord, Integer> colScore;
    @FXML private TableColumn<GameRecord, Integer> colWave;
    @FXML private TableColumn<GameRecord, String> colDifficulty;

    @FXML private Label lblTotalTime;
    @FXML private Label lblGoblins;
    @FXML private Label lblZombies;
    @FXML private Label lblSkeletons;

    // Campo rimosso (mantenuto come commento)
    // @FXML private Label lblWinRate;

    /**
     * Inizializza la schermata: musica, colonne, statistiche globali e Top 5.
     */
    @FXML
    public void initialize() {
        AudioManager.getInstance().playMenuMusic();

        // Le colonne occupano tutta la larghezza
        statsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        List<GameRecord> allRecords = StatsManager.loadRecords();

        // 1. Statistiche generali
        calculateGlobalStats(allRecords);

        // 2. Top 5 per punteggio decrescente
        List<GameRecord> top5 = allRecords.stream()
                .sorted(Comparator.comparingInt(this::getScore).reversed())
                .limit(5)
                .collect(Collectors.toList());

        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        colScore.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());
        colWave.setCellValueFactory(cellData -> cellData.getValue().waveProperty().asObject());
        colDifficulty.setCellValueFactory(cellData -> cellData.getValue().difficultyProperty());

        statsTable.setItems(FXCollections.observableArrayList(top5));
    }

    // Helper per estrarre il punteggio da un GameRecord
    private int getScore(GameRecord r) {
        return r.scoreProperty().get();
    }

    /**
     * Calcola e mostra i totali: tempo giocato e uccisioni per tipo.
     */
    private void calculateGlobalStats(List<GameRecord> records) {
        if (records.isEmpty()) return;

        int totalTimeSeconds = 0;
        int totalGoblins = 0, totalZombies = 0, totalSkeletons = 0;

        for (GameRecord r : records) {
            totalTimeSeconds += r.getTimePlayed();
            // Conteggio vittorie rimosso
            totalGoblins += r.getGoblinsKilled();
            totalZombies += r.getZombiesKilled();
            totalSkeletons += r.getSkeletonsKilled();
        }

        // Formatta il tempo in ore e minuti
        int hours = totalTimeSeconds / 3600;
        int minutes = (totalTimeSeconds % 3600) / 60;
        lblTotalTime.setText(hours + "h " + minutes + "m");

        // Uccisioni
        lblGoblins.setText("Goblin: " + totalGoblins);
        lblZombies.setText("Zombie: " + totalZombies);
        lblSkeletons.setText("Scheletri: " + totalSkeletons);
    }

    /**
     * Torna al menu principale.
     */
    @FXML
    private void handleIndietro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) statsTable.getScene().getWindow();

            stage.setScene(SceneFactory.create(root));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel ritorno al menu principale", e);
        }
    }
}