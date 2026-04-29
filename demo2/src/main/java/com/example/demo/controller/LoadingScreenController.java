package com.example.demo.controller;
import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoadingScreenController {
    private Stage stage;

    @FXML
    private ProgressBar progressBar;

    private boolean loadingStarted;

    public void setStage(Stage stage) {
        this.stage = stage;
        startLoadingIfReady();
    }

    @FXML
    private void initialize() {
        progressBar.setProgress(0);
        startLoadingIfReady();
    }

    private void startLoadingIfReady() {
        if (loadingStarted || stage == null || progressBar == null) {
            return;
        }

        loadingStarted = true;

        Timeline loadingTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(3), new KeyValue(progressBar.progressProperty(), 1))
        );

        loadingTimeline.setOnFinished(event -> showStartScreen());
        loadingTimeline.play();
    }

    private void showStartScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/start-screen-page.fxml"));
            Parent root = loader.load();
            StartScreenController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(new Scene(root, 1200, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}