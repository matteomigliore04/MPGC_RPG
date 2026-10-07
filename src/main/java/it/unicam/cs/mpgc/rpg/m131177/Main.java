package it.unicam.cs.mpgc.rpg.m131177;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // --- CARICAMENTO DELL'INTERFACCIA ---
        // Creiamo un FXMLLoader che si occupa di leggere il file menu.fxml,
        // il quale definisce la struttura grafica del menu principale del gioco
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));

        // --- CARICAMENTO DEL FONT PERSONALIZZATO ---
        // Carichiamo il font "Press Start 2P" (stile retrò/arcade)
        // dalla cartella resources/fonts, impostando una dimensione base di 10
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
}