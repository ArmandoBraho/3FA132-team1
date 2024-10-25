package com.example.demo;

import com.example.demo.database.DatabaseInitialization;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	// should be static to get used inside static context?
	static DatabaseInitialization databaseInitialization = new DatabaseInitialization();
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		databaseInitialization.initialize();
	}

}
