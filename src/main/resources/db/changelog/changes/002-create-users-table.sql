--liquibase formatted sql

--changeset sunrise-team:002-create-users-table
CREATE TABLE users
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(150),
    last_name  VARCHAR(150),
    password   VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

--rollback DROP TABLE users;