#  Order Intake & Notification System (Event-Driven)

A distributed microservices system built with Spring Boot, Apache Kafka, and MySQL. It demonstrates an asynchronous, event-driven flow where orders are processed and notifications are triggered via message events.

## Features

- ✅ Asynchronous Architecture - Loose coupling between Order and Notification services.
- ✅ Idempotent Consumer - Prevents duplicate notifications using a processed-events tracking table.
- ✅ Transactional Integrity - Ensures database records are consistent with event publishing.
- ✅ MySQL Persistence - Single MySQL instance using logical schema separation for streamlined infrastructure management.
- ✅ Database Version Control – Integrated with Flyway to handle automated schema migrations, ensuring consistent table structures across environments.
- ✅ Kafka Integration - Reliable message brokering using KRaft mode.
- ✅ Dockerized Environment - Full system setup with a single command.

## Technology Stack

- **Java 17**
- **Spring Boot 4.0.2**
- **Spring Data JPA**
- **MySQL 8.0**
- **Apache Kafka**
- **Maven**
- **Docker & Docker Compose**
- **Lombok**
- **Flyway** (for database migrations)

## Architecture Overview
```
Client
   ↓
order-service (Producer)
   ↓
Kafka (Message Broker)
   ↓
notification-service (Consumer)
   ↓
Database (MySQL)
```

- `order-service` → exposes REST APIs & publishes `OrderCreated` event
- `notification-service` → consumes event & creates notification record

## How to Run Everything

### 1️⃣ Prerequisites
- Docker & Docker Compose installed

### 2️⃣ Run Entire System
From project root:
```bash
docker compose up --build
```
This will start:
- order-service
- notification-service
- Kafka
- Kafka-UI
- MySQL

### 3️⃣ Verify Services

- Order Service → `http://localhost:8081`
- Notification Service → `http://localhost:8082`
- Kafka-UI (Dashboard): `http://localhost:8180` (Monitor topics and messages here)

## Broker Choice & Configuration

### 1. Kafka Cluster (KRaft Mode)
This project uses:  
- **Kafka 3-node cluster**
- **KRaft mode (No Zookeeper)**
- Confluent Image: `confluentinc/cp-kafka:7.5.0`

### Bootstrap Servers 
#### Inside Docker Network (used by services):
```
kafka-1:29092,kafka-2:29092,kafka-3:29092
```
#### From Local Machine (for testing via CLI/tools):
```
localhost:9092,localhost:9093,localhost:9094
```

### Topic Configuration

- **Topic Name:** `order-created-topic`
- **Partitions: 3** (for scalability)
- **Replication Factor: 3** (for fault tolerance)

### Producer Configuration (order-service)
```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_URL:localhost:9092,localhost:9093,localhost:9094}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```
### Consumer Configuration (notification-service)
```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_URL:localhost:9092,localhost:9093,localhost:9094}
    consumer:
      group-id: notification-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
```


### 2. Event Contract
#### Event Name
```
OrderCreated
```
#### JSON Format
```json
{
	"eventId": "0a9bbf6a-42c6-47c6-853f-781d7fccd40b",
	"occurredAt": 1770906286.873054800,
	"orderId": 1,
	"customerEmail": "test@gmail.com",
	"productCode": "P019",
	"quantity": 15
}
```
#### Fields
| Field         | Type          | Description             |
| ------------- | ------------- | ----------------------- |
| eventId       | String (UUID) | Unique event identifier |
| occurredAt    | Timestamp     | Event creation time     |
| orderId       | Long          | ID of created order     |
| customerEmail | String        | Customer email          |
| productCode   | String        | Product code            |
| quantity      | Integer       | Ordered quantity        |


## Error Handling Strategy
### 1️⃣ Order Creation Flow
**If:**
- Validation fails → `400 Bad Request`
- Order not found → `404 Not Found`
- Database error → `500 Internal Server Error`
- Kafka is temporarily unavailable → **Order still succeeds**

### ✅ Strategy Used: Transactional Outbox Pattern

Instead of publishing directly to Kafka inside the service transaction, this system implements the **Transactional Outbox Pattern**.

