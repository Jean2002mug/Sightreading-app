package com.example.demo.service;

import org.springframework.stereotype.Service;

import javax.sound.midi.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Service

public class MidiService {
    private MidiDevice connectedDevice;
    private Synthesizer synthesizer;
    private MidiChannel channel;

    private Consumer<Set<Integer>> noteListener;

    public boolean connectMidiDevice() {

        try {

            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

            for (MidiDevice.Info info : infos) {

                MidiDevice device = MidiSystem.getMidiDevice(info);

                if (!device.isOpen()) {
                    device.open();
                }

                if (device.getMaxTransmitters() != 0) {

                    synthesizer = MidiSystem.getSynthesizer();
                    synthesizer.open();

                    channel = synthesizer.getChannels()[0];

                    device.getTransmitter().setReceiver(new MidiInputReceiver());

                    connectedDevice = device;

                    System.out.println("Connected MIDI device: " + info);

                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setNoteListener(Consumer<Set<Integer>> listener) {
        this.noteListener = listener;
    }

    public boolean isConnected() {
        return connectedDevice != null;
    }

    public void disconnect() {

        try {

            if (connectedDevice != null && connectedDevice.isOpen()) {
                connectedDevice.close();
            }

            if (synthesizer != null && synthesizer.isOpen()) {
                synthesizer.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MidiInputReceiver implements Receiver {

        private static final int NUM_NOTES = 12;

        private final Set<Integer> currentNotes = new HashSet<>();

        @Override
        public void send(MidiMessage message, long timeStamp) {

            if (!(message instanceof ShortMessage sm)) {
                return;
            }

            int command = sm.getCommand();

            int rawKey = sm.getData1();

            int key = rawKey % NUM_NOTES;

            int velocity = sm.getData2();

            if (command == ShortMessage.NOTE_ON && velocity > 0) {

                currentNotes.add(key);

                if (channel != null) {
                    channel.noteOn(rawKey, velocity);
                }

                if (noteListener != null) {
                    noteListener.accept(new HashSet<>(currentNotes));
                }

            } else if (command == ShortMessage.NOTE_OFF ||
                    (command == ShortMessage.NOTE_ON && velocity == 0)) {

                currentNotes.remove(key);

                if (channel != null) {
                    channel.noteOff(rawKey);
                }
            }
        }

        @Override
        public void close() {
        }
    }
}
