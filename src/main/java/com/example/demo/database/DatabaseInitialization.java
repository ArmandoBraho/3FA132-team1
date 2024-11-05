package com.example.demo.database;


import com.example.demo.interfaces.IReading;
import com.example.demo.models.Customer;
import com.example.demo.models.Reading;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ReadingService;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DatabaseInitialization {

    DatabaseConnection databaseConnection = new DatabaseConnection();
    CustomerService customerService = new CustomerService(databaseConnection);
        ReadingService readingService = new ReadingService(databaseConnection);

    Properties properties = new Properties();


    public void initialize() {
        loadPropretiesFromApplicationProperties();
        databaseConnection = (DatabaseConnection) databaseConnection.openConnection(properties);
        databaseConnection.removeAllTables();
        databaseConnection.createAllTables();

        initializeCustomersTable();
        databaseConnection = (DatabaseConnection) databaseConnection.openConnection(properties);
        initializeReadingsTable();

        // todo: temporary solution to check if it works ----> tests   or  endpoint
//        databaseConnection = (DatabaseConnection) databaseConnection.openConnection(properties);
//        List<Customer> customers = customerService.getAllCustomers();
//        databaseConnection = (DatabaseConnection) databaseConnection.openConnection(properties);
//        List<Reading> readings = readingService.getAllReadings();

//        not working atm
//        Reading readingExample = new Reading( new Customer(UUID.fromString("f47b3b2b-3f3b-4b7b-8b3b-2b3f3b4b7b8b"), null, null, null, null), UUID.fromString("f47b3b2b-3f3b-4b7b-8b3b-2b3"), "123456", LocalDate.parse("2021-01-01"), 123.45, "aaaComment", IReading.KindOfMeter.ELECTRICITY, false);
//        databaseConnection = (DatabaseConnection) databaseConnection.openConnection(properties);
//        Reading readingBefore = readingService.getReadingById(UUID.fromString("f47b3b2b-3f3b-4b7b-8b3b-2b3f3b4b7b8b"));
//        databaseConnection = (DatabaseConnection) databaseConnection.openConnection(properties);
//        readingService.createReading(readingExample);
//        databaseConnection = (DatabaseConnection) databaseConnection.openConnection(properties);
//        Reading readingAfter = readingService.getReadingById(UUID.fromString("f47b3b2b-3f3b-4b7b-8b3b-2b3f3b4b7b8b"));
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
                        // todo: ask teacher if it is what they wanted
                        // what if there is no assigned custoemr??
                        //todo: should be following row
//                        customerService.getCustomerById(UUID.fromString(values[0])), //customer
                        // the problem with the above implementation is that the connection needs to be open every time we want to get a customer by id through customerService
                        new Customer(UUID.fromString(values[0]), null, null, null, null), //customer
                        // it"s ok atm because we just use it to keep the customer_uuid reference, then in the getCustomer should be a request -----------------
                        // --------------------------------------------------------------
//                        RIPRENDI DA THIS ISSUE; MAKE CONNECTION POOL??
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
            throw new RuntimeException("Error reading measurements from CSV file", e);
        }
    }

    private void insertCustomersToDB(List<Customer> customers) {
        String checkSQL = "SELECT COUNT(*) FROM customers";
        String insertSQL = "INSERT INTO customers (id, gender, first_name, last_name, birth_date) VALUES (?, ?, ?, ?, ?)";

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
        String insertSQL = "INSERT INTO readings (id, customer_id, meter_id, date_of_reading, meter_count, comment, kind_of_meter, substitute) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
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
