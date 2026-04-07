package com.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void testGetConnectionReturnsConnectionOrThrows() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            assertNotNull(conn);
            conn.close();
        } catch (SQLException e) {
            assertNotNull(e.getMessage());
        }
    }
}
