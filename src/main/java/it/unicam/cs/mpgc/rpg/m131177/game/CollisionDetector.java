package it.unicam.cs.mpgc.rpg.m131177.game;

import it.unicam.cs.mpgc.rpg.m131177.game.enemies.Enemy;
import it.unicam.cs.mpgc.rpg.m131177.game.players.Projectile;
import it.unicam.cs.mpgc.rpg.m131177.game.players.Player;

public class CollisionDetector {

    /**
     * Controllo AABB classico: usato per le frecce (devono sovrapporsi fisicamente).
     */
    public static boolean isProjectileHitEnemy(Projectile projectile, Enemy enemy) {
        return checkCollision(
                projectile.getX(), projectile.getY(), projectile.getWidth(), projectile.getHeight(),
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
        );
    }

    /**
     * Controllo a distanza: usato per Nemico-Player.
     * Rileva l'attacco anche se c'è un piccolo spazio vuoto tra i due sprite.
     */
    public static boolean isEnemyAttackingPlayer(Enemy enemy, Player player) {
        // Calcola i centri dei due personaggi
        double centerX1 = enemy.getX() + enemy.getWidth() / 2;
        double centerY1 = enemy.getY() + enemy.getHeight() / 2;
        double centerX2 = player.getX() + player.getWidth() / 2;
        double centerY2 = player.getY() + player.getHeight() / 2;

        double dx = centerX1 - centerX2;
        double dy = centerY1 - centerY2;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Raggio d'azione: somma dei "raggi" degli sprite + un margine di 5px
        // per compensare lo spazio lasciato dal blocco del movimento.
        double reach = (enemy.getWidth() / 2) + (player.getWidth() / 2) + 5;

        return distance <= reach;
    }

    /**
     * Metodo helper per il controllo AABB.
     */
    private static boolean checkCollision(
            double x1, double y1, double w1, double h1,
            double x2, double y2, double w2, double h2) {

        return x1 < x2 + w2 &&
                x1 + w1 > x2 &&
                y1 < y2 + h2 &&
                y1 + h1 > y2;
    }
}