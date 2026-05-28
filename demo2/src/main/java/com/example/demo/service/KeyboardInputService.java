package com.example.demo.service;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class KeyboardInputService {

    private final Map<KeyCode, Integer> keyMap = new HashMap<>();

    private Consumer<Set<Integer>> noteListener;

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
    }

    public void setNoteListener(Consumer<Set<Integer>> listener) {
        this.noteListener = listener;
    }

    public void attachToScene(Scene scene) {

        scene.setOnKeyPressed(event -> {

            Integer note = keyMap.get(event.getCode());

            if (note == null) {
                return;
            }

            Set<Integer> notes = new HashSet<>();

            notes.add(note);

            if (noteListener != null) {
                noteListener.accept(notes);
            }
        });
    }
}