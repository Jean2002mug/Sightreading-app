package com.example.demo.model;

/**
 * @author Jean Michel Mugabe
*/

import java.util.Set;

import javax.sound.midi.MidiDevice;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import com.example.demo.model.MidiInputProcessor;

public class MidiInputProcessorTest {

    private MidiInputProcessor midiInputProcessor;

    @Before
    public void setUp() {
        // Initialize the MidiInputProcessor before each test
        midiInputProcessor = new MidiInputProcessor();
    }

    @Test
    public void testDeviceInitialization() {
        // Verify that the MIDI device is initialized properly
        MidiDevice device = midiInputProcessor.getDevice();
        assertNotNull("MIDI device should not be null", device);
        assertTrue("MIDI device should be open", device.isOpen());
    }

    @Test
    public void testGetInputs() {
        // Verify that the set of currently played notes is initialized and accessible
        Set<Integer> inputs = midiInputProcessor.getInputs();
        assertNotNull("The set of inputs should not be null", inputs);
    }

    @Test
    public void testSynthesizerInitialization() {
        // Ensure the synthesizer is properly initialized and opened
        try {
            MidiDevice synthesizer = midiInputProcessor.getDevice();
            assertTrue("Synthesizer should be open", synthesizer.isOpen());
        } catch (Exception e) {
            fail("Synthesizer should not throw an exception during initialization");
        }
    }
}
