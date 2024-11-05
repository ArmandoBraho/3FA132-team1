package com.example.demo.database;

import com.example.demo.interfaces.IDatabaseConnection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection implements IDatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        // Initialize the database connection
        try {
            Properties properties = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                if (input == null) {
                    throw new FileNotFoundException("application.properties file not found");
                }
                properties.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Error loading properties file", e);
            }
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection openConnection() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        if (this.connection == null) {
            throw new RuntimeException("Database connection is not open");
        }
        return connection;
    }
//
//    @Override
//    public IDatabaseConnection openConnection(Properties properties) {
//        if (this.connection == null) {
//            try {
//                String url = properties.getProperty("db.url");
//                String user = properties.getProperty("db.user");
//                String password = properties.getProperty("db.password");
//                this.connection = DriverManager.getConnection(url, user, password);
//            } catch (SQLException e) {
//                throw new RuntimeException("Error opening database connection", e);
//            }
//        }
//        return this;
//    }

    @Override
    public void closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                this.connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing database connection", e);
        }
    }

    @Override
    public IDatabaseConnection openConnection(Properties properties) {
        return DatabaseConnection.openConnection();
    }

    @Override
    public void createAllTables() {
        String createCustomersTableSQL = "CREATE TABLE IF NOT EXISTS customers (" +
                "id CHAR(36)  PRIMARY KEY, " +
                "gender ENUM('D', 'M', 'U', 'W'), " +
                "first_name VARCHAR(255), " +
                "last_name VARCHAR(255), " +
                "birth_date DATE, " +
                "creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        String createReadingsTableSQL = "CREATE TABLE IF NOT EXISTS readings (" +
                "id CHAR(36) PRIMARY KEY, " +
                "customer_id CHAR(36), " +
                "meter_id VARCHAR(255), " +
                "date_of_reading DATE, " +
                "meter_count DOUBLE, " +
                "comment VARCHAR(255), " +
                "kind_of_meter ENUM('ELECTRICITY', 'HEATING', 'WATER', 'UNKNOWN'), " +
                "substitute BOOLEAN, " +
                "creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL" +
                ")";
        try (Statement stmt = connection.createStatement()) {
           stmt.executeQuery(createCustomersTableSQL);
           stmt.executeQuery(createReadingsTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating tables", e);
        }
    }

    @Override
    public void truncateAllTables() {
        String disableForeignKeyChecks = "SET FOREIGN_KEY_CHECKS = 0";
        String enableForeignKeyChecks = "SET FOREIGN_KEY_CHECKS = 1";
        String setCustomerIdToNull = "UPDATE readings SET customer_id = NULL";
        String truncateCustomersTableSQL = "TRUNCATE TABLE customers";
        String truncateReadingsTableSQL = "TRUNCATE TABLE readings";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(disableForeignKeyChecks);
            stmt.execute(setCustomerIdToNull);
            stmt.execute(truncateCustomersTableSQL);
            stmt.execute(enableForeignKeyChecks);
            stmt.execute(truncateReadingsTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error truncating tables", e);
        }
    }

    @Override
    public void removeAllTables() {
        String dropReadingsTableSQL = "DROP TABLE IF EXISTS readings";
        String dropCustomersTableSQL = "DROP TABLE IF EXISTS customers";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropReadingsTableSQL);
            stmt.execute(dropCustomersTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error removing tables", e);
        }
    }

}
