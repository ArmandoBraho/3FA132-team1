package com.example.demo.controller;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/hello")
public class HelloService {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response greetClient() {
        String output = "Hi " + "dear Customerrrrr";
        return Response.status(200).entity(output).build();
    }
}