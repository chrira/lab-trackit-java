CREATE TABLE comment (
    id         BIGSERIAL PRIMARY KEY,
    task_id    BIGINT       NOT NULL REFERENCES task (id) ON DELETE CASCADE,
    author     VARCHAR(255) NOT NULL,
    body       VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL
);
