package com.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

public class Controller {

    @FXML private Label lblDistance;
    @FXML private Label lblConsumption;
    @FXML private Label lblPrice;
    @FXML private Label lblResult;
    @FXML private TextField txtDistance;
    @FXML private TextField txtConsumption;
    @FXML private TextField txtPrice;
    @FXML private Button btnCalculate;
    @FXML private VBox root;
    @FXML private HBox langButtons;

    private Map<String, String> localizedStrings;

    @FXML
    private void initialize() {
        setLanguage(new Locale("en", "US"));
    }

    private void setLanguage(Locale locale) {
        localizedStrings = LocalizationService.getLocalizedStrings(locale);
        lblDistance.setText(localizedStrings.getOrDefault("distance.label", "Distance (km)"));
        lblConsumption.setText(localizedStrings.getOrDefault("consumption.label", "Fuel Consumption (L/100 km)"));
        lblPrice.setText(localizedStrings.getOrDefault("price.label", "Fuel Price (per liter)"));
        btnCalculate.setText(localizedStrings.getOrDefault("calculate.button", "Calculate Trip Cost"));
        lblResult.setText("");

        txtDistance.setPromptText(localizedStrings.getOrDefault("distance.prompt", "Enter distance"));
        txtConsumption.setPromptText(localizedStrings.getOrDefault("consumption.prompt", "Enter consumption"));
        txtPrice.setPromptText(localizedStrings.getOrDefault("price.prompt", "Enter price"));

        applyTextDirection(locale);
    }

    private void applyTextDirection(Locale locale) {
        String lang = locale.getLanguage();
        boolean isRTL = lang.equals("fa")
                || lang.equals("ur")
                || lang.equals("ar")
                || lang.equals("he");

        Platform.runLater(() -> {
            if (root != null) {
                root.setNodeOrientation(
                        isRTL ? NodeOrientation.RIGHT_TO_LEFT
                                : NodeOrientation.LEFT_TO_RIGHT
                );
            }

            // Keep text fields always LTR so arrow keys and number input work correctly
            

            langButtons.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        });
    }

    @FXML
    private void handleCalculate() {
        try {
            double distance = Double.parseDouble(txtDistance.getText());
            double consumption = Double.parseDouble(txtConsumption.getText());
            double price = Double.parseDouble(txtPrice.getText());

            double fuel = (consumption / 100) * distance;
            double cost = fuel * price;

            String pattern = localizedStrings.getOrDefault("result.label",
                    "Total fuel needed: {0} L | Total cost: {1}");
            String result = MessageFormat.format(pattern,
                    String.format("%.2f", fuel), String.format("%.2f", cost));
            lblResult.setText(result);
        } catch (NumberFormatException e) {
            lblResult.setText(localizedStrings.getOrDefault("invalid.input", "Invalid input"));
        }
    }

    @FXML private void setEnglish()  { setLanguage(new Locale("en", "US")); }
    @FXML private void setFrench()   { setLanguage(new Locale("fr", "FR")); }
    @FXML private void setJapanese() { setLanguage(new Locale("ja", "JP")); }
    @FXML private void setPersian()  { setLanguage(new Locale("fa", "IR")); }
}