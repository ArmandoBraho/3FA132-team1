package com.example.demo.service;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.models.Reading;
import com.example.demo.models.Customer;
import com.example.demo.utils.ReadingQueryBuilder;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReadingService {
    private final CustomerService customerService;
    private final Connection connection;
    private static final DatabaseConnection databaseConnection = DatabaseConnection.openConnection();

    public ReadingService() {
        this.connection = databaseConnection.getConnection();
        this.customerService = new CustomerService();
    }

    public List<Reading> getFilteredReadings(UUID customer, LocalDate startDate, LocalDate endDate, Reading.KindOfMeter kindOfMeter) {
        List<Reading> readings = new ArrayList<>();
        // todo: query and params handle at once
        String query = new ReadingQueryBuilder()
                .withCustomer(customer)
                .withDateRange(startDate, endDate)
                .withKindOfMeter(kindOfMeter == null ? null : kindOfMeter.toString())
                .getQuery();

        List<Object> params = new ReadingQueryBuilder()
                .withCustomer(customer)
                .withDateRange(startDate, endDate)
                .withKindOfMeter(kindOfMeter == null ? null : kindOfMeter.toString())
                .getParams();

        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Reading reading = new Reading(
                        rs.getString("customer_id") == null ? null : customerService.getCustomer(UUID.fromString(rs.getString("customer_id"))),
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

    //Overloading Pattern instead of building pattern, since simpler
    public List<Reading> getAllReadings() {
        return getAllReadings(new ArrayList<>());
    }

    public List<Reading> getAllReadings(List<UUID> ids) {
        List<Reading> readings = new ArrayList<>();
        String query;

        if (ids.isEmpty()) {
            query = "SELECT * FROM readings";
        } else {
            // Create placeholders string
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ids.size(); i++) {
                sb.append("?");
                if (i < ids.size() - 1) {
                    sb.append(", ");
                }
            }

            query = "SELECT * FROM readings WHERE id IN (" + sb.toString() + ")";
        }

        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            for (int i = 0; i < ids.size(); i++) {
                stmt.setString(i + 1, ids.get(i).toString());
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Reading reading = new Reading(
                        rs.getString("customer_id") == null ? null : customerService.getCustomer(UUID.fromString(rs.getString("customer_id"))),
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

    public List<Reading> getAllReadingsByCustomerId(UUID id) {
        List<Reading> readings = new ArrayList<>();
        String query = "SELECT * FROM readings WHERE customer_id = ?";

        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {

            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Reading reading = new Reading(
                        rs.getString("customer_id") == null ? null : customerService.getCustomer(UUID.fromString(rs.getString("customer_id"))),
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
                        rs.getString("customer_id") == null ? null : customerService.getCustomer(UUID.fromString(rs.getString("customer_id"))),
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
        String selectQuery = "SELECT * FROM readings WHERE id = ?";
        String readingId = reading.getId() == null ? UUID.randomUUID().toString() : reading.getId().toString();
        String customerId = Optional.ofNullable(reading.getCustomer())
                .map(Customer::getId)
                .map(Object::toString)
                .orElse(null);
        try (PreparedStatement checkCustomerStmt = this.connection.prepareStatement(checkCustomerQuery);
             PreparedStatement checkReadingStmt = this.connection.prepareStatement(checkReadingQuery);
             PreparedStatement insertReadingStmt = this.connection.prepareStatement(insertReadingQuery);
             PreparedStatement selectStmt=this.connection.prepareStatement(selectQuery))
        {
            checkReadingStmt.setString(1, readingId);
            ResultSet rsReading = checkReadingStmt.executeQuery();
            if (rsReading.next() && rsReading.getInt(1) > 0) {
                System.out.println("Reading with id " + reading.getId() + " already exists");
                return reading;
            }
            checkCustomerStmt.setString(1, readingId);
            ResultSet rs = checkCustomerStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // desired business logic: if the customer, associated with the reading, does not exist (aka. the customerId does not exist) --> create the whole customer
                customerId = customerService.createCustomer(reading.getCustomer()).getId().toString();
            }

            insertReadingStmt.setString(1, readingId);
            insertReadingStmt.setString(2, customerId);
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

            selectStmt.setString(1, readingId);
            ResultSet savedReadingRs = selectStmt.executeQuery();
            if (savedReadingRs.next()) {
                return new Reading(
                        savedReadingRs.getString("customer_id") == null ? null : customerService.getCustomer(UUID.fromString(customerId)),
                        UUID.fromString(savedReadingRs.getString("id")),
                        savedReadingRs.getString("meter_id"),
                        savedReadingRs.getDate("date_of_reading") != null ? savedReadingRs.getDate("date_of_reading").toLocalDate() : null,
                        savedReadingRs.getDouble("meter_count"),
                        savedReadingRs.getString("comment"),
                        savedReadingRs.getString("kind_of_meter") != null ? Reading.KindOfMeter.valueOf(savedReadingRs.getString("kind_of_meter")) : null,
                        savedReadingRs.getBoolean("substitute")
                );
            } else {
                throw new SQLException("Failed to retrieve saved customer.");
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    public boolean updateReading(Reading reading) {
        UUID customerId;
        UUID readingId = reading.getId();
        if (reading.getCustomer().getId() == null) {
            customerId = getReading(readingId).getCustomer().getId();
            reading.getCustomer().setId(customerId);
        } else {
            customerId = reading.getCustomer().getId();
        }
        customerService.updateCustomer(reading.getCustomer());
        String query = "UPDATE readings SET id = ?, customer_id = ?, meter_id = ?, date_of_reading = ?, meter_count = ?, comment = ?, kind_of_meter = ?, substitute = ? WHERE id = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, reading.getId().toString());
            pstmt.setString(2, customerId.toString());
            pstmt.setString(3, reading.getMeterId());
            pstmt.setDate(4, reading.getDateOfReading() != null ? Date.valueOf(reading.getDateOfReading()) : null);
            pstmt.setDouble(5, reading.getMeterCount());
            pstmt.setString(6, reading.getComment());
            pstmt.setString(7, reading.getKindOfMeter() != null ? reading.getKindOfMeter().toString() : null);
            pstmt.setBoolean(8, reading.getSubstitute());
            pstmt.setString(9, reading.getId().toString());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating reading failed, no rows affected.");
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating reading", e);
        }
    }

    public Reading deleteReading(UUID id) {

        String checkQuery = "SELECT * FROM readings WHERE id = ?";
        String deleteQuery = "DELETE FROM readings WHERE id = ?";
        try (PreparedStatement checkStmt = this.connection.prepareStatement(checkQuery);
             PreparedStatement deleteStmt = this.connection.prepareStatement(deleteQuery)) {

            checkStmt.setString(1, id.toString());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                deleteStmt.setString(1, id.toString());
                Reading readingToDelete = new Reading(
                        rs.getString("customer_id") == null ? null : customerService.getCustomer(UUID.fromString(rs.getString("customer_id"))),
                        UUID.fromString(rs.getString("id")),
                        rs.getString("meter_id"),
                        rs.getDate("date_of_reading") != null ? rs.getDate("date_of_reading").toLocalDate() : null,
                        rs.getDouble("meter_count"),
                        rs.getString("comment"),
                        rs.getString("kind_of_meter") != null ? Reading.KindOfMeter.valueOf(rs.getString("kind_of_meter")) : null,
                        rs.getBoolean("substitute")
                );
                int affectedRows = deleteStmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Deleting customer failed, no rows affected.");
                }
                return readingToDelete;
            }
            System.out.println("No customer found with the specified ID.");
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }
}
