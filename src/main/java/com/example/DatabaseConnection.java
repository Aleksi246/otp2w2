package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = System.getenv().getOrDefault(
        "DB_URL",
         "jdbc:mysql://localhost:3308/fuel_calculator_localization?useUnicode=true&characterEncoding=UTF-8"
        );

    private static final String USER = "fuel_app_user";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}