CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id          VARCHAR(255) PRIMARY KEY,
    expiry_date TIMESTAMP,
    user_id     BIGINT,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);