# Quick Start Guide - Event Gallery

Get up and running in 5 minutes!

## Prerequisites
- Java 17+
- Node.js 16+
- PostgreSQL installed and running
- AWS account with S3 access (optional for initial testing)

## 1. Database Setup (2 minutes)

```bash
# Create database
psql postgres
CREATE DATABASE event_gallery_db;
CREATE USER event_gallery_user WITH ENCRYPTED PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE event_gallery_db TO event_gallery_user;
\q

# Initialize package data (after first run)
psql -U event_gallery_user -d event_gallery_db -f database/init.sql
```

**OR use Docker:**
```bash
docker-compose up postgres -d
# Wait 10 seconds for database to initialize
sleep 10
```

## 2. Backend Setup (1 minute)

```bash
# Update application.properties with your settings
# Minimum required:
# - Database credentials
# - AWS S3 credentials (or use default credentials chain)
# - JWT secret

# Run backend
./mvnw spring-boot:run
```

Backend will start on http://localhost:8080

## 3. Frontend Setup (1 minute)

```bash
cd frontend
npm install
npm start
```

Frontend will start on http://localhost:3000

## 4. First Use (1 minute)

1. **Register**: Go to http://localhost:3000/register
   - Enter your details
   - You'll be logged in automatically

2. **Create Event**:
   - Click "Create Event"
   - Fill in event details
   - Select a package
   - Submit

3. **Get QR Code**:
   - View your event
   - Download QR code
   - Share with guests

4. **Test Guest Upload**:
   - Open QR code link in new browser/incognito
   - Register as guest
   - Upload test images

## Quick Commands

```bash
# Start database only
docker-compose up postgres -d

# Start backend
./mvnw spring-boot:run

# Start frontend
cd frontend && npm start

# Initialize packages
psql -U event_gallery_user -d event_gallery_db -f database/init.sql

# Build for production
./mvnw clean package
cd frontend && npm run build
```

## Testing Without AWS S3

If you don't have AWS S3 configured yet:

1. The application will try to use default AWS credentials
2. Comment out S3-related code temporarily, or
3. Use LocalStack for S3 emulation:

```bash
docker run -d -p 4566:4566 localstack/localstack -e SERVICES=s3
# Update application.properties:
# aws.s3.endpoint=http://localhost:4566
```

## Default Packages

After running `database/init.sql`, you'll have:
- **Basic**: $29.99 - 50 guests, 500 images, 30 days
- **Standard**: $79.99 - 150 guests, 2000 images, 90 days
- **Premium**: $149.99 - 300 guests, 5000 images, 180 days
- **Enterprise**: $299.99 - Unlimited, 365 days

## Troubleshooting

**Port 8080 already in use:**
```bash
# Change port in application.properties
server.port=8081
```

**Database connection failed:**
```bash
# Check PostgreSQL is running
brew services list  # macOS
sudo systemctl status postgresql  # Linux
```

**Frontend can't connect to backend:**
- Check backend is running on port 8080
- Verify CORS settings in backend
- Check frontend/.env has correct API URL

## Next Steps

1. Configure AWS S3 properly
2. Update JWT secret for production
3. Customize packages in database
4. Add your branding
5. Deploy to production

See `README.md` for detailed documentation and `SETUP_GUIDE.md` for complete setup instructions.

## Support

Having issues? Check:
1. Backend logs in terminal
2. Browser console for frontend errors
3. Database connection
4. All environment variables are set

Happy event photo sharing! 📸