### How It Works
#### 1. Within a single database transaction:  
   - Order is persisted 
   - An Outbox Event record is stored in `outbox_events` table
#### 2. If anything fails during this step:
   - Entire transaction rolls back
   - No order is created
   - No event is stored
#### 3. A background publisher component:
   - Polls up to 50 events from `outbox_events` with status `PENDING` (using `SELECT ... FOR UPDATE SKIP LOCKED`)
   - Rows are pessimistically locked inside a transaction, ensuring:
     - Multiple publisher instances can run concurrently
     - Already locked rows are skipped
     - No event is processed twice
   - Publishes events to Kafka
   - If publishing succeeds → marks event as `PROCESSED`
   - If publishing fails event will be retried for a configurable number of attempts
   - If all attempts fail, event is marked as `FAILED` and error details are logged


### 2️⃣ Consumer Error Handling
Inside `notification-service`:
- Processing wrapped in try/catch
- If successful → `delivered = true`
- If failed → `delivered = false`
- `errorMessage` stored in DB

### 3️⃣ Idempotency Strategy (Critical)
To prevent duplicate processing:
- Before saving notification:
  - Check if event already processed
  - If yes → skip processing
  - If no → process and save eventId in `notification` table

This guarantees:  
✔ Same event processed twice → no duplicate notification  
✔ Safe for Kafka re-delivery

## Example Flow (Eventing Demonstration)
### Step 1 — Create Order
```bash
curl -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail":"test121@gmail.com",
    "productCode":"PR001",
    "quantity":10
  }'
```

### Step 2 — Verify Notification
```bash
curl http://localhost:8082/api/v1/notifications
```

You will see a notification created for that order.

## Database

#### MySQL
- Persistent database
- Data is stored in a Docker volume
- Connection details:
    - Host: `localhost`
    - Port: `3306`
    - Database: `order_db`
    - Username: `mysql`
    - Password: `mysql`

## API Endpoints

### Order Service (`:8081`)
```
http://localhost:8081/api/v1/orders
```

### Endpoints Overview

| Method | Endpoint           | Description                       |
|--------|-------------------|-----------------------------------|
| POST   | `/api/v1/orders`     | Create an order and trigger event |
| GET    | `/api/v1/orders`     | Get all orders                    |
| GET    | `/api/v1/orders/{id}`| Get an order by Id                |

### Notification Service (`:8082`)
```
http://localhost:8082/api/v1/notifications
```

### Endpoints Overview

| Method | Endpoint                  | Description                      |
|--------|---------------------------|----------------------------------|
| GET    | `/api/v1/notifications`      | List all notifications           |
| GET    | `/api/v1/notifications/{id}` | Get notification by Id           |
| GET    | `/api/v1/notifications?orderId={id}`     | Filter notifications by Order Id |


## API Usage Examples

See [docs/curl-examples.md](docs/curl-examples.md) for detailed curl commands.

### Quick Examples

#### Create an Order (Valid)
```bash
curl -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail":"test121@gmail.com",
    "productCode":"PR001",
    "quantity":10
  }'
```

#### Get All Orders
```bash
curl http://localhost:8081/api/v1/orders
```

#### Get an order by Id
```bash
curl http://localhost:8081/api/v1/orders/1
```

## Response Examples

### Success Response (201 Created)
```json
{
  "createdAt": "2026-02-13T10:31:43.442153100Z",
  "customerEmail": "test121@gmail.com",
  "id": 1,
  "productCode": "PR001",
  "quantity": 10,
  "status": "CREATED"
}
```

### Error Response (400 Bad Request - Invalid Order)
```json
{
  "error": "Validation Failed",
  "message": "Quantity should be positive",
  "path": "/api/v1/orders",
  "status": 400,
  "timestamp": "2026-02-13T15:37:59.6179756"
}
```

### Error Response (404 Not Found)
```json
{
  "error": "Not Found",
  "message": "Order not found with id: a306b0ed-9298-4dff-bdfd-d99eac4b0c70",
  "path": "/api/v1/orders/a306b0ed-9298-4dff-bdfd-d99eac4b0c70",
  "status": 404,
  "timestamp": "2026-02-13T15:40:16.4194449"
}
```

