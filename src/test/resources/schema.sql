-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS task_labels;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS labels;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;

-- ── Users ─────────────────────────────────────────────────────────────────────
CREATE TABLE users (
                       id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email          VARCHAR(255) NOT NULL UNIQUE,
                       password       VARCHAR(255) NOT NULL,
                       first_name     VARCHAR(100),
                       last_name      VARCHAR(100),
                       role           VARCHAR(20)  NOT NULL DEFAULT 'USER',
                       enabled        BOOLEAN      NOT NULL DEFAULT TRUE,
                       account_locked BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ── Projects ──────────────────────────────────────────────────────────────────
CREATE TABLE projects (
                          id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name       VARCHAR(255),
                          created_at TIMESTAMP
);

-- ── Labels ────────────────────────────────────────────────────────────────────
CREATE TABLE labels (
                        id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name  VARCHAR(50) NOT NULL UNIQUE,
                        color VARCHAR(7)
);

-- ── Tasks ─────────────────────────────────────────────────────────────────────
CREATE TABLE tasks (
                       id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title       VARCHAR(100) NOT NULL,
                       description TEXT,
                       status      VARCHAR(20)  NOT NULL DEFAULT 'TODO',
                       priority    VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
                       due_date    DATE,
                       created_at  TIMESTAMP,
                       project_id  BIGINT REFERENCES projects (id),
                       owner_id    BIGINT REFERENCES users (id)
);

-- ── Task ↔ Label join table (ManyToMany) ──────────────────────────────────────
CREATE TABLE task_labels (
                             task_id  BIGINT NOT NULL REFERENCES tasks (id),
                             label_id BIGINT NOT NULL REFERENCES labels (id),
                             PRIMARY KEY (task_id, label_id)
);

-- ── Comments ──────────────────────────────────────────────────────────────────
CREATE TABLE comments (
                          id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                          content    TEXT         NOT NULL,
                          author     VARCHAR(50),
                          created_at TIMESTAMP,
                          task_id    BIGINT NOT NULL REFERENCES tasks (id),
                          user_id    BIGINT REFERENCES users (id)
);