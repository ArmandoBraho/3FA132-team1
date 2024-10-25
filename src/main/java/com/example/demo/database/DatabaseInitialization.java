package com.example.demo.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

import com.example.demo.models.Client;


public class DatabaseInitialization {

    private static final String CSV_FILE_PATH = "src/main/resources/database-csv-files/clients.csv";
    private BasicDataSource dataSource; // Add this field

    // Setter for dataSource
    public void setDataSource(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initialize() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            List<Client> clients = new ArrayList<>();
            br.readLine(); // Skip the first line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Client client = new Client(
                        values[0],  // uuid
                        values[1],  // title
                        values[2],  // first_name
                        values[3],  // last_name
                        values.length > 4 && !values[4].isEmpty() ? new Date(new SimpleDateFormat("dd.MM.yyyy").parse(values[4]).getTime()) : null
                );
                clients.add(client);
            }
            saveClientsToDatabase(clients);
        } catch (IOException | RuntimeException | ParseException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    private void saveClientsToDatabase(List<Client> clients) {
        String insertSQL = "INSERT INTO clients (uuid, title, first_name, last_name, birth_date) VALUES (?, ?, ?, ?, ?)";
        
        // Load properties as before...
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new FileNotFoundException("application.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file", e);
        }

        if (dataSource == null) {
            dataSource = new BasicDataSource(); // Use the injected dataSource if available
            dataSource.setUrl(properties.getProperty("db.url"));
            dataSource.setUsername(properties.getProperty("db.user"));
            dataSource.setPassword(properties.getProperty("db.password"));
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            for (Client client : clients) {
                pstmt.setString(1, client.getUuid());
                pstmt.setString(2, client.getTitle());
                pstmt.setString(3, client.getFirstName());
                pstmt.setString(4, client.getLastName());
                pstmt.setDate(5, client.getBirthDate());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving clients to database", e);
        }
    }
}
