package com.example;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ChallengeLevel {
    private static Button continueButton;

    public void buildChallengeLevel(GridPane gamePane) {
        VBox challengeBox = new VBox(20);
        challengeBox.setAlignment(Pos.CENTER);
        Label challengeLabel = new Label("Challenge Label");
        continueButton = new Button("Continue");
        continueButton.setPrefWidth(200);
        continueButton.setPrefHeight(60);
        continueButton.setOnAction(e -> {
            AnimationUtils.pulse(continueButton, 200);
            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(event -> {
                LevelFile.AdvanceLocation();
                GameFile.buildGameInterface(MenuFile.menuPane);
            });
            pause.play();
        });
        challengeBox.getChildren().addAll(challengeLabel, continueButton);
        gamePane.add(challengeBox, 0, 1, 3, 6);
        AnimationUtils.rotateIn(challengeBox, 600);
    }
}
