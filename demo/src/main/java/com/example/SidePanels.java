package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SidePanels {
    // history and level
    public static Map<String, ArrayList<String>> historyDict = new HashMap<>();
    public static ArrayList<String> subHistoryList = new ArrayList<>();
    public static Map<String, ArrayList<SubLevel.ScoreResult>> historyScoreDict = new HashMap<>();
    public static Map<String, ArrayList<String>> levelDict = new HashMap<>();
    public static ArrayList<String> subLevelList = new ArrayList<>();
    public static String activeSessionKey = null;
    // display
    public static VBox historyPane_listContainer;
    public static VBox levelPane_listContainer;
    public static ScrollPane historyPane;
    public static ScrollPane levelPane;

    public SidePanels(Map<String, ArrayList<String>> historyDict, ArrayList<String> subHistoryList,
            Map<String, ArrayList<String>> levelDict, ArrayList<String> subLevelList) {
        this.historyDict = historyDict;
        this.subHistoryList = subHistoryList;
        this.levelDict = levelDict;
        this.subLevelList = subLevelList;
    }

    public static class createHistorySection {
        public static VBox BuildHistorySection(String time, ArrayList<SubLevel.ScoreResult> scoreResults) {
            VBox section = new VBox();
            section.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 3, 0, 0, 1);");
            HBox header = new HBox(10);
            header.setPadding(new Insets(12));
            header.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;");
            Label arrowLabel = new Label("v");
            arrowLabel.setFont(App.normal_Font);
            arrowLabel.setStyle("-fx-text-fill: #6c757d");
            Button removeButton = new Button("Remove");
            removeButton.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-cursor: hand;");
            removeButton.setOnAction(e -> historyPane_listContainer.getChildren().remove(section));
            removeButton.setOnMouseEntered(e -> removeButton
                    .setStyle("-fx-background-color: darkred; -fx-text-fill: white; -fx-font-weight: bold;"));
            removeButton.setOnMouseExited(e -> removeButton
                    .setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-weight: normal;"));
            // calculate total score
            double totalScore = 0.0;
            for (SubLevel.ScoreResult sr : scoreResults) {
                totalScore += sr.totalScore;
            }
            VBox titleBox = new VBox(5);
            Label totalLabel = new Label();
            Label timeLabel = new Label();
            if (time.isEmpty()) {
                totalLabel.setText("(no title)");
            } else {
                totalLabel.setText(String.format("Total: %.2f", totalScore));
                timeLabel.setText(time);
            }
            totalLabel.setFont(App.header_Font);
            totalLabel.setStyle("-fx-text-fill: #212529;");
            timeLabel.setFont(App.normal_Font);
            timeLabel.setStyle("-fx-text-fill: #212529;");
            titleBox.getChildren().addAll(totalLabel, timeLabel);
            header.getChildren().addAll(arrowLabel, titleBox, removeButton);
            header.setOnMouseClicked(e -> toggleSection(section, arrowLabel));
            header.setOnMouseEntered(e -> header
                    .setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;"));
            header.setOnMouseExited(e -> header
                    .setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;"));
            VBox subList = new VBox();
            subList.setSpacing(4);
            subList.setPadding(new Insets(8, 12, 12, 12));
            subList.setVisible(true);
            subList.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 8 8;");
            ArrayList<String> items = historyDict.getOrDefault(time, new ArrayList<>(subHistoryList));
            if (items.isEmpty()) {
                Label emptyLabel = new Label("No history items");
                emptyLabel.setFont(App.normal_Font);
                emptyLabel.setStyle("-fx-text-fill: #adb5bd; -fx-font-style: italic;");
                subList.getChildren().add(emptyLabel);
            } else {
                for (Integer idx = 0; idx < items.size(); idx++) {
                    String item = items.get(idx);
                    SubLevel.ScoreResult scoreResult = idx < scoreResults.size() ? scoreResults.get(idx)
                            : new SubLevel.ScoreResult();
                    HBox itemEntry = new HBox(10);
                    itemEntry.setStyle("-fx-padding: 4 0 4 10;");
                    Label itemLabel = new Label("• " + item + String.format(" (%.2f pts)", scoreResult.totalScore));
                    itemLabel.setFont(App.normal_Font);
                    itemLabel.setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10;");
                    ScrollPane scrollPane = new ScrollPane(itemLabel);
                    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                    scrollPane.setFitToHeight(true);
                    scrollPane.setStyle(
                            "-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent; -fx-hbar-policy: always; -fx-padding: 0;");
                    scrollPane.lookupAll(".scroll-bar").forEach(sb -> {
                        sb.setStyle("-fx-background-color: transparent; -fx-pref-height: 6px; -fx-background-insets: 0;");
                    });
                    scrollPane.lookupAll(".thumb").forEach(thumb -> {
                        thumb.setStyle("-fx-background-color: darkgray; -fx-background-radius: 3;");
                    });
                    // Create hint buttons
                    HBox buttonContainer = new HBox(5);
                    for (Map.Entry<String, Integer> threshold : scoreResult.thresholdCounts.entrySet()) {
                        Button hintButton = new Button();
                        hintButton.setText("?");
                        hintButton.setPrefSize(25, 25);
                        hintButton.setMinSize(25, 25);
                        hintButton.setMaxSize(25, 25);
                        String[] buttonStyle = ButtonLogic(threshold.getKey());
                        hintButton.setStyle(buttonStyle[0] + " -fx-font-size: 12px; -fx-padding: 0;");
                        Tooltip hint = new Tooltip();
                        String tooltipText = threshold.getValue() > 1
                                ? buttonStyle[1] + " (X" + threshold.getValue() + ")"
                                : buttonStyle[1];
                        hint.setText(tooltipText);
                        hintButton.setTooltip(hint);
                        buttonContainer.getChildren().add(hintButton);
                    }
                    itemLabel.setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10;");
                    itemLabel.setOnMouseEntered(e -> itemLabel
                            .setStyle("-fx-background-color: #e9ecef; -fx-padding: 4 0 4 10; -fx-cursor: hand;"));
                    itemLabel.setOnMouseExited(e -> itemLabel
                            .setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10; -fx-cursor: hand;"));
                    itemEntry.getChildren().addAll(scrollPane, buttonContainer);
                    subList.getChildren().add(itemEntry);
                    subList.setOnMouseClicked(event -> {
                        SubLevel.loadHistorySession(time, item);
                    });
                }
            }
            header.setOnMouseClicked(e -> toggleSection(section, arrowLabel));
            section.getChildren().addAll(header, subList);
            section.setUserData(subList);
            return section;
        }

        public static void toggleSection(VBox section, Label arrowLabel) {
            VBox subList = (VBox) section.getUserData();
            boolean isVisible = subList.isVisible();
            subList.setVisible(!isVisible);
            subList.setManaged(!isVisible);
            arrowLabel.setText(isVisible ? "^" : "v");
        }

        private static String[] ButtonLogic(String thresholdType) {
            String returnColor;
            String returnHint;
            switch (thresholdType) {
                case "Correct":
                    returnColor = "#1E90FF";
                    returnHint = "You guessed the correct number!";
                    break;
                case "Correct_Exacts":
                    returnColor = "#ff1effff";
                    returnHint = "Correct number already found";
                    break;
                case "Exists_Exact":
                    returnColor = "#00C851";
                    returnHint = "A digit was found in the correct location and correct value.";
                    break;
                case "Exists":
                    returnColor = "#007E33";
                    returnHint = "A digit was found in the number.";
                    break;
                case "Property_Exists":
                    returnColor = "#FFBB33";
                    returnHint = "A digit in your guess matches the property of another digit.";
                    break;
                case "Duplicate_Exists":
                    returnColor = "#FF4444";
                    returnHint = "A digit appears more than once in the target number.";
                    break;
                case "Prime_Exists":
                    returnColor = "#AA66CC";
                    returnHint = "A digit has the same prime state.";
                    break;
                default:
                    returnColor = "#B0B0B0";
                    returnHint = "Sorry, no points where applied.";
                    break;
            }
            String[] returnString = { "-fx-background-color: " + returnColor + ";", returnHint };
            return returnString;
        }
    }

    public static class CreateLevelSection {
        public static VBox BuildLevelSection(String level) {
            VBox section = new VBox();
            section.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 3, 0, 0, 1);");
            HBox header = new HBox(10);
            header.setPadding(new Insets(12));
            header.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;");
            Label arrowLabel = new Label("v");
            arrowLabel.setFont(App.normal_Font);
            arrowLabel.setStyle("-fx-text-fill: #6c757d");
            Label levelLabel = new Label(level.isEmpty() ? "(no title)" : level);
            levelLabel.setFont(App.header_Font);
            levelLabel.setStyle("-fx-text-fill: #212529;");
            header.getChildren().addAll(arrowLabel, levelLabel);
            header.setOnMouseClicked(e -> toggleSection(section, arrowLabel));
            header.setOnMouseEntered(e -> header
                    .setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;"));
            header.setOnMouseExited(e -> header
                    .setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;"));
            VBox subList = new VBox();
            subList.setSpacing(4);
            subList.setPadding(new Insets(8, 12, 12, 12));
            subList.setVisible(true);
            subList.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 8 8;");
            ArrayList<String> items = levelDict.getOrDefault(level, new ArrayList<>(subLevelList));
            if (items.isEmpty()) {
                Label emptyLabel = new Label("No Level items");
                emptyLabel.setFont(App.normal_Font);
                emptyLabel.setStyle("-fx-text-fill: #adb5bd; -fx-font-style: italic;");
                subList.getChildren().add(emptyLabel);
            } else {
                for (String item : items) {
                    HBox itemEntry = new HBox(10);
                    itemEntry.setStyle("-fx-padding: 4 0 4 10;");
                    Label itemLabel = new Label("• " + item);
                    itemLabel.setFont(App.normal_Font);
                    itemLabel.setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10;");
                    itemEntry.setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10;");
                    itemEntry.setOnMouseEntered(e -> itemEntry
                            .setStyle("-fx-background-color: #e9ecef; -fx-padding: 4 0 4 10; -fx-cursor: hand;"));
                    itemEntry.setOnMouseExited(e -> itemEntry
                            .setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10; -fx-cursor: hand;"));
                    itemEntry.getChildren().add(itemLabel);
                    subList.getChildren().add(itemEntry);
                }
            }
            header.setOnMouseClicked(e -> toggleSection(section, arrowLabel));
            section.getChildren().addAll(header, subList);
            section.setUserData(subList);
            return section;
        }

        public static void toggleSection(VBox section, Label arrowLabel) {
            VBox subList = (VBox) section.getUserData();
            boolean isVisible = subList.isVisible();
            subList.setVisible(!isVisible);
            subList.setManaged(!isVisible);
            arrowLabel.setText(isVisible ? "^" : "v");
        }

        public static void LevelSectionUpdate() {
            // pass
        }
    }

    public static ScrollPane LevelPane() {
        levelPane = new ScrollPane();
        levelPane.setFitToWidth(true);
        levelPane.setStyle(App.levelPane_color);
        VBox topSection = new VBox(8);
        topSection.setStyle(App.levelPane_color);
        HBox levelTitle_container = new HBox(5);
        levelTitle_container.setStyle("-fx-background-color: transparent;");
        Label levelTitle = new Label("Levels");
        levelTitle.setStyle(
                "-fx-text-fill: gray; -fx-font-size: 20px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        levelTitle.setFont(App.title_Font);
        levelTitle.setPadding(new Insets(0, 0, 10, 0));
        levelTitle_container.getChildren().add(levelTitle);
        levelTitle_container.setAlignment(Pos.CENTER);
        levelPane_listContainer = new VBox(8);
        levelPane_listContainer.setPadding(new Insets(15));
        levelPane_listContainer.setStyle(App.levelPane_color);
        VBox.setVgrow(levelPane_listContainer, Priority.ALWAYS);
        StackPane mainMenuReturn = new StackPane();
        mainMenuReturn.setStyle(
                "-fx-border-color: black; -fx-background-color: #f5f5f5;; -fx-background-color-radius: 8; -fx-border-radius: 8; -fx-border-width: 2;");
        mainMenuReturn.setPadding(new Insets(12));
        VBox buttonWrapper = new VBox(mainMenuReturn);
        buttonWrapper.setPadding(new Insets(10, 15, 15, 15));
        buttonWrapper.setStyle("-fx-background-color: #f5f5f5;");
        Button mainMenuReturn_button = new Button("Return to main menu");
        mainMenuReturn_button.setStyle("-fx-background-color: #f5f5f5;; -fx-cursor: hand;");
        mainMenuReturn_button.setMaxWidth(Double.MAX_VALUE);
        mainMenuReturn_button.setOnAction(e -> {
            MenuFile.mainBox.getChildren().remove(historyPane);
            MenuFile.mainBox.getChildren().remove(levelPane);
            MenuFile menuFile = new MenuFile();
            menuFile.BuildMenu();
        });
        mainMenuReturn.getChildren().add(mainMenuReturn_button);
        topSection.getChildren().addAll(levelTitle_container, buttonWrapper, levelPane_listContainer);
        BorderPane container = new BorderPane();
        container.setStyle(App.levelPane_color);
        container.setTop(topSection);
        levelPane.setContent(container);
        levelPane.setMinWidth(250);
        levelPane.prefWidth(250);
        levelPane.setMaxWidth(250);
        AnimationUtils.fadein(levelPane, 400);
        return levelPane;
    }

    public static ScrollPane HistoryPane() {
        historyPane = new ScrollPane();
        historyPane.setFitToWidth(true);
        historyPane.setStyle(App.historyPane_color);
        historyPane_listContainer = new VBox(8);
        historyPane_listContainer.setPadding(new Insets(15));
        historyPane_listContainer.setStyle(App.historyPane_color);
        HBox historyTitle_container = new HBox(5);
        historyTitle_container.setStyle("-fx-background-color: transparent;");
        Label historyTitle = new Label("History");
        historyTitle.setStyle(
                "-fx-text-fill: gray; -fx-font-size: 20px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        historyTitle.setFont(App.title_Font);
        historyTitle.setPadding(new Insets(0, 0, 10, 0));
        historyTitle_container.getChildren().add(historyTitle);
        historyTitle_container.setAlignment(Pos.CENTER);
        VBox container = new VBox(historyTitle_container, historyPane_listContainer);
        container.setStyle(App.historyPane_color);
        historyPane.setContent(container);
        historyPane.setMinWidth(250);
        historyPane.prefWidth(250);
        AnimationUtils.fadein(historyPane, 400);
        return historyPane;
    }

    public void BuildSidePanels(GridPane menuPane) {
        MenuFile.displayBox = new VBox(menuPane);
        MenuFile.displayBox.setAlignment(Pos.CENTER);
        MenuFile.displayBox.setPadding(new Insets(40));
        MenuFile.displayBox.setStyle("-fx-background-color: #e9ecef;");
        HBox.setHgrow(MenuFile.displayBox, Priority.ALWAYS);
        VBox.setVgrow(MenuFile.displayBox, Priority.ALWAYS);

    }
}