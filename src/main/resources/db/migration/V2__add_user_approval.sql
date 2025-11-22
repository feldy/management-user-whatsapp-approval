-- Add approved column to users table
ALTER TABLE users ADD COLUMN approved BOOLEAN NOT NULL DEFAULT FALSE;

-- Set all existing users to approved (so they don't lose access)
UPDATE users SET approved = TRUE;

-- Ensure admin user is approved
UPDATE users SET approved = TRUE WHERE email = 'admin@example.com';
