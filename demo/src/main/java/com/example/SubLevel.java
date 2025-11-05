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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SubLevel {
    /// point system
    private Integer wrongPoints;
    public static Double points;
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
        Long max = 9L;
        Long randomNumber = min + (long) (Math.random() * ((max - min) + 1));
        return CodeConfig.encrypt(randomNumber.toString());
    }

    public void BuildCenter(GridPane menuPane) {
        // Display points
        VBox topDisplay = new VBox(8);
        topDisplay.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");
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
        codeField.setFont(App.displayFont);
        codeField.setPadding(new Insets(5));
        codeField.setStyle(
                "-fx-background-color: darkgray; -fx-border-color: lightgray; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: white; -fx-padding: 10;");
        menuPane.add(codeField, 0, 1, 3, 1);
        displayField = new TextField();
        displayField.setEditable(false);
        displayField.setPrefHeight(60);
        displayField.setFont(App.displayFont);
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
        btn.setStyle(
                "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #0056b3; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        btn.setOnAction(e -> {
            displayString += number.toString();
            displayField.setText(displayString);
        });
        return btn;
    }

    public Button createElementButton(String text, String argument) {
        Button btn = new Button(text);
        btn.setPrefWidth(70);
        btn.setPrefHeight(70);
        String baseColor = argument.equals("Clear") ? "#e0a800" : "#c82333";
        String hoverColor = argument.equals("Clear") ? "#c82333" : "#e0a800";
        btn.setStyle("-fx-background-color: " + baseColor
                + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + hoverColor
                + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 2);"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + baseColor
                + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);"));
        btn.setOnAction(e -> {
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
                    double totalRoundScore = 0.0;
                    for (String entry : SidePanels.subHistoryList) {
                        totalRoundScore += checkCode(entry);
                    }
                    SidePanels.historyPane_listContainer.getChildren()
                            .add(SidePanels.createHistorySection.BuildHistorySection(formattedDate, totalRoundScore));
                    SidePanels.historyDict.put(formattedDate, new ArrayList<>(SidePanels.subHistoryList));
                    SidePanels.subHistoryList.clear();
                    displayString = "";
                }
                displayField.setText(displayString);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        });
        return btn;
    }

    private Double checkCode(String displayString) {
        String decryptCode = codeConfig.decrypt(codeValue);
        System.out.println("" + decryptCode + " " + displayString);
        double roundScore = 0;
        if (displayString.equals(decryptCode)) {
            points += CenterPanel.level;
            CenterPanel.currentLevel_XP += 1;
            roundScore = 1.00;
        } else {
            roundScore = calculatedScore(decryptCode, displayString);
            if (roundScore > 0) {
                points += roundScore;
            } else {
                wrongPoints++;
            }
        }
        String pointsText = String.format("Points: %.2f", points);
        displayPoints.setText(pointsText);
        return roundScore;
    }

    private static double calculatedScore(String randomNumber, String guess) {
        double score = 0.0;
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
                if (digit == randDigit) {
                    score += 0.50;
                    matchedPositions.add(i);
                } else if (randomNumber.indexOf(g) != -1 && !matchedPositions.contains(i)) {
                    score += 0.25;
                }
                // odd/even check
                if ((digit % 2 == 0 && randDigit % 2 == 0) || (digit % 2 == 1 && randDigit % 2 == 1)) {
                    score += 0.10;
                }
            }
            if (duplicate) {
                score += 0.05;
            }
            if (prime) {
                score += 0.01;
            }
        }
        return Math.min(score, 1.00);
    }
}
