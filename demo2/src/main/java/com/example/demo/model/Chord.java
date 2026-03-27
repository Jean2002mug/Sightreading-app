package com.example.demo.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author Ogheneovo Grant-Oyeye
 * This class represents a midi chord
 * A chord can contain at least one note called the root note
 * all other notes are randomly generated using a complexity parameter
 */

public class Chord {
    private static final int MIN_COMPLEXITY = 0;
    private static final int MAX_COMPLEXITY = 1;
    private static final int MAX_NOTE_VALUE = 127;
    private static final int MIN_NOTE_VALUE = 0;
    private static final String SINGLE = "Single";
    private static final String MAJOR = "Major";
    private static final String MINOR = "Minor";
    private static final Map<String, Integer> CHORD_COMPLEXITY = Map.of(
            SINGLE, 0,
            MAJOR, 1,
            MINOR, 1
    );
    private static final Map<String, List<Integer>> intervals = Map.of(
            SINGLE, List.of(0),
            MAJOR, List.of(0, 4, 7),
            MINOR, List.of(0, 3, 7)
    );
    String[] numToLetterSharps = {"C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C"};
    String[] numToLetterFlats = {"Db", "D", "Eb", "E", "F", "Gb", "G", "G", "Ab", "A", "Bb", "B", "C"};

    private int rootNote;
    private int duration;
    private Set<Integer> notes;
    private Random random;
    private String noteName;

    private String chordType;


    public Chord(int rootNote, int duration, int minComplexity, int maxComplexity) {

        if (rootNote < MIN_NOTE_VALUE || rootNote > MAX_NOTE_VALUE) {
            throw new IllegalArgumentException("Argument rootNote must be between 0 and 127");
        }

        if (duration < 0) {
            throw new IllegalArgumentException("Argument length must be greater than 0");
        }

        this.rootNote = rootNote;
        this.duration = duration;

        if(minComplexity < MIN_COMPLEXITY ){
            throw new IllegalArgumentException("Min complexity must be greater than 0");
        }

        if(maxComplexity > MAX_COMPLEXITY){
            throw new IllegalArgumentException("Max complexity must be less than 1");
        }

        if(minComplexity > maxComplexity){
            throw new IllegalArgumentException("minComplexity must be greater than maxComplexity");
        }


        List<String> possibleChords = CHORD_COMPLEXITY.entrySet()
                .stream()
                .filter(entry -> entry.getValue() <= maxComplexity && entry.getValue() >= minComplexity)
                .map(Map.Entry::getKey).toList();

        this.random = new Random();

        String chord = possibleChords.get(random.nextInt(possibleChords.size()));
        this.chordType = chord;

        this.notes = new HashSet<>();

        for (Integer interval : intervals.get(chord)) {
            //only add notes within bounds
            int newNote = rootNote + interval;
            if(newNote <= MAX_NOTE_VALUE  && newNote >= MIN_NOTE_VALUE) notes.add(rootNote + interval);
        }

        this.noteName = numToLetterSharps[rootNote % 12] + chord;

    }

    public int getRootNote() {
        return this.rootNote;
    }

    public Set<Integer> getNotes() {
        return this.notes;
    }

    public int getDuration() {
        return this.duration;
    }

    public int size() {
        return notes.size();
    }

    public String noteName() {
        return this.noteName;
    }


    public String getRootNoteName(){
        return numToLetterSharps[rootNote % 12];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ ");
        for (Integer note : notes) {
            builder.append(numToLetterSharps[note % numToLetterSharps.length]);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

}

