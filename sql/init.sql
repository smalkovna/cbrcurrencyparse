CREATE DATABASE mydb;

\c mydb;

CREATE TABLE currency (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(3) NOT NULL,
    symbol VARCHAR(5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);