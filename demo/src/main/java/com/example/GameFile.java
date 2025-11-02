package com.example;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GameFile {
    private CodeConfig codeConfig;
    private VBox listContainer;
    private Integer counter = 1;
    private TextField displayField;
    private String displayString = "";
    private Map<String, ArrayList<String>> historyDict = new HashMap<>();
    private ArrayList<String> subHistoryList = new ArrayList<>();
    private TextField codeField;
    private String codeValue;
    private Double points = 0.0;
    private Label displayPoints;

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
        Long min = 100000L;
        Long max = 999999L;
        Long randomNumber = min + (long) (Math.random() * ((max - min) + 1));
        return CodeConfig.encrypt(randomNumber.toString());
    }

    private ScrollPane HistoryPane() {
        ScrollPane historyPane = new ScrollPane();
        historyPane.setFitToWidth(true);
        historyPane.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5;");
        listContainer = new VBox(8);
        listContainer.setPadding(new Insets(15));
        listContainer.setStyle("-fx-background-color: #f5f5f5;");
        Label historyTitle = new Label("History");
        historyTitle.setFont(App.historyFont);
        historyTitle.setStyle("-fx-text-fill: #333;");
        historyTitle.setPadding(new Insets(0, 0, 10, 0));
        VBox container = new VBox(historyTitle, listContainer);
        container.setStyle("-fx-background-color: #f5f5f5;");
        historyPane.setContent(container);
        historyPane.setMinWidth(250);
        historyPane.prefWidth(250);
        return historyPane;
    }

    // build
    public Parent Build() {
        GridPane displayPanel = MenuFile.menuPane;
        displayPanel = new GridPane();
        displayPanel.setHgap(8);
        displayPanel.setVgap(8);
        displayPanel.setPadding(new Insets(20));
        displayPanel.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");
        // Display points
        displayPoints = new Label();
        displayPoints.setText("Points: " + points.toString());
        displayPoints.setStyle(
                "-fx-text-fill: gray; -fx-font-size: 20px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        displayPanel.add(displayPoints, 0, 0, 3, 1);
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
        displayPanel.add(codeField, 0, 1, 3, 1);
        displayField = new TextField();
        displayField.setEditable(false);
        displayField.setPrefHeight(60);
        displayField.setFont(App.displayFont);
        displayField.setStyle(
                "-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #212529; -fx-padding: 10;");
        displayPanel.add(displayField, 0, 2, 3, 1);
        // Number 1-9 buttons
        for (int y = 3; y < 6; y++) {
            for (int x = 0; x < 3; x++) {
                Button btn = createNumberButton(counter++);
                displayPanel.add(btn, x, y);
            }
        }
        // bottom row
        Button btnClear = createElementButton("C", "Clear");
        btnClear.setStyle(getButtonStyle("#dc3545", "#c82333"));
        displayPanel.add(btnClear, 0, 6);
        Button btn0 = createNumberButton(0);
        displayPanel.add(btn0, 1, 6);
        Button btnBackspace = createElementButton("←", "Backspace");
        btnClear.setStyle(getButtonStyle("#ffc107", "#e0a800"));
        displayPanel.add(btnBackspace, 2, 6);
        // add all elements to center
        VBox displayBox = new VBox(displayPanel);
        displayBox.setAlignment(Pos.CENTER);
        displayBox.setPadding(new Insets(20));
        displayBox.setStyle("-fx-background-color: #e9ecef;");
        HBox mainBox = new HBox(15);
        mainBox.getChildren().addAll(displayBox, HistoryPane());
        mainBox.setStyle("-fx-background-color: #e9ecef;");
        HBox.setHgrow(displayBox, Priority.ALWAYS);
        return mainBox;
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
                        displayString = displayString.substring(0, displayString.length() - 1);
                        subHistoryList.add(displayString);
                        checkCode(displayString);
                    }
                }
                if (argument.equals("Clear")) {
                    if (!displayString.isEmpty()) {
                        LocalDateTime myDateObj = LocalDateTime.now();
                        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String formattedDate = myDateObj.format(myFormatObj);
                        subHistoryList.add(displayString);
                        listContainer.getChildren().add(createHistorySection(formattedDate));
                        historyDict.put(formattedDate, subHistoryList);
                        subHistoryList.clear();
                        checkCode(displayString);
                    }
                    displayString = "";
                }
                displayField.setText(displayString);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        });
        return btn;
    }

    private void checkCode(String displayString) {
        String decriptCode = codeConfig.decrypt(codeValue);
        String[] parts_decriptCode = decriptCode.split(",");
        String[] parts_displayString = displayString.split(",");
        if (displayString.equals(decriptCode)) {
            points += 5;
            codeField.setText(decriptCode);
        } else {
            for (int i = 0; i < parts_displayString.length; i++) {
                for (int j = 0; j < parts_decriptCode.length; j++) {
                    if (parts_displayString[i].equals(parts_decriptCode[i])) {
                        points += 0.1;
                    }
                }
            }
        }
        displayPoints.setText("Points: " + points.toString());
    }

    private VBox createHistorySection(String title) {
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
        removeButton.setOnAction(e -> listContainer.getChildren().remove(section));
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
