START TRANSACTION;

DELETE FROM appointments;
DELETE FROM users_services;
DELETE FROM services;
DELETE FROM work_spaces;
DELETE FROM staff_details;
DELETE FROM users;

COMMIT;