package it.unicam.cs.mpgc.rpg.m131177.game;

// Import delle classi del progetto e di JavaFX necessarie
// per gestire il game loop (AnimationTimer), il disegno (GraphicsContext)
// e le collezioni di proiettili (List, ArrayList)
import it.unicam.cs.mpgc.rpg.m131177.controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Game Loop principale.
 * Gestisce aggiornamento logica e rendering a 60 FPS.
 * Estende AnimationTimer di JavaFX: il metodo handle(long now) viene chiamato
 * automaticamente ad ogni frame (in genere 60 volte al secondo).
 */
public class GameLoop extends AnimationTimer {

    // Riferimenti condivisi:
    // - gc: contesto grafico del Canvas su cui disegnare
    // - controller: per aggiornare l'HUD (vita, scudo, ondata, punti, frecce)
    private final GraphicsContext gc;
    private final GameController controller;

    // --- GESTIONE DEL TEMPO ---
    // lastTime: timestamp dell'ultimo frame (in nanosecondi)
    // NANOS_PER_SECOND: costante per convertire nanosecondi → secondi
    // (deltaTime è diventato una variabile locale dentro handle(),
    //  perché non serve conservarlo tra un frame e l'altro)
    private long lastTime = 0;
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    // --- ENTITÀ DI GIOCO ---
    // player: il giocatore
    // projectiles: lista delle frecce attive
    final Player player;
    final List<Projectile> projectiles;

    // --- STATO DEI TASTI ---
    // Aggiornati dal GameController tramite i setter in fondo alla classe
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Altezza dell'HUD (zona proibita in alto):
    // il player non può salire sotto questa soglia
    private static final double HUD_HEIGHT = 70;

    /**
     * Crea il game loop, inizializza la lista dei proiettili
     * e posiziona il player al centro del canvas.
     */
    public GameLoop(GraphicsContext gc, GameController controller) {
        this.gc = gc;
        this.controller = controller;
        this.projectiles = new ArrayList<>();

        // Inizializza il player al centro, con limite HUD
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        this.player = new Player(canvasWidth, canvasHeight, HUD_HEIGHT);
    }

    /**
     * Metodo chiamato automaticamente da JavaFX ad ogni frame.
     * Calcola il deltaTime e chiama update() + render().
     *
     * @param now timestamp corrente in nanosecondi
     */
    @Override
    public void handle(long now) {
        // Al primo frame, salviamo solo il timestamp e usciamo:
        // così evitiamo un deltaTime enorme al primo aggiornamento
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        // Calcola il tempo trascorso dall'ultimo frame (in secondi).
        // deltaTime è una variabile locale: viene usata solo qui dentro
        // e passata subito a update(), quindi non serve come campo
        double deltaTime = (now - lastTime) / NANOS_PER_SECOND;
        lastTime = now;

        // Aggiorna la logica e ridisegna la scena
        update(deltaTime);
        render();
    }

    /**
     * Aggiorna la logica di gioco: movimento del player,
     * movimento dei proiettili e aggiornamento dell'HUD.
     */
    private void update(double dt) {
        // Aggiorna il player in base ai tasti premuti
        player.update(dt, upPressed, downPressed, leftPressed, rightPressed);

        // Aggiorna i proiettili e rimuove quelli inattivi.
        // Usiamo removeIf() invece di un Iterator esplicito:
        // più conciso e leggibile, fa la stessa cosa
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        projectiles.removeIf(p -> !p.update(dt, canvasWidth, canvasHeight));

        // AGGIORNAMENTO HUD CON LE FRECCE
        // I valori 100, 0, 1, 0 sono placeholder per vita, scudo, ondata e punti:
        // in futuro andranno presi dal GameContext o da un model dedicato
        controller.updateHUD(100, 0, 1, 0, player.getCurrentArrows());
    }

    /**
     * Ridisegna la scena: pulisce il canvas, disegna i proiettili
     * (sotto) e poi il player (sopra).
     */
    private void render() {
        // Pulisce il canvas prima di ridisegnare
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Disegna i proiettili (sotto il player)
        for (Projectile p : projectiles) {
            p.render(gc);
        }

        // Disegna il player (sopra i proiettili)
        player.render(gc);
    }

    /**
     * Metodo chiamato dal controller quando il giocatore preme SPAZIO.
     * Crea una nuova freccia e la aggiunge alla lista,
     * solo se il player aveva abbastanza munizioni.
     */
    public void shoot() {
        Projectile arrow = player.shoot();
        if (arrow != null) { // Aggiunge la freccia solo se il player ne aveva una
            projectiles.add(arrow);
        }
    }

    // --- METODI PER GESTIRE GLI INPUT ---
    // Chiamati dal GameController quando l'utente preme/rilascia i tasti.
    // Aggiornano lo stato dei booleani usati in update().
    public void setUpPressed(boolean pressed)    { this.upPressed = pressed; }
    public void setDownPressed(boolean pressed)  { this.downPressed = pressed; }
    public void setLeftPressed(boolean pressed)  { this.leftPressed = pressed; }
    public void setRightPressed(boolean pressed) { this.rightPressed = pressed; }
}