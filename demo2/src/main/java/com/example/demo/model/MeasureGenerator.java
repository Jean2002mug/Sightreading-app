package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Ogheneovo Grant-Oyeye
 * This class returns randomly generated sequences of chords
 */
public class MeasureGenerator {
    private static final int LOWEST_NOTE = 0;
    private static final int HIGHEST_NOTE = 127;


    private static final List<List<Integer>> noteLengths = List.of(
            List.of(4),
            List.of(2, 2),
            List.of(2, 1, 1),
            List.of(1, 2, 1),
            List.of(1, 1, 2),
            List.of(1, 1, 1, 1)
    );

    private int minRootNote;
    private int maxRootNote;
    private List<List<Integer>> noteSequences;
    private Random random;


    public MeasureGenerator(int minRootNote, int maxRootNote, int minNotesPerBar, int maxNotesPerBar) {

        if (minRootNote < LOWEST_NOTE || maxRootNote > HIGHEST_NOTE) {
            throw new IllegalArgumentException("Argument root note must be between 0 and 127");
        }

        if (minRootNote > maxRootNote) {
            throw new IllegalArgumentException("minBeatsPerMeasure must be less than maxBeatsPerMeausre");
        }

        if (minNotesPerBar < 0 || maxNotesPerBar > 4) {
            throw new IllegalArgumentException("maxNotesPerBar must be between 0 and 4");
        }

        if (minNotesPerBar > maxNotesPerBar) {
            throw new IllegalArgumentException("maxNotesPerBar must bebe greater than minNotesPerBar");
        }

        this.minRootNote = minRootNote;
        this.maxRootNote = maxRootNote;

        noteSequences = noteLengths.stream()
                .filter(sequence -> sequence.size() <= maxNotesPerBar).toList();

        this.random = new Random();

    }

    public List<Chord> nextMeasure(int minComplexity, int maxComplexity) {

        List<Integer> sequence = noteSequences.get(random.nextInt(0, noteSequences.size()));
        List<Chord> bar = new ArrayList<>();

        for (Integer duration : sequence) {
            int rootNote = random.nextInt(minRootNote, maxRootNote + 1);
            bar.add(new Chord(rootNote, duration, minComplexity, maxComplexity));
        }

        return bar;
    }

}

