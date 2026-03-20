--liquibase formatted sql

--changeset sunrise-team:004-create-tasks-table
CREATE TABLE tasks
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    completed   BOOLEAN DEFAULT false,
    created_at  TIMESTAMP,
    project_id  BIGINT REFERENCES projects (id)
);

--rollback DROP TABLE tasks;