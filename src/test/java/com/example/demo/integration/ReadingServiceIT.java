package com.example.demo.integration;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.interfaces.ICustomer;
import com.example.demo.interfaces.IReading;
import com.example.demo.models.Customer;
import com.example.demo.models.Reading;
import com.example.demo.service.ReadingService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReadingServiceIT {

    private final DatabaseConnection databaseConnection = DatabaseConnection.openConnection();

    private final ReadingService readingService = new ReadingService(databaseConnection);


    Customer customer1 = new Customer(UUID.fromString("00000000-0000-0000-0000-000000000000"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));
    Customer customer2 = new Customer(UUID.fromString("11111111-1111-1111-1111-111111111111"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));

    Reading reading1 = new Reading(
            customer1,
            UUID.fromString("88888888-9999-9999-9999-999999999999"),
            "meterId",
            LocalDate.of(2021, 1, 1),
            100.0,
            "comment",
            IReading.KindOfMeter.HEATING,
            false
    );
    Reading reading2 = new Reading(
            customer2,
            UUID.fromString("77777777-8888-8888-8888-888888888888"),
            "meterId",
            LocalDate.of(2021, 1, 1),
            100.0,
            "comment",
            IReading.KindOfMeter.HEATING,
            true
    );

    @BeforeAll
    public void setUp() {
        databaseConnection.createAllTables();
    }

    @BeforeEach
    public void resetDatabaseToDefaultValues() {
        databaseConnection.truncateAllTables();

        readingService.createReading(reading1);
        readingService.createReading(reading2);
    }

    @AfterAll
    public void tearDown() {
        databaseConnection.removeAllTables();

//        databaseConnection.closeConnection();
    }


    @Test
    public void getReading() {
        // the reading exists since it is created in the setUp method
        Reading fetchedReading = readingService.getReading(UUID.fromString("77777777-8888-8888-8888-888888888888"));

        assertNotNull(fetchedReading);
        assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), fetchedReading.getCustomer().getId());
        assertEquals("meterId", fetchedReading.getMeterId());
        assertEquals(LocalDate.of(2021, 1, 1), fetchedReading.getDateOfReading());
        assertEquals(100.0, fetchedReading.getMeterCount());
        assertEquals("comment", fetchedReading.getComment());
        assertEquals(IReading.KindOfMeter.HEATING, fetchedReading.getKindOfMeter());
        assertTrue(fetchedReading.getSubstitute());
    }
    @Test
    public void getReadingShouldReturnNull() {
        // the reading does NOT exist since it is NOT created in the setUp method
        Reading fetchedReading = readingService.getReading(UUID.fromString("abc33377-8888-8888-8888-888888888888"));

        assertNull(fetchedReading);
    }

    @Test
    public void getAllReadings() {
        List<Reading> fetchedReadings = readingService.getAllReadings();

        // exactly two because before each test we truncate the tables in the database and create two readings
        assertEquals(2, fetchedReadings.size());

        for (Reading reading : fetchedReadings) {
            // ^ is the XOR operator, the reading should be one of the two created in the setup.
            assertTrue(reading.getId().equals(UUID.fromString("88888888-9999-9999-9999-999999999999")) ^ reading.getId().equals(UUID.fromString("77777777-8888-8888-8888-888888888888")));
            if (reading.getId().equals(UUID.fromString("88888888-9999-9999-9999-999999999999"))) {
                assertEquals(reading1.getCustomer().getId(), reading.getCustomer().getId());
                assertEquals(reading1.getMeterId(), reading.getMeterId());
                assertEquals(reading1.getDateOfReading(), reading.getDateOfReading());
                assertEquals(reading1.getMeterCount(), reading.getMeterCount());
                assertEquals(reading1.getComment(), reading.getComment());
                assertEquals(reading1.getKindOfMeter(), reading.getKindOfMeter());
                assertEquals(reading1.getSubstitute(), reading.getSubstitute());
            }
            if (reading.getId().equals(UUID.fromString("77777777-8888-8888-8888-888888888888"))) {
                assertEquals(reading2.getCustomer().getId(), reading.getCustomer().getId());
                assertEquals(reading2.getMeterId(), reading.getMeterId());
                assertEquals(reading2.getDateOfReading(), reading.getDateOfReading());
                assertEquals(reading2.getMeterCount(), reading.getMeterCount());
                assertEquals(reading2.getComment(), reading.getComment());
                assertEquals(reading2.getKindOfMeter(), reading.getKindOfMeter());
                assertEquals(reading2.getSubstitute(), reading.getSubstitute());
            }
        }

    }

    // maybe nice to have but need database properties to get smarter
//    @Test
//    public void getReadingShouldThrowExceptionWhenConnectionIsClosed() {
//
//        assertThrows(RuntimeException.class, () -> {
//            this.databaseConnection.getConnection().close();
//            readingService.getReadingById(UUID.fromString("77777777-8888-8888-8888-888888888888"));
//            this.databaseConnection.openConnection();
//        });
//    }

    @Test
    public void createReading() {
        Reading readingToCreate = new Reading(
                customer1,
                UUID.fromString("12345678-1234-1234-1234-123456781234"),
                "meterId",
                LocalDate.of(2021, 1, 1),
                100.0,
                "comment",
                IReading.KindOfMeter.HEATING,
                false
        );

        Reading createdReading = readingService.createReading(readingToCreate);

        assertNotNull(createdReading);
        assertEquals(UUID.fromString("12345678-1234-1234-1234-123456781234"), createdReading.getId());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), createdReading.getCustomer().getId());
        assertEquals("meterId", createdReading.getMeterId());
        assertEquals(LocalDate.of(2021, 1, 1), createdReading.getDateOfReading());
        assertEquals(100.0, createdReading.getMeterCount());
        assertEquals("comment", createdReading.getComment());
        assertEquals(IReading.KindOfMeter.HEATING, createdReading.getKindOfMeter());
        assertFalse(createdReading.getSubstitute());
    }

    //todo: makes sense only if I can reopen the connection, how else can I trigger the exception?
//    @Test
//    public void createReadingShouldThrowExceptionWhenDBConnectionIsClosed() {
//        Reading readingToCreate = new Reading(
//                customer1,
//                UUID.fromString("12345678-1234-1234-1234-123456781234"),
//                "meterId",
//                LocalDate.of(2021, 1, 1),
//                100.0,
//                "comment",
//                IReading.KindOfMeter.HEATING,
//                false
//        );
//
//        assertThrows(RuntimeException.class, () -> {
//            this.databaseConnection.getConnection(). close();
//            readingService.createReading(readingToCreate);
//            //todo: openConnection method improve
//            this.databaseConnection.openConnection(new Properties());
//        });
//    }

    @Test
    public void deleteReading() {
        readingService.deleteReading(reading1.getId());
        Reading deletedReading = readingService.getReading(UUID.fromString("88888888-9999-9999-9999-999999999999"));
        assertNull(deletedReading);
        //reset the previous state
        readingService.createReading(reading1);
    }


    @Test
    public void shouldCreateCustomerAndReading() {
        //Given
        Customer notExistingCustomer = new Customer(UUID.fromString("11111111-2222-3333-4444-555566667777"), ICustomer.Gender.D, "Ada", "Lovelace", LocalDate.of(1815, 12, 10));
        Reading readingToCreate = new Reading(
                notExistingCustomer,
                UUID.fromString("12345678-1234-1234-1234-123456781234"),
                "meterId",
                LocalDate.of(2021, 1, 1),
                100.0,
                "comment",
                IReading.KindOfMeter.HEATING,
                false
        );

        //When
        Reading createdReading = readingService.createReading(readingToCreate);

        //Then
        assertNotNull(createdReading);
        assertEquals(UUID.fromString("12345678-1234-1234-1234-123456781234"), createdReading.getId());
        assertEquals(UUID.fromString("11111111-2222-3333-4444-555566667777"), createdReading.getCustomer().getId());
        assertEquals("meterId", createdReading.getMeterId());
        assertEquals(LocalDate.of(2021, 1, 1), createdReading.getDateOfReading());
        assertEquals(100.0, createdReading.getMeterCount());
        assertEquals("comment", createdReading.getComment());
        assertEquals(IReading.KindOfMeter.HEATING, createdReading.getKindOfMeter());
        assertFalse(createdReading.getSubstitute());
    }

    @Test
    public void updateReading() {
        Reading reading1DesiredUpdate = new Reading(
                customer1,
                UUID.fromString("88888888-9999-9999-9999-999999999999"),
                "modified modified modified",
                LocalDate.of(2021, 1, 1),
                100.0,
                "modified comment",
                IReading.KindOfMeter.HEATING,
                false
        );

        readingService.updateReading(reading1DesiredUpdate);
        Reading updatedReading = readingService.getReading(UUID.fromString("88888888-9999-9999-9999-999999999999"));

        assertNotNull(updatedReading);
        assertEquals(UUID.fromString("88888888-9999-9999-9999-999999999999"), updatedReading.getId());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), updatedReading.getCustomer().getId());
        assertEquals("modified modified modified", updatedReading.getMeterId());
        assertEquals(LocalDate.of(2021, 1, 1), updatedReading.getDateOfReading());
        assertEquals(100.0, updatedReading.getMeterCount());
        assertEquals("modified comment", updatedReading.getComment());
        assertEquals(IReading.KindOfMeter.HEATING, updatedReading.getKindOfMeter());
        assertFalse(updatedReading.getSubstitute());
    }

//    @Test
//    public void testCreateAndGetReading() {
//        UUID customerId = UUID.randomUUID();
//        Customer customer = new Customer(customerId, null, null, null, null);
//        Reading reading = new Reading(customer, UUID.randomUUID(), "meter1", LocalDate.of(2021, 1, 1), 100.0, "comment1", Reading.KindOfMeter.ELECTRICITY, false);
//
//        readingService.createReading(reading);
//
//        Reading retrievedReading = readingService.getReadingById(reading.getId());
//        assertNotNull(retrievedReading);
//        assertEquals(reading.getId(), retrievedReading.getId());
//        assertEquals("meter1", retrievedReading.getMeterId());
//    }


}