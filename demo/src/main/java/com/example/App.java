package com.example;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene;
    private VBox listContainer;
    private Integer counter = 1;
    private TextField displayField;
    private String displayString = "";
    private Map<String, ArrayList<String>> historyDict = new HashMap<>();
    private String historyName = new String();
    private ArrayList<String> subHistoryList = new ArrayList<>();

    private ScrollPane HistoryPane() {
        ScrollPane historyPane = new ScrollPane();
        listContainer = new VBox(10);
        listContainer.setPadding(new Insets(10));
        historyPane.setContent(listContainer);
        return historyPane;
    }
 
    // build
    private Parent Build() {
        GridPane panel = new GridPane();
        panel.setHgap(10);
        panel.setVgap(10);
        panel.add(HistoryPane(), 4, 0, 1, 6);
        // show buttons
        displayField = new TextField();
        displayField.setEditable(false);
        panel.add(displayField, 0, 0, 3, 1);
        for (int y = 1; y < 4; y++) {
            for (int x = 0; x < 3; x++) {
                Button btn = createNumberButton(counter++);
                panel.add(btn, x, y);
            }
        }
        Button btnBackspace = createElementButton("←", "Backspace");
        panel.add(btnBackspace, 2, 4);
        Button btnClear = createElementButton("C", "Clear");
        panel.add(btnClear, 0, 4);
        Button btn0 = createNumberButton(0);
        panel.add(btn0, 1, 4);
        // add all elements to center
        VBox vbox = new VBox(panel);
        vbox.setAlignment(Pos.CENTER);
        HBox hbox = new HBox(vbox);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public Button createNumberButton(Integer number) {
        Button btn = new Button(number.toString());
        btn.setPrefWidth(50);
        btn.setPrefHeight(50);
        btn.setOnAction(e -> {
            displayString += number.toString();
            displayField.setText(displayString);
        });
        return btn;
    }

    public Button createElementButton(String text, String argument) {
        Button btn = new Button(text);
        btn.setPrefWidth(50);
        btn.setPrefHeight(50);
        btn.setOnAction(e -> {
            try {
                if (argument.equals("Backspace")) {
                    if (!displayString.isEmpty()) {
                        displayString = displayString.substring(0, displayString.length() - 1);
                        subHistoryList.add(displayString);
                    } else {
                        subHistoryList.clear();
                        listContainer.getChildren().add(createHsitorySection(displayString));
                    }
                }
                if (argument.equals("Clear")) {
                    if (!displayString.isEmpty()) {
                        listContainer.getChildren().add(createHsitorySection(displayString));
                        historyName = displayString;
                        historyDict.put(historyName, subHistoryList);
                        subHistoryList.clear();
                    }
                    historyName = "";
                    displayString = "";
                }
                displayField.setText(displayString);
                //updateArrays(true);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        });
        return btn;
    }

    private VBox createHsitorySection(String title) {
        VBox section = new VBox();
        section.setSpacing(5);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-background-color: gray;");
        HBox header = new HBox();
        header.setSpacing(5);
        header.setPadding(new Insets(5));
        header.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold;");
        header.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            header.setStyle("-fx-background-color: yellow; -fx-font-weight: bold;");
        });
        header.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            header.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold;");
        });
        Label titleLabel = new Label("History: " + title);
        Label arrowLabel = new Label("v");
        header.getChildren().addAll(arrowLabel, titleLabel);
        header.setOnMouseClicked(e -> toggleSection(section, arrowLabel));
        VBox subList = new VBox();
        subList.setSpacing(3);
        subList.setPadding(new Insets(5));
        subList.setPadding(new Insets(0, 0, 0, 20));
        subList.setVisible(true);
        subList.setStyle("-fx-background-color: darkgray; -fx-font-weight: normal;");
        subList.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            subList.setStyle("-fx-font-weight: bold; -fx-background-color: darkgray;");
        });
        subList.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            subList.setStyle("-fx-font-weight: normal; -fx-background-color: darkgray;");
        });
        // Example sub-items
        for (int x = 0; x < subHistoryList.size(); x++) {
            subList.getChildren().add(new Label("Subitem: " + subHistoryList.get(x)));
        }

        for (Map.Entry<String, ArrayList<String>> entry: historyDict.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            subList.getChildren().add(new Label("Subitem: " + value.toString()));
        }
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

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(Build(), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}