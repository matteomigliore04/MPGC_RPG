package it.unicam.cs.mpgc.rpg.m131177.ui;

import it.unicam.cs.mpgc.rpg.m131177.game.GameConstants;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.Objects;

public final class SceneFactory {

    private static final String STYLESHEET_PATH = "/css/style.css";

    private SceneFactory() {
    }

    public static Scene create(Parent root) {
        Scene scene = new Scene(root, GameConstants.CANVAS_WIDTH, GameConstants.CANVAS_HEIGHT);
        String stylesheet = Objects.requireNonNull(
                SceneFactory.class.getResource(STYLESHEET_PATH),
                "Stylesheet not found: " + STYLESHEET_PATH
        ).toExternalForm();
        scene.getStylesheets().add(stylesheet);
        return scene;
    }
}
