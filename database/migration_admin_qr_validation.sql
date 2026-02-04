-- Admin table and QR code validation updates
-- Add admin table
CREATE TABLE IF NOT EXISTS admins (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(20) DEFAULT 'ADMIN',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add QR code timing columns to events
ALTER TABLE events ADD COLUMN IF NOT EXISTS event_start_time TIME;
ALTER TABLE events ADD COLUMN IF NOT EXISTS event_end_time TIME;
ALTER TABLE events ADD COLUMN IF NOT EXISTS qr_valid_until TIMESTAMP;

-- Update existing events to set qr_valid_until (3 days after event date)
UPDATE events 
SET qr_valid_until = (event_date + INTERVAL '3 days')::DATE + TIME '23:59:59'
WHERE qr_valid_until IS NULL;

-- Create default admin user (password: admin123 - CHANGE THIS!)
-- Password hash for 'admin123'
INSERT INTO admins (username, email, password, full_name, role) 
VALUES (
    'admin',
    'admin@eventgallery.com',
    '$2a$10$rKZWVhG8tH8YfGq7Y1xQR.VNZhqGx8VvK6yYqmJXKH7l8DqQqLK1m',
    'System Administrator',
    'SUPER_ADMIN'
) ON CONFLICT (username) DO NOTHING;

-- Add indexes
CREATE INDEX IF NOT EXISTS idx_events_qr_valid_until ON events(qr_valid_until);
CREATE INDEX IF NOT EXISTS idx_events_event_date ON events(event_date);
CREATE INDEX IF NOT EXISTS idx_admins_username ON admins(username);
CREATE INDEX IF NOT EXISTS idx_admins_email ON admins(email);

-- Add comments
COMMENT ON TABLE admins IS 'Admin users with full system access';
COMMENT ON COLUMN events.event_start_time IS 'Event start time for QR code validation';
COMMENT ON COLUMN events.event_end_time IS 'Event end time';
COMMENT ON COLUMN events.qr_valid_until IS 'QR code remains valid until this timestamp (event date + 3 days)';
