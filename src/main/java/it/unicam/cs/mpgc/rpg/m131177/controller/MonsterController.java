package it.unicam.cs.mpgc.rpg.m131177.controller;

import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller della schermata di scelta del mostro da affrontare.
 * Permette di selezionare tra 4 tipi di mostri e, in futuro,
 * avvierà la partita vera e propria.
 */
public class MonsterController {

    @FXML
    private Button btnTroll;
    @FXML
    private Button btnRagno;
    @FXML
    private Button btnCavaliere;
    @FXML
    private Button btnDragone;
    @FXML
    private Button btnIndietro;

    private GameContext context;
    private Stage stage;

    public void setContext(GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    @FXML
    private void handleTroll() {
        selectMonster("Troll");
    }

    @FXML
    private void handleRagno() {
        selectMonster("Ragno Gigante");
    }

    @FXML
    private void handleCavaliere() {
        selectMonster("Cavaliere Nero");
    }

    @FXML
    private void handleDragone() {
        selectMonster("Dragone");
    }

    @FXML
    private void handleIndietro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/difficulty.fxml"));
            Parent root = loader.load();

            DifficultyController controller = loader.getController();
            controller.setContext(context, stage);

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Salva il mostro scelto e avvia la partita (placeholder).
     */
    private void selectMonster(String monster) {
        context.setMonster(monster);
        showAlert(
                "Partita Pronta!",
                "Stai per affrontare un " + monster + " in difficoltà " + context.getDifficulty() + "!\n\n" +
                        "La schermata di gioco verrà implementata nella prossima release."
        );
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}