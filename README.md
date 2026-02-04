# 🎉 Event Gallery Platform - Complete Guide

## 📖 Table of Contents
1. [Quick Start](#quick-start)
2. [Features](#features)
3. [Setup & Installation](#setup--installation)
4. [Running the Application](#running-the-application)
5. [User Guides](#user-guides)
6. [Technical Stack](#technical-stack)
7. [Project Structure](#project-structure)

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL 14 or higher
- Maven

### Start in 3 Steps
```bash
# 1. Start Backend
./mvnw spring-boot:run

# 2. Start Frontend
cd frontend
npm install
npm start

# 3. Access Application
open http://localhost:3000
```

---

## ✨ Features

### For Event Organizers (Customers)
- ✅ **Create Events** - Set up events with unique QR codes
- ✅ **Guest Management** - View photos organized by guest folders
- ✅ **Bulk Operations** - Select and download multiple photos as ZIP
- ✅ **Shared Folders** - Create password-protected shared albums
- ✅ **QR Code Generation** - Time-bound QR codes for photo uploads
- ✅ **Dashboard** - Modern interface with event statistics

### For Guests
- ✅ **QR Scan Upload** - Scan QR code to upload event photos
- ✅ **Personal Dashboard** - View all uploaded photos across events
- ✅ **Time-Limited Delete** - Delete photos during event + 1 day
- ✅ **Multiple Events** - Manage photos from different events

### For Administrators
- ✅ **Admin Dashboard** - Comprehensive management interface
- ✅ **Event Management** - View, search, and manage all events
- ✅ **Customer Management** - Monitor customer accounts
- ✅ **Analytics** - System-wide statistics and insights
- ✅ **Content Moderation** - Delete events or customer accounts

---

## 🛠 Setup & Installation

### 1. Database Setup
```bash
# Create database
psql -U postgres
CREATE DATABASE event_gallery_db;

# Run migrations
psql -d event_gallery_db -f database/init.sql
psql -d event_gallery_db -f database/migration_new_features.sql
psql -d event_gallery_db -f database/migration_admin_qr_validation.sql
```

### 2. Backend Configuration

**application.properties (Development)**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/event_gallery_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.profiles.active=local
storage.location=./uploads
```

**application-prod.properties (Production)**
```properties
spring.profiles.active=prod
aws.s3.bucket-name=your-bucket-name
aws.s3.region=us-east-1
```

### 3. Environment Variables
```bash
# For S3 (Production)
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key

# For JWT
export JWT_SECRET=your_jwt_secret_key
```

---

## 🏃 Running the Application

### Development Mode

**Backend:**
```bash
cd /path/to/myEventGallery
./mvnw spring-boot:run
```
- Backend runs on: http://localhost:8080
- API docs: http://localhost:8080/swagger-ui.html

**Frontend:**
```bash
cd frontend
npm install
npm start
```
- Frontend runs on: http://localhost:3000

### Production Build

**Backend:**
```bash
./mvnw clean package
java -jar target/myeventgallery-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd frontend
npm run build
# Deploy build/ folder to hosting service
```

---

## 👥 User Guides

### Customer Guide

#### 1. Register & Login
- Navigate to http://localhost:3000/register
- Create account with email and password
- Auto-redirect to dashboard

#### 2. Create Event
- Click "New Event" in dashboard
- Fill in event details (name, date, venue, etc.)
- Select package (Basic, Standard, Premium)
- Get unique QR code

#### 3. Manage Photos
- View event details → See guest folders
- Each folder shows guest name + photo count
- Click "Select Images" to enable selection mode
- Select photos and:
  - Download as ZIP with folder structure
  - Create shared folder with password

#### 4. Share Collections
- Select photos across multiple guests
- Click "Create Shared Folder"
- Set name and optional password
- Share generated link

### Guest Guide

#### 1. Upload Photos
- Scan event QR code
- Register with name, email, password
- Upload photos during event time + 3 days

#### 2. Manage Uploads
- Login at http://localhost:3000/guest/login
- View all events attended
- See uploaded photos per event
- Delete photos during event + 1 day only

### Admin Guide

#### 1. Access Dashboard
- Login at http://localhost:3000/admin/login
- Default: username: `admin`, password: `admin123`

#### 2. Monitor System
- View stats: customers, events, photos, storage
- See event distribution by type
- Monitor recent activity

#### 3. Manage Content
- Search events by name/code
- View event details
- Delete events or customers
- Track QR code validity

---

## 💻 Technical Stack

### Backend
- **Framework:** Spring Boot 3.2.2
- **Language:** Java 17
- **Database:** PostgreSQL 14
- **Security:** Spring Security + JWT
- **Storage:** AWS S3 (prod) / Local (dev)
- **QR Code:** ZXing
- **Testing:** JUnit 5, Mockito

### Frontend
- **Framework:** React 18
- **Routing:** React Router v6
- **Styling:** Custom CSS (Modern/GuestPix-inspired)
- **Icons:** React Icons
- **HTTP:** Axios
- **Build:** Create React App

### Architecture
- **Pattern:** RESTful API
- **Auth:** JWT-based authentication
- **Storage:** Environment-agnostic (Local/S3)
- **Design:** Responsive, mobile-first

---

## 📂 Project Structure

```
myEventGallery/
├── src/main/java/com/example/myeventgallery/
│   ├── config/          # Security, CORS, S3 configuration
│   ├── controller/      # REST API endpoints
│   ├── dto/             # Data Transfer Objects
│   ├── model/           # JPA Entities
│   ├── repository/      # Database access
│   ├── service/         # Business logic
│   └── util/            # Utilities (JWT, QR generation)
├── src/main/resources/
│   ├── application.properties
│   └── application-prod.properties
├── frontend/
│   ├── src/
│   │   ├── pages/       # React components
│   │   ├── services/    # API calls
│   │   └── App.js       # Main router
│   └── package.json
├── database/            # SQL migrations
└── README.md           # This file
```

---

## 🎨 UI Features

### Modern Design (Inspired by GuestPix/PhotoMall)
- ✅ **Gradient Backgrounds** - Purple (#667eea → #764ba2)
- ✅ **Glassmorphism** - Translucent cards with blur
- ✅ **Smooth Animations** - 300ms transitions
- ✅ **Icon-Based UI** - Clear visual hierarchy
- ✅ **Badge System** - Status indicators
- ✅ **Responsive** - Mobile, tablet, desktop

### Color Scheme
```css
Primary: #667eea → #764ba2  (Purple gradient)
Success: #10b981              (Green)
Danger: #dc2626               (Red)
Info: #4facfe → #00f2fe       (Blue gradient)
Warning: #f59e0b              (Orange)
```

---

## 🔐 Security

### Authentication
- JWT tokens with 24-hour expiration
- BCrypt password hashing
- Role-based access (Customer, Guest, Admin)
- Protected routes

### Authorization
- Customers: Own events only
- Guests: Own uploads only (time-limited delete)
- Admins: Full system access

### QR Code Validation
- Time-bound upload windows
- Valid: Event start → Event end + 3 days
- Automatic expiration

---

## 📊 API Endpoints

### Customer APIs
```
POST   /api/auth/register          - Register customer
POST   /api/auth/login             - Login customer
GET    /api/events                 - Get customer events
POST   /api/events                 - Create event
GET    /api/events/{id}            - Get event details
GET    /api/events/qr/{code}       - Get QR code image
GET    /api/images/event/{id}/grouped  - Get images by guest
POST   /api/images/download-zip    - Download selected images
POST   /api/shared-folders         - Create shared folder
```

### Guest APIs
```
POST   /api/guest/login            - Guest login
POST   /api/guest/register         - Guest registration
GET    /api/guest/dashboard        - Guest dashboard data
POST   /api/guest/{id}/upload      - Upload images
DELETE /api/guest/image/{id}       - Delete image (time-limited)
```

### Admin APIs
```
POST   /api/admin/login            - Admin login
GET    /api/admin/dashboard/stats  - Dashboard statistics
GET    /api/admin/events           - All events (paginated)
GET    /api/admin/events/search    - Search events
DELETE /api/admin/events/{id}      - Delete event
GET    /api/admin/customers        - All customers
DELETE /api/admin/customers/{id}   - Delete customer
```

---

## 🧪 Testing

### Run Tests
```bash
# All tests
./mvnw test

# Specific test
./mvnw test -Dtest=EventServiceTest

# With coverage
./mvnw test jacoco:report
```

### Test Coverage
- 67 tests passing
- Unit tests for services
- Integration tests for controllers
- Mock external dependencies (S3, etc.)

---

## 🔧 Troubleshooting

### Common Issues

**Backend won't start:**
```bash
# Check Java version
java -version  # Should be 17+

# Check PostgreSQL running
psql -U postgres -l

# Check port 8080 available
lsof -i :8080
```

**Frontend won't start:**
```bash
# Clear node_modules
rm -rf node_modules package-lock.json
npm install

# Check port 3000 available
lsof -i :3000
```

**Images not loading:**
- Verify `uploads/` directory exists
- Check `storage.location` in properties
- Ensure file permissions correct
- For S3: Verify AWS credentials

**Database connection failed:**
- Check PostgreSQL running: `pg_isready`
- Verify credentials in application.properties
- Ensure database exists: `psql -l`

---

## 📝 Default Credentials

### Admin
```
Username: admin
Password: admin123
URL: http://localhost:3000/admin/login
```

### Test Customer (if seeded)
```
Email: customer@test.com
Password: password123
URL: http://localhost:3000/login
```

### Test Guest (after registration)
```
Email: guest@test.com
Password: password123
Event Code: (from QR scan)
URL: http://localhost:3000/guest/login
```

---

## 🚀 Deployment

### Backend (Heroku/AWS)
```bash
# Build
./mvnw clean package

# Deploy JAR
java -jar target/myeventgallery-0.0.1-SNAPSHOT.jar

# Environment variables needed:
# - DATABASE_URL
# - JWT_SECRET
# - AWS_ACCESS_KEY_ID (if using S3)
# - AWS_SECRET_ACCESS_KEY (if using S3)
# - SPRING_PROFILES_ACTIVE=prod
```

### Frontend (Netlify/Vercel)
```bash
# Build
cd frontend
npm run build

# Deploy build/ folder
# Set environment variable: REACT_APP_API_URL=https://your-api.com
```

---

## 📧 Support

For issues or questions:
- Check troubleshooting section above
- Review API documentation
- Check backend logs: `logs/spring.log`
- Check browser console for frontend errors

---

## 📄 License

This project is proprietary software. All rights reserved.

---

## 🎉 Quick Reference

### Ports
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:3000`
- PostgreSQL: `localhost:5432`

### Key Directories
- Backend code: `src/main/java/`
- Frontend code: `frontend/src/`
- Database migrations: `database/`
- Uploads (local): `./uploads/`

### Key Files
- Backend config: `src/main/resources/application.properties`
- Frontend routes: `frontend/src/App.js`
- API service: `frontend/src/services/api.js`

---

**Built with ❤️ using Spring Boot & React**
