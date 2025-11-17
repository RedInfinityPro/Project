package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SubLevel {
    /// point system
    private static Double points = 0.0;
    public static boolean correct = false;
    private double sessionScore = 0.0;
    private static boolean hasCorrectAppeared = false;
    // visual elements
    private Integer counter = 1;
    private String displayString = "";
    private String codeValue;
    // display
    private CodeConfig codeConfig;
    private TextField codeField;
    private TextField displayField;
    private static Label displayPoints;
    private GridPane gamePane;
    private Button continueButton;
    // short cuts
    private final Set<KeyCode> activeKeys = new HashSet<>();

    SubLevel() {
        Reset();
    }

    private void CodeConfig_init() {
        try {
            codeConfig.initialize("demo\\src\\main\\java\\assets\\input.txt");
            codeConfig = CodeConfig.getInstance();
            System.out.println("configuration initialized successfully");
        } catch (IllegalStateException e) {
            System.out.println("already initialized: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateCode() {
        CodeConfig_init();
        Long min = 0L;
        Long max = (long) Math.pow(10, LevelFile.getPlayerLevel());
        Long randomNumber = min + (long) (Math.random() * ((max - min) + 1));
        return CodeConfig.encrypt(randomNumber.toString());
    }

    public void BuildCenter(GridPane gamePane) {
        // Display points
        VBox topDisplay = new VBox(8);
        displayPoints = new Label();
        String pointsText = String.format("Points: %.2f", SubLevel.points);
        displayPoints.setText(pointsText);
        GameFile.displayLevel = new Label();
        GameFile.displayLevel.setText("Level: " + LevelFile.getPlayerLevel().toString());
        topDisplay.getChildren().addAll(GameFile.displayLevel, SubLevel.displayPoints);
        gamePane.add(topDisplay, 0, 0, 3, 1);
        GameFile.buildGameInterface(gamePane);
        AnimationUtils.fadein(gamePane, 800);
    }

    private void Reset() {
        points = 0.0;
        counter = 1;
        displayString = "";
        correct = false;
        hasCorrectAppeared = false;
        sessionScore = 0.0;
        HistoryFile.clearAll();
        // UI elements
        if (displayPoints != null) {
            displayPoints.setText(String.format("Points: %.2f", points));
        }
    }

    public void buildNormalLevel(GridPane gamePane) {
        // TextField
        codeField = new TextField();
        codeValue = generateCode();
        codeField.setText(codeValue);
        codeField.setEditable(false);
        codeField.setPrefHeight(60);
        codeField.setFont(App.header_Font);
        codeField.setPadding(new Insets(5));
        gamePane.add(codeField, 0, 1, 3, 1);
        displayField = new TextField();
        displayField.setEditable(false);
        displayField.setPrefHeight(60);
        displayField.setFont(App.header_Font);
        gamePane.add(displayField, 0, 2, 3, 1);
        // Number 1-9 buttons
        for (int y = 5; y >= 3; y--) {
            for (int x = 0; x < 3; x++) {
                Button btn = createNumberButton(counter++);
                gamePane.add(btn, x, y);
            }
        }
        // bottom row
        Button btnClear = createElementButton("Clear", "Clear");
        gamePane.add(btnClear, 0, 6);
        Button btn0 = createNumberButton(0);
        gamePane.add(btn0, 1, 6);
        Button btnBackspace = createElementButton("Back", "Backspace");
        gamePane.add(btnBackspace, 2, 6);
        // add button
        continueButton = new Button("Continue");
        continueButton.setOnAction(e -> {
            ContinueFunction();
        });
        gamePane.add(continueButton, 0, 7, 3, 1);
        gamePane.setOnKeyPressed((KeyEvent event) -> {
            KeyCode code = event.getCode();
            if (code.isDigitKey()) {
                String digit = code.getName();
                displayString += digit;
                displayField.setText(displayString);
            }
            if (code == KeyCode.BACK_SPACE) {
                BackspaceFunction();
            }
            if (code == KeyCode.ENTER) {
                ContinueFunction();
            }
            activeKeys.add(event.getCode());
            handleMultiKeyInput();
            displayField.setText(displayString);
        });
        gamePane.setOnKeyReleased(event -> {
            activeKeys.remove(event.getCode());
        });
        Platform.runLater(() -> {
            continueButton.setDisable(correct);
            gamePane.requestFocus();
        });
        AnimationUtils.fadein(gamePane, 800);
    }

    private void handleMultiKeyInput() {
        if (activeKeys.contains(KeyCode.CONTROL) && activeKeys.contains(KeyCode.BACK_SPACE)) { // control + backspace
            ClearFunction();
        }
        if (activeKeys.contains(KeyCode.CONTROL) && activeKeys.contains(KeyCode.DELETE)) { // control + delete
            HistoryFile.clearAll();
        }
    }

    private Button createNumberButton(Integer number) {
        Button btn = new Button(number.toString());
        btn.setPrefWidth(70);
        btn.setPrefHeight(70);
        btn.setOnAction(e -> {
            AnimationUtils.pulse(btn, 200);
            displayString += number.toString();
            displayField.setText(displayString);
        });
        AnimationUtils.fadein(btn, 400);
        return btn;
    }

    private Button createElementButton(String text, String argument) {
        Button btn = new Button(text);
        btn.setPrefWidth(70);
        btn.setPrefHeight(70);
        btn.setOnAction(e -> {
            createElementButton_Function(btn, argument);
        });
        AnimationUtils.fadein(btn, 400);
        return btn;
    }

    private void createElementButton_Function(Button btn, String argument) {
        AnimationUtils.pulse(btn, 200);
        try {
            if (argument.equals("Backspace")) {
                BackspaceFunction();
            }
            if (argument.equals("Clear")) {
                ClearFunction();
            }
            displayField.setText(displayString);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void BackspaceFunction() {
        // Add attempt to history
        if (!displayString.isEmpty()) {
            String decryptCode = codeConfig.decrypt(codeValue);
            ScoreFile result = CalculatedScoreDetailed(decryptCode, displayString);
            // Create hint list from result
            List<HintData> hintList = new ArrayList<>();
            for (Map.Entry<String, Integer> threshold : result.thresholdCounts.entrySet()) {
                String message = threshold.getKey();
                Integer count = threshold.getValue();
                if (count > 0) {
                    if (message.equals("Correct")) {
                        hasCorrectAppeared = true;
                    }
                    HintData hintData = HistoryFile.CreateHistoryItem.ButtonLogic(message, count);
                    if (hintData != null) {
                        hintList.add(hintData);
                    }
                }
            }
            // Add to history session
            HistoryFile.add_GuessAttempt(displayString, hintList, result.totalScore);
            sessionScore += result.totalScore;
            // Clear one character
            displayString = displayString.substring(0, displayString.length() - 1);
        }
    }

    private void ClearFunction() {
        // Check answer and finalize session
        ScoreFile result = CheckCodeDetailed(displayString);
        // Create hint list for final attempt
        List<HintData> hintList = new ArrayList<>();
        for (Map.Entry<String, Integer> threshold : result.thresholdCounts.entrySet()) {
            String message = threshold.getKey();
            Integer count = threshold.getValue();
            if (count > 0) {
                if (message.equals("Correct")) {
                    hasCorrectAppeared = true;
                }
                HintData hintData = HistoryFile.CreateHistoryItem.ButtonLogic(message, count);
                if (hintData != null) {
                    hintList.add(hintData);
                }
            }
        }
        // Add final attempt
        HistoryFile.add_GuessAttempt(displayString, hintList, result.totalScore);
        sessionScore += result.totalScore;
        // Finalize and show in history pane
        HistoryFile.finalizeSession();
        // clear
        sessionScore = 0.0;
        displayString = "";
    }

    private void ContinueFunction() {
        if (correct) {
            try {
                LevelFile.AdvanceLocation();
                Reset();
                GameFile.buildGameInterface(MenuFile.menuPane);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ScoreFile CheckCodeDetailed(String displayString) {
        String decryptCode = codeConfig.decrypt(codeValue);
        System.out.println("" + decryptCode + " " + displayString);
        ScoreFile result = CalculatedScoreDetailed(displayString, decryptCode);
        points += result.totalScore;
        // UI
        String pointsText = String.format("Points: %.2f", points);
        displayPoints.setText(pointsText);
        return result;
    }

    private static ScoreFile CalculatedScoreDetailed(String randomNumber, String userGuess) {
        ScoreFile result = new ScoreFile();
        double score = 0.0;
        ScoreFile.primeDigit_list = Set.of(2, 3, 5, 7);
        int len = Math.min(randomNumber.length(), userGuess.length());
        boolean[] matchedExact = new boolean[len];
        boolean[] matchedExists = new boolean[randomNumber.length()];
        Set<Character> existsAwarded = new HashSet<>();
        Set<Character> repeatAwarded = new HashSet<>();
        if (!correct) {
            if (randomNumber.equals(userGuess) && !hasCorrectAppeared) {
                result.totalScore = 1.0 * LevelFile.getPlayerLevel();
                result.addThreshold("Correct", 1);
                correct = true;
                hasCorrectAppeared = true;
            } else {
                // Count digit frequencies in random number
                Map<Character, Integer> randomFreq = new HashMap<>();
                for (char c : randomNumber.toCharArray()) {
                    randomFreq.put(c, randomFreq.getOrDefault(c, 0) + 1);
                }
                // Count digit frequencies in user guess
                Map<Character, Integer> guessFreq = new HashMap<>();
                for (char c : userGuess.toCharArray()) {
                    guessFreq.put(c, guessFreq.getOrDefault(c, 0) + 1);
                }
                // Prime check
                for (int i = 0; i < len; i++) {
                    if (matchedExact[i] || matchedExists[i])
                        continue;
                    int userDigit = Character.getNumericValue(userGuess.charAt(i));
                    int randomDigit = Character.getNumericValue(randomNumber.charAt(i));
                    if (ScoreFile.isPrime(userDigit) == ScoreFile.isPrime(randomDigit)) {
                        score += 0.01;
                        result.addThreshold("Prime_Exists", 1);
                    }
                }
                // Repeating digit match
                for (char c : guessFreq.keySet()) {
                    if (randomFreq.getOrDefault(c, 0) > 1) {
                        if (!repeatAwarded.contains(c)) {
                            score += 0.05;
                            repeatAwarded.add(c);
                            result.addThreshold("Duplicate_Exists", 1);
                        }
                    }
                }
                // Odd/Even match in same position
                for (int i = 0; i < len; i++) {
                    if (matchedExact[i] || matchedExists[i])
                        continue;
                    int userDigit = Character.getNumericValue(userGuess.charAt(i));
                    int randomDigit = Character.getNumericValue(randomNumber.charAt(i));
                    if ((userDigit % 2) == (randomDigit % 2)) {
                        score += 0.10;
                        result.addThreshold("Property_Exists", 1);
                    }
                }
                // Check for presence matches (+0.25)
                for (int i = 0; i < len; i++) {
                    if (matchedExact[i])
                        continue;
                    char uc = userGuess.charAt(i);
                    if (randomNumber.indexOf(uc) != -1 && !existsAwarded.contains(uc)) {
                        score += 0.25;
                        matchedExists[i] = true;
                        existsAwarded.add(uc);
                        result.addThreshold("Exists", 1);
                    }
                }
                // Check for exact matches
                for (int i = 0; i < len; i++) {
                    if (userGuess.charAt(i) == randomNumber.charAt(i)) {
                        score += 0.50;
                        matchedExact[i] = true;
                        matchedExists[i] = true;
                        existsAwarded.add(userGuess.charAt(i));
                        result.addThreshold("Exists_Exact", 1);
                    }
                }
            }
        }
        if (!correct && score <= 0) {
            GameFile.wrongPoints++;
            result.addThreshold("None", 1);
        }
        if (correct) {
            score = 0.0;
            result.addThreshold("Correct_Exists", 1);
        }
        result.totalScore = Math.min(score, 1.00);
        return result;
    }
}