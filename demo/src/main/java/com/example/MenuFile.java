package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class MenuFile {
    public static GridPane menuPane;
    public static HBox mainBox;
    public static VBox displayBox;
    private static Label displayLabel;
    private static final String tutorial_PATH = "demo\\src\\main\\java\\assets\\tutorial.txt";
    private ToggleButton toggleLight;
    private static boolean isDarkMode = false;
    // style
    public static String menuPane_color = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ffffff, #f8f9fa); ";
    public static String displayBox_color = "-fx-background-color: linear-gradient(to bottom, #e9ecef, #dee2e6); ";
    public static String mainBox_color = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #667eea, #764ba2); ";

    private void ClearMenuScreen(String customizeTitle) {
        menuPane.getChildren().clear();
        // add label
        displayLabel = new Label(customizeTitle);
        displayLabel.setStyle(
                "-fx-text-fill: gray; -fx-font-size: 40px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        displayLabel.setAlignment(Pos.CENTER);
        displayLabel.setMaxWidth(Double.MAX_VALUE);
        menuPane.add(displayLabel, 0, 0, 2, 1);
    }

    public void BuildMenu() {
        ClearMenuScreen("Welcome");
        Button newGameButton = CustomButton("New Game", "start_game");
        Button settingsButton = CustomButton("Settings", "settings");
        Button tutorialButton = CustomButton("Tutorial", "tutorial");
        Button exitButton = CustomButton("Exit", "exit_game");
        Button[] buttons = { newGameButton, settingsButton, tutorialButton, exitButton };
        for (int i = 0; i < buttons.length; i++) {
            menuPane.add(buttons[i], 0, i + 2, 2, 1);
        }
    }

    public Parent MainMenu() {
        menuPane = new GridPane();
        menuPane.setAlignment(Pos.CENTER);
        menuPane.setHgap(15);
        menuPane.setVgap(12);
        menuPane.setPadding(new Insets(40));
        menuPane.setStyle(menuPane_color + "-fx-background-radius: 10;");
        // display elements
        ClearMenuScreen("Welcome");
        BuildMenu();
        displayBox = new VBox(menuPane);
        displayBox.setAlignment(Pos.CENTER);
        displayBox.setPadding(new Insets(40));
        displayBox.setStyle(displayBox_color);
        mainBox = new HBox(displayBox);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setStyle(mainBox_color);
        HBox.setHgrow(displayBox, Priority.ALWAYS);
        VBox.setVgrow(displayBox, Priority.ALWAYS);
        return mainBox;
    }

    private void BuildSettings() {
        displayLabel.setText("Settings");
        // read jason file
        VBox settingsContent = new VBox(25);
        settingsContent.setPadding(new Insets(10, 0, 20, 0));
        HBox lightSection = new HBox(15);
        lightSection.setAlignment(Pos.CENTER_LEFT);
        lightSection.setPadding(new Insets(15, 20, 15, 20));
        VBox lightLabels = new VBox();
        Label lightLabel = new Label("Light Mode");
        lightLabel.setStyle("-fx-text-fill: #7b7e81ff; -fx-font-size: 14px;");
        Label lightDesc = new Label("Toggle between light and dark theme");
        lightDesc.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 14px;");
        lightLabels.getChildren().addAll(lightLabel, lightDesc);
        toggleLight = new ToggleButton();
        toggleLight.setPrefWidth(80);
        toggleLight.setPrefHeight(40);
        toggleLight.setStyle(
                "-fx-background-color: gray; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        // Set initial state
        if (isDarkMode) {
            toggleLight.setText("ON");
            toggleLight.setStyle(
                    "-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        } else {
            toggleLight.setText("OFF");
            toggleLight.setStyle(
                    "-fx-background-color: gray; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        }
        toggleLight.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            updateTheme();
            applyTheme();
            if (isDarkMode) {
                toggleLight.setText("ON");
                toggleLight.setStyle(
                        "-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
            } else {
                toggleLight.setText("OFF");
                toggleLight.setStyle(
                        "-fx-background-color: gray; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
            }
        });
        lightSection.getChildren().addAll(lightLabels, toggleLight);
        settingsContent.getChildren().addAll(lightSection);
        menuPane.add(settingsContent, 0, 2, 2, 1);
        Button backButton = CustomButton("Main Menu", "main_menu");
        menuPane.add(backButton, 0, 3, 2, 1);
    }

    private void BuildTutorial() {
        displayLabel.setText("Tutorial");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox tutorialList = new VBox(12);
        tutorialList.setPadding(new Insets(10, 0, 20, 0));
        ReadTXT readTXT = new ReadTXT();
        ArrayList<String> get_names = readTXT.getTextFromFile(tutorial_PATH, "get_name");
        ArrayList<String> get_description = readTXT.getTextFromFile(tutorial_PATH, "get_description");
        for (int i = 0; i < get_names.size(); i++) {
            VBox tutorialSegment = createTutorialCard(get_names.get(i), get_description.get(i));
            tutorialList.getChildren().add(tutorialSegment);
        }
        scrollPane.setContent(tutorialList);
        menuPane.add(scrollPane, 0, 2, 2, 1);
        // read jason file
        Button backButton = CustomButton("Main Menu", "main_menu");
        menuPane.add(backButton, 0, 1);
    }

    private VBox createTutorialCard(String Title, String description) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 4, 0, 0, 2);");
        Label titleLabel = new Label(Title);
        titleLabel.setPadding(new Insets(1, 20, 15, 20));
        titleLabel.setStyle(
                "-fx-text-fill: #495057; -fx-font-size: 20px; -fx-font-weight: bold;");
        WrappedTextBox wrappedDescription = new WrappedTextBox(description, 280);
        wrappedDescription.setStyle("-fx-padding: 0;");
        card.widthProperty().addListener((obs, oldVal, newVal) -> {
            wrappedDescription.setWidth(newVal.doubleValue() - 40); // subtract padding
        });
        card.getChildren().addAll(titleLabel, wrappedDescription);
        return card;
    }

    private class WrappedTextBox extends Region {
        private Text wrappedText;

        private WrappedTextBox(String content, double width) {
            wrappedText = new Text(content);
            wrappedText.setWrappingWidth(width);
            wrappedText.setFill(Color.web("#6c757d")); // match your card style
            wrappedText.setFont(Font.font("Arial", 16));
            wrappedText.setTextAlignment(TextAlignment.JUSTIFY);
            getChildren().add(wrappedText);
        }

        private void setContent(String content) {
            wrappedText.setText(content);
        }

        public void setWidth(double width) {
            wrappedText.setWrappingWidth(width);
        }
    }

    private Button CustomButton(String text, String argument) {
        Button customButton = new Button(text);
        customButton.setPrefHeight(65);
        customButton.setPrefWidth(200);
        customButton.setPadding(new Insets(0, 20, 0, 30));
        String baseStyle = "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(102, 126, 234, 0.4), 8, 0, 0, 4);";
        String hoverStyle = "-fx-background-color: linear-gradient(to right, #764ba2, #667eea); -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 12; -fx-border-radius: 12; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(118, 75, 162, 0.6), 12, 0, 0, 6);";

        customButton.setStyle(baseStyle);
        customButton.setOnMouseEntered(e -> {
            customButton.setStyle(hoverStyle);
            AnimationUtils.scaleButton(customButton);
        });
        customButton.setOnMouseExited(e -> customButton.setStyle(baseStyle));
        customButton.setOnAction(e -> {
            ClearMenuScreen("");
            if (argument.equals("start_game")) {
                Integer playerLevel = 1;
                Map<String, ArrayList<String>> levelDict = new HashMap<>();
                ArrayList<String> subLevelList = new ArrayList<>();
                GameFile centerPanel = new GameFile(playerLevel, levelDict, subLevelList);
                centerPanel.BuildCenter(menuPane);
            }
            if (argument.equals("settings")) {
                BuildSettings();
            }
            if (argument.equals("tutorial")) {
                BuildTutorial();
            }
            if (argument.equals("exit_game")) {
                AnimationUtils.fadein(App.primaryScene.getRoot(), 500);
                App.stage.close();
            }
            if (argument.equals("main_menu")) {
                BuildMenu();
            }
        });
        AnimationUtils.fadein(customButton, 400);
        customButton.setOnMouseEntered(e -> {
            AnimationUtils.pulse(customButton, 200);
        });
        return customButton;
    }

    private static void updateTheme() {
        if (isDarkMode) {
            // Dark theme
            menuPane_color = "-fx-background-color: linear-gradient(to bottom right, #2c3e50, #34495e); ";
            displayBox_color = "-fx-background-color: linear-gradient(to bottom, #34495e, #2c3e50); ";
            mainBox_color = "-fx-background-color: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); ";
        } else {
            // Light theme
            menuPane_color = "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f8f9fa); ";
            displayBox_color = "-fx-background-color: linear-gradient(to bottom, #e9ecef, #dee2e6); ";
            mainBox_color = "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%); ";
        }
    }

    // Method to apply theme to current scene
    private static void applyTheme() {
        if (MenuFile.mainBox != null) {
            MenuFile.mainBox.setStyle(mainBox_color);
            // Animate the theme change
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), MenuFile.mainBox);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0.7);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), MenuFile.mainBox);
            fadeIn.setFromValue(0.7);
            fadeIn.setToValue(1);
            SequentialTransition seq = new SequentialTransition(fadeOut, fadeIn);
            seq.play();
        }
        if (MenuFile.displayBox != null) {
            MenuFile.displayBox.setStyle(displayBox_color);
        }
        if (MenuFile.menuPane != null) {
            String menuStyle = menuPane_color + "-fx-background-radius: 10;";
            MenuFile.menuPane.setStyle(menuStyle);
        }
        // Update side panels if they exist
        updateSidePanelTheme();
    }

    private static void updateSidePanelTheme() {
        if (isDarkMode) {
            // Dark theme
            App.historyPane_color = "-fx-background: rgba(52, 73, 94, 0.9); -fx-background-color: linear-gradient(135deg, rgba(44,62,80,0.9), rgba(52,73,94,0.9)); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);";
            App.levelPane_color = "-fx-background: rgba(52, 73, 94, 0.9); -fx-background-color: linear-gradient(135deg, rgba(44,62,80,0.9), rgba(52,73,94,0.9)); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);";
        } else {
            // Light theme
            App.historyPane_color = "-fx-background: rgba(255, 255, 255, 0.1); -fx-background-color: linear-gradient(135deg, rgba(255,255,255,0.9), rgba(248,249,250,0.9)); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";
            App.levelPane_color = "-fx-background: rgba(255, 255, 255, 0.1); -fx-background-color: linear-gradient(135deg, rgba(255,255,255,0.9), rgba(248,249,250,0.9)); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";
        }
    }
}
