package com.example.demo.service;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.interfaces.ICustomer;
import com.example.demo.models.Customer;

import java.sql.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerService {

    private final DatabaseConnection databaseConnection;

    public CustomerService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers";
        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement();
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
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving customers", e);
        }
        return customers;
    }

    public Customer getCustomerById(UUID id) {
        String query = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             // todo: im constructor define?
             PreparedStatement pstmt = conn.prepareStatement(query)) {
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

    // todo: https://stackoverflow.com/questions/25745094/getting-resultset-from-insert-statement
    public Customer createCustomer(Customer customer) {
        String query = "INSERT INTO customers (id, gender, first_name, last_name, birth_date) VALUES (?, ?, ?, ?, ?)";
        //todo: also in constructor? by reference so maybe also because of that closed
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            // duplicate code with DatabaseInitialization , put in common function addCustomer ??? todo:
            // todo: sql injection
            pstmt.setString(1, customer.getId().toString());
            pstmt.setString(2, customer.getGender().toString());
            pstmt.setString(3, customer.getFirstName());
            pstmt.setString(4, customer.getLastName());
            pstmt.setDate(5, customer.getBirthDate() != null ? Date.valueOf(customer.getBirthDate()) : null);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }
            //todo: does this return make sense,itself it is not what was put in the database but just what was given to the method----
            //todo: jetzt dank affectedRows wissen wir that at least one row was affected. Anyway if no row affected should throw exception ---> so it inserts probably something....
            //todo: can I exclude it will be what I wanted? Or is this something I check in the test? but in the test it also just checks the creation ( alternative I check creation + get in the same test but then I do not have separation of concerns)
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating customer by id", e);
        }
    }

    public void updateCustomer(Customer customer) {
        String query = "UPDATE customers SET gender = ?, first_name = ?, last_name = ?, birth_date = ? WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, customer.getGender().toString());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setDate(4, customer.getBirthDate() != null ? Date.valueOf(customer.getBirthDate()) : null);
            pstmt.setString(5, customer.getId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    public void deleteCustomer(UUID id) {
        // SELECT 1 common pratice just so that we can execute the query and check if customer exists
        String checkQuery = "SELECT 1 FROM customers WHERE id = ?";
        String deleteQuery = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            checkStmt.setString(1, id.toString());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                deleteStmt.setString(1, id.toString());
                deleteStmt.executeUpdate();
            } else {
                System.out.println("No customer found with the specified ID.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }
}