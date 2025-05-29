CREATE TABLE employee_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    salary DECIMAL(10, 2) NOT NULL,
    profit DECIMAL(10, 2) NOT NULL,
    completed_appointments INT NOT NULL,
    start_date DATE NOT NULL,
    begin_working_hour TIME NOT NULL,
    end_working_hour TIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
