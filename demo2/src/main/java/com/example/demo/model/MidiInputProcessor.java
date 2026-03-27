package com.example.demo.model;

import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 * Takes in MIDI keyboard input, and translates it
 * to sound and a <set of> letter name values corresponding
 * to the musical note(s) that have been played.
 * 
 * @author Lucas Arsenault
 * @author Jean Michel Mugabe
 */
public class MidiInputProcessor {

    /**
     * Synthesizer generates sound.
     */
    private Synthesizer synthesizer;

    /**
     * Channel for sending and receiving MIDI messages.
     */
    private MidiChannel channel;

    /**
     * Receiver for MIDI keyboard input.
     */
    private MidiInputReceiver receiver;

    /**
     * Device for MIDI keyboard.
     */
    private MidiDevice device;

    private Set<Integer> noteNames = new HashSet<>();

    /**
     * Constructor for a MidiInputProcessor object.
     * Initializes the device, receiver, and synthesizer.
     */
    public MidiInputProcessor() {
        // Initialize MIDI device
        MidiDevice.Info[] deviceInfo = MidiSystem.getMidiDeviceInfo();
        for(int i=0; i< deviceInfo.length;i++){
            try {
                device = MidiSystem.getMidiDevice(deviceInfo[i]);
                if (!device.isOpen()){
                    device.open();
                }
                System.out.println("Successfully connected to: " + deviceInfo[i]);
                
                // Set the MIDI input reciever to get MIDI data from the device 
                receiver = new MidiInputReceiver(device.getDeviceInfo().toString(), channel);
               
                receiver.setNoteNames(noteNames);
                device.getTransmitter().setReceiver(receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Initialize MIDI synthesizer
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channel = synthesizer.getChannels()[0];
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of notes currently being played as part of a chord.
     * 
     * @return List of notes currently played.
     */
    public Set<Integer> getInputs() {
        return receiver.getNoteNames();
    }

    /**
     * Returns the MIDI device.
     * 
     * @return MIDI device.
     */
    public MidiDevice getDevice() {
        return device;
    }
    
}
