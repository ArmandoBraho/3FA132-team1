package com.example.demo.database;


import com.example.demo.models.Client;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseInitialization {

    private static final String CSV_FILE_PATH = "src/main/resources/database-csv-files/clients.csv";

    public void initialize() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            List<Client> clients = new ArrayList<>();
            // the skip here
            br.readLine(); // Skip the first line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Client client = new Client(
                        values[0],  // uuid
                        values[1],  // title
                        values[2],  // first_name
                        values[3],   // last_name
                        values.length > 4 && !values[4].isEmpty() ? new Date(new SimpleDateFormat("dd.MM.yyyy").parse(values[4]).getTime()) : null);
                clients.add(client);
            }

            saveClientsToDatabase(clients);
        } catch (IOException | RuntimeException | ParseException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    private void saveClientsToDatabase(List<Client> clients) {
        String insertSQL = "INSERT INTO clients (uuid, title, first_name, last_name, birth_date) VALUES (?, ?, ?, ?, ?)";
// RIPRENDI DA    K.A MUSS ZU NULL GEHEN.......> MAYBE GENERAL LOGIC IF K.A OR EMPTY THEN null???? ITERATE INPUT PROBABLY
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new FileNotFoundException("application.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file", e);
        }

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(properties.getProperty("db.url"));
        dataSource.setUsername(properties.getProperty("db.user"));
        dataSource.setPassword(properties.getProperty("db.password"));

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            for (Client client : clients) {
                pstmt.setString(1, client.getUuid());
                pstmt.setString(2, client.getTitle());
                pstmt.setString(3, client.getFirstName());
                pstmt.setString(4, client.getLastName());
                pstmt.setDate(5, client.getBirthDate());
                // todo birthdate as date not as string
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving clients to database", e);
        }
    }

//    List<List<String>> records = new ArrayList<>();
//
//    public void initialize() {
//        try {
//
//            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/database-csv-files/clients.csv"));
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] values = line.split(",");
//                records.add(Arrays.asList(values));
//            }
////            saveRecordsToDatabase();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


//    private void saveRecordsToDatabase() {
//        BasicDataSource dataSource = new BasicDataSource();
//        String insertSQL = "INSERT INTO clients (uuid, title, first_name, last_name, birth_date) VALUES (?, ?, ?, ?, ?)"; // Adjust columns as needed
//
//        // Load properties from application.properties
//        Properties properties = new Properties();
//        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
//            if (input == null) {
//                throw new FileNotFoundException("application.properties file not found");
//            }
//            properties.load(input);
//        } catch (IOException e) {
//            throw new RuntimeException("Error loading properties file", e);
//        }
//
//        dataSource.setUrl(properties.getProperty("db.url"));
//        dataSource.setUsername(properties.getProperty("db.user"));
//        dataSource.setPassword(properties.getProperty("db.password"));
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
//
//            for (List<String> record : records) {
//                pstmt.setString(1, record.get(0));
//
////  RIPRENDI DA QUI ::::::::::::::::::::
////  RIPRENDI DA QUI ::::::::::::::::::::
////  RIPRENDI DA QUI ::::::::::::::::::::
////                GO DEBUG MODUS AND SEE RECORD;
////                pstmt.setArray(1, record.get(0));
//                pstmt.setString(3, record.get(2));
//                pstmt.setString(4, record.get(3));
//                // Set more columns as needed
//                pstmt.addBatch();
//            }
//            pstmt.executeBatch();
//        } catch (SQLException e) {
//            throw new RuntimeException("Error saving records to database", e);
//        }
//    }

}
