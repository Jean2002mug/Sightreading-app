module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.core;



    opens com.example.demo to javafx.fxml, spring.core;
    opens com.example.demo.controller to javafx.fxml, spring.core, spring.beans, spring.context;
    opens com.example.demo.service to spring.core, spring.beans, spring.context;
    opens com.example.demo.settings to spring.core;


    exports com.example.demo;
    exports com.example.demo.controller;
    exports com.example.demo.model;
    exports com.example.demo.service;
    exports com.example.demo.settings;
}