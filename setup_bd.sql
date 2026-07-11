CREATE DATABASE IF NOT EXISTS inventario_db;
USE inventario_db;

CREATE TABLE equipos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    numero_serie VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(100) NOT NULL
);