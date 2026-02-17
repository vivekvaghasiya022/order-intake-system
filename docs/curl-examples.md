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
  "status": "SUCCESS",
  "message": "Order created successfully",
  "data": {
    "id": 2,
    "customerEmail": "test@gmail.com",
    "productCode": "PR01",
    "quantity": 10,
    "status": "CREATED",
    "createdAt": "2026-02-17T07:14:55.878088Z"
  },
  "timestamp": "2026-02-17T07:14:56.508Z"
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
  "status": "FAILURE",
  "message": "Validation failed",
  "code": "VALIDATION_ERROR",
  "errors": [
    "quantity : Quantity should be positive"
  ],
  "timestamp": "2026-02-17T07:15:17.471Z"
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
  "status": "FAILURE",
  "message": "Validation failed",
  "code": "VALIDATION_ERROR",
  "errors": [
    "customerEmail : Invalid email format"
  ],
  "timestamp": "2026-02-17T07:15:43.476Z"
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
  "status": "FAILURE",
  "message": "Validation failed",
  "code": "VALIDATION_ERROR",
  "errors": [
    "productCode : Product code is required"
  ],
  "timestamp": "2026-02-17T07:16:19.220Z"
}
```

## 5. Get All Orders

Retrieve a list of all orders from database.

```bash
curl http://localhost:8081/api/v1/orders
```

### Expected Response (200 OK) - With Data

```json
{
  "status": "SUCCESS",
  "message": "Orders fetched successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "customerEmail": "test121@gmail.com",
        "productCode": "PR001",
        "quantity": 10,
        "status": "CREATED",
        "createdAt": "2026-02-17T07:03:08.013163Z"
      },
      {
        "id": 2,
        "customerEmail": "test@gmail.com",
        "productCode": "PR01",
        "quantity": 10,
        "status": "CREATED",
        "createdAt": "2026-02-17T07:14:55.878088Z"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 2,
    "totalPages": 1
  },
  "timestamp": "2026-02-17T07:16:59.420Z"
}
```

### Expected Response (200 OK) - Empty Database

```json
{
  "status": "SUCCESS",
  "message": "Orders fetched successfully",
  "data": {
    "content": [],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 0,
    "totalPages": 0
  },
  "timestamp": "2026-02-17T07:18:06.791Z"
}
```

### Notes
- Returns an empty array if no records exist
- Records are returned in the order they were created

## 6. Get Order by ID (Existing)

Retrieve a specific order by its ID.

```bash
curl http://localhost:8081/api/v1/orders/3
```

### Expected Response (200 OK) - With Data

```json
{
  "status": "SUCCESS",
  "message": "Order fetched successfully for id: 3",
  "data": {
    "id": 3,
    "customerEmail": "test@gmail.com",
    "productCode": "PR01",
    "quantity": 10,
    "status": "CREATED",
    "createdAt": "2026-02-17T07:18:57.416050Z"
  },
  "timestamp": "2026-02-17T07:19:30.302Z"
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
  "status": "FAILURE",
  "message": "Order not found",
  "code": "ORDER_NOT_FOUND",
  "errors": [
    "Order not found with id: 999"
  ],
  "timestamp": "2026-02-17T07:19:55.064Z"
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
{
  "status": "SUCCESS",
  "message": "Notifications fetched successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "orderId": 2,
        "type": "ORDER_CREATED",
        "delivered": true,
        "message": "SMS sent successfully",
        "createdAt": "2026-02-17T07:15:00.438246Z",
        "eventId": "81aafc46-cf37-47e3-be61-5e0cd07640b2"
      },
      {
        "id": 2,
        "orderId": 2,
        "type": "ORDER_CREATED",
        "delivered": true,
        "message": "EMAIL sent successfully",
        "createdAt": "2026-02-17T07:15:00.498126Z",
        "eventId": "81aafc46-cf37-47e3-be61-5e0cd07640b2"
      },
      {
        "id": 3,
        "orderId": 2,
        "type": "ORDER_CREATED",
        "delivered": true,
        "message": "FCM sent successfully",
        "createdAt": "2026-02-17T07:15:00.504750Z",
        "eventId": "81aafc46-cf37-47e3-be61-5e0cd07640b2"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 3,
    "totalPages": 1
  },
  "timestamp": "2026-02-17T07:20:21.502Z"
}
```


## 9. Get Notification by ID (Existing)

Retrieve a specific notification by its ID.

```bash
curl http://localhost:8082/api/v1/notifications/1
```

### Expected Response (200 OK) - With Data

```json
{
  "status": "SUCCESS",
  "message": "Notification fetched successfully with id: 1",
  "data": {
    "id": 1,
    "orderId": 2,
    "type": "ORDER_CREATED",
    "delivered": true,
    "message": "SMS sent successfully",
    "createdAt": "2026-02-17T07:15:00.438246Z",
    "eventId": "81aafc46-cf37-47e3-be61-5e0cd07640b2"
  },
  "timestamp": "2026-02-17T07:21:15.869Z"
}
```


## 10. Get Notification by Order ID (Existing)

Retrieve all notifications for a specific order by its ID.

```bash
curl http://localhost:8082/api/v1/notifications?orderId=2
```

### Expected Response (200 OK) - With Data

```json
{
  "status": "SUCCESS",
  "message": "Notifications fetched successfully for orderId: 2",
  "data": [
    {
      "id": 1,
      "orderId": 2,
      "type": "ORDER_CREATED",
      "delivered": true,
      "message": "SMS sent successfully",
      "createdAt": "2026-02-17T07:15:00.438246Z",
      "eventId": "81aafc46-cf37-47e3-be61-5e0cd07640b2"
    },
    {
      "id": 2,
      "orderId": 2,
      "type": "ORDER_CREATED",
      "delivered": true,
      "message": "EMAIL sent successfully",
      "createdAt": "2026-02-17T07:15:00.498126Z",
      "eventId": "81aafc46-cf37-47e3-be61-5e0cd07640b2"
    },
    {
      "id": 3,
      "orderId": 2,
      "type": "ORDER_CREATED",
      "delivered": true,
      "message": "FCM sent successfully",
      "createdAt": "2026-02-17T07:15:00.504750Z",
      "eventId": "81aafc46-cf37-47e3-be61-5e0cd07640b2"
    }
  ],
  "timestamp": "2026-02-17T07:22:05.900Z"
}
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
  "status": "SUCCESS",
  "message": "Order created successfully",
  "data": {
    "id": 3,
    "customerEmail": "test@gmail.com",
    "productCode": "PR01",
    "quantity": 10,
    "status": "CREATED",
    "createdAt": "2026-02-17T07:18:57.416050Z"
  },
  "timestamp": "2026-02-17T07:18:57.542Z"
}
```

### 2. Wait ~2–5 seconds

Kafka will deliver an `OrderCreated` event to the Notification Service.

### 3. Verify Notification for the order

```bash
curl http://localhost:8082/api/notifications?orderId=3
```

Expected output: Notification record for that order.
```json
{
  "status": "SUCCESS",
  "message": "Notifications fetched successfully for orderId: 3",
  "data": [
    {
      "id": 4,
      "orderId": 3,
      "type": "ORDER_CREATED",
      "delivered": true,
      "message": "SMS sent successfully",
      "createdAt": "2026-02-17T07:19:00.686873Z",
      "eventId": "77325ab8-e5f5-4ff9-89e5-7e215fd28a64"
    },
    {
      "id": 5,
      "orderId": 3,
      "type": "ORDER_CREATED",
      "delivered": true,
      "message": "EMAIL sent successfully",
      "createdAt": "2026-02-17T07:19:00.706962Z",
      "eventId": "77325ab8-e5f5-4ff9-89e5-7e215fd28a64"
    },
    {
      "id": 6,
      "orderId": 3,
      "type": "ORDER_CREATED",
      "delivered": true,
      "message": "FCM sent successfully",
      "createdAt": "2026-02-17T07:19:00.736596Z",
      "eventId": "77325ab8-e5f5-4ff9-89e5-7e215fd28a64"
    }
  ],
  "timestamp": "2026-02-17T07:23:32.000Z"
}
```