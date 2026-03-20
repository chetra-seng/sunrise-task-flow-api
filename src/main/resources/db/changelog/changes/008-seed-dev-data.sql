--liquibase formatted sql

--changeset sunrise-team:008-seed-dev-data context:dev,uat
INSERT INTO projects (name, created_at) VALUES ('Task Management System', CURRENT_TIMESTAMP);
INSERT INTO projects (name, created_at) VALUES ('E-Commerce Platform', CURRENT_TIMESTAMP);
INSERT INTO tasks (title, completed, project_id, created_at)
VALUES ('Design login page UI', true, 1, CURRENT_TIMESTAMP);
INSERT INTO tasks (title, completed, project_id, created_at)
VALUES ('Implement authentication API', false, 1, CURRENT_TIMESTAMP);

--rollback DELETE FROM tasks WHERE title IN ('Design login page UI','Implement authentication API');
--rollback DELETE FROM projects WHERE name IN ('Task Management System','E-Commerce Platform');