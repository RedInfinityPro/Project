package com.example;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameFile {
    private CodeConfig codeConfig;
    private VBox historyPane_listContainer;
    private VBox levelPane_listContainer;
    private Integer counter = 1;
    private TextField displayField;
    private String displayString = "";
    public static ScrollPane historyPane;
    public static ScrollPane levelPane;
    private Map<String, ArrayList<String>> historyDict = new HashMap<>();
    private ArrayList<String> subHistoryList = new ArrayList<>();
    private Map<String, ArrayList<String>> levelDict = new HashMap<>();
    private ArrayList<String> subLevelList = new ArrayList<>();
    private TextField codeField;
    private Label displayPoints;
    private String codeValue;
    private Double points = 0.0;
    private Integer wrongPoints = 0;
    private Integer userLevel = 1;
    private Integer userLevel_XP = 0;
    private boolean guessed = false;

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
        Long max = 9L * userLevel;
        Long randomNumber = min + (long) (Math.random() * ((max - min) + 1));
        return CodeConfig.encrypt(randomNumber.toString());
    }

    private ScrollPane HistoryPane() {
        historyPane = new ScrollPane();
        historyPane.setFitToWidth(true);
        historyPane.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5; -fx-background-radius: 10;");
        historyPane_listContainer = new VBox(8);
        historyPane_listContainer.setPadding(new Insets(15));
        historyPane_listContainer.setStyle("-fx-background-color: #f5f5f5;");
        Label historyTitle = new Label("History");
        historyTitle.setFont(App.historyFont);
        historyTitle.setStyle("-fx-text-fill: #333;");
        historyTitle.setPadding(new Insets(0, 0, 10, 0));
        VBox container = new VBox(historyTitle, historyPane_listContainer);
        container.setStyle("-fx-background-color: #f5f5f5;");
        historyPane.setContent(container);
        historyPane.setMinWidth(250);
        historyPane.prefWidth(250);
        return historyPane;
    }

    private ScrollPane LevelPane() {
        levelPane = new ScrollPane();
        levelPane.setFitToWidth(true);
        levelPane.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5; -fx-background-radius: 10;");
        VBox topSection = new VBox(8);
        topSection.setStyle("-fx-background: #f5f5f5;");
        Label levelTitle = new Label("Levels");
        levelTitle.setFont(App.historyFont);
        levelTitle.setStyle("-fx-text-fill: #333;");
        levelTitle.setPadding(new Insets(0, 0, 10, 0));
        levelPane_listContainer = new VBox(8);
        levelPane_listContainer.setPadding(new Insets(15));
        levelPane_listContainer.setStyle("-fx-background-color: #f5f5f5;");
        topSection.getChildren().addAll(levelTitle, levelPane_listContainer);
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
        BorderPane container = new BorderPane();
        container.setStyle("-fx-background-color: #f5f5f5;");
        container.setTop(topSection);
        container.setBottom(buttonWrapper);
        levelPane.setContent(container);
        levelPane.setMinWidth(250);
        levelPane.prefWidth(250);
        levelPane.setMaxWidth(250);
        return levelPane;
    }

    // build
    public void BuildGame(GridPane menuPane) {
        // Display points
        displayPoints = new Label();
        displayPoints.setText("Points: " + points.toString());
        displayPoints.setStyle(
                "-fx-text-fill: gray; -fx-font-size: 20px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        menuPane.add(displayPoints, 0, 0, 3, 1);
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
        // remove items
        MenuFile.mainBox.getChildren().clear();
        // add level and history
        MenuFile.displayBox = new VBox(menuPane);
        MenuFile.displayBox.setAlignment(Pos.CENTER);
        MenuFile.displayBox.setPadding(new Insets(40));
        MenuFile.displayBox.setStyle("-fx-background-color: #e9ecef;");
        HBox.setHgrow(MenuFile.displayBox, Priority.ALWAYS);
        VBox.setVgrow(MenuFile.displayBox, Priority.ALWAYS);
        MenuFile.mainBox.getChildren().addAll(LevelPane(), MenuFile.displayBox, HistoryPane());
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
                    displayString = displayString.substring(0, displayString.length() - 1);
                    subHistoryList.add(displayString);
                }
                if (argument.equals("Clear")) {
                    LocalDateTime myDateObj = LocalDateTime.now();
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    String formattedDate = myDateObj.format(myFormatObj);
                    subHistoryList.add(displayString);
                    double checkScore = checkCode(displayString);
                    historyPane_listContainer.getChildren().add(createHistorySection(formattedDate, checkScore));
                    historyDict.put(formattedDate, subHistoryList);
                    subHistoryList.clear();
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
        double roundScore = 0;
        if (displayString.equals(decryptCode)) {
            points += userLevel;
            userLevel_XP += 1;
            codeValue = generateCode();
            codeField.setText(codeValue);
            roundScore = 1.00;
        } else {
            roundScore = calculatedScore(decryptCode, displayString);
            if (roundScore > 0) {
                points += roundScore;
            } else {
                wrongPoints++;
            }
        }
        displayPoints.setText("Points: " + points);
        return roundScore;
    }

    private static double calculatedScore(String randomNumber, String guess) {
        double score = 0.0;
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
                } else if (randomNumber.indexOf(g) != -1) {
                    score += 0.25;
                }
                // odd even check
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
        return score;
    }

    private VBox createHistorySection(String title, Double checkScore) {
        VBox section = new VBox();
        section.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 3, 0, 0, 1);");
        HBox header = new HBox(10);
        header.setPadding(new Insets(12));
        header.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;");
        Label arrowLabel = new Label("v");
        arrowLabel.setFont(App.arrowLabelFont);
        arrowLabel.setStyle("-fx-text-fill: #6c757d");
        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-cursor: hand;");
        removeButton.setOnAction(e -> historyPane_listContainer.getChildren().remove(section));
        removeButton.setOnMouseEntered(e -> removeButton
                .setStyle("-fx-background-color: darkred; -fx-text-fill: white; -fx-font-weight: bold;"));
        removeButton.setOnMouseExited(e -> removeButton
                .setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-weight: normal;"));
        Label titleLabel = new Label(title.isEmpty() ? "(no title)" : title);
        titleLabel.setFont(App.arrowLabelFont);
        titleLabel.setStyle("-fx-text-fill: #212529;");
        header.getChildren().addAll(arrowLabel, titleLabel, removeButton);
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
        ArrayList<String> items = historyDict.getOrDefault(title, new ArrayList<>(subHistoryList));
        if (items.isEmpty()) {
            Label emptyLabel = new Label("No history items");
            emptyLabel.setFont(App.labelFont);
            emptyLabel.setStyle("-fx-text-fill: #adb5bd; -fx-font-style: italic;");
            subList.getChildren().add(emptyLabel);
        } else {
            for (String item : items) {
                HBox itemEntry = new HBox(10);
                itemEntry.setStyle("-fx-padding: 4 0 4 10;");
                Label itemLabel = new Label("• " + item);
                itemLabel.setFont(App.labelFont);
                itemLabel.setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10;");
                Button pointsEaredType = new Button();
                pointsEaredType.setText("?");
                String[] buttonString = ButtonLogic(checkScore);
                pointsEaredType.setStyle(buttonString[0]);
                Tooltip hint = new Tooltip();
                hint.setText(buttonString[1].toString());
                pointsEaredType.setTooltip(hint);
                itemEntry.setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10;");
                itemEntry.setOnMouseEntered(e -> itemEntry
                        .setStyle("-fx-background-color: #e9ecef; -fx-padding: 4 0 4 10; -fx-cursor: hand;"));
                itemEntry.setOnMouseExited(e -> itemEntry
                        .setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10; -fx-cursor: hand;"));
                itemEntry.getChildren().addAll(itemLabel, pointsEaredType);
                subList.getChildren().add(itemEntry);
            }
        }
        header.setOnMouseClicked(e -> toggleSection(section, arrowLabel));
        section.getChildren().addAll(header, subList);
        section.setUserData(subList);
        return section;
    }

    private String[] ButtonLogic(Double checkScore) {
        String returnColor;
        String returnHint;
        if (checkScore == 1.00) {
            returnColor = "#228B22";
            returnHint = "You guessed the correct number!";
        } else if (checkScore == 0.50) {
            returnColor = "#008000";
            returnHint = "A digit was found in the correct location and correct value.";
        } else if (checkScore == 0.25) {
            returnColor = "#006400";
            returnHint = "A digit was found in the number.";
        } else if (checkScore == 0.10) {
            returnColor = "#ADFF2F";
            returnHint = "A digit in your guess matches the property of another digit.";
        } else if (checkScore == 0.05) {
            returnColor = "#7FFF00";
            returnHint = "A digit appears more than once in the target number.";
        } else if (checkScore == 0.01) {
            returnColor = "#7CFC00";
            returnHint = "If a digit is prime.";
        } else {
            returnColor = "#A9A9A9";
            returnHint = "Sorry, no points where applied.";
        }
        String[] returnString = {"-fx-background-color: " + returnColor + ";", returnHint};
        return returnString;
    }

    private VBox createLevelSection(String title) {
        VBox section = new VBox();
        section.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 3, 0, 0, 1);");
        HBox header = new HBox(10);
        header.setPadding(new Insets(12));
        header.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8 8 0 0; -fx-cursor: hand;");
        Label arrowLabel = new Label("v");
        arrowLabel.setFont(App.arrowLabelFont);
        arrowLabel.setStyle("-fx-text-fill: #6c757d");
        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-cursor: hand;");
        removeButton.setOnAction(e -> historyPane_listContainer.getChildren().remove(section));
        removeButton.setOnMouseEntered(e -> removeButton
                .setStyle("-fx-background-color: darkred; -fx-text-fill: white; -fx-font-weight: bold;"));
        removeButton.setOnMouseExited(e -> removeButton
                .setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-font-weight: normal;"));
        Label titleLabel = new Label(title.isEmpty() ? "(no title)" : title);
        titleLabel.setFont(App.arrowLabelFont);
        titleLabel.setStyle("-fx-text-fill: #212529;");
        header.getChildren().addAll(arrowLabel, titleLabel, removeButton);
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
        ArrayList<String> items = historyDict.getOrDefault(title, new ArrayList<>(subHistoryList));
        if (items.isEmpty()) {
            Label emptyLabel = new Label("No history items");
            emptyLabel.setFont(App.labelFont);
            emptyLabel.setStyle("-fx-text-fill: #adb5bd; -fx-font-style: italic;");
            subList.getChildren().add(emptyLabel);
        } else {
            for (String item : items) {
                Label itemLabel = new Label("• " + item);
                itemLabel.setFont(App.labelFont);
                itemLabel.setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10;");
                itemLabel.setOnMouseEntered(e -> itemLabel
                        .setStyle("-fx-background-color: #e9ecef; -fx-padding: 4 0 4 10; -fx-cursor: hand;"));
                itemLabel.setOnMouseExited(e -> itemLabel
                        .setStyle("-fx-text-fill: #495057; -fx-padding: 4 0 4 10; -fx-cursor: hand;"));
                subList.getChildren().add(itemLabel);
            }
        }
        header.setOnMouseClicked(e -> toggleSection(section, arrowLabel));
        section.getChildren().addAll(header, subList);
        section.setUserData(subList);
        return section;
    }

    private void toggleSection(VBox section, Label arrowLabel) {
        VBox subList = (VBox) section.getUserData();
        boolean isVisible = subList.isVisible();
        subList.setVisible(!isVisible);
        arrowLabel.setText(isVisible ? "^" : "v");
    }
}