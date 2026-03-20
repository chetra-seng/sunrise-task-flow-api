-- liquibase formatted sql

-- changeset senghak:009-add-assignee-to-task
ALTER TABLE tasks
ADD COLUMN assignee VARCHAR(50);

-- rollback ALTER TABLE tasks DROP COLUMN assignee;