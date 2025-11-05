package com.example.demo.service;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.interfaces.ICustomer;
import com.example.demo.models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerService {

    private final Connection connection;
    private static final DatabaseConnection databaseConnection = DatabaseConnection.openConnection();

    public CustomerService() {
        this.connection = databaseConnection.getConnection();
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers";
        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Customer customer = new Customer(
                        UUID.fromString(rs.getString("id")),
                        com.example.demo.interfaces.ICustomer.Gender.valueOf(rs.getString("gender")),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null
                );
                customers.add(customer);
            }
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException("Error retrieving customers", e);
        }
        return customers;
    }

    public Customer getCustomer(UUID id) {
        String query = "SELECT * FROM customers WHERE id = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        UUID.fromString(rs.getString("id")),
                        com.example.demo.interfaces.ICustomer.Gender.valueOf(rs.getString("gender")),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all customers", e);
        }
        return null;
    }


    public boolean updateCustomer(Customer customer) {
        String customerId = customer.getId() == null ? UUID.randomUUID().toString() : customer.getId().toString();
        String checkQuery = "SELECT 1 FROM customers WHERE id = ?";

        String query = "UPDATE customers SET gender = ?, first_name = ?, last_name = ?, birth_date = ? WHERE id = ?";
        try ( PreparedStatement checkStmt = this.connection.prepareStatement(checkQuery);
                PreparedStatement pstmt = this.connection.prepareStatement(query)) {

            checkStmt.setString(1, customerId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                // no customer found with the given customerId
                return false;
            }
            pstmt.setString(1, customer.getGender().toString());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setDate(4, customer.getBirthDate() != null ? Date.valueOf(customer.getBirthDate()) : null);
            pstmt.setString(5, customerId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting customer failed, no rows affected.");
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    public Customer createCustomer(Customer customer) {
        String checkQuery = "SELECT 1 FROM customers WHERE id = ?";
        String insertQuery = "INSERT INTO customers (id, gender, first_name, last_name, birth_date) VALUES (?, ?, ?, ?, ?)";
        String selectQuery = "SELECT * FROM customers WHERE id = ?";
        String customerId = customer.getId() == null ? UUID.randomUUID().toString() : customer.getId().toString();

        try (PreparedStatement checkStmt = this.connection.prepareStatement(checkQuery);
             PreparedStatement insertStmt = this.connection.prepareStatement(insertQuery);
             PreparedStatement selectStmt = this.connection.prepareStatement(selectQuery)) {

            // Check if the customer already exists
            checkStmt.setString(1, customerId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("The passed customer already exists, not creating anything since not necessary.");
                return customer;
            }

            // Insert new customer
            insertStmt.setString(1, customerId);
            insertStmt.setString(2, customer.getGender().toString());
            insertStmt.setString(3, customer.getFirstName());
            insertStmt.setString(4, customer.getLastName());
            insertStmt.setDate(5, customer.getBirthDate() != null ? Date.valueOf(customer.getBirthDate()) : null);
            int affectedRows = insertStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            // Retrieve and return the saved customer
            selectStmt.setString(1, customerId);
            ResultSet savedCustomerRs = selectStmt.executeQuery();
            if (savedCustomerRs.next()) {
                return new Customer(
                        UUID.fromString(savedCustomerRs.getString("id")),
                        ICustomer.Gender.valueOf(savedCustomerRs.getString("gender")),
                        savedCustomerRs.getString("first_name"),
                        savedCustomerRs.getString("last_name"),
                        savedCustomerRs.getDate("birth_date") != null ? savedCustomerRs.getDate("birth_date").toLocalDate() : null
                );
            } else {
                throw new SQLException("Failed to retrieve saved customer.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating or retrieving customer by id", e);
        }
    }

    public Customer deleteCustomer(UUID id) {
        String checkQuery = "SELECT * FROM customers WHERE id = ?";
        String deleteQuery = "DELETE FROM customers WHERE id = ?";
        try (PreparedStatement checkStmt = this.connection.prepareStatement(checkQuery);
             PreparedStatement deleteStmt = this.connection.prepareStatement(deleteQuery)) {

            checkStmt.setString(1, id.toString());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                deleteStmt.setString(1, id.toString());
                Customer customerToDelete = new Customer(
                        UUID.fromString(rs.getString("id")),
                        com.example.demo.interfaces.ICustomer.Gender.valueOf(rs.getString("gender")),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null
                );
                int affectedRows = deleteStmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Deleting customer failed, no rows affected.");
                }
                return customerToDelete;
            }
            System.out.println("No customer found with the specified ID.");
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }
}