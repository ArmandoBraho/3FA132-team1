package com.example.demo.database;

import com.example.demo.interfaces.IDatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection implements IDatabaseConnection {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    @Override
    public IDatabaseConnection openConnection(Properties properties) {
        try {
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Error opening database connection", e);
        }
        return this;
    }

    @Override
    public void closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing database connection", e);
        }
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
                "FOREIGN KEY (customer_id) REFERENCES customers(id)" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCustomersTableSQL);
            stmt.execute(createReadingsTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating tables", e);
        }
    }

    @Override
    public void truncateAllTables() {
        String disableForeignKeyChecks = "SET FOREIGN_KEY_CHECKS = 0";
        String enableForeignKeyChecks = "SET FOREIGN_KEY_CHECKS = 1";
        String setCustomerUuidToNull = "UPDATE readings SET customer_uuid = NULL";
        String truncateCustomersTableSQL = "TRUNCATE TABLE customers";
        String truncateReadingsTableSQL = "TRUNCATE TABLE readings";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(disableForeignKeyChecks);
            stmt.execute(setCustomerUuidToNull);
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
