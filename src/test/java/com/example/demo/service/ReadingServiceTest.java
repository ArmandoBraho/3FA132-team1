package com.example.demo.service;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.models.Customer;
import com.example.demo.models.Reading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ReadingServiceTest {
    private DatabaseConnection databaseConnection;
    private ReadingService readingService;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;

    @BeforeEach
    public void setUp() throws Exception {
        databaseConnection = mock(DatabaseConnection.class);
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);
        preparedStatement = mock(PreparedStatement.class);


        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        // do I need those here? or better in each test? todo:
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        readingService = new ReadingService(databaseConnection);
    }

    @Test
    public void testGetAllReadings() throws Exception {
        // Mock the ResultSet
        when(resultSet.next()).thenReturn(true, true, false);
        when(UUID.fromString(anyString())).thenReturn(UUID.fromString(UUID.randomUUID().toString()));
        when(resultSet.getString("customer_uuid")).thenReturn(String.valueOf(new Customer(UUID.randomUUID(), null, null, null, null)));
        when(resultSet.getString("uuid")).thenReturn(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        when(resultSet.getString("gender")).thenReturn("M", "W");
        when(resultSet.getString("first_name")).thenReturn("John", "Jane");
        when(resultSet.getString("last_name")).thenReturn("Doe", "Doe");
        when(resultSet.getDate("birth_date")).thenReturn(null, null);

        List<Reading> readings = readingService.getAllReadings();

        assertEquals(2, readings.size());
        assertEquals("John", readings.get(0).getCustomer());
        assertEquals("Jane", readings.get(1).getId());

        verify(statement, times(1)).executeQuery(anyString());
        verify(resultSet, times(3)).next();
        //todo: negative test
    }
}
