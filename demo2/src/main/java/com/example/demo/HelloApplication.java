package com.example.demo;

import java.io.IOException;

import com.example.demo.controller.LevelSelectPageController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("level-select-page.fxml"));
        Parent root = fxmlLoader.load();
        LevelSelectPageController controller = fxmlLoader.getController();
        controller.setStage(stage);
        Scene scene = new Scene(root, 1200, 600);
        stage.setTitle("Sight-reading App");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}