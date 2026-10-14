package it.unicam.cs.mpgc.rpg.m131177;

// Import delle classi JavaFX necessarie per avviare l'applicazione,
// caricare l'FXML, costruire la scena, gestire il font e la finestra
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// Classe principale del gioco: estende Application di JavaFX
// e rappresenta il punto di ingresso dell'applicazione
public class Main extends Application {

    // Metodo start(): è il metodo di avvio chiamato automaticamente da JavaFX
    // quando l'applicazione viene lanciata. Riceve lo Stage principale (la finestra)
    @Override
    public void start(Stage primaryStage) throws Exception {

        // --- CARICAMENTO DELL'INTERFACCIA ---
        // Creiamo un FXMLLoader che si occupa di leggere il file menu.fxml,
        // il quale definisce la struttura grafica del menu principale del gioco
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));

        // --- CARICAMENTO DEL FONT PERSONALIZZATO ---
        // Carichiamo il font "Press Start 2P" (stile retrò/arcade)
        // dalla cartella resources/fonts, impostando una dimensione base di 10.
        // Nota: la dimensione indicata qui è solo un valore di registrazione,
        // le dimensioni effettive vengono poi ridefinite nel CSS
        Font.loadFont(Main.class.getResourceAsStream("/fonts/PressStart2P-Regular.ttf"), 10);

        // --- CREAZIONE DELLA SCENA ---
        // Costruiamo l'albero dei nodi grafici a partire dal file FXML
        Parent root = loader.load();

        // Creiamo la scena con dimensioni fisse di 900x700 pixel
        Scene scene = new Scene(root, 900, 700);

        // Applichiamo il foglio di stile CSS esterno per personalizzare
        // colori, font e aspetto generale dell'interfaccia
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // --- CONFIGURAZIONE DELLA FINESTRA ---
        // Impostiamo la scena sulla finestra principale,
        // disabilitiamo il ridimensionamento (finestra a dimensione fissa)
        // e infine mostriamo la finestra all'utente
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Metodo main(): punto di ingresso standard di Java.
    // La chiamata a launch(args) avvia il ciclo di vita di JavaFX,
    // che a sua volta chiama il metodo start() qui sopra
    public static void main(String[] args) {
        launch(args);
    }
}