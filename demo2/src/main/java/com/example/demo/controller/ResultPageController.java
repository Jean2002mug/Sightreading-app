package com.example.demo.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ResultPageController {
    @FXML
    Label scoreLabel;

    @FXML
    Label accuracyLabel;

    Stage stage;


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
        accuracyLabel.setText(Integer.toString((int)((double)score / (double)totalMeasures * 100)) + "%");
    }

    public void moveToNextPage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/level-select-page.fxml"));
            Parent root = loader.load();
            LevelSelectPageController controller = loader.getController();
            controller.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e){

        }
    }
}