## Project Structure

```
order-intake-system
│
├── docker-compose.yml
├── README.md
│
├── order-service
│   ├── Dockerfile
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com/springboot/orderservice
│       │   │       ├── OrderServiceApplication.java
│       │   │       ├── config
│       │   │       │   ├── AppConfig.java
│       │   │       │   └── KafkaTopicConfig.java
│       │   │       ├── controller
│       │   │       │   └── OrderController.java
│       │   │       ├── service
│       │   │       │   └── OrderService.java
│       │   │       ├── repository
│       │   │       │   ├── OutboxEventRepository.java
│       │   │       │   └── OrderRepository.java
│       │   │       ├── model
│       │   │       │   ├── Order.java
│       │   │       │   └── OutboxEvent.java
│       │   │       ├── dto
│       │   │       │   ├── event/OrderCreated.java
│       │   │       │   ├── OrderRequest.java
│       │   │       │   ├── OrderResponse.java
│       │   │       │   ├── EventStatusEnum.java
│       │   │       │   └── OrderStatusEnum.java
│       │   │       ├── producer
│       │   │       │   └── OutboxEventPublisher.java
│       │   │       └── exception
│       │   │           ├── GlobalExceptionHandler.java
│       │   │           └── OrderNotFoundException.java
│       │   │
│       │   └── resources
│       │       ├── db/migration/V1__init_order_schema.sql
│       │       └── application.yml
│       │
│       └── test
│           └── java/com/springboot/orderservice
│               ├── controller/OrderControllerTest.java
│               ├── service/OrderServiceTest.java
│               └── repository/OrderRepositoryTest.java
│
│
├── notification-service
│   ├── Dockerfile
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com/springboot/notificationservice
│       │   │       ├── NotificationServiceApplication.java
│       │   │       ├── controller
│       │   │       │   └── NotificationController.java
│       │   │       ├── service
│       │   │       │   ├── NotificationService.java
│       │   │       │   └── impl/NotificationServiceImpl.java
│       │   │       ├── repository
│       │   │       │   └── NotificationRepository.java
│       │   │       ├── model
│       │   │       │   └── Notification.java
│       │   │       ├── dto
│       │   │       │   ├── event/OrderCreated.java
│       │   │       │   └── NotificationResponse.java
│       │   │       ├── mapper
│       │   │       │   └── NotificationMapper.java
│       │   │       ├── utility
│       │   │       │   └── NotificationUtil.java
│       │   │       ├── listener
│       │   │       │   └── OrderCreatedListener.java
│       │   │       └── exception
│       │   │           └── GlobalExceptionHandler.java
│       │   │
│       │   └── resources
│       │       ├── db/migration/V1__init_notification_schema.sql
│       │       └── application.yml
│       │
│       └── test
│           └── java/com/springboot/notificationservice
│               ├── controller/NotificationControllerTest.java
│               ├── service/NotificationServiceTest.java
│               ├── repository/NotificationRepositoryTest.java
│               └── listener/OrderCreatedListenerTest.java
│
└── docs
    └── curl-examples.md
```

## API Testing

### Postman Collection

A comprehensive Postman collection is available with 17+ automated tests covering all positive and negative scenarios:

- **Location**: `postman/` directory
- **Files**:
  - `Order_Intake_System_API_Collection.json` - Complete test suite
  - `Order_Intake_System_API_Environment.json` - Environment configuration
  - `POSTMAN_GUIDE.md` - Detailed usage guide
  - `QUICK_REFERENCE.md` - Quick start reference

**Quick Start**:
1. Import both JSON files into Postman
2. Select the environment from the dropdown
3. Run the collection

See [postman/POSTMAN_GUIDE.md](postman/POSTMAN_GUIDE.md) for complete instructions.

### cURL Examples

For detailed API documentation with all curl examples, see [docs/curl-examples.md](docs/curl-examples.md).

## License

This project is for interview/challenge purposes.

## Contact

For questions or issues, please refer to the challenge requirements document.