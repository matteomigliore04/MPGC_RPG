package it.unicam.cs.mpgc.rpg.m131177.controller;

import it.unicam.cs.mpgc.rpg.m131177.model.GameContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller della schermata di scelta della difficoltà.
 * Gestisce la selezione tra 4 livelli di difficoltà e
 * passa alla schermata successiva (scelta del mostro).
 */
public class DifficultyController {

    @FXML
    private Button btnFacile;
    @FXML
    private Button btnMedia;
    @FXML
    private Button btnAlta;
    @FXML
    private Button btnSuperiore;
    @FXML
    private Button btnIndietro;

    private GameContext context;
    private Stage stage;

    /**
     * Inietta il contesto condiviso e lo stage per la navigazione.
     */
    public void setContext(GameContext context, Stage stage) {
        this.context = context;
        this.stage = stage;
    }

    @FXML
    private void handleFacile() {
        selectDifficulty("Facile");
    }

    @FXML
    private void handleMedia() {
        selectDifficulty("Media");
    }

    @FXML
    private void handleAlta() {
        selectDifficulty("Alta");
    }

    @FXML
    private void handleSuperiore() {
        selectDifficulty("Superiore");
    }

    @FXML
    private void handleIndietro() {
        try {
            navigateTo("/fxml/menu.fxml", "Menu Principale");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Salva la difficoltà scelta nel contesto e naviga alla schermata del mostro.
     */
    private void selectDifficulty(String difficulty) {
        context.setDifficulty(difficulty);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/monster.fxml"));
            Parent root = loader.load();

            // Passa il contesto al prossimo controller
            MonsterController controller = loader.getController();
            controller.setContext(context, stage);

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo di utilità per navigare a un'altra schermata FXML.
     */
    private void navigateTo(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Se torno al menu, reinizializzo il MenuController senza context
        if (fxmlPath.contains("menu")) {
            context.reset();
        }

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
    }
}