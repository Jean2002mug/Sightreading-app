package com.example.demo.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.example.demo.service.FxSceneService;

@Component
@Scope("prototype")
public class LevelSelectPageController {
    private Stage stage;
    private final FxSceneService fxSceneService;

    public LevelSelectPageController(FxSceneService fxSceneService) {
        this.fxSceneService = fxSceneService;
    }

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
            FxSceneService.LoadedView<LevelDescriptionController> view =
                fxSceneService.load("/com/example/demo/LevelDescription.fxml");

            view.controller().setStage(stage);
            view.controller().setLevelDetails(minComplexity, maxComplexity, description);

            stage.setScene(new Scene(view.root()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToStartPage() {
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

    @FXML
    private void exitApp() {
        javafx.application.Platform.exit();
    }
}

