INSERT INTO work_spaces (name, available_slots)
VALUES ('Gym', 10),
       ('Spa', 5),
       ('Clinic', 1);

INSERT INTO services (name, description, duration_minutes, price, work_space_id)
VALUES ('Fitness', 'Fitness training session', 60, 50.00,
        (SELECT id FROM work_spaces WHERE name = 'Gym')),
       ('Massage', 'Relaxing massage session', 45, 70.00,
        (SELECT id FROM work_spaces WHERE name = 'Spa')),
       ('Skin Care', 'Skin care treatment', 30, 40.00,
        (SELECT id FROM work_spaces WHERE name = 'Clinic'));

INSERT INTO users_services (user_id, service_id)
VALUES ((SELECT id FROM users WHERE email = 'jane.doe@example.com'),
        (SELECT id FROM services WHERE name = 'Fitness')),
       ((SELECT id FROM users WHERE email = 'jane.doe@example.com'),
        (SELECT id FROM services WHERE name = 'Massage')),
       ((SELECT id FROM users WHERE email = 'jane.doe@example.com'),
        (SELECT id FROM services WHERE name = 'Skin Care'));