package com.example.demo.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.example.demo.service.FxSceneService;

@Component
@Scope("prototype")
public class ResultPageController {
    @FXML
    Label scoreLabel;

    @FXML
    Label accuracyLabel;

    Stage stage;
    private final FxSceneService fxSceneService;
    public ResultPageController(FxSceneService fxSceneService) {
        this.fxSceneService = fxSceneService;
    }


    @FXML
    public void setScoreLabel(int score){
        scoreLabel.setText(Integer.toString(score));
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void scoreLabel(int score) {
        scoreLabel.setText(Integer.toString(score));
    }

    public void accuracyLabel(int score, int totalMeasures) {
        if (totalMeasures == 0) {
            accuracyLabel.setText("0%");
            return;
        }

        accuracyLabel.setText((int) ((double) score / totalMeasures * 100) + "%");
    }

    public void moveToNextPage() {
        try {
            FxSceneService.LoadedView<LevelSelectPageController> view =
                    fxSceneService.load("/com/example/demo/level-select-page.fxml");

            view.controller().setStage(stage);

            Scene scene = new Scene(view.root());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

