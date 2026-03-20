-- liquibase formatted sql
-- changeset chetra:006-add-task-indexes

CREATE INDEX idx_tasks_completed ON tasks(completed);
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_created_at ON tasks(created_at);

-- rollback DROP INDEX idx_tasks_completed;
-- rollback DROP INDEX idx_tasks_project_id;
-- rollback DROP INDEX idx_tasks_created_at;