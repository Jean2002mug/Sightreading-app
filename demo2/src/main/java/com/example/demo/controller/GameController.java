package com.example.demo.controller;

import com.example.demo.model.Chord;
import com.example.demo.model.MeasureGenerator;
import com.example.demo.service.FxSceneService;
import com.example.demo.service.KeyboardInputService;
import com.example.demo.service.MidiService;
import com.example.demo.service.SettingsService;
import com.example.demo.settings.InputMode;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Scope("prototype")
public class GameController {

    private final FxSceneService fxSceneService;
    private final MidiService midiService;
    private final KeyboardInputService keyboardInputService;
    private final SettingsService settingsService;

    private MeasureGenerator measureGenerator;

    private int minComplexity;
    private int maxComplexity;
    private Set<Integer> currentNotes = new HashSet<>();
    private List<Chord> currentMeasure = new ArrayList<>();
    private int currentBeat;

    private int timeRemaining;
    private int score;
    private int totalMeasures;

    private Timeline countdownTimer;
    private Stage stage;

    @FXML
    private Label countDownLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private ImageView noteView;

    @FXML
    private ImageView feedbackView;

    @FXML
    private Button pauseButton;

    @FXML
    private Label chordLabel;

    public GameController(
            FxSceneService fxSceneService,
            MidiService midiService,
            KeyboardInputService keyboardInputService,
            SettingsService settingsService
    ) {
        this.fxSceneService = fxSceneService;
        this.midiService = midiService;
        this.keyboardInputService = keyboardInputService;
        this.settingsService = settingsService;
    }

    @FXML
    public void initialize() {
        scoreLabel.setText("Score: 0");
        countDownLabel.setText("Time: 1:00");
    }

    public void setStage(Stage stage, int minComplexity, int maxComplexity, Scene scene) {
        this.stage = stage;
        this.minComplexity = minComplexity;
        this.maxComplexity = maxComplexity;

        this.timeRemaining = 60;
        this.score = 0;
        this.totalMeasures = 0;
        this.currentBeat = 0;
        this.currentNotes.clear();

        this.measureGenerator = new MeasureGenerator(1, 11, 1, 4);

        newMeasure();
        setupInputMode(scene);
        startCountDown();
    }

    private void setupInputMode(Scene scene) {
        midiService.setNoteListener(this::handleInputNotes);
        keyboardInputService.setNoteListener(this::handleInputNotes);

        InputMode mode = settingsService.getInputMode();

        boolean midiConnected = false;

        if (mode == InputMode.MIDI || mode == InputMode.AUTO_DETECT) {
            midiConnected = midiService.connectMidiDevice();
        }

        if (mode == InputMode.MIDI) {
            System.out.println(midiConnected ? "Using MIDI input" : "MIDI selected, but no MIDI device found");
            return;
        }

        if (mode == InputMode.COMPUTER_KEYBOARD) {
            System.out.println("Using computer keyboard only");
            keyboardInputService.attachToScene(scene);
            return;
        }

        if (mode == InputMode.AUTO_DETECT) {
            if (midiConnected) {
                System.out.println("Auto detect: MIDI connected. Keyboard fallback also enabled.");
            } else {
                System.out.println("Auto detect: no MIDI found. Using computer keyboard.");
            }

            keyboardInputService.attachToScene(scene);
        }
    }

    private void startCountDown() {
        countdownTimer = new Timeline(new KeyFrame(
                Duration.seconds(1),
                event -> {
                    timeRemaining--;

                    int minutes = timeRemaining / 60;
                    int seconds = timeRemaining % 60;

                    String secondsString = seconds < 10 ? "0" + seconds : Integer.toString(seconds);

                    countDownLabel.setText("Time: " + minutes + ":" + secondsString);

                    if (timeRemaining <= 0) {
                        countdownTimer.stop();
                        moveToResultPage();
                    }
                }
        ));

        countdownTimer.setCycleCount(Timeline.INDEFINITE);
        countdownTimer.play();
    }

    private void newMeasure() {
        currentBeat = 0;
        currentNotes.clear();
        keyboardInputService.clearInput();

        currentMeasure = measureGenerator.nextMeasure(minComplexity, maxComplexity);

        Chord chord = currentMeasure.get(currentBeat);
        chordLabel.setText(chord.noteName());
        System.out.println("New chord: " + chord.noteName() + " " + chord.getNotes());

        Image image = new Image(
                getClass().getResource("/noteImages/" + chord.noteName() + ".png").toExternalForm()
        );

        noteView.setImage(image);
    }

