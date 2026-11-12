package it.unicam.cs.mpgc.rpg.m131177.game.players;

// Import di progetto (ordinati alfabeticamente)
import it.unicam.cs.mpgc.rpg.m131177.game.enemies.Enemy;

/**
 * Classe di utilità per il rilevamento delle collisioni tra entità di gioco.
 * Tutti i metodi sono statici: non è necessario istanziare la classe.
 */
public class CollisionDetector {

    // --- Collisioni specifiche ---

    /**
     * Verifica se un proiettile colpisce un nemico (AABB).
     */
    public static boolean isProjectileHitEnemy(Projectile projectile, Enemy enemy) {
        return checkCollision(
                projectile.getX(), projectile.getY(), projectile.getWidth(), projectile.getHeight(),
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
        );
    }

    /**
     * Verifica se un nemico attacca il player (collisione a distanza).
     * Usa un margine di 5 px per compensare lo spazio tra gli sprite.
     */
    public static boolean isEnemyAttackingPlayer(Enemy enemy, Player player) {
        // Calcola i centri dei due personaggi
        double centerX1 = enemy.getX() + enemy.getWidth() / 2;
        double centerY1 = enemy.getY() + enemy.getHeight() / 2;
        double centerX2 = player.getX() + player.getWidth() / 2;
        double centerY2 = player.getY() + player.getHeight() / 2;

        // Distanza euclidea tra i due centri
        double dx = centerX1 - centerX2;
        double dy = centerY1 - centerY2;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Raggio d'azione: somma dei raggi degli sprite + margine di 5 px
        double reach = (enemy.getWidth() / 2) + (player.getWidth() / 2) + 5;

        return distance <= reach;
    }

    // --- Helper generico ---

    /**
     * Controllo AABB (Axis-Aligned Bounding Box) per due rettangoli.
     */
    public static boolean checkCollision(
            double x1, double y1, double w1, double h1,
            double x2, double y2, double w2, double h2) {

        return x1 < x2 + w2 &&
                x1 + w1 > x2 &&
                y1 < y2 + h2 &&
                y1 + h1 > y2;
    }
}