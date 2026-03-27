package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import com.example.demo.HelloApplication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;




/*
 *   @ Jean Michel Mugabe
 */


 public class HelloApplicationTest {

    private HelloApplication helloApplication;
    private Stage testStage;

    @Before
    public void setUp() {
        // Initialize the JavaFX toolkit by launching the application
        Application.launch(HelloApplication.class);
        
        helloApplication = new HelloApplication();
        testStage = new Stage();
    }

    @Test
    public void testStart() throws Exception {
        // Ensure the setup code has finished before starting the test
        Platform.runLater(() -> {
            try {
                // Call the start method (sets up the stage, scene, etc.)
                helloApplication.start(testStage);

                // Verify that the stage's scene is set correctly
                Scene scene = testStage.getScene();
                assertNotNull("Scene should be set", scene);
                assertEquals("Scene width should be 1200", 1200.0, scene.getWidth(), 0.1);
                assertEquals("Scene height should be 580", 580.0, scene.getHeight(), 0.1);

                // Verify that the stage's title is set correctly
                assertEquals("Stage title should be 'Sight-reading App'", "Sight-reading App", testStage.getTitle());
            } catch (Exception e) {
                fail("Exception during start: " + e.getMessage());
            }
        });
    }

    @Test
    public void testMain() {
        // The main method of HelloApplication just calls launch(),
        // so we need to run it on the JavaFX thread to avoid IllegalStateException
        Platform.runLater(() -> {
            try {
                HelloApplication.main(new String[]{});
            } catch (Exception e) {
                fail("Main method threw an exception: " + e.getMessage());
            }
        });
    }
}