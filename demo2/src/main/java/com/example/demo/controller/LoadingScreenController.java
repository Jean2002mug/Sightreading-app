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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.example.demo.service.FxSceneService;
import com.example.demo.controller.StartScreenController;

@Component
@Scope("prototype")
public class LoadingScreenController {
    private Stage stage;

    @FXML
    private ProgressBar progressBar;

    private boolean loadingStarted;
    private final FxSceneService fxSceneService;
    public LoadingScreenController(FxSceneService fxSceneService) {
        this.fxSceneService = fxSceneService;
    }

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
            new KeyFrame(Duration.seconds(1), new KeyValue(progressBar.progressProperty(), 1))
        );

        loadingTimeline.setOnFinished(event -> showStartScreen());
        loadingTimeline.play();
    }

    private void showStartScreen() {
        try {
                FxSceneService.LoadedView<StartScreenController> view =
                        fxSceneService.load("/com/example/demo/start-screen-page.fxml");

                view.controller().setStage(stage);

                stage.setScene(new Scene(view.root(), 1200, 600));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }