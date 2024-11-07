// package com.example.demo.database;


// import org.apache.commons.dbcp.BasicDataSource;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.io.IOException;
// import java.io.InputStream;
// import java.sql.Connection;
// import java.sql.SQLException;
// import java.util.Properties;

// import static org.junit.jupiter.api.Assertions.assertNotNull;

// public class DatabaseConnectionIT {

//     private DatabaseInitialization dbInit;
//     private BasicDataSource dataSource;

 
//     @BeforeEach
//     void setUp() throws Exception {
//         // Load database properties from application.properties file
//         Properties properties = new Properties();
//         try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
//             if (input == null) {
//                 throw new IOException("application.properties file not found");
//             }
//             properties.load(input);
//         } catch (IOException e) {
//             throw new RuntimeException("Error loading properties file", e);
//         }

//         // Initialize the data source with properties
//         dataSource = new BasicDataSource();
//         dataSource.setUrl(properties.getProperty("db.url"));
//         dataSource.setUsername(properties.getProperty("db.user"));
//         dataSource.setPassword(properties.getProperty("db.password"));

//         dbInit = new DatabaseInitialization();
//         dbInit.setDataSource(dataSource); // Set the data source
//     }

//     @Test
//     void testDatabaseConnection() {
//         try (Connection conn = dataSource.getConnection()) {
//             assertNotNull(conn, "Connection should not be null");
//         } catch (SQLException e) {
//             throw new RuntimeException("Database connection failed", e);
//         }
//     }

//     // Additional tests can be added here as needed

//     public DatabaseInitialization getDbInit() {
//         return dbInit;
//     }

//     public void setDbInit(DatabaseInitialization dbInit) {
//         this.dbInit = dbInit;
//     }
// }
