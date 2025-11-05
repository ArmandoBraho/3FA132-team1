package com.example.demo.controller;

import com.example.demo.models.Customer;
import com.example.demo.models.Reading;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ReadingService;
import com.example.demo.utils.CustomerValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/customers")
public class CustomerController {

    CustomerService customerService = new CustomerService();
    ReadingService readingService = new ReadingService();
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() throws JsonProcessingException {
        List<Customer> customers = customerService.getAllCustomers();
        return Response.ok(customers).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") UUID id) throws JsonProcessingException {
        Customer customer = customerService.getCustomer(id);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        }
        return Response.ok(customer).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomerById(@PathParam("id") UUID id) throws JsonProcessingException {
        List<Reading> readingsOriginal = readingService.getAllReadingsByCustomerId(id);
        List<UUID> readingsIds = readingsOriginal.stream().map(Reading::getId).toList();
        Customer customer = customerService.deleteCustomer(id);
        List<Reading> readingsAfterDelete = readingService.getAllReadings(readingsIds);
        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Customer not found").build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("customer", customer);
        response.put("readingsAfterDelete", readingsAfterDelete);

        return Response.ok(response).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(@Valid Customer customer) throws JsonProcessingException {

        CustomerValidation customerValidation = new CustomerValidation();
        try {
            customerValidation.validate(customer);
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }

        Customer createdCustomer = customerService.createCustomer(customer);
        return Response.status(Response.Status.CREATED)
                .entity(createdCustomer)
                .build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateCustomer(Customer customer) throws JsonProcessingException {
        boolean wasUpdated = false;
        if (customer.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Customer id is required")
                    .build();
        }
        CustomerValidation customerValidation = new CustomerValidation();
        try {
            customerValidation.validate(customer);
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }

        wasUpdated = customerService.updateCustomer(customer);
        if (wasUpdated){
            return Response.status(Response.Status.OK)
                    .entity(String.format("the customer with id %s was updated.", customer.getId().toString()))
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format("the customer with id %s was not found.", customer.getId().toString()))
                .build();

    }


}
