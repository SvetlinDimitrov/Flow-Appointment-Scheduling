CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255) UNIQUE                          NOT NULL,
    password   VARCHAR(255)                                 NOT NULL,
    role       ENUM ('ADMINISTRATOR', 'EMPLOYEE', 'CLIENT') NOT NULL
);