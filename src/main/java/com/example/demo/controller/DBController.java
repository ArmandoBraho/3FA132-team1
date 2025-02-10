package com.example.demo.controller;

import com.example.demo.models.Reading;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/setupDB")
public class DBController {

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response setupDB() throws JsonProcessingException {
        // todo: need Server class to call this setup thing?
        return Response.status(Response.Status.NOT_IMPLEMENTED).entity("Not implemented").build();
    }
}
