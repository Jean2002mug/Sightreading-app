package com.example.demo.model;

import javafx.event.Event;
import javafx.event.EventType;

public class NoteEvent extends Event {
    public static final EventType<NoteEvent> NOTE_PLAYED = new EventType<>(Event.ANY, "NOTE_PLAYED");
    private final int noteKey;
    private final int velocity;

    public NoteEvent(int noteKey, int velocity) {
        super(NOTE_PLAYED);
        this.noteKey = noteKey;
        this.velocity = velocity;
    }

    public int getNoteKey() {
        return noteKey;
    }

    public int getVelocity() {
        return velocity;
    }
}

