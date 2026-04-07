package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecordDaoTest {

    private MockedStatic<DatabaseConnection> dbMock;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private RecordDao recordDao;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);

        dbMock = mockStatic(DatabaseConnection.class);
        dbMock.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        recordDao = new RecordDao();
    }

    @AfterEach
    void tearDown() {
        dbMock.close();
    }

    @Test
    void testSaveRecordSetsCorrectParameters() throws SQLException {
        recordDao.saveRecord(100.0, 8.5, 1.50, 8.5, 12.75, "en");

        verify(mockStatement).setDouble(1, 100.0);
        verify(mockStatement).setDouble(2, 8.5);
        verify(mockStatement).setDouble(3, 1.50);
        verify(mockStatement).setDouble(4, 8.5);
        verify(mockStatement).setDouble(5, 12.75);
        verify(mockStatement).setString(6, "en");
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testSaveRecordWithZeroValues() throws SQLException {
        recordDao.saveRecord(0.0, 0.0, 0.0, 0.0, 0.0, "fr");

        verify(mockStatement).setDouble(1, 0.0);
        verify(mockStatement).setDouble(2, 0.0);
        verify(mockStatement).setDouble(3, 0.0);
        verify(mockStatement).setDouble(4, 0.0);
        verify(mockStatement).setDouble(5, 0.0);
        verify(mockStatement).setString(6, "fr");
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testSaveRecordWithLargeValues() throws SQLException {
        recordDao.saveRecord(99999.99, 50.0, 9.99, 49999.995, 499499.95005, "ja");

        verify(mockStatement).setDouble(1, 99999.99);
        verify(mockStatement).setDouble(2, 50.0);
        verify(mockStatement).setDouble(3, 9.99);
        verify(mockStatement).setDouble(4, 49999.995);
        verify(mockStatement).setDouble(5, 499499.95005);
        verify(mockStatement).setString(6, "ja");
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testSaveRecordHandlesSQLException() throws SQLException {
        dbMock.close();
        dbMock = mockStatic(DatabaseConnection.class);
        dbMock.when(DatabaseConnection::getConnection).thenThrow(new SQLException("Connection refused"));

        // Should not throw, just prints error
        assertDoesNotThrow(() -> recordDao.saveRecord(100.0, 8.5, 1.50, 8.5, 12.75, "en"));
    }

    @Test
    void testSaveRecordHandlesExecuteUpdateFailure() throws SQLException {
        when(mockStatement.executeUpdate()).thenThrow(new SQLException("Insert failed"));

        assertDoesNotThrow(() -> recordDao.saveRecord(100.0, 8.5, 1.50, 8.5, 12.75, "en"));
    }
}
