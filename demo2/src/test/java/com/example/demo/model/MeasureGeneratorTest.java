package com.example.demo.model;

/**
 * @author Jean Michel Mugabe
*/

import com.example.demo.model.Chord;
import com.example.demo.model.MeasureGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MeasureGeneratorTest {

    private MeasureGenerator measureGenerator;

    @Before
    public void setUp() {
        // Initialize MeasureGenerator with valid parameters
        measureGenerator = new MeasureGenerator(40, 80, 1, 4);
    }

    @Test
    public void testConstructorValidParameters() {
        // Ensure the MeasureGenerator is properly created
        assertNotNull("MeasureGenerator should be instantiated", measureGenerator);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidRootNoteRange() {
        // Test invalid root note range
        new MeasureGenerator(-1, 130, 1, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidMinGreaterThanMaxRootNote() {
        // Test minRootNote greater than maxRootNote
        new MeasureGenerator(80, 40, 1, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidNotesPerBarRange() {
        // Test invalid minNotesPerBar and maxNotesPerBar range
        new MeasureGenerator(40, 80, 5, 6);
    }

    @Test
    public void testNextMeasureGeneration() {
        // Generate a measure and validate it contains chords
        List<Chord> measure = measureGenerator.nextMeasure(0, 1);
        assertNotNull("Generated measure should not be null", measure);
        assertFalse("Generated measure should contain chords", measure.isEmpty());
        for (Chord chord : measure) {
            assertTrue("Chord root note should be within range",
                    chord.getRootNote() >= 40 && chord.getRootNote() <= 80);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNextMeasureInvalidComplexityRange() {
        // Test invalid complexity range (minComplexity > maxComplexity)
        measureGenerator.nextMeasure(2, 1);
    }

    @Test
    public void testChordDurationsInMeasure() {
        // Validate that the generated chords have valid durations
        List<Chord> measure = measureGenerator.nextMeasure(0, 1);
        for (Chord chord : measure) {
            assertTrue("Chord duration should be valid (1, 2, or 4)",
                    chord.getDuration() == 1 || chord.getDuration() == 2 || chord.getDuration() == 4);
        }
    }

    @Test
    public void testMeasureSizeLimits() {
        // Ensure the generated measure respects the note per bar constraints
        List<Chord> measure = measureGenerator.nextMeasure(0, 1);
        assertTrue("Generated measure size should be within valid range",
                measure.size() <= 4);
    }
}