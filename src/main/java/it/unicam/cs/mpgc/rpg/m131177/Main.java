package it.unicam.cs.mpgc.rpg.m131177;

// ============================================================
// IMPORT
// ============================================================
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// ============================================================
// CLASSE PRINCIPALE
// ============================================================
// Punto di ingresso dell'applicazione JavaFX.
public class Main extends Application {

    // ============================================================
    // START (avvio applicazione)
    // ============================================================
    // Chiamato automaticamente da JavaFX per inizializzare la UI.
    @Override
    public void start(Stage primaryStage) throws Exception {

        // 1. CARICAMENTO FXML
        // Legge il file menu.fxml per costruire la schermata del menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));

        // 2. CARICAMENTO FONT
        // Registra il font "Press Start 2P" (stile retrò).
        // La dimensione (10) è solo un default: il CSS la sovrascriverà.
        Font.loadFont(Main.class.getResourceAsStream("/fonts/PressStart2P-Regular.ttf"), 10);

        // 3. CREAZIONE SCENA
        // Costruisce l'albero dei nodi grafici a partire dall'FXML
        Parent root = loader.load();

        // Crea la scena con dimensioni fisse (900x700)
        Scene scene = new Scene(root, 900, 700);

        // Applica il foglio di stile CSS per colori, font e layout
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // 4. CONFIGURAZIONE FINESTRA
        // Imposta la scena, blocca il ridimensionamento e mostra la finestra
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // ============================================================
    // MAIN (punto di ingresso Java)
    // ============================================================
    // Avvia il ciclo di vita di JavaFX (che a sua volta chiama start)
    public static void main(String[] args) {
        launch(args);
    }
}