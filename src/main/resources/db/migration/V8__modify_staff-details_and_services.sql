ALTER TABLE employee_details ADD COLUMN availability BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE employee_details RENAME TO staff_details;