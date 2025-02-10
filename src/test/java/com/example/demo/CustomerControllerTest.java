//package com.example.demo;
//
//import com.example.demo.controller.CustomerController;
//import com.example.demo.models.Customer;
//import com.example.demo.service.CustomerService;
//import com.example.demo.utils.CustomerValidation;
//import org.glassfish.jersey.jackson.JacksonFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.glassfish.jersey.server.ResourceConfig;
//import org.glassfish.jersey.test.JerseyTest;
//import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
//import org.glassfish.jersey.test.spi.TestContainerFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import jakarta.ws.rs.client.Entity;
//import jakarta.ws.rs.core.Application;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//import static jakarta.ws.rs.sse.SseEventSource.target;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//public class CustomerControllerTest extends JerseyTest {
//    @Override
//    protected TestContainerFactory getTestContainerFactory() {
//        return new GrizzlyWebTestContainerFactory();
//    }
//    @Override
//    protected Application configure() {
//        ResourceConfig config = ResourceConfig.forApplication(new DemoApplication());
//
//        // Register your controller
//        config.register(CustomerController.class);
//
//        // Add Jackson JSON support
//        config.register(JacksonFeature.class);
//
//        return config;
//    }
//    @Mock
//    private CustomerService customerService;
//
//    @Mock
//    private CustomerValidation customerValidation;
//
//    @InjectMocks
//    private CustomerController controller;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    public void testGetAllCustomers() throws Exception {
//        // Given
//        Customer customer1 = new Customer(
//                UUID.fromString("8670e527-3f5e-44cc-ae61-fba80268bd7f"),
//                Customer.Gender.W,
//                "Cindy",
//                "Gerhardt",
//                null
//        );
//        Customer customer2 = new Customer(
//                UUID.fromString("8670e527-3f5e-44cc-ae61-fba80268bd7f"),
//                Customer.Gender.W,
//                "Vera",
//                "Vera",
//                null
//        );
//        List<Customer> returnedCustomers = List.of(customer1, customer2);
//
//
//        when(customerService.getAllCustomers()).thenReturn(returnedCustomers);
//
//        // When
//        Response response = target("/customers")
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        // Then
//        assertEquals(200, response.getStatus());
//    }
//}