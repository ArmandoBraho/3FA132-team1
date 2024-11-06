package com.example.demo.unit;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.models.Customer;
import com.example.demo.models.Reading;
import com.example.demo.service.ReadingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
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
        //todo: use instancio for random data generation, here not necessary but in create and update yes
        // Mock the ResultSet
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("id")).thenReturn(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        when(resultSet.getString("customer_id")).thenReturn(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        when(resultSet.getString("meter_id")).thenReturn(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        when(resultSet.getString("date_of_reading")).thenReturn(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        when(resultSet.getString("meter_count")).thenReturn("M", "W");
        when(resultSet.getString("comment")).thenReturn("first comment", "second comment");
        when(resultSet.getString("kind_of_meter")).thenReturn("WATER", "ELECTRICITY");
        when(resultSet.getBoolean("substitute")).thenReturn(true, false);
        when(resultSet.getDate("creation_date")).thenReturn(new Date(2022, 12, 12), new Date(2025, 11, 14));

        List<Reading> readings = readingService.getAllReadings();

        assertEquals(2, readings.size());
        assertEquals("first comment", readings.get(0).getComment());
        assertEquals("second comment", readings.get(1).getComment());

        verify(statement, times(1)).executeQuery(anyString());
        verify(resultSet, times(3)).next();
        //todo: negative test
    }

    @Test
    public void testGetReadingById() throws Exception {
        UUID idOfSearchedReading = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        // Mock the ResultSet
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("id")).thenReturn(idOfSearchedReading.toString());
        when(resultSet.getString("customer_id")).thenReturn(UUID.randomUUID().toString());
        when(resultSet.getString("meter_id")).thenReturn(UUID.randomUUID().toString());
        when(resultSet.getDate("date_of_reading")).thenReturn(Date.valueOf(LocalDate.of(2025, 11, 15)));
        when(resultSet.getString("meter_count")).thenReturn("W");
        when(resultSet.getString("comment")).thenReturn("second comment");
        when(resultSet.getString("kind_of_meter")).thenReturn("ELECTRICITY");
        when(resultSet.getBoolean("substitute")).thenReturn(false);
        when(resultSet.getDate("creation_date")).thenReturn(Date.valueOf(LocalDate.of(2022, 9, 12)));

        Reading reading = readingService.getReadingById(idOfSearchedReading);

        assert(reading.getId()).equals(idOfSearchedReading);
        assert(reading.getDateOfReading()).equals(LocalDate.of(2025, 11, 15));
        assert(reading.getSubstitute()).equals(false);
        assert("second comment").equals(reading.getComment());
        assert("ELECTRICITY").equals(reading.getKindOfMeter().toString());

        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    public void testCreateReading() throws Exception {
        Reading reading = new Reading(
                new Customer(UUID.randomUUID(), null, null, null, null),
                UUID.randomUUID(),
                "meter1",
                LocalDate.of(2021, 1, 1),
                100.0,
                "comment1",
                Reading.KindOfMeter.ELECTRICITY,
                false
        );

        readingService.createReading(reading);

        //todo: does it makes sense? advantage: I check it does it only once but nothing more, no much extra logic that it checks
        verify(preparedStatement, times(1)).setString(1, reading.getId().toString());
        verify(preparedStatement, times(1)).setString(2, reading.getCustomer().getId().toString());
        verify(preparedStatement, times(1)).setString(3, reading.getMeterId());
        verify(preparedStatement, times(1)).setDate(4, Date.valueOf(reading.getDateOfReading()));
        verify(preparedStatement, times(1)).setDouble(5, reading.getMeterCount());
        verify(preparedStatement, times(1)).setString(6, reading.getComment());
        verify(preparedStatement, times(1)).setString(7, reading.getKindOfMeter().toString());
        verify(preparedStatement, times(1)).setBoolean(8, reading.getSubstitute());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testCreateAndUpdateReading() throws Exception {
        Reading reading = new Reading(
                new Customer(UUID.randomUUID(), null, null, null, null),
                UUID.randomUUID(),
                "Not of",
                LocalDate.of(2021, 1, 1),
                100.0,
                "your business",
                Reading.KindOfMeter.ELECTRICITY,
                false
        );

//        Reading createdReading = readingService.createReading(reading);
//        AND THEN UPDATE BUT STILL DOES NOT MAKE SO MUCH SENSE SUCH A TEST
//        readingService.updateReading(reading);
//
//        verify(preparedStatement, times(1)).setString(1, reading.getId().toString());
//        verify(preparedStatement, times(1)).setString(2, reading.getCustomer().getId().toString());
//        verify(preparedStatement, times(1)).setString(3, reading.getMeterId());
//        verify(preparedStatement, times(1)).setDate(4, Date.valueOf(reading.getDateOfReading()));
//        verify(preparedStatement, times(1)).setDouble(5, reading.getMeterCount());
//        verify(preparedStatement, times(1)).setString(6, reading.getComment());
//        verify(preparedStatement, times(1)).setString(7, reading.getKindOfMeter().toString());
//        verify(preparedStatement, times(1)).setBoolean(8, reading.getSubstitute());
//        verify(preparedStatement, times(1)).setString(9, reading.getId().toString());
//        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteReading() throws Exception {
        UUID readingId = UUID.randomUUID();

        readingService.deleteReading(readingId);

        verify(preparedStatement, times(1)).setString(1, readingId.toString());
        verify(preparedStatement, times(1)).executeUpdate();
    }
}
