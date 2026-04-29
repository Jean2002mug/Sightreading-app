package com.example.demo.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LevelSelectPageController {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void levelOne() {
        moveToDescriptionPage(0, 0, "Level One: Single notes, Timeframe: 1 minute, Good luck!");
    }

    public void levelTwo() {
        moveToDescriptionPage(1, 1, "Level Two: Chords with multiple notes, Timeframe: 1 minute, Good luck!");
    }

    private void moveToDescriptionPage(int minComplexity, int maxComplexity, String description) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/LevelDescription.fxml"));
            Parent root = loader.load();

            LevelDescriptionController controller = loader.getController();
            controller.setStage(stage);
            controller.setLevelDetails(minComplexity, maxComplexity, description);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToStartPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/start-screen-page.fxml"));
            Parent root = loader.load();
            StartScreenController controller = loader.getController();
            controller.setStage(stage);
            Scene scene = new Scene(root, 1200, 600);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitApp() {
        javafx.application.Platform.exit();
    }
}

