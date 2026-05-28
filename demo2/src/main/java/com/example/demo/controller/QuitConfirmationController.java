package com.example.demo.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class QuitConfirmationController {

    @FXML
    private void confirmQuit() {
        Stage stage = (Stage) null;
        // Get the stage from the scene
        javafx.scene.Node source = null;
        if (source != null) {
            stage = (Stage) source.getScene().getWindow();
        }
        
        // Iterate through all windows to find this one
        for (javafx.stage.Window window : javafx.stage.Stage.getWindows()) {
            if (window instanceof Stage) {
                Stage s = (Stage) window;
                if (s.getTitle().equals("Confirm Quit")) {
                    s.setUserData("quit_confirmed");
                    s.close();
                    break;
                }
            }
        }
    }

    @FXML
    private void cancelQuit() {
        // Close the dialog without quitting
        for (javafx.stage.Window window : javafx.stage.Stage.getWindows()) {
            if (window instanceof Stage) {
                Stage s = (Stage) window;
                if (s.getTitle().equals("Confirm Quit")) {
                    s.close();
                    break;
                }
            }
        }
    }
}
