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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.example.demo.service.FxSceneService;


@Component
@Scope("prototype")

public class CountDownController {

    @FXML
    private Label countDownLabel;

    private Timeline timeline;
    private final FxSceneService fxSceneService;

    private static final int COUNTDOWN_SECONDS = 3; // Countdown duration
    private int timeRemaining = COUNTDOWN_SECONDS;
    private Stage stage;
   // Variables to hold the complexity levels and the measure generator for the game
    private int minComplexity;// Minimum complexity level for the game
    private int maxComplexity;// Maximum complexity level for the game
    private MeasureGenerator generator;// Object responsible for generating musical measures based on the specified complexity levels

    public CountDownController(FxSceneService fxSceneService) {
        this.fxSceneService = fxSceneService;
    }
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

    private void moveToNextPage() {
        try {
            FxSceneService.LoadedView<GameController> view =
                    fxSceneService.load("/com/example/demo/game-core-view.fxml");

            Scene scene = new Scene(view.root());

            view.controller().setStage(stage, minComplexity, maxComplexity, scene);

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCountdown() {
        timeRemaining = COUNTDOWN_SECONDS; // Reset countdown
        timeline.playFromStart();
    }


}

