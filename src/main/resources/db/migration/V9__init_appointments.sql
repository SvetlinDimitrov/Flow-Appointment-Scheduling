-- Create the appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status ENUM('NOT_APPROVED', 'APPROVED', 'COMPLETED', 'CANCELED') NOT NULL,
    service_id BIGINT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES users(id),
    FOREIGN KEY (staff_id) REFERENCES users(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Modify the users table to add a reference to services
ALTER TABLE users
ADD COLUMN service_id BIGINT,
ADD FOREIGN KEY (service_id) REFERENCES services(id);

-- Modify the services table to add a reference to users
ALTER TABLE services
ADD COLUMN user_id BIGINT,
ADD FOREIGN KEY (user_id) REFERENCES users(id);