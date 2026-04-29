package com.example.demo;

import java.io.IOException;

import com.example.demo.controller.LoadingScreenController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file and set the controller
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loading-screen.fxml"));
        Parent root = fxmlLoader.load();// Load the FXML file start-screen-page.fxml and get the root node of the scene graph
        LoadingScreenController controller = fxmlLoader.getController();// Get the controller associated with the FXML file
        controller.setStage(stage);// Set the stage which is the main window where the application is displayed in the controller so that it can be accessed later
        Scene scene = new Scene(root, 1200, 600);// Create a new scene which is the main container for all content with the root node and set its dimensions to 1200x600 pixels
        stage.setTitle("Sight-reading App");// Set the title of the stage
        stage.setScene(scene);// Set the scene for the stage
        stage.setResizable(true);// Allow the stage to be resizable
        stage.show();// Show the stage

    }

    public static void main(String[] args) {
        launch();
    }
}