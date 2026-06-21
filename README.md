# XPrinter Test — Sales Management System

A Spring Boot web application for managing sales records with thermal receipt printing support via an XPrinter USB thermal printer.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.1.0, Spring MVC, Spring Data JPA |
| Frontend | Thymeleaf, HTML/CSS |
| Database | MySQL 8.0 |
| Printing | Java Print Service (javax.print) with ESC/POS |
| Build | Maven (Maven Wrapper included) |
| Java | Java 21 |

---

## Features

- Add new sales (customer name, item, quantity, unit price)
- Auto-calculates total price and records sale date
- View all sales in a table
- Print a thermal receipt for any sale via USB thermal printer
- Flash messages for print success/failure

---

## Prerequisites

- Java 21+
- Maven (or use the included `mvnw` wrapper)
- MySQL 8.0 running (Docker recommended)
- XPrinter 80T (or compatible) USB thermal printer connected and installed

---

## Database Setup

Start MySQL using Docker:

```bash
docker run -d --name mysql-server \
  -e MYSQL_ROOT_PASSWORD=your_password \
  -p 3306:3306 \
  mysql:8.0
```

The database `mad_db` is created automatically on first run (`createDatabaseIfNotExist=true`).

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mad_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Colombo&useLegacyDatetimeCode=false&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

printer.name=XP-80C (copy 1)
```

> **Finding your printer name:** Start the app and visit `http://localhost:8080/printers` to see all printer names registered on your machine. Copy the exact name into `printer.name`.

---

## Running the App

```bash
.\mvnw spring-boot:run
```

Then open: [http://localhost:8080/sales](http://localhost:8080/sales)

---

## Project Structure

```
src/
└── main/
    ├── java/com/example/xprinter_test/
    │   ├── XprinterTestApplication.java   # Entry point
    │   ├── Sale.java                      # JPA entity → sales table
    │   ├── SaleRepository.java            # Spring Data repository
    │   ├── SaleService.java               # Business logic
    │   ├── SaleController.java            # MVC controller (routes)
    │   └── ThermalPrinterService.java     # ESC/POS receipt printer
    └── resources/
        ├── application.properties         # App configuration
        └── templates/
            └── sales.html                 # Thymeleaf UI
```

---

## API Endpoints

| Method | URL | Description |
|---|---|---|
| GET | `/sales` | View sales page with form |
| POST | `/sales` | Submit a new sale |
| POST | `/sales/{id}/print` | Print receipt for a sale |
| GET | `/printers` | List all available printer names (JSON) |

---

## Receipt Format

```
--------------------------------
          MY STORE
        Sales Receipt
--------------------------------
Customer : John Silva
Item     : Printer Paper
Qty      : 5
Unit     : Rs. 150.00
Total    : Rs. 750.00
Date     : 2026-06-21 14:30
--------------------------------
         Thank You!
--------------------------------


[paper cut]
```

---

## Printer Setup Notes

- Printer must be installed and visible in Windows **Devices and Printers**
- Uses `DocFlavor.BYTE_ARRAY.AUTOSENSE` to send raw ESC/POS bytes over USB
- If printing fails, visit `/printers` to verify the printer name matches `application.properties`
