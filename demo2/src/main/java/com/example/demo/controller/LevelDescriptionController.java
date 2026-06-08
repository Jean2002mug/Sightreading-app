package com.example.demo.controller;

import java.io.IOException;

import com.example.demo.model.MeasureGenerator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.example.demo.service.FxSceneService;

@Component
@Scope("prototype")
public class LevelDescriptionController {
    private Stage stage;
    // Variables to hold the complexity levels and the measure generator for the game
    private int minComplexity;// Minimum complexity level for the game
    private int maxComplexity;// Maximum complexity level for the game
    private MeasureGenerator generator;// Object responsible for generating musical measures based on the specified complexity levels

    @FXML
    private Label descriptionLabel;

    @FXML
    private Button startButton;
    private final FxSceneService fxSceneService;

    public LevelDescriptionController(FxSceneService fxSceneService) {
        this.fxSceneService = fxSceneService;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLevelDetails(int minComplexity, int maxComplexity, String description) {
        this.minComplexity = minComplexity;
        this.maxComplexity = maxComplexity;
        this.generator = new MeasureGenerator(1, 11, 1, 4); // Example generator
        descriptionLabel.setText(description);
    }

    @FXML
    private void startLevel() {
        try {
            FxSceneService.LoadedView<CountDownController> view =
                fxSceneService.load("/com/example/demo/clock.fxml");

            view.controller().setState(minComplexity, maxComplexity, generator);
            view.controller().setStage(stage);

            Scene scene = new Scene(view.root());
            stage.setScene(scene);

            view.controller().startCountdown();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/level-select-page.fxml"));
            Parent root = loader.load();
            LevelSelectPageController controller = loader.getController();
            controller.setStage(stage);
            Scene scene = new Scene(root, 1200, 600);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

