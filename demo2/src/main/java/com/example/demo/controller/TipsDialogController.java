package com.example.demo.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;


@Component
public class TipsDialogController {

    @FXML
    private void closeDialog() {
        // Close the tips dialog
        for (javafx.stage.Window window : javafx.stage.Stage.getWindows()) {
            if (window instanceof Stage) {
                Stage s = (Stage) window;
                if (s.getTitle().equals("Tips & Instructions")) {
                    s.close();
                    break;
                }
            }
        }
    }
}
