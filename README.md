# Courier Tracking Service

This is a Spring Boot application designed to track the location of couriers, calculate total distance traveled, and log whenever a courier enters within a 100-meter radius of specific Migros stores.

## Tech Stack
- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Data JPA**
- **MySQL**
- **Lombok**
- **Maven**
- **JUnit 5 & Mockito** (Unit Testing)

## Features
- **Streaming Location Acceptance:** Allows continuous updates of a courier's latitude and longitude.
- **Store Entry Logging:** Logs occurrences when a courier enters a 100m radius of a store. Re-entries within 1 minute are ignored.
- **Distance Calculation:** Calculates the real-time total travel distance using the Haversine formula.
- **Thread Safety:** Employs Pessimistic Write Locking to ensure that concurrent location updates for the same courier don't result in incorrect total distance aggregation.
- **Asynchronous Event Processing:** Store entrance validation and database logging are handled asynchronously (Observer Pattern) without blocking the main HTTP location update thread.
- **High Throughput with Virtual Threads:** Utilizes Java 21 Virtual Threads natively via Spring Boot 3.2+ for maximum I/O scalability and low memory footprint.
- **Unit Testing:** Core business logic, algorithms, and services are covered by unit tests using JUnit 5 and Mockito.

## Design Patterns Used
1. **Strategy Pattern:** The distance calculation formula is abstracted behind a `DistanceCalculatorStrategy` interface. This allows easily swapping out the `HaversineDistanceCalculator` for another algorithm (like Google Maps Routing APIs) without modifying the core service.
2. **Observer Pattern:** Implemented via Spring's `ApplicationEventPublisher`. When a courier's location is saved, a `CourierLocationUpdatedEvent` is emitted. A separate `StoreEntryListener` asynchronously catches this event, checks the 100-meter proximity against the stores, and logs the entry. This keeps the core update logic fast and decoupled.
3. **Builder Pattern:** Used extensively via Lombok's `@Builder` annotation for object creation (e.g., `Courier.builder()...build()`), ensuring clean and robust entity instantiations.


## How to Run

1. Make sure you have **Java 21** installed on your system.
2. **Run via Docker Compose**  
   The command below will start both the MySQL database and the Spring Boot application together.
```bash
docker-compose up -d
```
*(No further steps required. The application will be available at `http://localhost:8080`)*


## How to Test

Once the application is running, the APIs will be available on `http://localhost:8080`.
By default, the 5 stores are loaded into the MySQL database on application startup automatically via the `data.sql` file.

> **Note:** A dummy `Courier` with ID `1` is automatically pre-loaded into the database via `data.sql` so that you can easily test this endpoint.

### Option 1: Swagger UI (API Documentation)
You can view the interactive API documentation and test the endpoints directly from your browser by navigating to:
**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### Option 2: Postman Collection
For the easiest testing experience, you can use the provided [Postman Collection](courier-tracking-service.postman_collection.json).
Simply open Postman, go to **Import > File**, and upload the `courier-tracking-service.postman_collection.json` file located in the root directory. This collection contains all necessary endpoints and sample payloads.

