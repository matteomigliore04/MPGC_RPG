package it.unicam.cs.mpgc.rpg.m131177.controller;

import it.unicam.cs.mpgc.rpg.m131177.Main;
import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GameOverController {

    @FXML private Label lblFinalScore;
    @FXML private Label lblFinalWave;
    @FXML private Label lblFinalEnemies;
    @FXML private Button btnRetry;
    @FXML private Button btnMenu;

    private GameContext context;
    private Stage stage;

    /**
     * Imposta i dati della partita terminata.
     */
    public void setGameData(int score, int wave, int enemiesKilled, GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;

        lblFinalScore.setText("⭐ Punti: " + score);
        lblFinalWave.setText("🌊 Ondata raggiunta: " + wave);
        lblFinalEnemies.setText("👹 Nemici uccisi: " + enemiesKilled);
    }

    /**
     * Riavvia la partita con la stessa difficoltà.
     */
    @FXML
    private void handleRetry() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.setContext(context, stage);

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("🌿 Weed Adventure - In Gioco");

            controller.setupKeyboardInput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Torna al menu principale.
     */
    @FXML
    private void handleMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnMenu.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("🌿 Weed Adventure");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}