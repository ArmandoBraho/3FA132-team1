package com.example.demo;

import com.example.demo.database.DatabaseInitialization;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ReadingService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class DemoApplication {

    static DatabaseInitialization databaseInitialization = new DatabaseInitialization();

    public static void main(String[] args) {
        System.out.println("Starting application...");

        databaseInitialization.initialize();
        startHttpServer();


        System.out.println("Server running...");
    }

    private static void startHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new MyHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("Server started on port 8080");
        } catch (IOException e) {
            throw new RuntimeException("Error starting HTTP server", e);
        }
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Hello, World!";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            System.out.println("Response sent for 8080 call.");
        }
    }
}