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

    DatabaseConnection databaseConnection = DatabaseConnection.openConnection();
    private final Connection connection;

    Properties properties = new Properties();

    public DatabaseInitialization(Connection connection) {
        this.connection = connection;
    }


    public void initialize() {
        loadPropretiesFromApplicationProperties();
        List<Customer> customers = new ArrayList<>();
        List<Reading> readings = new ArrayList<>();

        databaseConnection.removeAllTables();
        databaseConnection.removeAllTables();
        databaseConnection.createAllTables();

        initializeCustomersTable(customers);
        initializeReadingsTable(readings);
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

    private void initializeCustomersTable(List<Customer> customers) {
        extractCustomersInformationsFromCSVFile(customers);
        insertCustomersToDB(customers);
    }

    private void initializeReadingsTable(List<Reading> readings) {
        //todo: is it better so??
//        final String ELECTRICITY_READINGS_CSV_FILE_PATH = Thread.currentThread().getContextClassLoader().getResourceAsStream("database-csv-files/electricity.csv");

        final String ELECTRICITY_READINGS_CSV_FILE_PATH = "src/main/resources/database-csv-files/electricity.csv";
        final String HEATING_READINGS_CSV_FILE_PATH = "src/main/resources/database-csv-files/heating.csv";
        final String WATER_READINGS_CSV_FILE_PATH = "src/main/resources/database-csv-files/water.csv";

        extractMeasurementsInformationsFromCSVFile(readings, ELECTRICITY_READINGS_CSV_FILE_PATH, IReading.KindOfMeter.ELECTRICITY);
        extractMeasurementsInformationsFromCSVFile(readings, HEATING_READINGS_CSV_FILE_PATH, IReading.KindOfMeter.HEATING);
        extractMeasurementsInformationsFromCSVFile(readings, WATER_READINGS_CSV_FILE_PATH, IReading.KindOfMeter.WATER);

        insertReadingsToDB(readings);
    }

    public void extractCustomersInformationsFromCSVFile(List<Customer> customers) {
        //todo: leo InputStream thing
        //   pass etc. into separate folder
        final String CUSTOMERS_CSV_FILE_PATH = "src/main/resources/database-csv-files/customers.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_CSV_FILE_PATH))) {
            String line;
            br.readLine(); // Skip the first line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Customer customer = new Customer(
                        UUID.fromString(values[0]),  // id
                        com.example.demo.interfaces.ICustomer.Gender.valueOf(values[1].equals("Herr") ? "M" : values[1].equals("Frau") ? "W" : values[1].equals("Divers") ? "D" : "U"),  // gender
                        values[2],  // first_name
                        values[3],   // last_name
                        values.length > 4 && !values[4].isEmpty() ? new Date(new SimpleDateFormat("dd.MM.yyyy").parse(values[4]).getTime()).toLocalDate() : null); // birth_date
                customers.add(customer);
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
                        new Customer(UUID.fromString(values[0]), null, null, null, null), //customer
                        UUID.fromString(values[0]),  // customer_id
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
            throw new RuntimeException("Error reading measurements from CSV file", e);
        }
    }

    private void insertCustomersToDB(List<Customer> customers) {
        String checkSQL = "SELECT COUNT(*) FROM customers";
        String insertSQL = "INSERT INTO customers (id, gender, first_name, last_name, birth_date) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = this.connection.prepareStatement(checkSQL);
             PreparedStatement pstmt = this.connection.prepareStatement(insertSQL)) {
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) { // If the table is empty
                for (Customer customer : customers) {
                    pstmt.setString(1, customer.getId().toString());
                    pstmt.setString(2, customer.getGender().toString());
                    pstmt.setString(3, customer.getFirstName());
                    pstmt.setString(4, customer.getLastName());
                    pstmt.setDate(5, customer.getBirthDate() != null ? Date.valueOf(customer.getBirthDate()) : null);
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
        String insertSQL = "INSERT INTO readings (id, customer_id, meter_id, date_of_reading, meter_count, comment, kind_of_meter, substitute) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = this.connection.prepareStatement(checkSQL);
             PreparedStatement pstmt = this.connection.prepareStatement(insertSQL)) {
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) { // If the table is empty
                for (Reading reading : readings) {
                    pstmt.setString(1, UUID.randomUUID().toString());
                    pstmt.setString(2, reading.getCustomer().getId().toString());
                    pstmt.setString(3, reading.getMeterId());
                    pstmt.setObject(4, reading.getDateOfReading());
                    pstmt.setDouble(5, reading.getMeterCount());
                    pstmt.setString(6, reading.getComment());
                    pstmt.setString(7, reading.getKindOfMeter().toString());
                    pstmt.setBoolean(8, reading.getSubstitute());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving readings to database", e);
        }
    }

}
