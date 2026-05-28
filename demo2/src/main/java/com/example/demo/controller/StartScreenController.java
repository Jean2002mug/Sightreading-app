package com.example.demo.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;


@Component
public class StartScreenController {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handlePlay() {
        try {
            // gets the fxml file from the resource folder and loads it to create the scene for the level select page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/level-select-page.fxml"));
            Parent root = loader.load();// loads the fxml file and returns the root node of the scene graph which is the top-level container for all the UI elements in the scene
            LevelSelectPageController controller = loader.getController();
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
        Platform.exit();
    }

    @FXML
    private void maximizeApp() {
        if (stage != null) {
            stage.setMaximized(!stage.isMaximized());
        }
    }
    @FXML
    private void minimizeApp(){
        if(stage!=null){
            stage.setIconified(true);// Minimize the stage
        }
    }
}
