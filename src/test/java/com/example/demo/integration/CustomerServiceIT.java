package com.example.demo.integration;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.interfaces.ICustomer;
import com.example.demo.models.Customer;
import com.example.demo.service.CustomerService;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerServiceIT {

    //    RIPRENDI DA HERE i GUEES     the test should be mostly combined like you create and then make a get by id or get all customers......
//    RIPRENDI DA HERE i GUEES     the test should be mostly combined like you create and then make a get by id or get all customers......
//    RIPRENDI DA HERE i GUEES     the test should be mostly combined like you create and then make a get by id or get all customers......
//    RIPRENDI DA HERE i GUEES     the test should be mostly combined like you create and then make a get by id or get all customers......
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


        // todo: should I change the method createCustomer so that it returns the created customer and then I can just test it without also testing the getter
        databaseConnection = new DatabaseConnection();
        databaseConnection.openConnection(properties);
        customerService = new CustomerService(databaseConnection);

        // Initialize the database schema
        databaseConnection.createAllTables();
        databaseConnection.openConnection(properties);
    }

    @BeforeEach
    public void resetDatabaseToDefaultValues() {
        customerService.deleteCustomer(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        databaseConnection.openConnection(properties);
        customerService.deleteCustomer(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        Customer customer1 = new Customer(UUID.fromString("00000000-0000-0000-0000-000000000000"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));
        Customer customer2 = new Customer(UUID.fromString("11111111-1111-1111-1111-111111111111"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));

        databaseConnection.openConnection(properties);
        databaseConnection.openConnection(properties);
        customerService.createCustomer(customer1);
        databaseConnection.openConnection(properties);
        customerService.createCustomer(customer2);
    }


    @AfterAll
    public void tearDown() {
        databaseConnection.openConnection(properties);
        databaseConnection.removeAllTables();
//        databaseConnection.closeConnection();
    }

    // todo: is this test necessary??
    @Test
    public void createCustomer() {
        Customer customer = new Customer(UUID.randomUUID(), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));

        // todo: should I change the method createCustomer so that it returns the created customer and then I can just test it without also testing the getter
        Customer createdCustomer = customerService.createCustomer(customer);

        assertNotNull(createdCustomer);
        assertEquals(createdCustomer.getId(), customer.getId());
        assertEquals(createdCustomer.getGender(), customer.getGender());
        assertEquals(createdCustomer.getFirstName(), customer.getFirstName());
        assertEquals(createdCustomer.getLastName(), customer.getLastName());
        assertEquals(createdCustomer.getBirthDate(), customer.getBirthDate());
    }

    @Test
    public void getCustomer() {
        databaseConnection.openConnection(properties);
        Customer fetchedCustomer = customerService.getCustomerById(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        assertNotNull(fetchedCustomer);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), fetchedCustomer.getId());
        assertEquals("Ada", fetchedCustomer.getFirstName());
        assertEquals(ICustomer.Gender.D, fetchedCustomer.getGender());
        assertEquals("Lovelace", fetchedCustomer.getLastName());
        assertEquals(LocalDate.of(1815, 12, 10), fetchedCustomer.getBirthDate());
    }

    @Test
    public void getAllCustomer() {
        databaseConnection.openConnection(properties);
        List<Customer> fetchedCustomers = customerService.getAllCustomers();

        assertNotNull(fetchedCustomers);
        for (Customer customer : fetchedCustomers) {
            if (customer.getId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
                assertEquals("Ada", customer.getFirstName());
                //todo: the other fields.....
            }
            if (customer.getId().equals(UUID.fromString("11111111-1111-1111-1111-111111111111"))) {
                assertEquals("Lovelace", customer.getLastName());
                //todo: the other fields.....
            }
        }
    }

    @Test
    public void createAndGetCustomer() {
        Customer customer = new Customer(UUID.randomUUID(), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));

        // todo: should I change the method createCustomer so that it returns the created customer and then I can just test it without also testing the getter
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
    public void createManyCustomersAndGetThemCustomer() {
        Customer customer1 = new Customer(UUID.randomUUID(), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));
        Customer customer2 = new Customer(UUID.randomUUID(), ICustomer.Gender.D, "Konrad", "Zuse", LocalDate.of(1910, 6, 22));

        // todo: should I change the method createCustomer so that it returns the created customer and then I can just test it without also testing the getter
        databaseConnection.truncateAllTables();
        Customer createdCustomer1 = customerService.createCustomer(customer1);
        Customer createdCustomer2 = customerService.createCustomer(customer2);
        databaseConnection.openConnection(properties);
        List<Customer> fetchedCustomers = customerService.getAllCustomers();

        assertNotNull(fetchedCustomers);
        assert (fetchedCustomers.size() >= 2);
        //todo: ask if we can use softassertion library
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
        List<Customer> customers = customerService.getAllCustomers();
        Customer customer = customers.get(0);
        customer.setFirstName("SponGebob");

        customerService.updateCustomer(customer);

        Customer updatedCustomer = customerService.getCustomerById(customer.getId());
        assertEquals("SponGebob", updatedCustomer.getFirstName());
    }

    @Test
    public void deleteCustomer() {
        //todo: does this make sense?? we always suppose there is at least a customer in our table---> maybe improve set up and clean it up after the test ----> something like after and before each test create
        List<Customer> customers = customerService.getAllCustomers();
        Customer customer = customers.get(0);

        customerService.deleteCustomer(customer.getId());

        Customer customerAfterDeletion = customerService.getCustomerById(customer.getId());
        assertEquals(null, customerAfterDeletion);
    }


}