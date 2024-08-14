CREATE TABLE work_spaces (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    available_slots_at_a_time INT NOT NULL
);

CREATE TABLE services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    work_space_id BIGINT,
    FOREIGN KEY (work_space_id) REFERENCES work_spaces(id)
);


CREATE TABLE users_services (
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, service_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Insert default work spaces
INSERT INTO work_spaces (name, available_slots_at_a_time) VALUES
('Gym', 10),
('Spa', 5),
('Clinic', 3);

-- Insert default services with work_space references
INSERT INTO services (name, description, duration_minutes, price, work_space_id) VALUES
('Fitness', 'Fitness training session', 60, 50.00, (SELECT id FROM work_spaces WHERE name = 'Gym')),
('Massage', 'Relaxing massage session', 45, 70.00, (SELECT id FROM work_spaces WHERE name = 'Spa')),
('Skin Care', 'Skin care treatment', 30, 40.00, (SELECT id FROM work_spaces WHERE name = 'Clinic'));

-- Associate the EMPLOYEE user (Jane Doe) with these services
INSERT INTO users_services (user_id, service_id) VALUES
((SELECT id FROM users WHERE email = 'jane.doe@example.com'), (SELECT id FROM services WHERE name = 'Fitness')),
((SELECT id FROM users WHERE email = 'jane.doe@example.com'), (SELECT id FROM services WHERE name = 'Massage')),
((SELECT id FROM users WHERE email = 'jane.doe@example.com'), (SELECT id FROM services WHERE name = 'Skin Care'));