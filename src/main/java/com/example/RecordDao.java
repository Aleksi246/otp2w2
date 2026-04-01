package com.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class RecordDao {
    private static final String INSERT_SQL = "INSERT INTO calculation_records(distance, consumption, price, total_fuel, total_cost, language)" +
    "VALUES (?,?,?,?,?,?)";
    
    public void saveRecord(
        double distance,
        double consumption,
        double price,
        double total_fuel,
        double total_cost,
        String language
    ){
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement stmt = c.prepareStatement(INSERT_SQL)) {

            stmt.setDouble(1, distance);
            stmt.setDouble(2, consumption);
            stmt.setDouble(3, price);
            stmt.setDouble(4, total_fuel);
            stmt.setDouble(5, total_cost);
            stmt.setString(6, language);

            stmt.executeUpdate();


        } catch (SQLException e) {
            System.err.println("Error saving record: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
