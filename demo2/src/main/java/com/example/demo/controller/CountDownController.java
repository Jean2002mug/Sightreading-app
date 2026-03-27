package com.example.demo.controller;

import java.io.IOException;

import com.example.demo.model.MeasureGenerator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CountDownController {

    @FXML
    private Label countDownLabel;

    private Timeline timeline;

    private static final int COUNTDOWN_SECONDS = 3; // Countdown duration
    private int timeRemaining = COUNTDOWN_SECONDS;
    private Stage stage;

    private int minComplexity;
    private int maxComplexity;
    private MeasureGenerator generator;


    public void setState(int minComplexity, int maxComplexity, MeasureGenerator generator){
        this.minComplexity = minComplexity;
        this.maxComplexity = maxComplexity;
        this.generator = generator;
    }
    @FXML
    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    timeRemaining--;
                    countDownLabel.setText(Integer.toString(timeRemaining));

                    if (timeRemaining <= 0) {
                        timeline.stop();
                        moveToNextPage();

                    }
                })
        );
        timeline.setCycleCount(COUNTDOWN_SECONDS);
    }

    private void  moveToNextPage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/game-core-view.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.setStage(stage, this.minComplexity, this.maxComplexity);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e){

        }

    }

    public void startCountdown() {
        timeRemaining = COUNTDOWN_SECONDS; // Reset countdown
        timeline.playFromStart();
    }


}

