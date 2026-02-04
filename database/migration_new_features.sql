-- Migration script for new features
-- Add guest password column for authentication
ALTER TABLE guests ADD COLUMN IF NOT EXISTS password VARCHAR(255);

-- Create shared_folders table
CREATE TABLE IF NOT EXISTS shared_folders (
    id BIGSERIAL PRIMARY KEY,
    folder_name VARCHAR(255) NOT NULL,
    share_code VARCHAR(100) NOT NULL UNIQUE,
    access_password VARCHAR(255),
    event_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    expiry_date TIMESTAMP,
    download_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Create folder_images junction table
CREATE TABLE IF NOT EXISTS folder_images (
    folder_id BIGINT NOT NULL,
    image_id BIGINT NOT NULL,
    PRIMARY KEY (folder_id, image_id),
    FOREIGN KEY (folder_id) REFERENCES shared_folders(id) ON DELETE CASCADE,
    FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_shared_folders_share_code ON shared_folders(share_code);
CREATE INDEX IF NOT EXISTS idx_shared_folders_customer_id ON shared_folders(customer_id);
CREATE INDEX IF NOT EXISTS idx_shared_folders_event_id ON shared_folders(event_id);
CREATE INDEX IF NOT EXISTS idx_folder_images_folder_id ON folder_images(folder_id);
CREATE INDEX IF NOT EXISTS idx_folder_images_image_id ON folder_images(image_id);
CREATE INDEX IF NOT EXISTS idx_guests_email_event ON guests(email, event_id);

-- Add comments
COMMENT ON TABLE shared_folders IS 'Shared folders for organizing and sharing images with external parties';
COMMENT ON TABLE folder_images IS 'Junction table linking shared folders with images';
COMMENT ON COLUMN guests.password IS 'Hashed password for guest authentication (BCrypt)';
COMMENT ON COLUMN shared_folders.share_code IS 'Unique code for accessing the shared folder publicly';
COMMENT ON COLUMN shared_folders.access_password IS 'Optional password protection for the shared folder';
