package com.example.demo.model;

/*
 * This class is responsible for setting up MIDI- keyboard.
 * It is responsible for detecting notes played on the keyboard and 
 * sending the signal that the keyboard. 
 *  @author : Jean Michel Mugabe
 * 
 */

import javax.sound.midi.MidiDevice;

public class MidiSetup {
    private Boolean keyboardSetup = false; // Initialize to false
    private MidiDevice midiDevice;
    private String deviceName;
    private MidiInputProcessor midiInputProcessor;

    public MidiSetup() {
        midiInputProcessor= new MidiInputProcessor();
        openMidiConnection(midiInputProcessor);
        keyboardSetup();
    }

    private void openMidiConnection(MidiInputProcessor midiInputProcessor)  {

        deviceName= midiInputProcessor.getDevice().toString();
        if (deviceName != null && !deviceName.equalsIgnoreCase("Microsoft MIDI Mapper")
            && !deviceName.equalsIgnoreCase("Real Time Sequencer")
            && !deviceName.equalsIgnoreCase("Gervill")) {
        keyboardSetup = true;
    } else {
        keyboardSetup = false;
        deviceName= null;
    }
    }
    

    public  boolean isKeyboardSetup() {
        return keyboardSetup;
    }
    public String getDeviceName(){
        return deviceName;
    }
    public void keyboardSetup() {
        if (isKeyboardSetup()) {
            System.out.println("MIDI device: " + getDeviceName() + " setup successfully.");
        } else {
            System.out.println("No keyboard has been connected");
        }
    }

    public static void main(String[] args) {
        new MidiSetup();
    }
}
