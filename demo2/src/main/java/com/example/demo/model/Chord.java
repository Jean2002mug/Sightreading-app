package com.example.demo.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Chord {
    private static final int MIN_COMPLEXITY = 0;
    private static final int MAX_COMPLEXITY = 1;
    private static final int MAX_NOTE_VALUE = 127;
    private static final int MIN_NOTE_VALUE = 0;
    private static final int NUM_NOTES = 12;

    private static final String SINGLE = "Single";
    private static final String MAJOR = "Major";
    private static final String MINOR = "Minor";

    private static final Map<String, Integer> CHORD_COMPLEXITY = Map.of(
            SINGLE, 0,
            MAJOR, 1,
            MINOR, 1
    );

    private static final Map<String, List<Integer>> INTERVALS = Map.of(
            SINGLE, List.of(0),
            MAJOR, List.of(0, 4, 7),
            MINOR, List.of(0, 3, 7)
    );

    private final String[] numToLetterSharps = {
            "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C"
    };

    private final int rootNote;
    private final int duration;
    private final Set<Integer> notes;
    private final String noteName;
    private final String chordType;

    public Chord(int rootNote, int duration, int minComplexity, int maxComplexity) {
        if (rootNote < MIN_NOTE_VALUE || rootNote > MAX_NOTE_VALUE) {
            throw new IllegalArgumentException("Argument rootNote must be between 0 and 127");
        }

        if (duration < 0) {
            throw new IllegalArgumentException("Argument duration must be greater than 0");
        }

        if (minComplexity < MIN_COMPLEXITY) {
            throw new IllegalArgumentException("Min complexity must be at least 0");
        }

        if (maxComplexity > MAX_COMPLEXITY) {
            throw new IllegalArgumentException("Max complexity must be at most 1");
        }

        if (minComplexity > maxComplexity) {
            throw new IllegalArgumentException("minComplexity must not be greater than maxComplexity");
        }

        this.rootNote = rootNote;
        this.duration = duration;

        List<String> possibleChords = CHORD_COMPLEXITY.entrySet()
                .stream()
                .filter(entry -> entry.getValue() <= maxComplexity && entry.getValue() >= minComplexity)
                .map(Map.Entry::getKey)
                .toList();

        String chord = possibleChords.get(new Random().nextInt(possibleChords.size()));
        this.chordType = chord;

        this.notes = new HashSet<>();

        for (Integer interval : INTERVALS.get(chord)) {
            int pitchClass = (rootNote + interval) % NUM_NOTES;
            notes.add(pitchClass);
        }

        this.noteName = numToLetterSharps[rootNote % NUM_NOTES] + chord;
    }

    public int getRootNote() {
        return rootNote;
    }

    public Set<Integer> getNotes() {
        return notes;
    }

    public int getDuration() {
        return duration;
    }

    public int size() {
        return notes.size();
    }

    public String noteName() {
        return noteName;
    }

    public String getRootNoteName() {
        return numToLetterSharps[rootNote % NUM_NOTES];
    }

    public String getChordType() {
        return chordType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{ ");

        for (Integer note : notes) {
            builder.append(numToLetterSharps[note % NUM_NOTES]).append(" ");
        }

        builder.append("}");
        return builder.toString();
    }
}