--liquibase formatted sql

--changeset chetra:001-create-projects-table
CREATE TABLE projects
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255),
    created_at TIMESTAMP
);

--rollback DROP TABLE projects;