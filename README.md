# Receipt Processor

A Spring Boot application that processes receipts and calculates points based on specific rules.

## Overview

This application provides a RESTful API for processing receipts and calculating points based on various rules. The service exposes two endpoints:

1. `POST /receipts/process` - Process a receipt and return an ID
2. `GET /receipts/{id}/points` - Get points for a processed receipt

## Technical Stack

- Java 17
- Spring Boot 3.4.4
- Lombok
- Maven
- Docker

## Rules for Point Calculation

1. One point for every alphanumeric character in the retailer name.
2. 50 points if the total is a round dollar amount with no cents.
3. 25 points if the total is a multiple of 0.25.
4. 5 points for every two items on the receipt.
5. If the trimmed length of the item description is a multiple of 3, multiply the price by 0.2 and round up to the nearest integer.
6. 6 points if the day in the purchase date is odd.
7. 10 points if the time of purchase is after 2:00pm and before 4:00pm.

## API Specification

### Process Receipt

**Endpoint:** `POST /receipts/process`

**Request Body:**
```json
{
    "retailer": "Target",
    "purchaseDate": "2022-01-02",
    "purchaseTime": "13:13",
    "total": "1.25",
    "items": [
        {"shortDescription": "Pepsi - 12-oz", "price": "1.25"}
    ]
}
```

**Response:**
```json
{
    "id": "7fb1377b-b223-49d9-a31a-5a02701dd310"
}
```

### Get Points

**Endpoint:** `GET /receipts/{id}/points`

**Response:**
```json
{
    "points": 32
}
```

## Building and Running

### Using Maven

1. Clone the repository
2. Navigate to the project directory
3. Build the project
```
mvn clean package
```
4. Run the application
```
java -jar target/receipt-service-0.0.1-SNAPSHOT.jar
```

### Using Docker

1. Build the Docker image
```
docker build -t receipt-service .
```
2. Run the Docker container
```
docker run -p 8080:8080 receipt-service
```

### Using Docker Compose

```
docker-compose up
```

## Example Usage

### Process a receipt

```bash
curl -X POST http://localhost:8080/receipts/process \
  -H "Content-Type: application/json" \
  -d '{
    "retailer": "Target",
    "purchaseDate": "2022-01-02",
    "purchaseTime": "13:13",
    "total": "1.25",
    "items": [
        {"shortDescription": "Pepsi - 12-oz", "price": "1.25"}
    ]
}'
```

### Get points for a receipt

```bash
curl -X GET http://localhost:8080/receipts/{id}/points
```
(Replace `{id}` with the actual ID returned from the process endpoint)

## Data Persistence

Data is stored in memory and does not persist when the application is restarted. This is implemented using ConcurrentHashMap for thread safety.