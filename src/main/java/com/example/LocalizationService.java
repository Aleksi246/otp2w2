package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocalizationService {

    private static final String LOAD_STRINGS_SQL =
            "SELECT `key`, value FROM localization_strings WHERE language = ?";

    private static final Map<String, String> cache = new HashMap<>();
    private static String currentLanguage;

    public static Map<String, String> loadStrings(Locale locale) {
        String language = locale.getLanguage();
        if (language != null && language.equals(currentLanguage) && !cache.isEmpty()) {
            return cache;
        }

        cache.clear();
        currentLanguage = language;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(LOAD_STRINGS_SQL)) {

            stmt.setString(1, language);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cache.put(rs.getString("key"), rs.getString("value"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading localization strings: " + e.getMessage());
            e.printStackTrace();
        }

        return cache;
    }

}
