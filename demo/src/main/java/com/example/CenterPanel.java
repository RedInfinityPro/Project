package com.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Random;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CenterPanel {
    // setup
    public static boolean buildGameFile;
    public static Deque<String> levelHistoryDeque = new ArrayDeque<>();
    // levels and challenge levels
    public static Integer level = 0;
    public static Integer minLevel = 1;
    public static Integer amount_challengeLevel;
    public static Integer amount_subLevel;
    public static Integer currentLevel_XP;
    public static Integer maxLevel_XP;
    public static Label displayLevel;

    CenterPanel(Integer level, Integer amount_challengeLevel, Integer amount_subLevel, Integer currentLevel_XP, Integer maxLevel_XP) {
        this.level = level;
        this.amount_challengeLevel = amount_challengeLevel;
        this.amount_subLevel = amount_subLevel;
        this.currentLevel_XP = currentLevel_XP;
        this.maxLevel_XP = maxLevel_XP;
    }

    public static void ChangeLevelValues() {
        Random random = new Random();
        Integer min = 1;
        Integer max = 100;
        if (displayLevel != null) {
            if (currentLevel_XP >= maxLevel_XP) {
                levelHistoryDeque.push(level.toString());
                level += 1;
                currentLevel_XP = 0;
                maxLevel_XP = min + (int) (Math.random() * ((max - min) + 1));
                for (int i = 0; i < maxLevel_XP; i++) {
                    if (random.nextInt(2) == 1 && i > 1) {
                        SidePanels.subLevelList.add("Challenge level: " + amount_challengeLevel++);
                    } else {
                        SidePanels.subLevelList.add("Sublevel: " + amount_subLevel++);
                    }
                }
                SidePanels.levelPane_listContainer.getChildren()
                        .add(SidePanels.CreateLevelSection.BuildLevelSection("Level: " + level.toString()));
                SidePanels.levelDict.put(level.toString(), new ArrayList<>(SidePanels.subLevelList));
                SidePanels.subLevelList.clear();
            }
            displayLevel.setText("Level: " + level.toString());
        }
    }

    public static boolean rollBackLevel() {
        if (levelHistoryDeque.isEmpty()) {
            return false;
        }
        try {
            String prevkey = levelHistoryDeque.pop();
            if (!SidePanels.levelDict.containsKey(prevkey)) {
                try {
                    level = Integer.parseInt(prevkey.replaceAll("[^0-9]", ""));
                } catch (Exception e) {
                    level = Math.max(minLevel, level - 1);
                }
                CenterPanel.displayLevel.setText("Level: " + level.toString());
                return true;
            } else {
                level = Math.max(minLevel, level - 1);
                CenterPanel.displayLevel.setText("Level: " + level.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void BuildCenter(GridPane menuPane) {
        SubLevel subLevel = new SubLevel(0, 0.0);
        subLevel.BuildCenter(menuPane);
        SidePanels sidePanels = new SidePanels(new HashMap<>(), new ArrayList<>(), new HashMap<>(), new ArrayList<>());
        sidePanels.BuildSidePanels(menuPane);
        // remove items
        MenuFile.mainBox.getChildren().clear();
        // add level and history
        MenuFile.displayBox = new VBox(menuPane);
        MenuFile.displayBox.setAlignment(Pos.CENTER);
        MenuFile.displayBox.setPadding(new Insets(40));
        MenuFile.displayBox.setStyle(App.displayBox_color);
        HBox.setHgrow(MenuFile.displayBox, Priority.ALWAYS);
        VBox.setVgrow(MenuFile.displayBox, Priority.ALWAYS);
        MenuFile.mainBox.getChildren().addAll(SidePanels.LevelPane(), MenuFile.displayBox, SidePanels.HistoryPane());
    }
}
