package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SubLevel {
    /// point system
    private Integer wrongPoints;
    public static Double points;
    private boolean correct = false;
    // code setup
    private String codeValue;
    // visual elements
    private Integer counter = 1;
    private String displayString = "";
    // display
    private CodeConfig codeConfig;
    private TextField codeField;
    private TextField displayField;
    public static Label displayPoints;

    SubLevel(Integer wrongPoints, Double points) {
        this.wrongPoints = wrongPoints;
        this.points = points;
    }

    private void CodeConfig_init() {
        try {
            codeConfig.initialize("demo\\src\\main\\java\\assets\\input.txt");
            codeConfig = CodeConfig.getInstance();
            System.out.println("configuration initialized successfully");
        } catch (IllegalStateException e) {
            System.out.println("already initialized: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to initialize crypto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateCode() {
        CodeConfig_init();
        Long min = 0L;
        Long max = 9L * (CenterPanel.level + 1);
        Long randomNumber = min + (long) (Math.random() * ((max - min) + 1));
        return CodeConfig.encrypt(randomNumber.toString());
    }

    public void BuildCenter(GridPane menuPane) {
        // Display points
        VBox topDisplay = new VBox(8);
        topDisplay.setStyle(App.levelPane_color);
        displayPoints = new Label();
        Platform.runLater(() -> {
            CenterPanel.ChangeLevelValues();
        });
        String pointsText = String.format("Points: %.2f", SubLevel.points);
        displayPoints.setText(pointsText);
        displayPoints.setStyle(
                "-fx-text-fill: gray; -fx-font-size: 20px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        CenterPanel.displayLevel = new Label();
        CenterPanel.displayLevel.setText("Level: " + CenterPanel.level.toString());
        CenterPanel.displayLevel.setStyle(
                "-fx-text-fill: gray; -fx-font-size: 20px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        topDisplay.getChildren().addAll(CenterPanel.displayLevel, SubLevel.displayPoints);
        menuPane.add(topDisplay, 0, 0, 3, 1);
        // TextField
        codeField = new TextField();
        codeValue = generateCode();
        codeField.setText(codeValue);
        codeField.setEditable(false);
        codeField.setPrefHeight(60);
        codeField.setFont(App.header_Font);
        codeField.setPadding(new Insets(5));
        codeField.setStyle(
                "-fx-background-color: darkgray; -fx-border-color: lightgray; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: white; -fx-padding: 10;");
        menuPane.add(codeField, 0, 1, 3, 1);
        displayField = new TextField();
        displayField.setEditable(false);
        displayField.setPrefHeight(60);
        displayField.setFont(App.header_Font);
        displayField.setStyle(
                "-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #212529; -fx-padding: 10;");
        menuPane.add(displayField, 0, 2, 3, 1);
        // Number 1-9 buttons
        for (int y = 5; y >= 3; y--) {
            for (int x = 0; x < 3; x++) {
                Button btn = createNumberButton(counter++);
                menuPane.add(btn, x, y);
            }
        }
        // bottom row
        Button btnClear = createElementButton("C", "Clear");
        btnClear.setStyle(getButtonStyle("#dc3545", "#c82333"));
        menuPane.add(btnClear, 0, 6);
        Button btn0 = createNumberButton(0);
        menuPane.add(btn0, 1, 6);
        Button btnBackspace = createElementButton("←", "Backspace");
        btnClear.setStyle(getButtonStyle("#ffc107", "#e0a800"));
        menuPane.add(btnBackspace, 2, 6);
        AnimationUtils.fadein(menuPane, 800);
    }

    private String getButtonStyle(String bgColor, String hoverColor) {
        return String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);",
                bgColor);
    }

    public Button createNumberButton(Integer number) {
        Button btn = new Button(number.toString());
        btn.setPrefWidth(70);
        btn.setPrefHeight(70);
        String baseStyle = "-fx-background-color: linear-gradient(to bottom, #4facfe, #00f2fe); -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(79, 172, 254, 0.4), 6, 0, 0, 3);";
        String hoverStyle = "-fx-background-color: linear-gradient(to bottom, #00f2fe, #4facfe); -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 242, 254, 0.6), 10, 0, 0, 5);";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        AnimationUtils.fadein(btn, 400);
        btn.setOnAction(e -> {
            AnimationUtils.pulse(btn, 200);
            displayString += number.toString();
            displayField.setText(displayString);
        });
        return btn;
    }

    public Button createElementButton(String text, String argument) {
        Button btn = new Button(text);
        btn.setPrefWidth(70);
        btn.setPrefHeight(70);
        String baseColor = argument.equals("Clear") ? "linear-gradient(to bottom, #fa709a, #fee140)" : "linear-gradient(to bottom, #ff6b6b, #ee5a6f)";
        String hoverColor = argument.equals("Clear") ? "linear-gradient(to bottom, #fee140, #fa709a)" : "linear-gradient(to bottom, #ee5a6f, #ff6b6b)";
        String baseStyle = "-fx-background-color: " + baseColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(250, 112, 154, 0.4), 6, 0, 0, 3);";
        String hoverStyle = "-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(238, 90, 111, 0.6), 10, 0, 0, 5);";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        btn.setOnAction(e -> {
            AnimationUtils.pulse(btn, 200);
            try {
                if (argument.equals("Backspace")) {
                    if (!displayString.isEmpty()) {
                        SidePanels.subHistoryList.add(displayString);
                        displayString = displayString.substring(0, displayString.length() - 1);
                    }
                }
                if (argument.equals("Clear")) {
                    // get current time
                    LocalDateTime myDateObj = LocalDateTime.now();
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    String formattedDate = myDateObj.format(myFormatObj);
                    // commit history
                    SidePanels.subHistoryList.add(displayString);
                    // calculate scores for each entry
                    ArrayList<ScoreResult> scoreResults = new ArrayList<>();
                    double totalRoundScore = 0.0;
                    for (String entry : SidePanels.subHistoryList) {
                        ScoreResult result = CheckCodeDetailed(entry);
                        scoreResults.add(result);
                        totalRoundScore += result.totalScore;
                    }
                    // build history section
                    SidePanels.historyPane_listContainer.getChildren()
                            .add(SidePanels.createHistorySection.BuildHistorySection(formattedDate, scoreResults));
                    SidePanels.historyDict.put(formattedDate, new ArrayList<>(SidePanels.subHistoryList));
                    SidePanels.historyScoreDict.put(formattedDate, scoreResults);
                    SidePanels.subHistoryList.clear();
                    displayString = "";
                }
                displayField.setText(displayString);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        });
        AnimationUtils.fadein(btn, 400);
        return btn;
    }

    public static class ScoreResult {
        public double totalScore;
        public Map<String, Integer> thresholdCounts;

        ScoreResult() {
            this.totalScore = 0.0;
            this.thresholdCounts = new HashMap<>();
        }

        public void addThreshold(String threshold, Integer count) {
            thresholdCounts.put(threshold, thresholdCounts.getOrDefault(threshold, 0) + count);
        }
    }

    private ScoreResult CheckCodeDetailed(String displayString) {
        String decryptCode = codeConfig.decrypt(codeValue);
        System.out.println("" + decryptCode + " " + displayString);
        ScoreResult result = new ScoreResult();
        if (correct == false) {
            if (displayString.equals(decryptCode)) {
                points += 1.00;
                result.totalScore = 1.00;
                CenterPanel.currentLevel_XP += 1;
                result.addThreshold("Correct", 1);
                correct = true;
                codeField.setText(decryptCode);
            } else {
                result = CalculatedScoreDetailed(decryptCode, displayString);
                if (result.totalScore > 0) {
                    points += result.totalScore;
                } else {
                    wrongPoints++;
                }
            }
        } else {
            result.totalScore = 0;
            result.addThreshold("Correct_Exacts", 1);
        }
        // UI
        String pointsText = String.format("Points: %.2f", points);
        displayPoints.setText(pointsText);
        return result;
    }

    private static ScoreResult CalculatedScoreDetailed(String randomNumber, String guess) {
        ScoreResult result = new ScoreResult();
        Set<Integer> matchedPositions = new HashSet<>();
        for (int i = 0; i < guess.length(); i++) {
            char g = guess.charAt(i);
            if (!Character.isDigit(g)) {
                continue;
            }
            int digit = Character.getNumericValue(g);
            boolean duplicate = guess.indexOf(g) != guess.lastIndexOf(g);
            boolean prime = (digit == 2 || digit == 3 || digit == 5 || digit == 7);
            if (i < randomNumber.length()) {
                char r = randomNumber.charAt(i);
                int randDigit = Character.getNumericValue(r);
                // exact match
                if (digit == randDigit) {
                    result.totalScore += 0.50;
                    result.addThreshold("Exists_Exact", 1);
                    matchedPositions.add(i);
                } else if (randomNumber.indexOf(g) != -1 && !matchedPositions.contains(i)) {
                    result.totalScore += 0.25;
                    result.addThreshold("Exists", 1);
                    matchedPositions.add(i);
                }
                // odd/even check
                if ((digit % 2 == 0 && randDigit % 2 == 0) || (digit % 2 == 1 && randDigit % 2 == 1)) {
                    result.totalScore += 0.10;
                    result.addThreshold("Property_Exists", 1);
                }
            }
            // Duplicate
            if (duplicate) {
                result.totalScore += 0.05;
                result.addThreshold("Duplicate_Exists", 1);
            }
            // prime
            if (prime) {
                result.totalScore += 0.01;
                result.addThreshold("Prime_Exists", 1);
            }
        }
        result.totalScore = Math.min(result.totalScore, 1.00);
        return result;
    }
}
