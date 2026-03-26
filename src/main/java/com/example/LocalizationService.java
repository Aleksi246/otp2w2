package com.example;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LocalizationService {
    /**
     * Get localized strings for a specific locale
     */
    public static Map<String, String> getLocalizedStrings(Locale locale) {
        Map<String, String> strings = new HashMap<>();

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", locale);
            for (String key : bundle.keySet()) {
                strings.put(key, bundle.getString(key));
            }
        } catch (Exception e) {
            System.err.println("Failed to load resource bundle for locale: " + locale);
            // Fallback to English
            try {
                ResourceBundle fallback = ResourceBundle.getBundle(
                        "MessagesBundle", new Locale("en", "US"));
                for (String key : fallback.keySet()) {
                    strings.put(key, fallback.getString(key));
                }
            } catch (Exception ex) {
                strings.put("distance.label", "Distance (km)");
                strings.put("consumption.label", "Fuel Consumption (L/100 km)");
                strings.put("price.label", "Fuel Price (per liter)");
                strings.put("calculate.button", "Calculate Trip Cost");
                strings.put("result.label", "Total fuel needed: {0} L | Total cost: {1}");
                strings.put("invalid.input", "Invalid input");
            }
        }

        return strings;
    }
}
