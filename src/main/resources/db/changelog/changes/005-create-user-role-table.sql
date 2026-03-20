--liquibase formatted sql

--changeset sunrise-team:005-create-user-role-table
CREATE TABLE user_role
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created_at TIMESTAMP,
    user_id    BIGINT NOT NULL REFERENCES users (id),
    role_id    BIGINT NOT NULL REFERENCES roles (id)
);

--rollback DROP TABLE user_role;