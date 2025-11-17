package com.example;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HistoryFile {
    private static Map<TimePointsKey, Map<String, List<HintData>>> historyDict = new HashMap();
    private static Map<String, List<HintData>> subHistoryList = new HashMap<>();
    private static TimePointsKey currentSessionKey = null;
    private static Double currentSessionPoints = 0.0;
    // UI
    private static VBox historyPane_listContainer;
    public static ScrollPane historyPane;

    HistoryFile(Map<TimePointsKey, Map<String, List<HintData>>> historyDict,
            Map<String, List<HintData>> subHistoryList) {
        this.historyDict = historyDict;
        this.subHistoryList = subHistoryList;
    }

    public static void start_NewSession() {
        LocalTime time = LocalTime.now();
        currentSessionKey = new TimePointsKey(time, 0.0);
        currentSessionPoints = 0.0;
        subHistoryList.clear();
    }

    public static void add_GuessAttempt(String guess, List<HintData> hintData, Double pointsEarned) {
        if (currentSessionKey == null) {
            start_NewSession();
        }
        subHistoryList.put(guess, new ArrayList<>(hintData));
        currentSessionPoints += pointsEarned;
    }

    public static void finalizeSession() {
        if (currentSessionKey == null) {
            System.out.print("No session to finalize");
            return;
        }
        // Update the key with final total points
        TimePointsKey finalKey = new TimePointsKey(currentSessionKey.getTime(), currentSessionPoints);
        // Store in history dictionary
        historyDict.put(finalKey, new HashMap<>(subHistoryList));
        // Create and add UI element
        VBox historyElement = CreateHistoryItem.BuildHistoryElement(finalKey);
        if (historyPane_listContainer != null) {
            historyPane_listContainer.getChildren().add(0, historyElement);
        }
        // Reset for next session
        subHistoryList.clear();
        currentSessionKey = null;
        currentSessionPoints = 0.0;
    }

    private Map<TimePointsKey, Map<String, List<HintData>>> getAll() {
        return Collections.unmodifiableMap(historyDict);
    }

    private Map<String, List<HintData>> getByKey(TimePointsKey key) {
        return historyDict.getOrDefault(key, Collections.emptyMap());
    }

    public static void clearAll() {
        historyDict.clear();
        subHistoryList.clear();
        currentSessionKey = null;
        currentSessionPoints = 0.0;
        if (historyPane_listContainer != null) {
            historyPane_listContainer.getChildren().clear();
        }
    }

    public static ScrollPane historyPane() {
        historyPane = new ScrollPane();
        historyPane.setFitToWidth(true);
        VBox topSection = new VBox(8);
        HBox historyTitle_container = new HBox(5);
        Label historyTitle = new Label("History");
        historyTitle.setFont(App.title_Font);
        historyTitle.setPadding(new Insets(0, 0, 10, 0));
        historyTitle_container.getChildren().add(historyTitle);
        historyTitle_container.setAlignment(Pos.CENTER);
        historyPane_listContainer = new VBox(8);
        historyPane_listContainer.setPadding(new Insets(15));
        VBox.setVgrow(historyPane_listContainer, Priority.ALWAYS);
        StackPane deleteHistoryPane = new StackPane();
        deleteHistoryPane.setPadding(new Insets(12));
        VBox buttonWrapper = new VBox(deleteHistoryPane);
        buttonWrapper.setPadding(new Insets(10, 15, 15, 15));
        Button deleteHistory_button = new Button("Delete History");
        deleteHistory_button.setMaxWidth(Double.MAX_VALUE);
        deleteHistory_button.setOnAction(e -> {
            clearAll();
        });
        deleteHistoryPane.getChildren().add(deleteHistory_button);
        topSection.getChildren().addAll(historyTitle_container, buttonWrapper, historyPane_listContainer);
        BorderPane container = new BorderPane();
        container.setTop(topSection);
        historyPane.setContent(container);
        historyPane.setMinWidth(250);
        historyPane.prefWidth(250);
        historyPane.setMaxWidth(250);
        AnimationUtils.fadein(historyPane, 400);
        return historyPane;
    }

    public static class CreateHistoryItem {
        public static VBox BuildHistoryElement(TimePointsKey key) {
            VBox historyElement = new VBox();
            HBox header = new HBox(10);
            header.setPadding(new Insets(12));
            header.setSpacing(10);
            Button arrowButton = new Button("V");
            arrowButton.setFont(App.normal_Font);
            Button removeButton = new Button("X");
            removeButton.setFont(App.normal_Font);
            removeButton.setPrefSize(30, 30);
            VBox titleBox = new VBox(5);
            Label totalLabel = new Label(String.format("Total: %.2f", key.getPoints()));
            Label timeLabel = new Label(key.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            totalLabel.setText(String.format("Total: %.2f", key.getPoints()));
            totalLabel.setFont(App.header_Font);
            timeLabel.setFont(App.normal_Font);
            titleBox.getChildren().addAll(totalLabel, timeLabel);
            HBox.setHgrow(titleBox, Priority.ALWAYS);
            header.getChildren().addAll(arrowButton, titleBox, removeButton);
            VBox historyItem_list = BuildHistoryItems(key);
            arrowButton.setOnAction(e -> {
                ToggleSection(historyElement, arrowButton);
            });
            removeButton.setOnAction(e -> {
                historyDict.remove(key);
                if (historyPane_listContainer != null) {
                    historyPane_listContainer.getChildren().remove(historyElement);
                }
            });
            historyElement.getChildren().addAll(header, historyItem_list);
            historyElement.setUserData(historyItem_list);
            AnimationUtils.rotateIn(historyElement, 400);
            return historyElement;
        }

        private static VBox BuildHistoryItems(TimePointsKey timePointsKey) {
            VBox historyItem_list = new VBox();
            historyItem_list.setSpacing(4);
            historyItem_list.setPadding(new Insets(8, 12, 12, 12));
            historyItem_list.setVisible(true);
            // get items for history entry
            Map<String, List<HintData>> items = historyDict.get(timePointsKey);
            if (items == null || items.isEmpty()) {
                Label emptyLabel = new Label("No history items found");
                emptyLabel.setFont(App.normal_Font);
                historyItem_list.getChildren().add(emptyLabel);
                return historyItem_list;
            } else {
                // Build each guess attempt
                for (Map.Entry<String, List<HintData>> entry : items.entrySet()) {
                    String guess = entry.getKey();
                    List<HintData> hints = entry.getValue();
                    HBox historyItem = new HBox(10);
                    historyItem_list.setAlignment(Pos.CENTER_LEFT);
                    Label guessLabel = new Label("• " + guess);
                    guessLabel.setFont(App.normal_Font);
                    guessLabel.setMaxWidth(100);
                    // create hint buttons
                    HBox hintButton_container = new HBox(5);
                    if (hints != null) {
                        for (HintData hint : hints) {
                            if (hint == null) {
                                continue;
                            }
                            Button hintButton = new Button("?");
                            hintButton.setPrefSize(25, 25);
                            hintButton.setMinSize(25, 25);
                            hintButton.setMaxSize(25, 25);
                            hintButton.setStyle("-fx-background-color: " + hint.color + ";" + "-fx-text-fill: white; -fx-font-weight: bold;");
                            Tooltip tooltip = new Tooltip();
                            String tooltipText = hint.count > 1 ? hint.message + " (x" + hint.count + ")": hint.message;
                            tooltip.setText(tooltipText);
                            hintButton.setTooltip(tooltip);
                            hintButton_container.getChildren().add(hintButton);
                        }
                    }
                    historyItem.getChildren().addAll(guessLabel, hintButton_container);
                    historyItem_list.getChildren().add(historyItem);
                }
            }
            return historyItem_list;
        }

        public static HintData ButtonLogic(String type, int count) {
            switch (type) {
                case "Correct":
                    return new HintData("#1E90FF", "You guessed the correct number!", count);
                case "Correct_Exists":
                    return new HintData("#FF00FF", "Correct number already found", count);
                case "Exists_Exact":
                    return new HintData("#00C851", "Found correct digit in correct place", count);
                case "Exists":
                    return new HintData("#007E33", "Digit exists in the number", count);
                case "Property_Exists":
                    return new HintData("#FFBB33", "Digit shares same property", count);
                case "Duplicate_Exists":
                    return new HintData("#FF4444", "Digit appears more than once", count);
                case "Prime_Exists":
                    return new HintData("#AA66CC", "Digit is prime", count);
                default:
                    return new HintData("#bababaff", "No thresholds found", count);
            }
        }

    }

    private static void ToggleSection(VBox section, Button arrowButton) {
        VBox subList = (VBox) section.getUserData();
        boolean isVisible = subList.isVisible();
        subList.setVisible(!isVisible);
        subList.setManaged(!isVisible);
        arrowButton.setText(isVisible ? "^" : "v");
    }
}