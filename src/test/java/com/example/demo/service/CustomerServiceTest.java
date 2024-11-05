package com.example.demo.service;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.models.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {
    //RIPRENDI DA REST API WITH JERSEY .................. WATCH REST FOLDER OF LAST YEAR, THERE THERE ARE ALL THE ENDPOINTS ETC
    private DatabaseConnection databaseConnection;
    private CustomerService customerService;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;

    @BeforeEach
    public void setUp() throws Exception {
        databaseConnection = mock(DatabaseConnection.class);
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);
        preparedStatement = mock(PreparedStatement.class);


        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        // do I need those here? or better in each test? todo:
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        customerService = new CustomerService(databaseConnection);
    }

    @Test
    public void testGetAllCustomers() throws Exception {
        // Mock the ResultSet
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("uuid")).thenReturn(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        when(resultSet.getString("gender")).thenReturn("M", "W");
        when(resultSet.getString("first_name")).thenReturn("John", "Jane");
        when(resultSet.getString("last_name")).thenReturn("Doe", "Doe");
        when(resultSet.getDate("birth_date")).thenReturn(null, null);

        List<Customer> customers = customerService.getAllCustomers();

        assertEquals(2, customers.size());
        assertEquals("John", customers.get(0).getFirstName());
        assertEquals("Jane", customers.get(1).getFirstName());

        verify(statement, times(1)).executeQuery(anyString());
        verify(resultSet, times(3)).next();
        //todo: negative test
    }

    @Test
    public void testGetCustomerById() throws Exception {
        UUID customerId = UUID.randomUUID();

        // Mock the ResultSet
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("uuid")).thenReturn(customerId.toString());
        when(resultSet.getString("gender")).thenReturn("M");
        when(resultSet.getString("first_name")).thenReturn("John");
        when(resultSet.getString("last_name")).thenReturn("Doe");
        when(resultSet.getDate("birth_date")).thenReturn(null);

        // Call the method
        Customer customer = customerService.getCustomerById(customerId);

        // Verify the results
        assertEquals(customerId, customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());

        // Verify interactions
        verify(preparedStatement, times(1)).setString(1, customerId.toString());
        verify(preparedStatement, times(1)).executeQuery();
        verify(resultSet, times(1)).next();
    }

    @Test
    public void testCreateCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
                customerId,
                com.example.demo.interfaces.ICustomer.Gender.M,
                "Pika",
                "Chu",
                null
        );

        // Call the method
        customerService.createCustomer(customer);

        // todo: does this test make sense at all? It basically only checks how often where the functions called
        // Verify interactions
        verify(preparedStatement, times(1)).setString(1, customer.getId().toString());
        verify(preparedStatement, times(1)).setString(2, customer.getGender().toString());
        verify(preparedStatement, times(1)).setString(3, customer.getFirstName());
        verify(preparedStatement, times(1)).setString(4, customer.getLastName());
        verify(preparedStatement, times(1)).setDate(5, null);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testCreateAndUpdateCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Customer customer = new Customer(
                customerId,
                com.example.demo.interfaces.ICustomer.Gender.M,
                "Pika",
                "Chu",
                birthDate
        );

        // Create the customer
        customerService.createCustomer(customer);

        // Verify the customer creation
        verify(preparedStatement, times(1)).setString(1, customer.getId().toString());
        verify(preparedStatement, times(1)).setString(2, customer.getGender().toString());
        verify(preparedStatement, times(1)).setString(3, customer.getFirstName());
        verify(preparedStatement, times(1)).setString(4, customer.getLastName());
        verify(preparedStatement, times(1)).setDate(5, Date.valueOf(birthDate));
        verify(preparedStatement, times(1)).executeUpdate();

        // Update the customer
        customer.setFirstName("Pikachu");
        customer.setLastName("Electric");
        customerService.updateCustomer(customer);

        // Verify the customer update
        verify(preparedStatement, times(1)).setString(1, customer.getGender().toString());
        verify(preparedStatement, times(1)).setString(2, "Pikachu");
        verify(preparedStatement, times(1)).setString(3, "Electric");
        verify(preparedStatement, times(1)).setDate(4, Date.valueOf(birthDate));
        verify(preparedStatement, times(1)).setString(5, customer.getId().toString());
        verify(preparedStatement, times(2)).executeUpdate();
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();

        // Call the method
        customerService.deleteCustomer(customerId);

        // Verify interactions
        verify(preparedStatement, times(1)).setString(1, customerId.toString());
        verify(preparedStatement, times(1)).executeUpdate();
    }
}