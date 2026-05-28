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

import com.example.demo.service.GameSessionService;
import com.example.demo.service.KeyboardInputService;
import com.example.demo.service.MidiService;
import com.example.demo.service.SettingsService;
import com.example.demo.settings.InputMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component

public class GameController {

   
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

    @Autowired
    private MidiService midiService;

    @Autowired
    private KeyboardInputService keyboardInputService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private GameSessionService gameSessionService;

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
        startCountDown();
        setupInputMode();
    }
    // Increments the player's score and updates the scoreLabel on the UI thread using Platform.runLater to ensure thread safety when updating the UI from a non-UI thread
    private void incrementScore(){
        score++;
        Platform.runLater(() -> {
            scoreLabel.setText("Score: " + score);
        });
    }

    private void setupInputMode() {

        InputMode mode = settingsService.getInputMode();

        if (mode == InputMode.MIDI || mode == InputMode.AUTO_DETECT) {

            boolean connected = midiService.connectMidiDevice();

            if (connected) {

                midiService.setNoteListener(this::handleInputNotes);

                System.out.println("Using MIDI input");

                return;
            }
        }

        System.out.println("Using computer keyboard");

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
            keyboardInputService.attachToScene(scene);
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
    private void handleInputNotes(Set<Integer> notes) {

        currentNotes = notes;

        boolean correct =
                currentNotes.equals(currentMeasure.get(0).getNotes());

        Platform.runLater(() -> {

            if (correct) {

                Image image = new Image(
                        getClass()
                        .getResource("/feedbackImages/correct.png")
                        .toExternalForm()
                );

                feedbackView.setImage(image);

                incrementScore();

            } else {

                Image image = new Image(
                        getClass()
                        .getResource("/feedbackImages/incorrect.jpg")
                        .toExternalForm()
                );

                feedbackView.setImage(image);
            }
        });

        PauseTransition pause =
                new PauseTransition(Duration.millis(500));

        pause.setOnFinished(event -> {
            feedbackView.setImage(null);
        });

        newMeasure();

        totalMeasures++;

        pause.play();
    }

    

    

}

