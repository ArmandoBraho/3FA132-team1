package com.example.demo.controller;

import com.example.demo.service.DatabaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class DatabaseController {

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/test-database")
    public String testDatabaseConnection() {
        try {
            databaseService.performDatabaseOperation();
            return "Database connection successful!";
        } catch (SQLException e) {
            return "Error connecting to the database: " + e.getMessage();
        }
    }
}

