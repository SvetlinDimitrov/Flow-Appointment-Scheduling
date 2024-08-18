CREATE TABLE employee_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    salary DECIMAL(10, 2) NOT NULL,
    profit DECIMAL(10, 2) NOT NULL,
    completed_appointments INT NOT NULL,
    experience DOUBLE NOT NULL,
    begin_working_hour TIME NOT NULL,
    end_working_hour TIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO employee_details (user_id, salary, profit, completed_appointments, experience, begin_working_hour, end_working_hour)
VALUES
((SELECT id FROM users WHERE email = 'john.wick@example.com'), 100000.00, 500000.00, 100, 10.0, '09:00:00', '17:00:00'),
((SELECT id FROM users WHERE email = 'jane.doe@example.com'), 50000.00, 200000.00, 50, 5.0, '10:00:00', '18:00:00');

