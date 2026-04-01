-- mysql -u root -p < init.sql
-- Create Database

CREATE DATABASE IF NOT EXISTS fuel_calculator_localization
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fuel_calculator_localization;

-- Create Tables
CREATE TABLE IF NOT EXISTS calculation_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    distance DOUBLE NOT NULL,
    consumption DOUBLE NOT NULL,
    price DOUBLE NOT NULL,
    total_fuel DOUBLE NOT NULL,
    total_cost DOUBLE NOT NULL,
    language VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS localization_strings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    language VARCHAR(10) NOT NULL,
    UNIQUE KEY unique_key_lang (`key`, `language`)
);

-- Create Dedicated User

CREATE USER IF NOT EXISTS 'fuel_app_user'@'localhost' IDENTIFIED BY 'password';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP
ON fuel_calculator_localization.*
TO 'fuel_app_user'@'localhost';

FLUSH PRIVILEGES;


-- Localization Inserts


-- English (en)
INSERT INTO localization_strings (`key`, value, language) VALUES
('distance.label', 'Distance (km)', 'en'),
('consumption.label', 'Fuel Consumption (L/100 km)', 'en'),
('price.label', 'Fuel Price (per liter)', 'en'),
('calculate.button', 'Calculate Trip Cost', 'en'),
('result.label', 'Total fuel needed: {0} L | Total cost: {1}', 'en'),
('invalid.input', 'Invalid input', 'en'),
('distance.prompt', 'Enter distance', 'en'),
('consumption.prompt', 'Enter consumption', 'en'),
('price.prompt', 'Enter price', 'en')
ON DUPLICATE KEY UPDATE value = VALUES(value);

-- Persian / Farsi (fa)
INSERT INTO localization_strings (`key`, value, language) VALUES
('distance.label', 'مسافت (km)', 'fa'),
('consumption.label', 'مصرف سوخت (L/100 km)', 'fa'),
('price.label', 'قیمت سوخت (هر لیتر)', 'fa'),
('calculate.button', 'محاسبه هزینه سفر', 'fa'),
('result.label', 'سوخت مورد نیاز: {0} L | هزینه کل: {1}', 'fa'),
('invalid.input', 'ورودی نامعتبر', 'fa'),
('distance.prompt', 'مسافت را وارد کنید', 'fa'),
('consumption.prompt', 'مصرف را وارد کنید', 'fa'),
('price.prompt', 'قیمت را وارد کنید', 'fa')
ON DUPLICATE KEY UPDATE value = VALUES(value);

-- French (fr)
INSERT INTO localization_strings (`key`, value, language) VALUES
('distance.label', 'Distance (km)', 'fr'),
('consumption.label', 'Consommation de carburant (L/100 km)', 'fr'),
('price.label', 'Prix du carburant (par litre)', 'fr'),
('calculate.button', 'Calculer le coût du trajet', 'fr'),
('result.label', 'Carburant nécessaire : {0} L | Coût total : {1}', 'fr'),
('invalid.input', 'Entrée invalide', 'fr'),
('distance.prompt', 'Entrer la distance', 'fr'),
('consumption.prompt', 'Entrer la consommation', 'fr'),
('price.prompt', 'Entrer le prix', 'fr')
ON DUPLICATE KEY UPDATE value = VALUES(value);

-- Japanese (ja)
INSERT INTO localization_strings (`key`, value, language) VALUES
('distance.label', '距離 (km)', 'ja'),
('consumption.label', '燃料消費量 (L/100 km)', 'ja'),
('price.label', '燃料価格 (リットルあたり)', 'ja'),
('calculate.button', '旅行費用を計算', 'ja'),
('result.label', '必要な燃料: {0} L | 合計費用: {1}', 'ja'),
('invalid.input', '無効な入力', 'ja'),
('distance.prompt', '距離を入力', 'ja'),
('consumption.prompt', '消費量を入力', 'ja'),
('price.prompt', '価格を入力', 'ja')
ON DUPLICATE KEY UPDATE value = VALUES(value);