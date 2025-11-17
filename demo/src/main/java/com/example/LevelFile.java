package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LevelFile {
    private static Integer playerLevel = 1;
    private static Integer currentLocationIndex = 0;
    private static Map<String, ArrayList<String>> levelDict = new HashMap<>();
    private static ArrayList<String> subLevelList = new ArrayList<>();
    // UI
    private static VBox levelPane_listContainer;
    private static ScrollPane levelPane;

    LevelFile(Integer playerLevel, Integer currentLocationIndex, Map<String, ArrayList<String>> levelDict,
            ArrayList<String> subLevelList) {
        this.playerLevel = playerLevel;
        this.currentLocationIndex = currentLocationIndex;
        this.levelDict = levelDict;
        this.subLevelList = subLevelList;
    }

    public static void AdvanceLocation() {
        Set_CurrentLocationIndex(currentLocationIndex + 1);
    }

    public static Integer getPlayerLevel() {
        return playerLevel;
    }

    public static void setPlayerLevel(Integer new_playerLevel) {
        playerLevel = new_playerLevel;
    }

    public static String getCurrentActiveSubItem() {
        return CreateLevelSection.activeSubItem;
    }

    public static void CheckAndProgressLevel() {
        ArrayList<String> currentLevelItems = levelDict.get(playerLevel.toString());
        if (currentLevelItems != null && currentLocationIndex > currentLevelItems.size()) {
            playerLevel++;
            currentLocationIndex = 0;
            GenerateSubLevelItems();
        }
        if (!subLevelList.isEmpty()) {
            Add_LevelElement();
        }
        CreateLevelSection.UpdateActiveLevelItem();
        GameFile.displayLevel.setText("Level: " + LevelFile.getPlayerLevel());
    }

    private static void GenerateSubLevelItems() {
        Random random = new Random();
        int min = 1;
        int max = 100;
        int amount_challengeLevels = 1;
        int amount_subLevels = 1;
        int count = min + (int) (Math.random() * ((max - min) + 1));
        subLevelList.clear();
        for (int i = 1; i <= count; i++) {
            if (random.nextInt(2) == 1 && i > 2) {
                subLevelList.add("Challenge level: " + amount_challengeLevels++);
            } else {
                subLevelList.add("Sublevel: " + amount_subLevels++);
            }
        }
    }

    public static void Set_CurrentLocationIndex(Integer newIndex) {
        currentLocationIndex = newIndex;
        CheckAndProgressLevel();
    }

    private static void InitializeFirstLevel() {
        if (levelDict.isEmpty()) {
            GenerateSubLevelItems();
            if (!subLevelList.isEmpty()) {
                Add_LevelElement();
                CreateLevelSection.UpdateActiveLevelItem();
            }
        }
    }

    private static void Add_LevelItem(String type) {
        subLevelList.add(type);
    }

    private static void Add_LevelElement() {
        if (!subLevelList.isEmpty()) {
            VBox newElement = CreateLevelSection.BuildLevelElement("Level: " + playerLevel);
            levelPane_listContainer.getChildren().add(CreateLevelSection.BuildLevelElement("Level: " + playerLevel));
            levelDict.put(playerLevel.toString(), new ArrayList<>(subLevelList));
            subLevelList.clear();
        } else {
            throw new IllegalAccessError("Error, subLevelList needs to contain more than zero elements.");
        }
        CreateLevelSection.RefreshLevelPaneUI();
    }

    public static void ResetAllLevels() {
        playerLevel = 1;
        currentLocationIndex = 0;
        levelDict.clear();
        subLevelList.clear();
        // Clear active section selections
        CreateLevelSection.activeLevelKey = null;
        CreateLevelSection.activeSubItem = null;
        // Clear UI container if initialized
        if (levelPane_listContainer != null) {
            levelPane_listContainer.getChildren().clear();
        }
    }

    public static ScrollPane LevelPane() {
        levelPane = new ScrollPane();
        levelPane.setFitToWidth(true);
        VBox topSection = new VBox(8);
        HBox levelTitle_container = new HBox(5);
        Label levelTitle = new Label("Levels");
        levelTitle.setFont(App.title_Font);
        levelTitle.setPadding(new Insets(0, 0, 10, 0));
        levelTitle_container.getChildren().add(levelTitle);
        levelTitle_container.setAlignment(Pos.CENTER);
        levelPane_listContainer = new VBox(8);
        levelPane_listContainer.setPadding(new Insets(15));
        VBox.setVgrow(levelPane_listContainer, Priority.ALWAYS);
        StackPane mainMenuReturn = new StackPane();
        mainMenuReturn.setPadding(new Insets(12));
        VBox buttonWrapper = new VBox(mainMenuReturn);
        buttonWrapper.setPadding(new Insets(10, 15, 15, 15));
        Button mainMenuReturn_button = new Button("Return to main menu");
        mainMenuReturn_button.setMaxWidth(Double.MAX_VALUE);
        mainMenuReturn_button.setOnAction(e -> {
            MenuFile.mainBox.getChildren().remove(levelPane);
            MenuFile.mainBox.getChildren().remove(HistoryFile.historyPane);
            // Reset level system
            LevelFile.ResetAllLevels();
            MenuFile menuFile = new MenuFile();
            menuFile.BuildMenu();
        });
        mainMenuReturn.getChildren().add(mainMenuReturn_button);
        topSection.getChildren().addAll(levelTitle_container, buttonWrapper, levelPane_listContainer);
        BorderPane container = new BorderPane();
        container.setTop(topSection);
        levelPane.setContent(container);
        levelPane.setMinWidth(250);
        levelPane.prefWidth(250);
        levelPane.setMaxWidth(250);
        InitializeFirstLevel();
        AnimationUtils.fadein(levelPane, 400);
        return levelPane;
    }

    private static class CreateLevelSection {
        private static String activeLevelKey = null;
        private static String activeSubItem = null;
        // UI
        private static Label levelItemLabel;

        private static VBox BuildLevelElement(String level) {
            VBox levelElement = new VBox();
            HBox header = new HBox(10);
            header.setPadding(new Insets(12));
            Button arrowButton = new Button("V");
            arrowButton.setFont(App.normal_Font);
            Label levelLabel = new Label(level.isEmpty() ? "(no title)" : level);
            levelLabel.setFont(App.header_Font);
            String levelNum = level.replace("Level: ", "");
            VBox levelItem_list = BuildLevelItems(levelNum);
            arrowButton.setOnAction(e -> {
                ToggleSection(levelElement, arrowButton);
            });
            header.getChildren().addAll(arrowButton, levelLabel);
            levelElement.getChildren().addAll(header, levelItem_list);
            levelElement.setUserData(levelItem_list);
            AnimationUtils.rotateIn(levelElement, 400);
            return levelElement;
        }

        private static VBox BuildLevelItems(String levelNum) {
            VBox levelItem_list = new VBox();
            levelItem_list.setSpacing(4);
            levelItem_list.setPadding(new Insets(8, 12, 12, 12));
            levelItem_list.setVisible(true);
            // get items for this level
            ArrayList<String> items = levelDict.get(levelNum);
            if (items == null) {
                items = new ArrayList<>();
            }
            boolean isActive = ("Level: " + levelNum).equals(activeLevelKey);
            if (items.isEmpty()) {
                Label emptyLabel = new Label("No level items found");
                emptyLabel.setFont(App.normal_Font);
                levelItem_list.getChildren().add(emptyLabel);
            } else {
                for (String item : items) {
                    HBox levelItem = new HBox(10);
                    boolean isActiveSubItem = isActive && item.equals(activeSubItem);
                    // Add indicator for active sublevel
                    if (isActiveSubItem) {
                        Label indicator = new Label("►");
                        levelItem.getChildren().add(indicator);
                    } else {
                        levelItemLabel = new Label((isActiveSubItem ? "" : "• ") + item);
                        levelItemLabel.setFont(App.normal_Font);
                        levelItem.getChildren().add(levelItemLabel);
                        levelItem_list.getChildren().add(levelItem);
                    }
                }
            }
            return levelItem_list;
        }

        private static void ToggleSection(VBox section, Button arrowButton) {
            VBox subList = (VBox) section.getUserData();
            boolean isVisible = subList.isVisible();
            subList.setVisible(!isVisible);
            subList.setManaged(!isVisible);
            arrowButton.setText(isVisible ? "^" : "v");
        }

        private static void UpdateActiveLevelItem() {
            if (currentLocationIndex < 1) {
                currentLocationIndex = 1;
            }
            ArrayList<String> currentLevelItems = levelDict.get(playerLevel.toString());
            if (currentLevelItems != null && !currentLevelItems.isEmpty()) {
                int itemIndex = Math.min(currentLocationIndex - 1, currentLevelItems.size() - 1);
                activeLevelKey = "Level: " + playerLevel;
                activeSubItem = currentLevelItems.get(itemIndex);
                RefreshLevelPaneUI();
            }
        }

        private static void RefreshLevelPaneUI() {
            if (levelPane_listContainer == null)
                return;
            ObservableList<Node> children = FXCollections.observableArrayList(levelPane_listContainer.getChildren());
            levelPane_listContainer.getChildren().clear();
            for (Node node : children) {
                if (node instanceof VBox) {
                    VBox section = (VBox) node;
                    HBox header = (HBox) section.getChildren().get(0);
                    Label levelLabel = null;
                    for (Node hChild : header.getChildren()) {
                        if (hChild instanceof Label && ((Label) hChild).getText().startsWith("Level")) {
                            levelLabel = (Label) hChild;
                            break;
                        }
                    }
                    boolean isActive = levelLabel != null && activeLevelKey != null
                            && levelLabel.getText().equals(activeLevelKey);
                    // Rebuild sublevel items with updated active state
                    VBox oldSubList = (VBox) section.getUserData();
                    String levelKey = levelLabel != null ? levelLabel.getText().replace("Level: ", "") : null;
                    ArrayList<String> items = levelKey != null ? levelDict.get(levelKey) : null;
                    if (items != null) {
                        VBox newSubList = new VBox();
                        newSubList.setSpacing(4);
                        newSubList.setPadding(new Insets(8, 12, 12, 12));
                        newSubList.setVisible(oldSubList.isVisible());
                        newSubList.setManaged(oldSubList.isManaged());
                        for (String item : items) {
                            HBox itemEntry = new HBox(10);
                            boolean isActiveSubItem = isActive && activeSubItem != null && item.equals(activeSubItem);
                            if (isActiveSubItem) {
                                Label indicator = new Label("►");
                                indicator.setFont(App.normal_Font);
                                itemEntry.getChildren().add(indicator);
                            }
                            Label itemLabel = new Label((isActiveSubItem ? "" : "• ") + item);
                            itemLabel.setFont(App.normal_Font);
                            itemEntry.getChildren().add(itemLabel);
                            newSubList.getChildren().add(itemEntry);
                        }
                        section.getChildren().set(1, newSubList);
                        section.setUserData(newSubList);
                    }
                }
                levelPane_listContainer.getChildren().add(node);
            }
        }
    }
}
