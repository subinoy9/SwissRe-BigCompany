# Org Structure Analyzer Service

This project analyzes the organizational structure of a large company using employee information stored in a CSV file. 

It identifies:

- Underpaid managers (earning <20% above avg subordinate salary)

- Overpaid managers (earning >50% above avg subordinate salary)

- Employees with reporting lines deeper than 4 levels

The application exposes REST APIs.

## Requirements

- Java 17

- Maven 3.9.11

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
Spring Boot will automatically:

- Start an embedded Tomcat server.

- Initialize Spring Boot beans including OrgStructureController and CsvReader.

- Expose REST APIs (POST /api/import, GET /api/managers/underpaid, GET /api/managers/overpaid, GET /api/employees/reporting-lines).

- Load the CSV file and print the report to console when you call the POST /api/import API (either default CSV or a client-provided path).

## API Endpoints
- POST http://localhost:8080/api/import?file=&lt;path-to-csv-file&gt;
- GET http://localhost:8080/api/managers/underpaid
- GET http://localhost:8080/api/managers/overpaid
- GET http://localhost:8080/api/employees/reporting-lines


### Example Input

1. Default CSV - [employees.csv](src/main/resources/employees.csv)
2. Provide path using API:
   POST http://localhost:8080/api/import?file=&lt;path-to-csv-file&gt;
![Screenshot 2025-12-06 at 10.06.23 PM.png](src/main/resources/images/Screenshot%202025-12-06%20at%2010.06.23%E2%80%AFPM.png)

### Example Output

1. Output printed in console
2. Output exposed through APIs 

GET http://localhost:8080/api/managers/underpaid
![Screenshot 2025-12-06 at 10.08.01 PM.png](src/main/resources/images/Screenshot%202025-12-06%20at%2010.08.01%E2%80%AFPM.png)

GET http://localhost:8080/api/managers/overpaid
![Screenshot 2025-12-06 at 10.08.15 PM.png](src/main/resources/images/Screenshot%202025-12-06%20at%2010.08.15%E2%80%AFPM.png)

GET http://localhost:8080/api/employees/reporting-lines
![Screenshot 2025-12-06 at 10.09.10 PM.png](src/main/resources/images/Screenshot%202025-12-06%20at%2010.09.10%E2%80%AFPM.png)
