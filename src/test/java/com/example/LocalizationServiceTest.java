package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalizationServiceTest {

    private MockedStatic<DatabaseConnection> dbMock;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        dbMock = mockStatic(DatabaseConnection.class);
        dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @AfterEach
    void tearDown() {
        dbMock.close();
    }

    @Test
    void testLoadStringsReturnsLocalizedValues() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("key")).thenReturn("distance.label", "price.label");
        when(mockResultSet.getString("value")).thenReturn("Distance (km)", "Fuel Price");

        Map<String, String> strings = LocalizationService.loadStrings(new Locale("en", "US"));

        assertEquals("Distance (km)", strings.get("distance.label"));
        assertEquals("Fuel Price", strings.get("price.label"));
        verify(mockStatement).setString(1, "en");
    }

    @Test
    void testLoadStringsReturnsEmptyMapWhenNoResults() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        Map<String, String> strings = LocalizationService.loadStrings(new Locale("xx", "XX"));

        assertTrue(strings.isEmpty());
    }

    @Test
    void testLoadStringsCachesResultsForSameLanguage() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("key")).thenReturn("test.key");
        when(mockResultSet.getString("value")).thenReturn("Test Value");

        // First call loads from DB
        LocalizationService.loadStrings(new Locale("fr", "FR"));
        // Second call with same language should use cache
        Map<String, String> strings = LocalizationService.loadStrings(new Locale("fr", "FR"));

        assertEquals("Test Value", strings.get("test.key"));
        // DB should only be called once due to caching
        verify(mockConnection, times(1)).prepareStatement(anyString());
    }

    @Test
    void testLoadStringsClearsOnLanguageChange() throws SQLException {
        // Use unique locales not shared with other tests to avoid static cache hits
        when(mockResultSet.next()).thenReturn(true, false, true, false);
        when(mockResultSet.getString("key")).thenReturn("key1", "key2");
        when(mockResultSet.getString("value")).thenReturn("Value IT", "Value PT");

        LocalizationService.loadStrings(new Locale("it", "IT"));
        Map<String, String> strings = LocalizationService.loadStrings(new Locale("pt", "PT"));

        // Both calls should hit the DB since the language changed
        assertNotNull(strings);
        verify(mockConnection, times(2)).prepareStatement(anyString());
    }

    @Test
    void testLoadStringsHandlesSQLException() throws SQLException {
        dbMock.close();
        dbMock = mockStatic(DatabaseConnection.class);
        dbMock.when(DatabaseConnection::getConnection).thenThrow(new SQLException("Connection failed"));

        // Force a language change to avoid cache hit
        Map<String, String> strings = LocalizationService.loadStrings(new Locale("de", "DE"));

        assertNotNull(strings);
        assertTrue(strings.isEmpty());
    }
}
