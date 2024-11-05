package com.example.demo.integration;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.interfaces.ICustomer;
import com.example.demo.models.Customer;
import com.example.demo.service.CustomerService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerServiceIT {

    private final DatabaseConnection databaseConnection = DatabaseConnection.openConnection();
    private final CustomerService customerService = new CustomerService(databaseConnection);
    Customer customer1 = new Customer(UUID.fromString("00000000-0000-0000-0000-000000000000"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));
    Customer customer2 = new Customer(UUID.fromString("11111111-1111-1111-1111-111111111111"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));

    @BeforeAll
    public void setUp() {
        databaseConnection.createAllTables();
    }

    @BeforeEach
    public void resetDatabaseToDefaultValues() {
        databaseConnection.truncateAllTables();

        customerService.createCustomer(customer1);
        customerService.createCustomer(customer2);
    }


    @AfterAll
    public void tearDown() {
        databaseConnection.removeAllTables();
//        databaseConnection.closeConnection();
    }

    @Test
    public void getCustomer() {
        Customer fetchedCustomer = customerService.getCustomer(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        assertNotNull(fetchedCustomer);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), fetchedCustomer.getId());
        assertEquals("Ada", fetchedCustomer.getFirstName());
        assertEquals("Lovelace", fetchedCustomer.getLastName());
        assertEquals(ICustomer.Gender.D, fetchedCustomer.getGender());
        assertEquals(LocalDate.of(1815, 12, 10), fetchedCustomer.getBirthDate());
    }

    @Test
    public void getAllCustomer() {
        List<Customer> fetchedCustomers = customerService.getAllCustomers();

        assertNotNull(fetchedCustomers);
        for (Customer customer : fetchedCustomers) {
            if (customer.getId().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
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

        Customer createdCustomer = customerService.createCustomer(customer);
        Customer fetchedCustomer = customerService.getCustomer(customer.getId());

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

        Customer createdCustomer1 = customerService.createCustomer(customer1);
        Customer createdCustomer2 = customerService.createCustomer(customer2);
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
        Customer customer1DesirecUpdate = new Customer(UUID.fromString("00000000-0000-0000-0000-000000000000"), ICustomer.Gender.D, "SponGebob", "SquarePants", LocalDate.of(1999, 5, 1));
        customerService.updateCustomer(customer1DesirecUpdate);

        Customer updatedCustomer = customerService.getCustomer(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertEquals("SponGebob", updatedCustomer.getFirstName());
        assertEquals("SquarePants", updatedCustomer.getLastName());
        assertEquals(LocalDate.of(1999, 5, 1), updatedCustomer.getBirthDate());
    }

    @Test
    public void deleteCustomer() {
        List<Customer> customers = customerService.getAllCustomers();
        Customer customer = customers.getFirst();

        customerService.deleteCustomer(customer.getId());

        Customer customerAfterDeletion = customerService.getCustomer(customer.getId());
        assertNull(customerAfterDeletion);
    }

}