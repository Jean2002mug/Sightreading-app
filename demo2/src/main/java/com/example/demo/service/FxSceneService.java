package com.example.demo.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FxSceneService {

    private final ApplicationContext applicationContext;

    public FxSceneService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <T> LoadedView<T> load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(applicationContext::getBean);

        Parent root = loader.load();
        T controller = loader.getController();

        return new LoadedView<>(root, controller);
    }

    public record LoadedView<T>(Parent root, T controller) {
    }
}