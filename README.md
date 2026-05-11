# Production-Ready Order Service (Spring Boot)

A production-style Order Management Service built using Spring Boot, following clean architecture principles and real-world backend engineering practices.

This project demonstrates how to design scalable, resilient, and observable backend systems using modern Java and Spring ecosystem tools.

---

## Tech Stack

- Java 17+
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Spring Security (Basic Auth)
- Spring Cache
- Spring Retry
- Spring Async (ThreadPoolTaskExecutor)
- Micrometer (Metrics)
- Actuator (Health Monitoring)
- Transactional Outbox Pattern
- Hexagonal Architecture (Port-Adapter)

---

## Features Implemented

### Core Functionality
- Create Order
- Get Order by ID
- Pagination & Sorting
- Search Orders by item name

---

### Security
- HTTP Basic Authentication
- Role-based endpoint protection
- CSRF disabled for API usage

---

### Production-Level Enhancements

#### 1️) Idempotency
Prevents duplicate order creation using unique `idempotencyKey`.

#### 2️) Optimistic Locking
Handles concurrent updates safely using `@Version`.

#### 3️) Caching
In-memory caching using `ConcurrentMapCacheManager` for read optimization.

#### 4️) Pagination
Prevents heavy DB load by returning paged results.

#### 5) Logging with MDC
Each request is assigned a unique requestId for traceability.
Async logs preserve request context.

#### 6️) Async Processing
Custom ThreadPoolExecutor for background processing.

#### 7️) Transactional Outbox Pattern
Ensures reliable event publishing:
- Save order
- Save outbox event
- Scheduler processes events
- Publishes domain event
- Marks event as processed

#### 8️) Retry + Circuit Breaker (Basic Simulation)
- Retries inventory call 3 times
- Opens circuit after repeated failures
- Skips external calls when system unstable

#### 9️) Observability
- Custom Health Indicator
- Micrometer metrics for failures and circuit state

---

##  Architecture Overview

Controller → Service → Repository  
Event-Driven Internal Communication  
Port-Adapter for External Integrations

Order Creation Flow:

1. Save Order
2. Save Outbox Event
3. Scheduler fetches unprocessed events
4. Publishes Domain Event
5. Async Listener handles inventory
6. Event marked as processed

---

## How to Run

1. Clone repository
2. Configure PostgreSQL in `application.properties`
3. Run: ```mvn clean install```
        ```mvn spring-boot:run```


---

## 🔍 Sample API

### Create Order

POST /orders

```json
{
  "orderId": "ORD-101",
  "itemName": "Laptop",
  "quantity": 1,
  "idempotencyKey": "unique-key-123"
}
