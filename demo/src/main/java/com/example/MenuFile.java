package com.example;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
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

public class MenuFile {
    public static GridPane menuPane;
    public static HBox mainBox;
    public static VBox displayBox;
    private static Label displayLabel;
    private static final String saveFile_PATH = "demo\\src\\main\\java\\assets\\saveFile.json";
    private static final String tutorial_PATH = "demo\\src\\main\\java\\assets\\tutorial.txt";
    private ToggleButton toggleLight;

    public void ClearMenuScreen(String customizeTitle) {
        menuPane.getChildren().clear();
        if (CenterPanel.buildGameFile) {
            mainBox.getChildren().remove(SidePanels.historyPane);
            mainBox.getChildren().remove(SidePanels.levelPane);
        }
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
        Button loadGameButton = CustomButton("Load Game", "load_game");
        Button settingsButton = CustomButton("Settings", "settings");
        Button tutorialButton = CustomButton("Tutorial", "tutorial");
        Button exitButton = CustomButton("Exit", "exit_game");
        Button[] buttons = { newGameButton, loadGameButton, settingsButton, tutorialButton, exitButton };
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
        menuPane.setStyle(App.menuPane_color + "-fx-background-radius: 10;");
        // display elements
        ClearMenuScreen("Welcome");
        BuildMenu();
        displayBox = new VBox(menuPane);
        displayBox.setAlignment(Pos.CENTER);
        displayBox.setPadding(new Insets(40));
        displayBox.setStyle(App.displayBox_color);
        mainBox = new HBox(displayBox);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setStyle(App.mainBox_color);
        HBox.setHgrow(displayBox, Priority.ALWAYS);
        VBox.setVgrow(displayBox, Priority.ALWAYS);
        return mainBox;
    }

    private void BuildLoadGame() {
        displayLabel.setText("Load Game");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox savesList = new VBox(12);
        savesList.setPadding(new Insets(10, 0, 20, 0));
        int saveCount = 0;
        // read jason file
        try {
            ReadJSON readJSON = new ReadJSON();
            String stringData = readJSON.getJSONFromFile(saveFile_PATH);
            JSONParser parser = new JSONParser();
            Object object = parser.parse(stringData);
            JSONArray jsonArray = (JSONArray) object;
            for (Object item : jsonArray) {
                JSONObject entry = (JSONObject) item;
                String timeWritten = (String) entry.get("Time_Written");
                VBox saveCard = createSaveCard(timeWritten, saveCount++);
                savesList.getChildren().add(saveCard);
            }
            if (jsonArray.isEmpty()) {
                Label noSavesLabel = new Label("So saves found");
                savesList.getChildren().add(noSavesLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error loading save files");
            savesList.getChildren().add(errorLabel);
        }
        scrollPane.setContent(savesList);
        menuPane.add(scrollPane, 0, 2, 2, 1);
        Button backButton = CustomButton("Main Menu", "main_menu");
        menuPane.add(backButton, 0, 3, 2, 1);
    }

    private VBox createSaveCard(String timeWritten, int index) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 4, 0, 0, 2);");
        Label saveLabel = new Label("Save Slot " + (index + 1));
        saveLabel.setStyle(
                "-fx-text-fill: #495057; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label timeLabel = new Label(timeWritten);
        timeLabel.setStyle(
                "-fx-text-fill: #6c757d; -fx-font-size: 16px;");
        card.getChildren().addAll(saveLabel, timeLabel);
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #667eea; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(102, 126, 234, 0.3), 8, 0, 0, 4);"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-border-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 4, 0, 0, 2);"));
        card.setOnMouseClicked(e -> {
            // Load game logic here
            System.out.println("Loading save: " + timeWritten);
        });
        return card;
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
        if (App.isDarkMode) {
            toggleLight.setText("ON");
            toggleLight.setStyle(
                    "-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        } else {
            toggleLight.setText("OFF");
            toggleLight.setStyle(
                    "-fx-background-color: gray; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        }
        toggleLight.setOnAction(e -> {
            App.isDarkMode = !App.isDarkMode;
            App.updateTheme();
            App.applyTheme();
            if (App.isDarkMode) {
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

    public class WrappedTextBox extends Region {
        public Text wrappedText;

        public WrappedTextBox(String content, double width) {
            wrappedText = new Text(content);
            wrappedText.setWrappingWidth(width);
            wrappedText.setFill(Color.web("#6c757d")); // match your card style
            wrappedText.setFont(Font.font("Arial", 16));
            wrappedText.setTextAlignment(TextAlignment.JUSTIFY);
            getChildren().add(wrappedText);
        }

        public void setContent(String content) {
            wrappedText.setText(content);
        }

        public void setWidth(double width) {
            wrappedText.setWrappingWidth(width);
        }
    }

    public Button CustomButton(String text, String argument) {
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
                CenterPanel centerPanel = new CenterPanel(0, 1, 1, 0, 0);
                centerPanel.BuildCenter(menuPane);
            }
            if (argument.equals("load_game")) {
                BuildLoadGame();
            }
            if (argument.equals("settings")) {
                BuildSettings();
            }
            if (argument.equals("tutorial")) {
                BuildTutorial();
            }
            if (argument.equals("exit_game")) {
                AnimationUtils.fadein(App.scene.getRoot(), 500);
                App.main_stage.close();
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
}
