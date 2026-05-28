package com.example.demo.service;

import com.example.demo.settings.InputMode;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private InputMode inputMode = InputMode.AUTO_DETECT;

    public InputMode getInputMode() {
        return inputMode;
    }

    public void setInputMode(InputMode inputMode) {
        this.inputMode = inputMode;
    }

    public boolean shouldUseMidi() {
        return inputMode == InputMode.MIDI || inputMode == InputMode.AUTO_DETECT;
    }

    public boolean shouldUseComputerKeyboard() {
        return inputMode == InputMode.COMPUTER_KEYBOARD || inputMode == InputMode.AUTO_DETECT;
    }
    
}
