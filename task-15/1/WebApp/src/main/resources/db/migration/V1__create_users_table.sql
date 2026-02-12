CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100)
);

INSERT INTO users (username, email) VALUES ('Kolya', 'kolya@postgres.com');