package com.example.demo;

import com.example.demo.database.DatabaseInitialization;
<<<<<<< HEAD
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
=======
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
>>>>>>> 8a6db073a6c5a9382efe761f88ceb9e4e2f61f2f

public class DemoApplication {

<<<<<<< HEAD
	static DatabaseInitialization databaseInitialization = new DatabaseInitialization();

	public static void main(String[] args) {
		System.out.println("Starting application...");

		databaseInitialization.initialize();
		startHttpServer();

		System.out.println("Server running...");
=======
	// should be static to get used inside static context?
	static DatabaseInitialization databaseInitialization = new DatabaseInitialization();
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		databaseInitialization.initialize();
>>>>>>> 8a6db073a6c5a9382efe761f88ceb9e4e2f61f2f
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
