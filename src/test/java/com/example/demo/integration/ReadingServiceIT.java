package com.example.demo.integration;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.models.Customer;
import com.example.demo.models.Reading;
import com.example.demo.service.ReadingService;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReadingServiceIT {

//    RIPRENDI DA HERE i GUEES     the test should be mostly combined like you create and then make a get by id or get all customers......
//    RIPRENDI DA HERE i GUEES     the test should be mostly combined like you create and then make a get by id or get all customers......
//    RIPRENDI DA HERE i GUEES     the test should be mostly combined like you create and then make a get by id or get all customers......
//    RIPRENDI DA HERE i GUEES     the test should be mostly combined like you create and then make a get by id or get all customers......
    private DatabaseConnection databaseConnection;
    private ReadingService readingService;

    @BeforeAll
    public void setUp() {
        Properties properties = new Properties();
        // todo: duplicate code into sepearate utility class??
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new FileNotFoundException("application.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file", e);
        }

        databaseConnection = new DatabaseConnection();
        databaseConnection.openConnection(properties);
        readingService = new ReadingService(databaseConnection);

        // Initialize the database schema
        databaseConnection.createAllTables();
    }

    @AfterAll
    public void tearDown() {
        databaseConnection.removeAllTables();
        databaseConnection.closeConnection();
    }

    @Test
    public void testCreateAndGetReading() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, null, null, null, null);
        Reading reading = new Reading(customer, UUID.randomUUID(), "meter1", LocalDate.of(2021, 1, 1), 100.0, "comment1", Reading.KindOfMeter.ELECTRICITY, false);

        readingService.createReading(reading);

        Reading retrievedReading = readingService.getReadingById(reading.getId());
        assertNotNull(retrievedReading);
        assertEquals(reading.getId(), retrievedReading.getId());
        assertEquals("meter1", retrievedReading.getMeterId());
    }

//    @Test
//    public void testGetAllReadings() {
//        List<Reading> readings = readingService.getAllReadings();
//        assertNotNull(readings);
//        assertEquals(1, readings.size());
//    }
//
//    @Test
//    public void testUpdateReading() {
//        List<Reading> readings = readingService.getAllReadings();
//        Reading reading = readings.get(0);
//        reading.setComment("updated comment");
//
//        readingService.updateReading(reading);
//
//        Reading updatedReading = readingService.getReadingById(reading.getId());
//        assertEquals("updated comment", updatedReading.getComment());
//    }
//
//    @Test
//    public void testDeleteReading() {
//        List<Reading> readings = readingService.getAllReadings();
//        Reading reading = readings.get(0);
//
//        readingService.deleteReading(reading.getId());
//
//        Reading deletedReading = readingService.getReadingById(reading.getId());
//        assertEquals(null, deletedReading);
//    }
}