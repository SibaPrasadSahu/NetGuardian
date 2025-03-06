CREATE DATABASE network_logs;

USE network_logs;

CREATE TABLE network_traffic (
    id INT AUTO_INCREMENT PRIMARY KEY,
    source_ip VARCHAR(20),
    destination_ip VARCHAR(20),
    packet_size INT,
    protocol VARCHAR(10),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    alert_message VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
