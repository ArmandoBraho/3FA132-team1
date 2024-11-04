package com.example.demo.database;


import com.example.demo.interfaces.IReading;
import com.example.demo.models.Customer;
import com.example.demo.models.Reading;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DatabaseInitialization {

    DatabaseConnection databaseConnection = new DatabaseConnection();
    Properties properties = new Properties();


    public void initialize() {
        loadPropretiesFromApplicationProperties();
        databaseConnection = (DatabaseConnection) databaseConnection.openConnection(properties);
        databaseConnection.removeAllTables();
        databaseConnection.createAllTables();

        initializeCustomersTable();
        databaseConnection = (DatabaseConnection) databaseConnection.openConnection(properties);
        initializeReadingsTable();
    }

    private void loadPropretiesFromApplicationProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new FileNotFoundException("application.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file", e);
        }
    }

    private void initializeCustomersTable() {
        List<Customer> customers = new ArrayList<>();
        extractCustomersInformationsFromCSVFile(customers);
        insertCustomersToDB(customers);
    }

    private void initializeReadingsTable() {
        List<Reading> readings = new ArrayList<>();
        final String ELECTRICITY_READINGS_CSV_FILE_PATH = "src/main/resources/database-csv-files/electricity.csv";
        final String HEATING_READINGS_CSV_FILE_PATH = "src/main/resources/database-csv-files/heating.csv";
        final String WATER_READINGS_CSV_FILE_PATH = "src/main/resources/database-csv-files/water.csv";

        extractMeasurementsInformationsFromCSVFile(readings, ELECTRICITY_READINGS_CSV_FILE_PATH, IReading.KindOfMeter.ELECTRICITY);
        extractMeasurementsInformationsFromCSVFile(readings, HEATING_READINGS_CSV_FILE_PATH, IReading.KindOfMeter.HEATING);
        extractMeasurementsInformationsFromCSVFile(readings, WATER_READINGS_CSV_FILE_PATH, IReading.KindOfMeter.WATER);

        insertReadingsToDB(readings);
    }

    public void extractCustomersInformationsFromCSVFile(List<Customer> customers) {
        final String CUSTOMERS_CSV_FILE_PATH = "src/main/resources/database-csv-files/customers.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_CSV_FILE_PATH))) {
            String line;
            br.readLine(); // Skip the first line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Customer client = new Customer(
                        UUID.fromString(values[0]),  // uuid
                        com.example.demo.interfaces.ICustomer.Gender.valueOf(values[1].equals("Herr") ? "M" : values[1].equals("Frau") ? "W" : values[1].equals("Divers") ? "D" : "U"),  // gender
                        values[2],  // first_name
                        values[3],   // last_name
                        values.length > 4 && !values[4].isEmpty() ? new Date(new SimpleDateFormat("dd.MM.yyyy").parse(values[4]).getTime()).toLocalDate() : null); // birth_date
                customers.add(client);
            }
        } catch (IOException | RuntimeException | ParseException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    public void extractMeasurementsInformationsFromCSVFile(List<Reading> readings, String filePath, IReading.KindOfMeter kindOfMeter) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            boolean substitute = false;
            String line;
            br.readLine(); // Skip the first line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                Reading reading = new Reading(
                        UUID.fromString(values[0]),  // customer_uuid
                        values[1],  // meter_id
                        new Date(new SimpleDateFormat("dd.MM.yyyy").parse(values[2]).getTime()).toLocalDate(),  // date_of_reading
                        Double.valueOf(values[3].replace(",", ".")),  // meter_count
                        values.length > 4 && !values[4].isEmpty() ? values[4] : "",  // comment
                        kindOfMeter,
                        substitute
                );
                if (values.length > 4 && !values[4].isEmpty()) {
                    substitute = true;
                }
                readings.add(reading);
            }
        } catch (IOException | RuntimeException | ParseException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    private void insertCustomersToDB(List<Customer> customers) {
        String checkSQL = "SELECT COUNT(*) FROM customers";
        String insertSQL = "INSERT INTO customers (uuid, gender, first_name, last_name, birth_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) { // If the table is empty
                for (Customer client : customers) {
                    pstmt.setString(1, client.getId().toString());
                    pstmt.setString(2, client.getGender().toString());
                    pstmt.setString(3, client.getFirstName());
                    pstmt.setString(4, client.getLastName());
                    pstmt.setDate(5, client.getBirthDate() != null ? Date.valueOf(client.getBirthDate()) : null);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving customers to database", e);
        }
    }

    private void insertReadingsToDB(List<Reading> readings) {
        String checkSQL = "SELECT COUNT(*) FROM readings";
        String insertSQL = "INSERT INTO readings (customer_uuid, meter_id, date_of_reading, meter_count, comment, kind_of_meter, substitute) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) { // If the table is empty
                for (Reading reading : readings) {
                    pstmt.setString(1, reading.getCustomerUUID().toString());
                    pstmt.setString(2, reading.getMeterId());
                    pstmt.setObject(3, reading.getDateOfReading());
                    pstmt.setDouble(4, reading.getMeterCount());
                    pstmt.setString(5, reading.getComment());
                    pstmt.setString(6, reading.getKindOfMeter().toString());
                    pstmt.setBoolean(7, reading.getSubstitute());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving readings to database", e);
        }
    }

}
