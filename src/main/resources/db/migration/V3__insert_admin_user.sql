INSERT INTO users (
    id, email, password, password_last_updated, role, username, verification_code
)
SELECT
    99999999999,
    'admin@hotmail.com',
    '$2a$10$vetP.kJhtZkDQE/se1k5FOpujbxKJEgPS.lTL3wBW24j32iXnQkLe',
    NOW(),
    'ADMIN',
    'admin',
    NULL
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@hotmail.com' OR username = 'admin'
);
