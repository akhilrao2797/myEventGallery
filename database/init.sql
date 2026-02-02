-- Event Gallery Database Initialization Script
-- This script creates the initial package data

-- Insert package data
INSERT INTO packages (
    package_type, 
    name, 
    description, 
    max_guests, 
    max_images, 
    storage_days, 
    storage_gb, 
    base_price, 
    price_per_extra_guest, 
    price_per_extra_gb, 
    company_profit_percentage, 
    is_active, 
    created_at, 
    updated_at
) VALUES
(
    'BASIC', 
    'Basic Package', 
    'Perfect for small gatherings and intimate celebrations. Ideal for birthday parties, small anniversaries, and casual events.', 
    50, 
    500, 
    30, 
    5, 
    29.99, 
    0.50, 
    5.00, 
    20.00, 
    true, 
    NOW(), 
    NOW()
),
(
    'STANDARD', 
    'Standard Package', 
    'Ideal for medium-sized events like engagement parties, baby showers, and graduation ceremonies. Most popular choice!', 
    150, 
    2000, 
    90, 
    20, 
    79.99, 
    0.40, 
    4.00, 
    20.00, 
    true, 
    NOW(), 
    NOW()
),
(
    'PREMIUM', 
    'Premium Package', 
    'Great for large celebrations including weddings, receptions, and corporate events. Extended storage and capacity.', 
    300, 
    5000, 
    180, 
    50, 
    149.99, 
    0.30, 
    3.00, 
    20.00, 
    true, 
    NOW(), 
    NOW()
),
(
    'ENTERPRISE', 
    'Enterprise Package', 
    'Unlimited storage for grand events and multi-day celebrations. Perfect for large weddings, conferences, and festivals.', 
    999999, 
    999999, 
    365, 
    200, 
    299.99, 
    0.20, 
    2.00, 
    20.00, 
    true, 
    NOW(), 
    NOW()
)
ON CONFLICT (package_type) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    max_guests = EXCLUDED.max_guests,
    max_images = EXCLUDED.max_images,
    storage_days = EXCLUDED.storage_days,
    storage_gb = EXCLUDED.storage_gb,
    base_price = EXCLUDED.base_price,
    price_per_extra_guest = EXCLUDED.price_per_extra_guest,
    price_per_extra_gb = EXCLUDED.price_per_extra_gb,
    company_profit_percentage = EXCLUDED.company_profit_percentage,
    is_active = EXCLUDED.is_active,
    updated_at = NOW();

-- Verify insertion
SELECT 
    id,
    package_type,
    name,
    base_price,
    max_guests,
    max_images,
    storage_days
FROM packages
ORDER BY base_price;

-- Display summary
SELECT 
    COUNT(*) as total_packages,
    SUM(CASE WHEN is_active = true THEN 1 ELSE 0 END) as active_packages
FROM packages;
