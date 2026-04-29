package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import com.example.demo.model.Chord;
import com.example.demo.model.MeasureGenerator;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * this class represents the gameplay screen of the sightreading app game
 * 
 * @author Lucas Arsenault
 * @author 
 */
public class GameController {

    private MidiInputReceiver receiver;
    private MidiDevice device ;
    private MeasureGenerator measureGenerator;

    private int minComplexity;
    private int maxComplexity;
    private Set<Integer> currentNotes = new HashSet<>();
    public List<Chord> currentMeasure = new ArrayList<>();
    private int currentBeat;

    public int timeRemaining;
    private int score;
    private int totalMeasures;

    public Timeline countdownTimer;
    private Stage stage;

    @FXML
    private Label countDownLabel;


    @FXML
    private Label scoreLabel;

    @FXML
    public void setStage(Stage stage, int minComplexity, int maxComplexity){
        this.stage = stage;
        this.minComplexity = minComplexity;
        this.maxComplexity = maxComplexity;
        this.measureGenerator =  new MeasureGenerator(1, 11, 1,4);
        newMeasure();
    }
    
    @FXML
    // ImageView to display the current note/chord that the player needs to play
    private ImageView noteView;

    @FXML
    // ImageView to display feedback on whether the player's input was correct or incorrect
    private ImageView feedbackView;


    private void startCountDown(){
        // Start the countdown timer for the game level
        // The timer updates the countDownLabel every second and checks if the time has run out to move to the next page
        countdownTimer = new Timeline(new KeyFrame(
            // Set the duration which is the interval of each KeyFrame which is the countdown update interval to 1 second
            Duration.seconds(1),
            // Define the event handler for each KeyFrame, which will be executed every second
            event -> {
                timeRemaining--;
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                String minutesString = Integer.toString(minutes);
                String secondsString = seconds < 10 ? "0" + Integer.toString(seconds) : Integer.toString(seconds);
                // Update the countDownLabel with the remaining time in the format "Time: MM:SS"
                // using platform.runlater which is a method that allows us to update the UI from a non-UI thread
                // since the countdown timer runs on a separate thread and we need to update the UI with the remaining time
                Platform.runLater(() -> {
                    countDownLabel.setText("Time: " + minutesString + ":" + secondsString);
                });
                    if (timeRemaining <= 0) {
                        countdownTimer.stop();
                        moveToNextPage();
                    }
            }
        ));
        // Set the cycle count to indefinite so that the timer continues until we manually stop it when the time runs out
        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        // Start the countdown timer
        countdownTimer.play();
    }

    private void newMeasure(){
        // Generate a new measure using the measure generator based on the specified complexity levels 
        // and update the noteView with the first chord of the new measure
        this.currentMeasure = this.measureGenerator.nextMeasure(minComplexity, maxComplexity);

        Platform.runLater(()->{
           Chord chord = currentMeasure.get(0);
            System.out.println(chord.noteName());
            Image image = new Image(getClass().getResource("/noteImages/" + chord.noteName() + ".png").toExternalForm());
            noteView.setImage(image);
        });
    }
    // Check if the current measure is complete by comparing the current beat index with the size of the current measure
    private boolean measureComplete(){
        return currentBeat == currentMeasure.size();
    }

    /**
     * Compares the currently input set of notes to
     * the correct set of notes for a single beat in
     * a measure. If the measure is complete and
     * the every beat input was correct, the score increments.
     */
    private boolean checkChord(){
        
        if(currentNotes.equals(currentMeasure.get(currentBeat).getNotes())){
            currentBeat++;
            return true;
        }
        return false;
    }
    /*
     * Sets the complexity levels for the game.
     * @param minComplexity Minimum complexity level
     * @param maxComplexity Maximum complexity level
     */
    public void setComplexity(int minComplexity, int maxComplexity){
        this.minComplexity = minComplexity;
        this.maxComplexity = maxComplexity;
    }
   @FXML
   // Initializes the game by setting the time remaining, current beat, score, setting up MIDI connections, and starting the countdown timer
   public void initialize(){
        this.timeRemaining = 60;
        currentBeat = 0;
        this.score = 0;
        setupMidi();
        startCountDown();
    }
    // Increments the player's score and updates the scoreLabel on the UI thread using Platform.runLater to ensure thread safety when updating the UI from a non-UI thread
    private void incrementScore(){
        score++;
        Platform.runLater(() -> {
            scoreLabel.setText("Score: " + score);
        });
    }


