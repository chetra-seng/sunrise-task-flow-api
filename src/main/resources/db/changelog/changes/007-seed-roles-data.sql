-- liquibase formatted sql

-- changeset chetra:007-seed-roles-data
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM roles WHERE name IN ('VIEWER','USER','ADMIN','SUPER_ADMIN')
INSERT INTO roles (name)
VALUES ('VIEWER'),
       ('USER'),
       ('ADMIN'),
       ('SUPER_ADMIN');

-- rollback DELETE FROM roles where name in ('VIEWER', 'USER', 'ADMIN', 'SUPER_ADMIN');