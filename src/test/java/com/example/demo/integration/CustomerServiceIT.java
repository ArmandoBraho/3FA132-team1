package com.example.demo.integration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.interfaces.ICustomer;
import com.example.demo.models.Customer;
import com.example.demo.service.CustomerService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerServiceIT {

    private DatabaseConnection databaseConnection;
    private CustomerService customerService;
    Properties properties = new Properties();

    @BeforeAll
    public void setUp() {
        // todo: duplicate code into sepearate utility class??
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new FileNotFoundException("application.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file", e);
        }


        databaseConnection = new DatabaseConnection();
        databaseConnection.openConnection(properties);

        customerService = new CustomerService(databaseConnection);

        // Initialize the database schema
        databaseConnection.createAllTables();
        databaseConnection.openConnection(properties);
    }

    @BeforeEach
    public void resetDatabaseToDefaultValues() {
        // just in case the customers properties where modified in the other tests
        databaseConnection.openConnection(properties);
        customerService.deleteCustomer(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        databaseConnection.openConnection(properties);
        customerService.deleteCustomer(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        Customer customer1 = new Customer(UUID.fromString("00000000-0000-0000-0000-000000000000"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));
        Customer customer2 = new Customer(UUID.fromString("11111111-1111-1111-1111-111111111111"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));

        databaseConnection.openConnection(properties);
        customerService.createCustomer(customer1);
        databaseConnection.openConnection(properties);
        customerService.createCustomer(customer2);
    }


    @AfterAll
    public void tearDown() {
        databaseConnection.openConnection(properties);
        databaseConnection.removeAllTables();
        databaseConnection.closeConnection();
    }

    @Test
    public void getCustomer() {
        databaseConnection.openConnection(properties);
        Customer fetchedCustomer = customerService.getCustomerById(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        assertNotNull(fetchedCustomer);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), fetchedCustomer.getId());
        assertEquals("Ada", fetchedCustomer.getFirstName());
        assertEquals("Lovelace", fetchedCustomer.getLastName());
        assertEquals(ICustomer.Gender.D, fetchedCustomer.getGender());
        assertEquals(LocalDate.of(1815, 12, 10), fetchedCustomer.getBirthDate());
    }

    @Test
    public void getAllCustomer() {
        databaseConnection.openConnection(properties);
        List<Customer> fetchedCustomers = customerService.getAllCustomers();

        assertNotNull(fetchedCustomers);
        for (Customer customer : fetchedCustomers) {
            if (customer.getId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                // todo: duplicate code x3
                assertEquals("Ada", customer.getFirstName());
                assertEquals("Lovelace", customer.getLastName());
                assertEquals(ICustomer.Gender.D, customer.getGender());
                assertEquals(LocalDate.of(1815, 12, 10), customer.getBirthDate());
            }
            if (customer.getId().equals(UUID.fromString("11111111-1111-1111-1111-111111111111"))) {
                assertEquals("Ada", customer.getFirstName());
                assertEquals("Lovelace", customer.getLastName());
                assertEquals(ICustomer.Gender.D, customer.getGender());
                assertEquals(LocalDate.of(1815, 12, 10), customer.getBirthDate());
            }
        }
    }

    @Test
    public void createCustomer() {
        Customer customer = new Customer(UUID.randomUUID(), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));

        databaseConnection.openConnection(properties);
        Customer createdCustomer = customerService.createCustomer(customer);

        assertNotNull(createdCustomer);
        assertEquals(createdCustomer.getId(), customer.getId());
        assertEquals(createdCustomer.getGender(), customer.getGender());
        assertEquals(createdCustomer.getFirstName(), customer.getFirstName());
        assertEquals(createdCustomer.getLastName(), customer.getLastName());
        assertEquals(createdCustomer.getBirthDate(), customer.getBirthDate());
    }

    @Test
    public void createAndGetCustomer() {
        Customer customer = new Customer(UUID.randomUUID(), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));

        databaseConnection.openConnection(properties);
        Customer createdCustomer = customerService.createCustomer(customer);
        databaseConnection.openConnection(properties);
        Customer fetchedCustomer = customerService.getCustomerById(customer.getId());

        assertNotNull(createdCustomer);
        assertEquals(createdCustomer.getId(), fetchedCustomer.getId());
        assertEquals(createdCustomer.getGender(), fetchedCustomer.getGender());
        assertEquals(createdCustomer.getFirstName(), fetchedCustomer.getFirstName());
        assertEquals(createdCustomer.getLastName(), fetchedCustomer.getLastName());
        assertEquals(createdCustomer.getBirthDate(), fetchedCustomer.getBirthDate());
    }

    @Test
    public void createManyCustomersAndGetAllCustomer() {
        Customer customer1 = new Customer(UUID.randomUUID(), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));
        Customer customer2 = new Customer(UUID.randomUUID(), ICustomer.Gender.D, "Konrad", "Zuse", LocalDate.of(1910, 6, 22));

        databaseConnection.openConnection(properties);
        Customer createdCustomer1 = customerService.createCustomer(customer1);
        databaseConnection.openConnection(properties);
        Customer createdCustomer2 = customerService.createCustomer(customer2);
        databaseConnection.openConnection(properties);
        List<Customer> fetchedCustomers = customerService.getAllCustomers();

        assertNotNull(fetchedCustomers);
        assert (fetchedCustomers.size() >= 2);
        for (Customer customer : fetchedCustomers) {
            if (customer.getId().equals(customer1.getId())) {
                assertEquals(createdCustomer1.getId(), customer.getId());
                assertEquals(createdCustomer1.getGender(), customer.getGender());
                assertEquals(createdCustomer1.getFirstName(), customer.getFirstName());
                assertEquals(createdCustomer1.getLastName(), customer.getLastName());
                assertEquals(createdCustomer1.getBirthDate(), customer.getBirthDate());
            }
            if (customer.getId().equals(customer2.getId())) {
                assertEquals(createdCustomer2.getId(), customer.getId());
                assertEquals(createdCustomer2.getGender(), customer.getGender());
                assertEquals(createdCustomer2.getFirstName(), customer.getFirstName());
                assertEquals(createdCustomer2.getLastName(), customer.getLastName());
                assertEquals(createdCustomer2.getBirthDate(), customer.getBirthDate());
            }
        }
    }

    @Test
    public void updateCustomer() {
        databaseConnection.openConnection(properties);
        List<Customer> customers = customerService.getAllCustomers();
        Customer customer = customers.getFirst();
        customer.setFirstName("SponGebob");

        databaseConnection.openConnection(properties);
        customerService.updateCustomer(customer);

        databaseConnection.openConnection(properties);
        Customer updatedCustomer = customerService.getCustomerById(customer.getId());
        assertEquals("SponGebob", updatedCustomer.getFirstName());
    }

    @Test
    public void deleteCustomer() {
        databaseConnection.openConnection(properties);
        List<Customer> customers = customerService.getAllCustomers();
        Customer customer = customers.getFirst();

        databaseConnection.openConnection(properties);
        customerService.deleteCustomer(customer.getId());

        databaseConnection.openConnection(properties);
        Customer customerAfterDeletion = customerService.getCustomerById(customer.getId());
        assertNull(customerAfterDeletion);

    }


    @Test
    public void invalidCustomerID() {
        // Create a customer with an invalid UUID
        Customer invalidCustomer = new Customer(UUID.fromString("b624289d-fdfe-4cbb-9a06-f9bc24039538"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));
        databaseConnection.openConnection(properties);
        customerService.createCustomer(invalidCustomer);
        databaseConnection.openConnection(properties);
        Assertions.assertThrows(RuntimeException.class, () -> { customerService.createCustomer(invalidCustomer);
    });
}
    
    
    
 /*    @Test
    public void duplicatedId() {
        // Create a customer with an existing ID
        Customer existingCustomer = customerService.getCustomerById(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        Customer duplicateCustomer = new Customer(existingCustomer.getId(), ICustomer.Gender.D, "New", "Customer", LocalDate.now());
    
        // Assert that createCustomer throws a SQLException for duplicate key
        Assertions.assertThrows(RuntimeException.class, () -> { customerService.createCustomer(duplicateCustomer);
        }); 
    } */
    
   /*  @Test
    public void customerNotFound() {
        // Try to update a customer that doesn't exist
        UUID nonExistentCustomerId = UUID.randomUUID();
        Customer customerToUpdate = new Customer(UUID.fromString("cee2f8c5-11a4-4631-8f33-b13857a509ea"), ICustomer.Gender.D, "New", "Customer", LocalDate.now());
    
        // Assert that updateCustomer throws an SQLException
        assertThrows(SQLException.class, () -> customerService.updateCustomer(customerToUpdate));
    }
    */
}