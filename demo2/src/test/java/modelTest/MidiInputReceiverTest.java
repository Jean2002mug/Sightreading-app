package modelTest;


/**
 * @author Jean Michel Mugabe
*/


import org.junit.Before;
import org.junit.Test;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import java.util.Set;
import model.MidiInputReceiver;
import static org.junit.Assert.*;

public class MidiInputReceiverTest {

    private MidiInputReceiver midiInputReceiver;
    private MidiChannel mockChannel;

    @Before
    public void setUp() {
        // Create a basic MidiChannel instance (could be a minimal implementation or null)
        mockChannel = new MidiChannel() {
            @Override
            public void noteOn(int noteNumber, int velocity) {
                // Stub implementation
            }

            @Override
            public void noteOff(int noteNumber) {
                // Stub implementation
            }

            @Override
            public void noteOff(int noteNumber, int velocity) {
                // Stub implementation
            }

            // Implement other MidiChannel methods as no-op or throw UnsupportedOperationException
            @Override
            public void setPolyPressure(int noteNumber, int pressure) {}
            @Override
            public int getPolyPressure(int noteNumber) { return 0; }
            @Override
            public void setChannelPressure(int pressure) {}
            @Override
            public int getChannelPressure() { return 0; }
            @Override
            public void controlChange(int controller, int value) {}
            @Override
            public int getController(int controller) { return 0; }
            @Override
            public void programChange(int program) {}
            @Override
            public void programChange(int bank, int program) {}
            @Override
            public int getProgram() { return 0; }
            @Override
            public void setPitchBend(int bend) {}
            @Override
            public int getPitchBend() { return 0; }
            @Override
            public void resetAllControllers() {}
            @Override
            public void allNotesOff() {}
            @Override
            public void allSoundOff() {}
            @Override
            public boolean localControl(boolean on) { return false; }
            @Override
            public void setMono(boolean on) {}
            @Override
            public boolean getMono() { return false; }
            @Override
            public void setOmni(boolean on) {}
            @Override
            public boolean getOmni() { return false; }
            @Override
            public void setMute(boolean mute) {}
            @Override
            public boolean getMute() { return false; }
            @Override
            public void setSolo(boolean soloState) {}
            @Override
            public boolean getSolo() { return false; }
        };
        // Create an instance of MidiInputReceiver
        midiInputReceiver = new MidiInputReceiver("TestReceiver", mockChannel);
    }

    @Test
    public void testSendNoteOn() throws Exception {
        ShortMessage noteOnMessage = new ShortMessage(ShortMessage.NOTE_ON, 0, 4, 100);
        midiInputReceiver.send(noteOnMessage, -1);

        Set<Integer> noteNames = midiInputReceiver.getNoteNames();
        assertTrue("Note set should contain the note 60", noteNames.contains(4));
    }

    @Test
    public void testSendNoteOff() throws Exception {
        ShortMessage noteOnMessage = new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 100);
        midiInputReceiver.send(noteOnMessage, -1);

        ShortMessage noteOffMessage = new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 0);
        midiInputReceiver.send(noteOffMessage, -1);

        Set<Integer> noteNames = midiInputReceiver.getNoteNames();
        assertFalse("Note set should not contain the note 60 after NOTE_OFF", noteNames.contains(60));
    }

    @Test
    public void testGetNoteName() {
        String noteName = midiInputReceiver.getNoteName(11);
        assertEquals("C", noteName);

        noteName = midiInputReceiver.getNoteName(0);
        assertEquals("C#", noteName);

        noteName = midiInputReceiver.getNoteName(1);
        assertEquals("D", noteName);
    }
}
