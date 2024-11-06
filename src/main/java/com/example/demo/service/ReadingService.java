package com.example.demo.service;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.models.Customer;
import com.example.demo.models.Reading;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReadingService {
    private final DatabaseConnection databaseConnection;

    public ReadingService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public List<Reading> getAllReadings() {
        List<Reading> readings = new ArrayList<>();
        String query = "SELECT * FROM readings";
        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Reading reading = new Reading(
                        //todo: critical to change???!!!!
                        new Customer(UUID.fromString(rs.getString("customer_id")), null, null, null, null),
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

    // todo: to test it manually need like a stable uuid, but they are generated manually ---> test with a test or make a fix uuid for the first reading
    public Reading getReadingById(UUID id) {
        String query = "SELECT * FROM readings WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            // todo: ask if maybe a while would be better to check if it really only one readings is associated with the "id" paramater, but we have a return so we would need an extra condition...
            // todo: ... like while that and also length of rs == 1
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
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving reading by id", e);
        }
        return null;
    }

    public void createReading(Reading reading) {
        String query = "INSERT INTO readings (id, customer_id, meter_id, date_of_reading, meter_count, comment, kind_of_meter, substitute) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            // duplicate code with DatabaseInitialization , put in common function addReading ??? todo:
            // todo: sql injection
            pstmt.setString(1, reading.getId().toString());
            pstmt.setString(2, reading.getCustomer().getId().toString());
            pstmt.setString(3, reading.getMeterId());
            pstmt.setDate(4, Date.valueOf(reading.getDateOfReading()));
            pstmt.setDouble(5, reading.getMeterCount());
            pstmt.setString(6, reading.getComment());
            pstmt.setString(7, reading.getKindOfMeter().toString());
            pstmt.setBoolean(8, reading.getSubstitute());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating customer by id", e);
        }
    }

    public void updateReading(Reading reading) {
        String query = "UPDATE readings SET id = ?, customer_id = ?, meter_id = ?, date_of_reading = ?, meter_count = ?, comment = ?, kind_of_meter = ?, substitute = ? WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
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
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reading by id", e);
        }
    }
}
