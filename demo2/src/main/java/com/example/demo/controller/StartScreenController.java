package com.example.demo.controller;

import java.io.IOException;

import com.example.demo.service.FxSceneService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class StartScreenController {

    private final FxSceneService fxSceneService;
    private Stage stage;

    public StartScreenController(FxSceneService fxSceneService) {
        this.fxSceneService = fxSceneService;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handlePlay() {
        try {
            FxSceneService.LoadedView<LevelSelectPageController> view =
                    fxSceneService.load("/com/example/demo/level-select-page.fxml");

            view.controller().setStage(stage);

            Scene scene = new Scene(view.root(), 1200, 600);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitApp() {
        Platform.exit();
    }

    @FXML
    private void maximizeApp() {
        if (stage != null) {
            stage.setMaximized(!stage.isMaximized());
        }
    }

    @FXML
    private void minimizeApp() {
        if (stage != null) {
            stage.setIconified(true);
        }
    }

    @FXML
    private void openSettings() {
        try {
            FxSceneService.LoadedView<SettingsPageController> view =
                    fxSceneService.load("/com/example/demo/settings-page.fxml");

            view.controller().setStage(stage);

            Scene scene = new Scene(view.root(), 1200, 600);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}