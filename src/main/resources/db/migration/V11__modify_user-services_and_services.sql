ALTER TABLE users_services
DROP FOREIGN KEY users_services_ibfk_1,
DROP FOREIGN KEY users_services_ibfk_2;

ALTER TABLE users_services
ADD CONSTRAINT users_services_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
ADD CONSTRAINT users_services_ibfk_2 FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE;

ALTER TABLE services
DROP COLUMN duration_minutes;

ALTER TABLE services
ADD COLUMN duration NUMERIC(21, 0) NOT NULL;

