package com.example.demo.controller;

import com.example.demo.interfaces.IReading;
import com.example.demo.models.Customer;
import com.example.demo.models.Reading;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ReadingService;
import com.example.demo.utils.CustomerValidation;
import com.example.demo.utils.ReadingValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/readings")
public class ReadingController {

    ReadingService readingService = new ReadingService();
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReadings() throws JsonProcessingException {
        List<Reading> readings = readingService.getAllReadings();
        return Response.ok(objectMapper.writeValueAsString(readings)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFilteredReadings(
            @QueryParam("customer") UUID customerUuid,
            @QueryParam("start") String startDate,
            @QueryParam("end") String endDate,
            @QueryParam("kindOfMeter") String kindOfMeter) {

        LocalDate start = null;
        if (startDate != null) {
            try {
                start = parseDate(startDate, "start");
            } catch (WebApplicationException e) {
                return Response.status(400)
                        .entity(e.getMessage())
                        .build();
            }
        }

        LocalDate end = null;
        if (endDate != null) {
            try {
                end = parseDate(endDate, "end");
            } catch (WebApplicationException e) {
                return Response.status(400)
                        .entity(e.getMessage())
                        .build();
            }
        }

        List<Reading> readings = readingService.getFilteredReadings(customerUuid, start, end, kindOfMeter == null ? null : IReading.KindOfMeter.valueOf(kindOfMeter));
        return Response.ok().entity(readings).build();
    }

    private LocalDate parseDate(String dateStr, String paramName) {
        if (dateStr == null) {
            return null;
        }

        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new WebApplicationException(
                    String.format("Invalid %s date format. Use yyyy-MM-dd", paramName),
                    Response.Status.BAD_REQUEST
            );
        }
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadingById(@PathParam("id") UUID id) throws JsonProcessingException {
        Reading reading = readingService.getReading(id);
        if (reading == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Reading not found").build();
        }
        return Response.ok(objectMapper.writeValueAsString(reading)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReadingById(@PathParam("id") UUID id) throws JsonProcessingException {
        Reading reading = readingService.deleteReading(id);
        if (reading == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Reading not found").build();
        }

        return Response.ok(objectMapper.writeValueAsString(reading)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReading(@Valid Reading reading) throws JsonProcessingException {

        ReadingValidation readingValidation = new ReadingValidation();
        try {
            readingValidation.validate(reading);
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }

        Reading createdReading = readingService.createReading(reading);
        return Response.status(Response.Status.CREATED)
                .entity(createdReading)
                .build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateReading(@Valid Reading reading) throws JsonProcessingException {
        boolean wasUpdated = false;
        if (reading.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reading id is required")
                    .build();
        }
        ReadingValidation readingValidation = new ReadingValidation();
        try {
            readingValidation.validate(reading);
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }

        wasUpdated = readingService.updateReading(reading);
        if (wasUpdated){
            return Response.status(Response.Status.OK)
                    .entity(String.format("the reading with id %s was updated.", reading.getId().toString()))
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format("the reading with id %s was not found.", reading.getId().toString()))
                .build();

    }

}
