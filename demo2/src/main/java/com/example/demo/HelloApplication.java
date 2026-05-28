package com.example.demo;

import java.io.IOException;

import com.example.SpringBootApp;
import com.example.demo.controller.LoadingScreenController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
// Main application class that integrates JavaFX with Spring Boot

//boot springframework which provides a framework for building and running Spring applications with minimal configuration
import org.springframework.boot.builder.SpringApplicationBuilder;
//context class that provides configuration and lifecycle management for the Spring application
import org.springframework.context.ConfigurableApplicationContext;

public class HelloApplication extends Application {
    private ConfigurableApplicationContext springContext;

    @Override
    public void init(){
        // Initialize the Spring application context using SpringApplicationBuilder and run the SpringBootApp class to start the Spring application
        springContext = new SpringApplicationBuilder(SpringBootApp.class).run();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file and set the controller
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loading-screen.fxml"));
        // Set the controller factory to use Spring to create the controller instances, allowing for dependency injection and other Spring features in the controllers
        fxmlLoader.setControllerFactory(springContext::getBean);
        // Load the FXML file and get the root node of the scene graph, which is the top-level container for all the UI elements in the scene
        Parent root = fxmlLoader.load();// Load the FXML file start-screen-page.fxml and get the root node of the scene graph
        LoadingScreenController controller = fxmlLoader.getController();// Get the controller associated with the FXML file
        controller.setStage(stage);// Set the stage which is the main window where the application is displayed in the controller so that it can be accessed later
        Scene scene = new Scene(root, 1200, 600);// Create a new scene which is the main container for all content with the root node and set its dimensions to 1200x600 pixels
        stage.setTitle("Sight-reading App");// Set the title of the stage
        stage.setScene(scene);// Set the scene for the stage
        stage.setResizable(true);// Allow the stage to be resizable
        stage.show();// Show the stage

    }

    @Override
    public void stop(){
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}