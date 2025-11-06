package com.example;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * JavaFX App
 */
public class App extends Application {
    public static Scene scene;
    public static Stage main_stage;
    private static Parent mainRoot;
    // font
    public static final Font title_Font = Font.font("Arial", FontWeight.BOLD, 18);
    public static final Font header_Font = Font.font("Arial", FontWeight.SEMI_BOLD, 16);
    public static final Font normal_Font = Font.font("Arial", FontWeight.NORMAL, 12);
    // style
    public static boolean isDarkMode = false;
    public static String menuPane_color = "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f8f9fa); ";
    public static String displayBox_color = "-fx-background-color: linear-gradient(to bottom, #e9ecef, #dee2e6); ";
    public static String mainBox_color = "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%); ";
    public static String levelPane_color = "-fx-background: rgba(255, 255, 255, 0.1); -fx-background-color: linear-gradient(135deg, rgba(255,255,255,0.9), rgba(248,249,250,0.9)); -fx-background-radius: 15;";
    public static String historyPane_color = "-fx-background: rgba(255, 255, 255, 0.1); -fx-background-color: linear-gradient(135deg, rgba(255,255,255,0.9), rgba(248,249,250,0.9)); -fx-background-radius: 15;";

    @Override
    public void start(Stage stage) throws IOException {
        MenuFile menuFile = new MenuFile();
        mainRoot = menuFile.MainMenu();
        main_stage = stage;
        scene = new Scene(mainRoot, 900, 600);
        stage.setScene(scene);
        AnimationUtils.fadein(mainRoot, 800);
        stage.show();
    }

    public static void updateTheme() {
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
    public static void applyTheme() {
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
            historyPane_color = "-fx-background: rgba(52, 73, 94, 0.9); -fx-background-color: linear-gradient(135deg, rgba(44,62,80,0.9), rgba(52,73,94,0.9)); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);";
            levelPane_color = "-fx-background: rgba(52, 73, 94, 0.9); -fx-background-color: linear-gradient(135deg, rgba(44,62,80,0.9), rgba(52,73,94,0.9)); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);";
        } else {
            // Light theme
            historyPane_color = "-fx-background: rgba(255, 255, 255, 0.1); -fx-background-color: linear-gradient(135deg, rgba(255,255,255,0.9), rgba(248,249,250,0.9)); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";
            levelPane_color = "-fx-background: rgba(255, 255, 255, 0.1); -fx-background-color: linear-gradient(135deg, rgba(255,255,255,0.9), rgba(248,249,250,0.9)); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";
        }
    }

    public static void main(String[] args) {
        launch();
    }
}