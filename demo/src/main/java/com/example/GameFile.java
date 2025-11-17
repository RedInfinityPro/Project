package com.example;

import java.beans.EventHandler;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GameFile {
    // levels and challenge levels
    private static Map<String, ArrayList<String>> levelDict = new HashMap<>();
    private static ArrayList<String> subLevelList = new ArrayList<>();
    public static boolean isChallenge = false;
    // UI
    public static Label displayLevel;
    public static Integer wrongPoints = 0;

    GameFile(Integer playerLevel, Map<String, ArrayList<String>> levelDict, ArrayList<String> subLevelList) {
        LevelFile.setPlayerLevel(playerLevel);
        this.levelDict = levelDict;
        this.subLevelList = subLevelList;
    }

    public static void LevelDataInitialized() {
        if (levelDict == null) {
            levelDict = new HashMap<>();
        }
        if (subLevelList == null) {
            subLevelList = new ArrayList<>();
        }
        if (displayLevel == null) {
            displayLevel = new Label("Level: " + LevelFile.getPlayerLevel());
        }
    }

    public static void buildGameInterface(GridPane menuPane) {
        menuPane.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex > 0;
        });

        FindType();
        if (GameFile.isChallenge) {
            ChallengeLevel challengeLevel = new ChallengeLevel();
            challengeLevel.buildChallengeLevel(menuPane);
        } else {
            SubLevel subLevel = new SubLevel();
            subLevel.buildNormalLevel(menuPane);
        }
    }

    private static void FindType() {
        try {
            String type = LevelFile.getCurrentActiveSubItem();
            if (type.contains("Sublevel")) {
                GameFile.isChallenge = false;
            } 
            if (type.contains("Challenge level")) {
                GameFile.isChallenge = true;
            }
        } catch (Exception e) {
            GameFile.isChallenge = false;
        }
    }

    public void BuildCenter(GridPane menuPane) {
        SubLevel subLevel = new SubLevel();
        subLevel.BuildCenter(menuPane);
        // remove items
        MenuFile.mainBox.getChildren().clear();
        // add level and history
        MenuFile.displayBox = new VBox(menuPane);
        MenuFile.displayBox.setAlignment(Pos.CENTER);
        MenuFile.displayBox.setPadding(new Insets(40));
        MenuFile.displayBox.setStyle(MenuFile.displayBox_color);
        HBox.setHgrow(MenuFile.displayBox, Priority.ALWAYS);
        VBox.setVgrow(MenuFile.displayBox, Priority.ALWAYS);
        MenuFile.mainBox.getChildren().addAll(LevelFile.LevelPane(), MenuFile.displayBox, HistoryFile.historyPane());
        Platform.runLater(() -> {
            LevelDataInitialized();
            LevelFile.CheckAndProgressLevel();
        });
    }
}
