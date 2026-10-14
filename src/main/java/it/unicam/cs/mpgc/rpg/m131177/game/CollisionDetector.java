package it.unicam.cs.mpgc.rpg.m131177.game;

import it.unicam.cs.mpgc.rpg.m131177.game.enemies.Enemy;

/**
 * Classe dedicata al rilevamento delle collisioni tra entità di gioco.
 * Usa il metodo AABB (Axis-Aligned Bounding Box).
 */
public class CollisionDetector {

    /**
     * Verifica se due entità rettangolari si sovrappongono.
     */
    public static boolean checkCollision(
            double x1, double y1, double w1, double h1,
            double x2, double y2, double w2, double h2) {

        return x1 < x2 + w2 &&
                x1 + w1 > x2 &&
                y1 < y2 + h2 &&
                y1 + h1 > y2;
    }

    /**
     * Verifica collisione tra un proiettile e un nemico.
     */
    public static boolean isProjectileHitEnemy(Projectile projectile, Enemy enemy) {
        return checkCollision(
                projectile.getX(), projectile.getY(), projectile.getWidth(), projectile.getHeight(),
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight()
        );
    }

    /**
     * Verifica collisione tra un nemico e il player.
     */
    public static boolean isEnemyHitPlayer(Enemy enemy, Player player) {
        return checkCollision(
                enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight(),
                player.getX(), player.getY(), player.getWidth(), player.getHeight()
        );
    }
}