    private void  moveToNextPage(){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/result-page.fxml"));
            Parent root = loader.load();

            ResultPageController controller = loader.getController();
            controller.scoreLabel(this.score);
            controller.accuracyLabel(this.score, this.totalMeasures);
            controller.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e){

        }

    }
    @FXML
    private void pauseGame() {
        if (countdownTimer != null) {
            countdownTimer.pause();
        }
    }

    private void showQuitConfirmation() {
        pauseGame();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/quit-confirmation.fxml"));
            Parent root = loader.load();
            Stage confirmStage = new Stage();
            confirmStage.setTitle("Confirm Quit");
            confirmStage.setScene(new Scene(root, 400, 150));
            confirmStage.setResizable(false);
            confirmStage.initStyle(javafx.stage.StageStyle.UTILITY);
            confirmStage.showAndWait();
            
            // Check if user confirmed quit
            Object result = confirmStage.getUserData();
            if (result != null && result.equals("quit_confirmed")) {
                goBackToLevelSelection();
            } else {
                if (countdownTimer != null) {
                    countdownTimer.play();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (countdownTimer != null) {
                countdownTimer.play();
            }
        }
    }

    private void goBackToLevelSelection() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/level-select-page.fxml"));
            Parent root = loader.load();
            LevelSelectPageController controller = loader.getController();
            controller.setStage(stage);
            Scene scene = new Scene(root, 1200, 600);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTips() {
        pauseGame();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/tips-dialog.fxml"));
            Parent root = loader.load();
            Stage tipsStage = new Stage();
            tipsStage.setTitle("Tips & Instructions");
            tipsStage.setScene(new Scene(root, 500, 300));
            tipsStage.setResizable(true);
            tipsStage.initStyle(javafx.stage.StageStyle.UTILITY);
            tipsStage.setOnHidden(event -> {
                if (countdownTimer != null) {
                    countdownTimer.play();
                }
            });
            tipsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            if (countdownTimer != null) {
                countdownTimer.play();
            }
        }
    }

    public void handleQuitAction() {
        showQuitConfirmation();
    }

    public void handleTipsAction() {
        showTips();
    }

    public void handleMenuAction() {
        pauseGame();
        if (countdownTimer != null) {
            countdownTimer.play();
        }
    }

    private void setupMidi(){
        MidiDevice.Info[] deviceInfo = MidiSystem.getMidiDeviceInfo();
        for(int i=0; i< deviceInfo.length;i++){
            try {
                MidiDevice device = MidiSystem.getMidiDevice(deviceInfo[i]);
                if (!device.isOpen()){
                    device.open();
                }
                System.out.println("Successfully connected to: " + deviceInfo[i]);
                
                // Set the MIDI input reciever to get MIDI data from the device 
                Synthesizer synthesizer = MidiSystem.getSynthesizer();
                synthesizer.open();
                MidiChannel channel = synthesizer.getChannels()[0];
                receiver = new MidiInputReceiver(device.getDeviceInfo().toString(), channel);
                device.getTransmitter().setReceiver(receiver);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    
private  class MidiInputReceiver implements Receiver {
    
    /**
     * Number of unique notes on the keyboard.
     */
    private static final int NUM_NOTES = 12;

    /**
     * Channel for sending and receiving MIDI messages.
     */
    private MidiChannel channel;

    /**
     * List of note names for the inputs currently played.
     */
    private Set<Integer> noteNames = new HashSet<>();

    private Node eventTarget;

    /**
     * Constructs an input receiver.
     * 
     * @param name The name of the input receiver.
     * @param channel The channel for sending and receiving MIDI messages.
     */
    public MidiInputReceiver(String name, MidiChannel channel) {
        this.channel = channel;
        this.eventTarget = eventTarget;
    }

   


    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message instanceof ShortMessage) {
            ShortMessage sm = (ShortMessage) message;
            // Case of pressing PB1 or PB2 for pitch bending. 
            if (sm.getCommand() >= 0xE0 && sm.getCommand() <= 0xEF) {// if the button pressed is PB1 or PB2 then ignore it 
                System.out.println("Pitch Bend message ignored");
                return;
            }

            if (sm.getCommand() == 0xB0) {// This ignores the pressing of PB1 or PB2 for any modification except pitch bending .
                int controllerNumber = sm.getData1(); // Controller number
                if (isPB1OrPB2Controller(controllerNumber)) {
                    System.out.println("Control Change message from PB1/PB2 ignored");
                    return;
                }
            }
            int command = sm.getCommand();
            int rawkey = sm.getData1() ;
            int key=sm.getData1() % NUM_NOTES;
            String noteName = getNoteName(key);
            int velocity = sm.getData2();

            if (command == ShortMessage.NOTE_ON && velocity > 0) {
                System.out.println("Note ON: " + key + " | Velocity: " + velocity);
                currentNotes.add(key);
                if (currentNotes.equals(currentMeasure.get(0).getNotes())){
                    Image image = new Image(getClass().getResource("/feedbackImages/correct.png").toExternalForm());
                    feedbackView.setImage(image);
                    incrementScore();
                } else {
                    Image image = new Image(getClass().getResource("/feedbackImages/incorrect.jpg").toExternalForm());
                    feedbackView.setImage(image);
                }

                PauseTransition pause = new PauseTransition(Duration.millis(500));

                // After the pause, clear the feedback image
                pause.setOnFinished(event -> {
                    Platform.runLater(() -> {
                        feedbackView.setImage(null);
                    });
                });

                newMeasure();
                totalMeasures++;
                pause.play();
               
                try {
                    channel.noteOn(rawkey, velocity);
                } catch (NullPointerException e) {}
            } else if (command == ShortMessage.NOTE_OFF || (command == ShortMessage.NOTE_ON && velocity == 0)) {
                System.out.println("Note OFF: " + key);
                currentNotes.remove(key);
                
            
                try {
                    channel.noteOff(rawkey);
                } catch (NullPointerException e) {}
            }
        }
    }

    @Override
    public void close() {
        // Implement close if needed for resource management
    }
    private boolean isPB1OrPB2Controller(int controllerNumber) {
        // PB1/PB2 controller numbers may vary; this decides whether the button pressed is PB1 or PB2.
        return controllerNumber == 1 || controllerNumber == 2; 
    }

    /**
     * Returns the list of note names for the input currently being played.
     * 
     * @return List of note names for the input currently being played.
     */
    public Set<Integer> getNoteNames() {
        return noteNames;
    }

    /**
     * Translates a MIDI key integer value to
     * a string containing the musical note name
     * for the corresponding key.
     * 
     * @param key The MIDI integer note value.
     * @return The corresponding musical note name.
     */
    public String getNoteName(int key) {
        String noteName = "";
        if (key == 11) {
            noteName = "C";
        } else if (key == 0) {
            noteName = "C#";
        } else if (key == 1) {
            noteName = "D";
        } else if (key == 2) {
            noteName = "D#";
        } else if (key == 3) {
            noteName = "E";
        } else if (key == 4) {
            noteName = "F";
        } else if (key == 5) {
            noteName = "F#";
        } else if (key == 6) {
            noteName = "G";
        } else if (key == 7) {
            noteName = "G#";
        } else if (key == 8) {
            noteName = "A";
        } else if (key == 9) {
            noteName = "A#";
        } else if (key == 10) {
            noteName = "B";
        }
        return noteName;
    }

    public void setNoteNames(Set<Integer> noteNames) {
      this.noteNames = noteNames;
    }

    public void setEventTarget(Node eventTarget) {
        this.eventTarget = eventTarget;
    }
}
}

