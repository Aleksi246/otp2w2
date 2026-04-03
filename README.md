# Fuel Consumption and Trip Cost Calculator

A JavaFX desktop application that calculates fuel consumption and trip costs. It stores calculation history in a MySQL database and supports multiple UI languages (English, Persian/Farsi, French, Japanese) loaded from the database at runtime.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 21+ |
| Maven | 3.8+ |
| MySQL | 8.0+ **or** Docker + Docker Compose |

---

## Database Configuration

### Option A — Docker Compose (recommended)

The `docker-compose.yml` file starts a MySQL 8.0 container and the application container together.

```bash
docker compose up
```

The compose file automatically:
- Creates the database `fuel_calculator_localization`
- Creates the dedicated user `fuel_app_user` with password `password`
- Runs `init.sql` to create the schema and seed localization strings
- Exposes MySQL on host port **3307**

> **Note:** If port 3307 is already in use on your machine, change the host-side port in `docker-compose.yml`:
> ```yaml
> ports:
>   - "3308:3306"   # change 3307 to any free port
> ```

---

### Option B — Local MySQL (manual setup)

#### 1. Run the initialization script

```bash
mysql -u root -p < init.sql
```

`init.sql` performs the following steps automatically:

1. Creates the database `fuel_calculator_localization` (UTF-8 / utf8mb4)
2. Creates the tables `calculation_records` and `localization_strings`
3. Creates the dedicated database user and grants the required privileges
4. Seeds all localization strings for all supported languages

#### 2. Database connection details

| Parameter | Default value |
|-----------|---------------|
| Host | `localhost` |
| Port | `3308` |
| Database | `fuel_calculator_localization` |
| User | `fuel_app_user` |
| Password | `password` |

The connection URL read by the application:

```
jdbc:mysql://localhost:3308/fuel_calculator_localization?useUnicode=true&characterEncoding=UTF-8
```

#### 3. Override the connection URL at runtime

Set the `DB_URL` environment variable before launching the application to point to a different host, port, or database:

```bash
export DB_URL="jdbc:mysql://localhost:3306/fuel_calculator_localization?useUnicode=true&characterEncoding=UTF-8"
```

---

### Database Schema

```sql
-- Stores each calculation result
CREATE TABLE calculation_records (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    distance     DOUBLE NOT NULL,
    consumption  DOUBLE NOT NULL,
    price        DOUBLE NOT NULL,
    total_fuel   DOUBLE NOT NULL,
    total_cost   DOUBLE NOT NULL,
    language     VARCHAR(10),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stores UI strings for each supported language
CREATE TABLE localization_strings (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    `key`    VARCHAR(100) NOT NULL,
    value    VARCHAR(255) NOT NULL,
    language VARCHAR(10)  NOT NULL,
    UNIQUE KEY unique_key_lang (`key`, `language`)
);
```

### Supported Languages

| Code | Language |
|------|----------|
| `en` | English |
| `fa` | Persian / Farsi |
| `fr` | French |
| `ja` | Japanese |

### Sample Data Insertion (optional)

To add an additional language manually:

```sql
USE fuel_calculator_localization;

INSERT INTO localization_strings (`key`, value, language) VALUES
('distance.label',    'Distanz (km)',                              'de'),
('consumption.label', 'Kraftstoffverbrauch (L/100 km)',            'de'),
('price.label',       'Kraftstoffpreis (pro Liter)',               'de'),
('calculate.button',  'Reisekosten berechnen',                     'de'),
('result.label',      'Benötigter Kraftstoff: {0} L | Gesamtkosten: {1}', 'de'),
('invalid.input',     'Ungültige Eingabe',                         'de'),
('distance.prompt',   'Distanz eingeben',                          'de'),
('consumption.prompt','Verbrauch eingeben',                        'de'),
('price.prompt',      'Preis eingeben',                            'de')
ON DUPLICATE KEY UPDATE value = VALUES(value);
```

---

## Building and Running

### Build the JAR

```bash
mvn clean package
```

The shaded executable JAR is produced at `target/demo.jar`.

### Run locally

```bash
java -jar target/demo.jar
```

If the database is on a non-default port, set `DB_URL` first (see [Override the connection URL](#3-override-the-connection-url-at-runtime)).

### Run with Docker Compose

```bash
# Build your local image first (replace <yourname> with your Docker Hub username)
docker build -t <yourname>/otp2w2:latest .

# Update the image name in docker-compose.yml, then:
docker compose up
```

---

## Project Structure

```
src/main/java/com/example/
  Main.java               – JavaFX entry point
  Controller.java         – UI event handling and calculation logic
  DatabaseConnection.java – JDBC connection factory (reads DB_URL env var)
  RecordDao.java          – Data access for calculation_records
  LocalizationService.java– Loads UI strings from localization_strings table
src/main/resources/
  view.fxml               – JavaFX UI layout
init.sql                  – Database initialization and seed script
docker-compose.yml        – Multi-container setup (MySQL + app)
Dockerfile                – Application container definition
```
