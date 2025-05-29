START TRANSACTION;

DELETE FROM appointments;
DELETE FROM users_services;
DELETE FROM services;
DELETE FROM work_spaces;
DELETE FROM staff_details WHERE user_id != (select id from users where email = 'admin@abv.bg');
DELETE FROM users WHERE email != 'admin@abv.bg';

UPDATE users SET email = 'admin@flow.com' WHERE email = 'admin@abv.bg';

COMMIT;