# Event Gallery - Complete Setup Guide

This guide will walk you through setting up the Event Gallery application from scratch.

## Table of Contents
1. [Prerequisites Installation](#prerequisites-installation)
2. [Database Setup](#database-setup)
3. [AWS S3 Setup](#aws-s3-setup)
4. [Backend Configuration](#backend-configuration)
5. [Frontend Configuration](#frontend-configuration)
6. [Running the Application](#running-the-application)
7. [Initial Data Setup](#initial-data-setup)
8. [Troubleshooting](#troubleshooting)

## Prerequisites Installation

### 1. Install Java 17
**macOS:**
```bash
brew install openjdk@17
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Windows:**
Download from [Oracle](https://www.oracle.com/java/technologies/downloads/#java17) or use [Adoptium](https://adoptium.net/)

### 2. Install PostgreSQL
**macOS:**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

**Windows:**
Download from [PostgreSQL](https://www.postgresql.org/download/windows/)

### 3. Install Node.js
**macOS:**
```bash
brew install node
```

**Ubuntu/Debian:**
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
```

**Windows:**
Download from [Node.js](https://nodejs.org/)

### 4. Install Maven (if not using mvnw)
**macOS:**
```bash
brew install maven
```

**Ubuntu/Debian:**
```bash
sudo apt install maven
```

## Database Setup

### 1. Access PostgreSQL
```bash
# macOS/Linux
psql postgres

# Or connect as postgres user
sudo -u postgres psql
```

### 2. Create Database and User
```sql
-- Create database
CREATE DATABASE event_gallery_db;

-- Create user (change password)
CREATE USER event_gallery_user WITH ENCRYPTED PASSWORD 'your_secure_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE event_gallery_db TO event_gallery_user;

-- Connect to database
\c event_gallery_db

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO event_gallery_user;
```

### 3. Verify Connection
```bash
psql -U event_gallery_user -d event_gallery_db -h localhost
```

## AWS S3 Setup

### 1. Create AWS Account
Sign up at [AWS Console](https://aws.amazon.com/)

### 2. Create S3 Bucket
```bash
# Using AWS CLI (install first: brew install awscli)
aws s3 mb s3://event-gallery-images-{your-unique-suffix}

# Or use AWS Console:
# 1. Go to S3 service
# 2. Click "Create bucket"
# 3. Name: event-gallery-images-{your-unique-suffix}
# 4. Region: us-east-1 (or your preferred region)
# 5. Block public access: Keep default (blocked)
# 6. Create bucket
```

### 3. Configure CORS for S3 Bucket
```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
        "AllowedOrigins": ["http://localhost:3000", "http://localhost:8080"],
        "ExposeHeaders": ["ETag"]
    }
]
```

### 4. Create IAM User for S3 Access
1. Go to IAM service
2. Create new user: `event-gallery-s3-user`
3. Attach policy: `AmazonS3FullAccess` (or create custom policy)
4. Create access key
5. Save Access Key ID and Secret Access Key

**Custom S3 Policy (Recommended):**
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::event-gallery-images-*",
                "arn:aws:s3:::event-gallery-images-*/*"
            ]
        }
    ]
}
```

## Backend Configuration

### 1. Update application.properties
```bash
cd myEventGallery/src/main/resources
nano application.properties
```

Update the following:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/event_gallery_db
spring.datasource.username=event_gallery_user
spring.datasource.password=your_secure_password

# AWS S3 Configuration
aws.s3.bucket-name=event-gallery-images-your-unique-suffix
aws.s3.region=us-east-1
aws.access-key-id=YOUR_AWS_ACCESS_KEY_ID
aws.secret-access-key=YOUR_AWS_SECRET_ACCESS_KEY

# JWT Configuration
jwt.secret=YourVeryLongAndSecureSecretKeyForJWTTokenGeneration123456789
jwt.expiration=86400000

# Application Base URL (change for production)
app.base-url=http://localhost:8080
```

### 2. Environment Variables (Alternative to hardcoding)
Create `.env` file in project root:
```bash
export DB_URL="jdbc:postgresql://localhost:5432/event_gallery_db"
export DB_USERNAME="event_gallery_user"
export DB_PASSWORD="your_secure_password"
export AWS_ACCESS_KEY_ID="your_access_key"
export AWS_SECRET_ACCESS_KEY="your_secret_key"
export AWS_S3_BUCKET="event-gallery-images-your-suffix"
export JWT_SECRET="your_jwt_secret"
```

Load variables:
```bash
source .env
```

## Frontend Configuration

### 1. Install Dependencies
```bash
cd frontend
npm install
```

### 2. Create Environment File
Create `frontend/.env`:
```
REACT_APP_API_URL=http://localhost:8080/api
```

For production, update to your actual API URL.

## Running the Application

### 1. Start Backend
```bash
# From project root
cd myEventGallery
./mvnw clean install
./mvnw spring-boot:run

# Or if Maven is installed globally
mvn clean install
mvn spring-boot:run
```

Backend will start on: `http://localhost:8080`

### 2. Start Frontend
```bash
# In a new terminal
cd frontend
npm start
```

Frontend will start on: `http://localhost:3000`

### 3. Verify Application is Running
- Backend health: `http://localhost:8080/actuator/health` (if actuator is added)
- Frontend: `http://localhost:3000`

## Initial Data Setup

### 1. Create Package Data (SQL)
```sql
-- Connect to database
\c event_gallery_db

-- Insert packages
INSERT INTO packages (package_type, name, description, max_guests, max_images, storage_days, storage_gb, base_price, price_per_extra_guest, price_per_extra_gb, company_profit_percentage, is_active, created_at, updated_at)
VALUES
('BASIC', 'Basic Package', 'Perfect for small gatherings', 50, 500, 30, 5, 29.99, 0.50, 5.00, 20.00, true, NOW(), NOW()),
('STANDARD', 'Standard Package', 'Ideal for medium-sized events', 150, 2000, 90, 20, 79.99, 0.40, 4.00, 20.00, true, NOW(), NOW()),
('PREMIUM', 'Premium Package', 'Great for large celebrations', 300, 5000, 180, 50, 149.99, 0.30, 3.00, 20.00, true, NOW(), NOW()),
('ENTERPRISE', 'Enterprise Package', 'Unlimited storage for grand events', 999999, 999999, 365, 200, 299.99, 0.20, 2.00, 20.00, true, NOW(), NOW());
```

### 2. Register First User
1. Go to `http://localhost:3000/register`
2. Fill in registration form
3. You'll be redirected to dashboard

### 3. Create First Event
1. Click "Create Event" button
2. Fill in event details
3. Select a package
4. Submit

## Troubleshooting

### Database Connection Issues
```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql  # Linux
brew services list                # macOS

# Check connection
psql -U event_gallery_user -d event_gallery_db -h localhost

# If permission denied, check pg_hba.conf
sudo nano /etc/postgresql/15/main/pg_hba.conf  # Linux
nano /usr/local/var/postgres/pg_hba.conf       # macOS

# Add this line:
local   all             all                                     md5
```

### Port Already in Use
```bash
# Check what's using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process or change port in application.properties
server.port=8081
```

### AWS S3 Access Denied
1. Verify IAM user has correct permissions
2. Check bucket name is correct
3. Verify region matches configuration
4. Test with AWS CLI:
```bash
aws s3 ls s3://your-bucket-name
```

### Frontend Not Connecting to Backend
1. Check CORS configuration in backend
2. Verify backend is running on port 8080
3. Check `frontend/.env` has correct API URL
4. Open browser console for error details

### JWT Token Issues
1. Verify JWT secret is at least 256 bits
2. Check token expiration time
3. Clear browser localStorage and login again

### Maven Build Fails
```bash
# Clean and rebuild
./mvnw clean
./mvnw install -DskipTests

# If dependencies fail, try
./mvnw dependency:purge-local-repository
./mvnw clean install
```

### React Build Fails
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install

# Update dependencies
npm update
```

## Production Deployment Checklist

- [ ] Change all default passwords
- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS
- [ ] Set up proper CORS origins
- [ ] Configure production database
- [ ] Set up S3 bucket policies
- [ ] Enable CloudFront for S3
- [ ] Set up monitoring and logging
- [ ] Configure backup strategy
- [ ] Set up CI/CD pipeline
- [ ] Load test the application
- [ ] Security audit

## Next Steps

1. Test the complete flow:
   - Register as customer
   - Create an event
   - Generate QR code
   - Register as guest (scan QR or use link)
   - Upload photos
   - View gallery

2. Customize packages and pricing
3. Add your branding to frontend
4. Set up production deployment

## Getting Help

- Check the main README.md for API documentation
- Review Spring Boot logs for backend issues
- Check browser console for frontend errors
- Open an issue on GitHub repository

Good luck with your Event Gallery application!
