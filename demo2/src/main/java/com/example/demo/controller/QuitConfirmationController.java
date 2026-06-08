package com.example.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class QuitConfirmationController {

    @FXML
    private void confirmQuit(ActionEvent event) {
        Stage stage = getStage(event);
        stage.setUserData("quit_confirmed");
        stage.close();
    }

    @FXML
    private void cancelQuit(ActionEvent event) {
        Stage stage = getStage(event);
        stage.setUserData("quit_cancelled");
        stage.close();
    }

    private Stage getStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        return (Stage) source.getScene().getWindow();
    }
}