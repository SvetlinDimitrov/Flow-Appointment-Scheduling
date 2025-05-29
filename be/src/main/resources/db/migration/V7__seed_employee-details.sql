INSERT INTO employee_details (user_id, salary, profit, completed_appointments, start_date, begin_working_hour, end_working_hour)
VALUES
    ((SELECT id FROM users WHERE email = 'admin@abv.bg'), 100000.00, 500000.00, 100, '2013-01-01 09:00:00', '09:00:00', '17:00:00'),
    ((SELECT id FROM users WHERE email = 'staff1@abv.bg'), 50000.00, 200000.00, 50, '2018-01-01 10:00:00', '10:00:00', '18:00:00');
