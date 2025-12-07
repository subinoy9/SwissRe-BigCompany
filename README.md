# Hierarchy Analysis Service

A lightweight Java-based microservice that analyzes the organizational structure of a company (provided via CSV). It helps identify managers whose compensation may not align with that of their teams — and also detects overly deep reporting hierarchies.

## What it does

- Underpaid Managers — Managers earning less than 20% above the average salary of their direct/subordinate employees

- Overpaid Managers — Managers earning more than 50% above the average salary of their direct/subordinates

- Deep Reporting Lines — Employees whose chain of command exceeds 4 levels

- The service is exposed via REST-ful APIs for easy integration or automation.

## Prerequisites

- Java 21

- Apache Maven 3.9.11

- Spring Boot 3.1.4

## Build
```
mvn clean install
```

## Run Service locally

Run Service
```
mvn spring-boot:run
```

### Once started, the service will:

- Launch an embedded Tomcat server

- Initialize all required Spring beans (e.g. controller, CSV-reader component)

- Provide REST endpoints as described below

- Accept CSV input either from a default file or a client-provided path

## API Endpoints
- **POST** `http://localhost:8080/api/import`  
  If no request body is provided, the service automatically loads the default [employees.csv](src/main/resources/employees.csv).
![Screenshot 2025-12-07 at 1.47.40 AM.png](src/main/resources/images/Screenshot%202025-12-07%20at%201.47.40%E2%80%AFAM.png)
- `GET http://localhost:8080/api/managers/underpaid`
- `GET http://localhost:8080/api/managers/overpaid`
- `GET http://localhost:8080/api/employees/reporting-lines`

### Swagger Documentation
Open the live API documentation here:

👉 **Swagger yml:** [swagger.yml](src/main/resources/swagger/swagger.yml)

### Key Assumptions

- The CSV file has the format: employeeId, firstName, lastName, salary, managerId, and managerId may be empty for the CEO.

- Exactly one CEO exists (the only employee with no manager).

- Invalid or incomplete CSV rows are skipped, and missing manager references are treated as “orphan” but not counted as long reporting lines.