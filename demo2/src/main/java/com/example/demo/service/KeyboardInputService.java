package com.example.demo.service;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class KeyboardInputService {

    private final Map<KeyCode, Integer> keyMap = new HashMap<>();
    private final Set<Integer> currentNotes = new HashSet<>();

    private Consumer<Set<Integer>> noteListener;
    private PauseTransition inputDelay;

    public KeyboardInputService() {
        keyMap.put(KeyCode.A, 11); // C
        keyMap.put(KeyCode.W, 0);  // C#
        keyMap.put(KeyCode.S, 1);  // D
        keyMap.put(KeyCode.E, 2);  // D#
        keyMap.put(KeyCode.D, 3);  // E
        keyMap.put(KeyCode.F, 4);  // F
        keyMap.put(KeyCode.T, 5);  // F#
        keyMap.put(KeyCode.G, 6);  // G
        keyMap.put(KeyCode.Y, 7);  // G#
        keyMap.put(KeyCode.H, 8);  // A
        keyMap.put(KeyCode.U, 9);  // A#
        keyMap.put(KeyCode.J, 10); // B

        inputDelay = new PauseTransition(Duration.millis(120));
        inputDelay.setOnFinished(event -> notifyListener());
    }

    public void setNoteListener(Consumer<Set<Integer>> listener) {
        this.noteListener = listener;
    }

    public void attachToScene(Scene scene) {
        if (scene == null || scene.getRoot() == null) {
            return;
        }

        currentNotes.clear();

        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            Integer note = keyMap.get(event.getCode());

            if (note == null) {
                return;
            }

            synchronized (currentNotes) {
                currentNotes.add(note);
            }

            inputDelay.playFromStart();
            event.consume();
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            Integer note = keyMap.get(event.getCode());

            if (note == null) {
                return;
            }

            synchronized (currentNotes) {
                currentNotes.remove(note);
            }

            event.consume();
        });
    }

    public void clearInput() {
        synchronized (currentNotes) {
            currentNotes.clear();
        }
    }

    private void notifyListener() {
        if (noteListener == null) {
            return;
        }

        Set<Integer> snapshot;

        synchronized (currentNotes) {
            if (currentNotes.isEmpty()) {
                return;
            }

            snapshot = new HashSet<>(currentNotes);
        }

        noteListener.accept(snapshot);
    }
}