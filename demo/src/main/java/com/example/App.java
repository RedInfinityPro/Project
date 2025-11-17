package com.example;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * JavaFX App
 */
public class App extends Application {
    // elements
    public static Scene primaryScene;
    public static Stage stage;
    private static Parent mainRoot;
    // font
    public static final Font title_Font = Font.font("Arial", FontWeight.BOLD, 18);
    public static final Font header_Font = Font.font("Arial", FontWeight.SEMI_BOLD, 16);
    public static final Font normal_Font = Font.font("Arial", FontWeight.NORMAL, 12);
    // style
    public static String levelPane_color = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ffffff, #f8f9fa); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); ";
    public static String historyPane_color = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ffffff, #f8f9fa); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); ";

    @Override
    public void start(Stage stage) throws IOException {
        MenuFile menuFile = new MenuFile();
        mainRoot = menuFile.MainMenu();
        primaryScene = new Scene(mainRoot, 900, 600);
        stage.setScene(primaryScene);
        stage.show();
        AnimationUtils.fadein(mainRoot, 800);
    }

    public static void main(String[] args) {
        launch();
    }
}