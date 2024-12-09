package com.example.demo.service;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.models.Customer;
import com.example.demo.models.Reading;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReadingService {
    private final CustomerService customerService;
    private final Connection connection;


    public ReadingService(DatabaseConnection databaseConnection) {
        this.connection = databaseConnection.getConnection();
        this.customerService = new CustomerService(databaseConnection);
    }

    public List<Reading> getAllReadings() {
        List<Reading> readings = new ArrayList<>();
        String query = "SELECT * FROM readings";
        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Reading reading = new Reading(
                        customerService.getCustomer(UUID.fromString(rs.getString("customer_id"))),
                        UUID.fromString(rs.getString("id")),
                        rs.getString("meter_id"),
                        rs.getDate("date_of_reading") != null ? rs.getDate("date_of_reading").toLocalDate() : null,
                        rs.getDouble("meter_count"),
                        rs.getString("comment"),
                        rs.getString("kind_of_meter") != null ? Reading.KindOfMeter.valueOf(rs.getString("kind_of_meter")) : null,
                        rs.getBoolean("substitute")
                );
                readings.add(reading);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all readings", e);
        }
        return readings;
    }

    public Reading getReading(UUID id) {
        String query = "SELECT * FROM readings WHERE id = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Reading(
                        new Customer(UUID.fromString(rs.getString("customer_id")), null, null, null, null),
                        UUID.fromString(rs.getString("id")),
                        rs.getString("meter_id"),
                        rs.getDate("date_of_reading") != null ? rs.getDate("date_of_reading").toLocalDate() : null,
                        rs.getDouble("meter_count"),
                        rs.getString("comment"),
                        rs.getString("kind_of_meter") != null ? Reading.KindOfMeter.valueOf(rs.getString("kind_of_meter")) : null,
                        rs.getBoolean("substitute")
                );
            }
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException("Error retrieving reading by id", e);
        }
        return null;
    }


    public Reading createReading(Reading reading) {
        String checkCustomerQuery = "SELECT COUNT(*) FROM customers WHERE id = ?";
        String checkReadingQuery = "SELECT COUNT(*) FROM readings WHERE id = ?";
        String insertReadingQuery = "INSERT INTO readings (id, customer_id, meter_id, date_of_reading, meter_count, comment, kind_of_meter, substitute) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement checkCustomerStmt = this.connection.prepareStatement(checkCustomerQuery);
             PreparedStatement checkReadingStmt = this.connection.prepareStatement(checkReadingQuery);
             PreparedStatement insertReadingStmt = this.connection.prepareStatement(insertReadingQuery)
        ) {
            checkReadingStmt.setString(1, reading.getId().toString());
            ResultSet rsReading = checkReadingStmt.executeQuery();
            if (rsReading.next() && rsReading.getInt(1) > 0) {
                System.out.println("Reading with id " + reading.getId() + " already exists");
                return reading;
            }
            checkCustomerStmt.setString(1, reading.getCustomer().getId().toString());
            ResultSet rs = checkCustomerStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // desired business logic: if the customer, associated with the reading, does not exist --> create it
                customerService.createCustomer(reading.getCustomer());
            }

            insertReadingStmt.setString(1, reading.getId().toString());
            insertReadingStmt.setString(2, reading.getCustomer().getId().toString());
            insertReadingStmt.setString(3, reading.getMeterId());
            insertReadingStmt.setDate(4, Date.valueOf(reading.getDateOfReading()));
            insertReadingStmt.setDouble(5, reading.getMeterCount());
            insertReadingStmt.setString(6, reading.getComment());
            insertReadingStmt.setString(7, reading.getKindOfMeter().toString());
            insertReadingStmt.setBoolean(8, reading.getSubstitute());
            int affectedRows = insertReadingStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating reading failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reading;

    }


    public void updateReading(Reading reading) {
        String query = "UPDATE readings SET id = ?, customer_id = ?, meter_id = ?, date_of_reading = ?, meter_count = ?, comment = ?, kind_of_meter = ?, substitute = ? WHERE id = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, reading.getId().toString());
            pstmt.setString(2, reading.getCustomer().getId().toString());
            pstmt.setString(3, reading.getMeterId());
            pstmt.setDate(4, reading.getDateOfReading() != null ? Date.valueOf(reading.getDateOfReading()) : null);
            pstmt.setDouble(5, reading.getMeterCount());
            pstmt.setString(6, reading.getComment());
            pstmt.setString(7, reading.getKindOfMeter() != null ? reading.getKindOfMeter().toString() : null);
            pstmt.setBoolean(8, reading.getSubstitute());
            pstmt.setString(9, reading.getId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating reading", e);
        }
    }

    public void deleteReading(UUID id) {
        String query = "DELETE FROM readings WHERE id = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reading by id", e);
        }
    }
}
