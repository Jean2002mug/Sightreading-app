package com.example.demo.service;

import com.example.demo.model.Chord;
import com.example.demo.model.MeasureGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GameSessionService {

    private final MeasureGenerator measureGenerator = new MeasureGenerator(1, 11, 1, 4);

    private List<Chord> currentMeasure = new ArrayList<>();
    private int score = 0;
    private int totalMeasures = 0;

    public List<Chord> startNewMeasure(int minComplexity, int maxComplexity) {
        currentMeasure = measureGenerator.nextMeasure(minComplexity, maxComplexity);
        return currentMeasure;
    }

    public boolean checkInput(Set<Integer> inputNotes) {
        if (currentMeasure.isEmpty()) {
            return false;
        }

        Set<Integer> expectedNotes = new HashSet<>(getCurrentChord().getNotes());
        boolean correct = expectedNotes.equals(inputNotes);

        if (correct) {
            score++;
        }

        totalMeasures++;
        return correct;
    }

    public Chord getCurrentChord() {
        if (currentMeasure.isEmpty()) {
            return null;
        }

        return currentMeasure.get(0);
    }

    public int getScore() {
        return score;
    }

    public int getTotalMeasures() {
        return totalMeasures;
    }

    public int getAccuracy() {
        if (totalMeasures == 0) {
            return 0;
        }

        return (int) ((double) score / totalMeasures * 100);
    }

    public void reset() {
        score = 0;
        totalMeasures = 0;
        currentMeasure.clear();
    }
}