    private void handleInputNotes(Set<Integer> notes) {
        if (notes == null || notes.isEmpty()) {
            return;
        }

        Platform.runLater(() -> processInput(notes));
    }

    private void processInput(Set<Integer> notes) {
        if (currentMeasure == null || currentMeasure.isEmpty()) {
            return;
        }

        if (currentBeat >= currentMeasure.size()) {
            return;
        }

        currentNotes = new HashSet<>(notes);

        Chord expectedChord = currentMeasure.get(currentBeat);
        boolean correct = currentNotes.equals(expectedChord.getNotes());

        showFeedback(correct);

        if (correct) {
            score++;
            scoreLabel.setText("Score: " + score);

            currentBeat++;

            if (currentBeat >= currentMeasure.size()) {
                totalMeasures++;
                delayThenNewMeasure();
            }
        }
    }

    private void showFeedback(boolean correct) {
        String imagePath = correct
                ? "/feedbackImages/correct.png"
                : "/feedbackImages/incorrect.jpg";

        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        feedbackView.setImage(image);

        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(event -> feedbackView.setImage(null));
        pause.play();
    }

    private void delayThenNewMeasure() {
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(event -> newMeasure());
        pause.play();
    }

    private void moveToResultPage() {
        cleanupGameResources();

        try {
            FxSceneService.LoadedView<ResultPageController> view =
                    fxSceneService.load("/com/example/demo/result-page.fxml");

            view.controller().scoreLabel(score);
            view.controller().accuracyLabel(score, totalMeasures);
            view.controller().setStage(stage);

            stage.setScene(new Scene(view.root()));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void pauseGame() {
        if (countdownTimer != null) {
            countdownTimer.pause();
        }
    }

    @FXML
    public void handleQuitAction() {
        pauseGame();

        try {
            FxSceneService.LoadedView<QuitConfirmationController> view =
                    fxSceneService.load("/com/example/demo/quit-confirmation.fxml");

            Stage confirmStage = new Stage();
            confirmStage.setTitle("Confirm Quit");
            confirmStage.setScene(new Scene(view.root(), 400, 150));
            confirmStage.setResizable(false);
            confirmStage.initStyle(StageStyle.UTILITY);
            confirmStage.initOwner(stage);
            confirmStage.initModality(Modality.WINDOW_MODAL);

            confirmStage.showAndWait();

            Object result = confirmStage.getUserData();

            if ("quit_confirmed".equals(result)) {
                goBackToLevelSelection();
            } else if (countdownTimer != null) {
                countdownTimer.play();
            }

        } catch (IOException e) {
            e.printStackTrace();

            if (countdownTimer != null) {
                countdownTimer.play();
            }
        }
    }

    private void goBackToLevelSelection() {
        cleanupGameResources();

        try {
            FxSceneService.LoadedView<LevelSelectPageController> view =
                    fxSceneService.load("/com/example/demo/level-select-page.fxml");

            view.controller().setStage(stage);

            stage.setScene(new Scene(view.root(), 1200, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleTipsAction() {
        pauseGame();

        try {
            FxSceneService.LoadedView<TipsDialogController> view =
                    fxSceneService.load("/com/example/demo/tips-dialog.fxml");

            Stage tipsStage = new Stage();
            tipsStage.setTitle("Tips & Instructions");
            tipsStage.setScene(new Scene(view.root(), 500, 300));
            tipsStage.setResizable(true);
            tipsStage.initStyle(StageStyle.UTILITY);
            tipsStage.initOwner(stage);
            tipsStage.initModality(Modality.WINDOW_MODAL);

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

    @FXML
    public void handleMenuAction() {
        if (countdownTimer == null) {
            return;
        }

        switch (countdownTimer.getStatus()) {
            case RUNNING -> {
                countdownTimer.pause();

                if (pauseButton != null) {
                    pauseButton.setText("Resume");
                }
            }

            case PAUSED, STOPPED -> {
                countdownTimer.play();

                if (pauseButton != null) {
                    pauseButton.setText("Pause");
                }
            }

            default -> {
            }
        }
    }

    private void cleanupGameResources() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        keyboardInputService.clearInput();
        midiService.disconnect();
    }
}