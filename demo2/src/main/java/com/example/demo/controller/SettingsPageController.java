package com.example.demo.controller;

import com.example.demo.service.FxSceneService;
import com.example.demo.service.MidiService;
import com.example.demo.service.SettingsService;
import com.example.demo.settings.InputMode;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope("prototype")
public class SettingsPageController {

    private Stage stage;
    private final FxSceneService fxSceneService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private MidiService midiService;

    @FXML
    private RadioButton autoDetectRadio;

    @FXML
    private RadioButton midiRadio;

    @FXML
    private RadioButton computerKeyboardRadio;

    @FXML
    private Label midiStatusLabel;

    public SettingsPageController(FxSceneService fxSceneService) {
        this.fxSceneService = fxSceneService;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        System.out.println("SettingsPageController: initialize() called");
        // Create a ToggleGroup for radio buttons
        ToggleGroup inputModeGroup = new ToggleGroup();
        autoDetectRadio.setToggleGroup(inputModeGroup);
        midiRadio.setToggleGroup(inputModeGroup);
        computerKeyboardRadio.setToggleGroup(inputModeGroup);

        // Load current settings
        InputMode currentMode = settingsService.getInputMode();
        
        switch (currentMode) {
            case MIDI:
                midiRadio.setSelected(true);
                break;
            case COMPUTER_KEYBOARD:
                computerKeyboardRadio.setSelected(true);
                break;
            case AUTO_DETECT:
            default:
                autoDetectRadio.setSelected(true);
                break;
        }

        // Update MIDI status
        updateMidiStatus();
    }

    @FXML
    private void detectMidiDevice() {
        boolean connected = midiService.hasExternalMidiInputDevice();

        if (connected) {
            midiStatusLabel.setText("MIDI Device Status: Connected ✓");
            midiStatusLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-size: 12px;");
        } else {
            midiStatusLabel.setText("MIDI Device Status: Not Connected ✗");
            midiStatusLabel.setStyle("-fx-text-fill: #FF4444; -fx-font-size: 12px;");
        }
    }

   private void updateMidiStatus() {
        if (midiService.hasExternalMidiInputDevice()) {
            midiStatusLabel.setText("MIDI Device Status: Connected ✓");
            midiStatusLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-size: 12px;");
        } else {
            midiStatusLabel.setText("MIDI Device Status: Not Connected ✗");
            midiStatusLabel.setStyle("-fx-text-fill: #FF4444; -fx-font-size: 12px;");
        }
    }
    @FXML
    private void saveSettings() {
        InputMode selectedMode;

        if (midiRadio.isSelected()) {
            selectedMode = InputMode.MIDI;
        } else if (computerKeyboardRadio.isSelected()) {
            selectedMode = InputMode.COMPUTER_KEYBOARD;
        } else {
            selectedMode = InputMode.AUTO_DETECT;
        }

        settingsService.setInputMode(selectedMode);
        System.out.println("Settings saved. Input mode: " + selectedMode);

        goBack();
    }

    @FXML
    private void goBack() {
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
}
