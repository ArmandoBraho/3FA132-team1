package com.example.demo;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.database.DatabaseInitialization;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class DemoApplication {
    private static final DatabaseConnection databaseConnection = DatabaseConnection.openConnection();
    static DatabaseInitialization databaseInitialization = new DatabaseInitialization(databaseConnection.getConnection());

    public static void main(String[] args) {
        System.out.println("Starting application...");

        databaseInitialization.initialize();

        final String pack = "com.example.demo.controller";
        String url = "http://localhost:8080/rest";
        System.out.println("Start server");
        System.out.println(url);
        //final ResourceConfig rc = new ResourceConfig().packages(pack).register(AuthenticationFilter.class);
        final ResourceConfig rc = new ResourceConfig().packages(pack);
        final HttpServer server = JdkHttpServerFactory.createHttpServer(URI.create(url), rc);
        System.out.println("Ready for Requests....");

        System.out.println("Server running...");
    }
}
