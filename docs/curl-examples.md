# cURL Examples — Order Intake System

This document contains comprehensive cURL commands for testing all API endpoints.

## Prerequisites

- Ensure the application is running: `docker compose up --build`
- All examples use JSON format

## Table of Contents

1. [Create Order (Valid)](#1-create-order-valid)
2. [Create Order (Invalid Input: quantity <= 0)](#2-create-order-invalid-input-quantity--0)
3. [Create Order (Invalid Email)](#3-create-order-invalid-email)
4. [Create Order (Missing Fields)](#4-create-order-missing-fields)
5. [Get All Orders](#5-get-all-orders)
6. [Get Order by ID (Existing)](#6-get-order-by-id-existing)
7. [Get Order by ID (Non-Existing)](#7-get-order-by-id-non-existing)
8. [Get All Notifications](#8-get-all-notifications)
9. [Get Notification by ID (Existing)](#9-get-notification-by-id-existing)
10. [Get Notification by Order ID (Existing)](#10-get-notification-by-order-id-existing)
11. [Eventing Flow Example](#-eventing-flow-example)

---
## Order Service (Runs on port 8081)
## 1. Create Order (Valid)

Create an order with a valid customer email, quantity and product code.

### Request

```bash
curl -X POST http://localhost:8081/api/v1/orders \
--H "Content-Type: application/json" \
--d '{
    "customerEmail":"test@gmail.com",
    "productCode":"PR01",
    "quantity":10
}'
```

### Expected Response (201 Created)

```json
{
  "id": 1,
  "customerEmail": "test@gmail.com",
  "productCode": "PR01",
  "quantity": 10,
  "status": "CREATED",
  "createdAt": "2026-02-14T17:18:36.815914Z"
}
```


## 2. Create Order (Invalid Input: quantity <= 0)

Create an order with an invalid quantity (0 or negative).

### Request

```bash
curl -X POST http://localhost:8081/api/v1/orders \
--H "Content-Type: application/json" \
--d '{
    "customerEmail":"test@example.com",
    "productCode":"PR02",
    "quantity":0
}'
```

### Expected Response (400 Bad Request)

```json
{
  "timestamp": "2026-02-14T17:23:21.265785628",
  "status": 400,
  "error": "Validation Failed",
  "message": "Quantity should be positive",
  "path": "/api/v1/orders"
}
```

## 3. Create Order (Invalid Email)

Create an order with an invalid email format.

### Request

```bash
curl -X POST http://localhost:8081/api/v1/orders \
--H "Content-Type: application/json" \
--d '{
    "customerEmail":"not-email",
    "productCode":"PR03",
    "quantity":10
}'
```

### Expected Response (400 Bad Request)

```json
{
  "timestamp": "2026-02-14T17:27:31.119525372",
  "status": 400,
  "error": "Validation Failed",
  "message": "must be a well-formed email address",
  "path": "/api/v1/orders"
}
```

## 4. Create Order (Missing Fields)

Attempt to create an order with a missing required field

### Request

```bash
curl -X POST http://localhost:8081/api/v1/orders \
--H "Content-Type: application/json" \
--d '{
    "customerEmail":"test@example.com",
    "quantity":10
}'
```

### Expected Response (400 Bad Request)

```json
{
  "timestamp": "2026-02-14T17:32:01.231932187",
  "status": 400,
  "error": "Validation Failed",
  "message": "Product code is required",
  "path": "/api/v1/orders"
}
```

## 5. Get All Orders

Retrieve a list of all orders from database.

```bash
curl http://localhost:8081/api/v1/orders
```

### Expected Response (200 OK) - With Data

```json
[
    {
        "id": 1,
        "customerEmail": "test@gmail.com",
        "productCode": "PR01",
        "quantity": 10,
        "status": "CREATED",
        "createdAt": "2026-02-14T17:18:36.815914Z"
    }
]
```

### Expected Response (200 OK) - Empty Database

```json
[]
```

### Notes
- Returns an empty array if no records exist
- Records are returned in the order they were created

## 6. Get Order by ID (Existing)

Retrieve a specific order by its ID.

```bash
curl http://localhost:8081/api/v1/orders/1
```

### Expected Response (200 OK) - With Data

```json
{
  "id": 1,
  "customerEmail": "test@gmail.com",
  "productCode": "PR01",
  "quantity": 10,
  "status": "CREATED",
  "createdAt": "2026-02-14T17:18:36.815914Z"
}
```

## 7. Get Order by ID (Non-Existing)

Attempt to retrieve an order that does not exist.

```bash
curl http://localhost:8081/api/v1/orders/999
```

### Expected Response (404 Not Found)

```json
{
  "timestamp": "2026-02-14T17:41:39.854792683",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: 999",
  "path": "/api/v1/orders/999"
}
```

## Notification Service (Runs on port 8082)

## 8. Get All Notifications

Retrieve a list of all notifications from database.

```bash
curl http://localhost:8082/api/v1/notifications
```

### Expected Response (200 OK) - With Data

```json
[
  {
    "id": 1,
    "orderId": 1,
    "type": "ORDER_CREATED",
    "delivered": true,
    "message": "SMS sent successfully",
    "createdAt": "2026-02-14T17:18:38.809529Z",
    "eventId": "66676086-29f9-460a-83cd-7bfa06733368"
  },
  {
    "id": 2,
    "orderId": 1,
    "type": "ORDER_CREATED",
    "delivered": true,
    "message": "EMAIL sent successfully",
    "createdAt": "2026-02-14T17:18:38.856165Z",
    "eventId": "66676086-29f9-460a-83cd-7bfa06733368"
  },
  {
    "id": 3,
    "orderId": 1,
    "type": "ORDER_CREATED",
    "delivered": true,
    "message": "FCM sent successfully",
    "createdAt": "2026-02-14T17:18:38.861739Z",
    "eventId": "66676086-29f9-460a-83cd-7bfa06733368"
  }
]
```


## 9. Get Notification by ID (Existing)

Retrieve a specific notification by its ID.

```bash
curl http://localhost:8082/api/v1/notifications/1
```

### Expected Response (200 OK) - With Data

```json
{
  "id": 1,
  "orderId": 1,
  "type": "ORDER_CREATED",
  "delivered": true,
  "message": "SMS sent successfully",
  "createdAt": "2026-02-14T17:18:38.809529Z",
  "eventId": "66676086-29f9-460a-83cd-7bfa06733368"
}
```


## 10. Get Notification by Order ID (Existing)

Retrieve all notifications for a specific order by its ID.

```bash
curl http://localhost:8082/api/v1/notifications?orderId=1
```

### Expected Response (200 OK) - With Data

```json
[
  {
    "id": 1,
    "orderId": 1,
    "type": "ORDER_CREATED",
    "delivered": true,
    "message": "SMS sent successfully",
    "createdAt": "2026-02-14T18:01:40.768219Z",
    "eventId": "cfdd63c6-010a-4ff7-b283-fa21bc163cf6"
  },
  {
    "id": 2,
    "orderId": 1,
    "type": "ORDER_CREATED",
    "delivered": true,
    "message": "EMAIL sent successfully",
    "createdAt": "2026-02-14T18:01:40.803505Z",
    "eventId": "cfdd63c6-010a-4ff7-b283-fa21bc163cf6"
  },
  {
    "id": 3,
    "orderId": 1,
    "type": "ORDER_CREATED",
    "delivered": true,
    "message": "FCM sent successfully",
    "createdAt": "2026-02-14T18:01:40.808776Z",
    "eventId": "cfdd63c6-010a-4ff7-b283-fa21bc163cf6"
  }
]
```

## 🚀 Eventing Flow Example

### 1. Create an order

```bash
curl -X POST http://localhost:8081/api/v1/orders \
--H "Content-Type: application/json" \
--d '{
    "customerEmail":"test@gmail.com",
    "productCode":"PR01",
    "quantity":10
}'
```

### Expected Response (201 Created)

```json
{
  "id": 1,
  "customerEmail": "test@gmail.com",
  "productCode": "PR01",
  "quantity": 10,
  "status": "CREATED",
  "createdAt": "2026-02-14T17:18:36.815914Z"
}
```

### 2. Wait ~2–5 seconds

Kafka will deliver an `OrderCreated` event to the Notification Service.

### 3. Verify Notification for the order

```bash
curl http://localhost:8082/api/notifications?orderId=1
```

Expected output: Notification record for that order.
```json
[
    {
        "id": 1,
        "orderId": 1,
        "type": "ORDER_CREATED",
        "delivered": true,
        "message": "SMS sent successfully",
        "createdAt": "2026-02-14T18:01:40.768219Z",
        "eventId": "cfdd63c6-010a-4ff7-b283-fa21bc163cf6"
    },
    {
        "id": 2,
        "orderId": 1,
        "type": "ORDER_CREATED",
        "delivered": true,
        "message": "EMAIL sent successfully",
        "createdAt": "2026-02-14T18:01:40.803505Z",
        "eventId": "cfdd63c6-010a-4ff7-b283-fa21bc163cf6"
    },
    {
        "id": 3,
        "orderId": 1,
        "type": "ORDER_CREATED",
        "delivered": true,
        "message": "FCM sent successfully",
        "createdAt": "2026-02-14T18:01:40.808776Z",
        "eventId": "cfdd63c6-010a-4ff7-b283-fa21bc163cf6"
    }
]
```