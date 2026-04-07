# Fuel Consumption and Trip Cost Calculator

A JavaFX app that calculates trip fuel costs and stores results in MySQL. UI language is loaded from the database (English, Farsi, French, Japanese).

## Requirements

- Java 21+, Maven 3.8+
- MySQL 8.0+ or Docker

## Setup

### With Docker

```bash
docker compose up
```

This starts MySQL and the app together. The database and tables are created automatically from `init.sql`.

### Without Docker

1. Initialize the database:
   ```bash
   mysql -u root -p < init.sql
   ```
2. Build and run:
   ```bash
   mvn clean package
   java -jar target/demo.jar
   ```

## Database Connection

| Setting  | Default                          |
|----------|----------------------------------|
| Host     | `localhost`                      |
| Port     | `3308`                           |
| Database | `fuel_calculator_localization`   |
| User     | `fuel_app_user`                  |
| Password | `password`                       |

To override the connection URL, set the `DB_URL` environment variable before running:

```bash
export DB_URL="jdbc:mysql://localhost:3306/fuel_calculator_localization?useUnicode=true&characterEncoding=UTF-8"
```

## Sample Data

The `init.sql` script seeds all localization strings automatically. To insert additional records manually:

```sql
USE fuel_calculator_localization;

INSERT INTO calculation_records (distance, consumption, price, total_fuel, total_cost, language)
VALUES (150.0, 7.5, 1.80, 11.25, 20.25, 'en');
```
