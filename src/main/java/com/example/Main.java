package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        URL fxmlUrl = getClass().getResource("/view.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        VBox root = loader.load();

        // Create scene first
        Scene scene = new Scene(root, 400, 350);

        // Then add CSS

        primaryStage.setTitle("Fuel Consumption and Trip Cost Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}