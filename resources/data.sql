-- Clean up existing data to ensure idempotency, respecting foreign key constraints
DELETE FROM todos;
DELETE FROM users_roles;
DELETE FROM categories;
DELETE FROM users;
DELETE FROM roles;

-- Seed the roles table
INSERT INTO roles (id, name, created_by, created_date) VALUES (1, 'ROLE_USER', 'APP', NOW()) ON CONFLICT (id) DO NOTHING;

-- Seed the users table with the specific user from your query
INSERT INTO users (id, created_date, is_credentials_expired, email, is_email_verified, is_enabled, first_name, last_name, is_account_locked, password, phone_number, is_phone_verified)
VALUES ('4a2028bc-4e52-44ce-ac1a-24c581627ce3', '2025-12-04 10:40:34.717635', false, 'ali@mail.com', false, true, 'Ali', 'Bouali', false, '$2a$10$O2Gb7KZYSLvjozxIPLtvue84Gz9Ppiu6TN5sPDPOCp0aaoXTSRIAi', '+49887765445', false)
ON CONFLICT (id) DO NOTHING;

-- Link the user to the role
INSERT INTO users_roles (users_id, roles_id) VALUES ('4a2028bc-4e52-44ce-ac1a-24c581627ce3', 1);

-- Seed the categories table using the exact row provided, ensuring idempotency
INSERT INTO categories (id, created_by, created_date, last_modified_by, last_modified_date, description, name)
VALUES ('1', '4a2028bc-4e52-44ce-ac1a-24c581627ce3', '2025-11-26 00:00:00', NULL, '2025-11-26 21:23:00', 'Work', 'Work')
ON CONFLICT (id) DO UPDATE SET
    created_by = EXCLUDED.created_by,
    created_date = EXCLUDED.created_date,
    last_modified_by = EXCLUDED.last_modified_by,
    last_modified_date = EXCLUDED.last_modified_date,
    description = EXCLUDED.description,
    name = EXCLUDED.name;
