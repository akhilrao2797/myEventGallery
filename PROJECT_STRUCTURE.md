# Event Gallery - Project Structure

Complete overview of the project structure and all files.

## Directory Structure

```
myEventGallery/
├── src/main/java/com/example/myeventgallery/
│   ├── config/
│   │   ├── CorsConfig.java              # CORS configuration
│   │   └── S3Config.java                # AWS S3 client configuration
│   │
│   ├── controller/
│   │   ├── AuthController.java          # Authentication endpoints
│   │   ├── EventController.java         # Event management endpoints
│   │   ├── GuestController.java         # Guest registration & upload
│   │   ├── ImageController.java         # Image management endpoints
│   │   └── PackageController.java       # Package listing endpoint
│   │
│   ├── dto/
│   │   ├── ApiResponse.java             # Standard API response wrapper
│   │   ├── AuthResponse.java            # Authentication response
│   │   ├── CustomerLoginRequest.java    # Login request DTO
│   │   ├── CustomerRegisterRequest.java # Registration request DTO
│   │   ├── EventCreateRequest.java      # Event creation request
│   │   ├── EventResponse.java           # Event response DTO
│   │   ├── GuestRegistrationRequest.java# Guest registration DTO
│   │   ├── GuestResponse.java           # Guest response DTO
│   │   ├── ImageResponse.java           # Image response DTO
│   │   └── PackageResponse.java         # Package response DTO
│   │
│   ├── exception/
│   │   └── GlobalExceptionHandler.java  # Global exception handling
│   │
│   ├── model/
│   │   ├── Customer.java                # Customer entity
│   │   ├── Event.java                   # Event entity
│   │   ├── EventType.java               # Event type enum
│   │   ├── Guest.java                   # Guest entity
│   │   ├── Image.java                   # Image entity
│   │   ├── Package.java                 # Package entity
│   │   ├── PackageType.java             # Package type enum
│   │   ├── Payment.java                 # Payment entity
│   │   └── PaymentStatus.java           # Payment status enum
│   │
│   ├── repository/
│   │   ├── CustomerRepository.java      # Customer data access
│   │   ├── EventRepository.java         # Event data access
│   │   ├── GuestRepository.java         # Guest data access
│   │   ├── ImageRepository.java         # Image data access
│   │   ├── PackageRepository.java       # Package data access
│   │   └── PaymentRepository.java       # Payment data access
│   │
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java # JWT token validation filter
│   │   ├── JwtUtil.java                 # JWT utility methods
│   │   └── SecurityConfig.java          # Spring Security configuration
│   │
│   ├── service/
│   │   ├── CustomerService.java         # Customer business logic
│   │   ├── EventService.java            # Event business logic
│   │   ├── GuestService.java            # Guest business logic
│   │   ├── ImageService.java            # Image business logic
│   │   ├── PackageService.java          # Package business logic
│   │   ├── PaymentService.java          # Payment business logic
│   │   ├── QRCodeService.java           # QR code generation
│   │   └── S3Service.java               # AWS S3 operations
│   │
│   └── MyEventGalleryApplication.java   # Main application class
│
├── src/main/resources/
│   └── application.properties           # Application configuration
│
├── frontend/
│   ├── public/
│   │   └── index.html                   # HTML template
│   │
│   ├── src/
│   │   ├── pages/
│   │   │   ├── Login.js                 # Login page
│   │   │   ├── Register.js              # Registration page
│   │   │   ├── Dashboard.js             # Customer dashboard
│   │   │   ├── CreateEvent.js           # Event creation page
│   │   │   ├── EventDetails.js          # Event details & gallery
│   │   │   ├── GuestRegistration.js     # Guest registration page
│   │   │   ├── GuestUpload.js           # Guest upload page
│   │   │   ├── Auth.css                 # Auth pages styles
│   │   │   ├── Dashboard.css            # Dashboard styles
│   │   │   ├── CreateEvent.css          # Create event styles
│   │   │   ├── EventDetails.css         # Event details styles
│   │   │   └── Guest.css                # Guest pages styles
│   │   │
│   │   ├── services/
│   │   │   └── api.js                   # API service layer
│   │   │
│   │   ├── App.js                       # Main React component
│   │   ├── App.css                      # Global styles
│   │   ├── index.js                     # React entry point
│   │   └── index.css                    # Base styles
│   │
│   ├── package.json                     # NPM dependencies
│   ├── Dockerfile                       # Frontend Docker config
│   └── nginx.conf                       # Nginx configuration
│
├── database/
│   └── init.sql                         # Database initialization script
│
├── .env.example                         # Example environment variables
├── .gitignore                           # Git ignore rules
├── docker-compose.yml                   # Docker Compose configuration
├── Dockerfile                           # Backend Docker configuration
├── pom.xml                              # Maven dependencies
├── README.md                            # Main documentation
├── SETUP_GUIDE.md                       # Detailed setup instructions
├── QUICK_START.md                       # Quick start guide
├── API_TESTING.md                       # API testing guide
└── PROJECT_STRUCTURE.md                 # This file
```

## Key Components

### Backend (Spring Boot)

**Controllers (REST APIs)**
- Handle HTTP requests and responses
- Input validation
- Route to appropriate services

**Services (Business Logic)**
- Core application logic
- Transaction management
- Data processing

**Repositories (Data Access)**
- JPA repositories for database operations
- Custom queries
- Data persistence

**Security**
- JWT token generation and validation
- Authentication and authorization
- Password encryption

**Configuration**
- AWS S3 client setup
- CORS policies
- Security rules

### Frontend (React)

**Pages**
- Customer-facing pages (Login, Dashboard, Events)
- Guest-facing pages (Registration, Upload)
- Responsive mobile-first design

**Services**
- Axios-based API client
- Request/response handling
- Token management

**Styling**
- CSS modules for component isolation
- Responsive breakpoints
- Mobile-optimized layouts

## Technology Stack

### Backend Technologies
- **Framework**: Spring Boot 4.0.2
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Hibernate/JPA
- **Security**: Spring Security + JWT
- **Cloud Storage**: AWS S3
- **QR Codes**: ZXing
- **Build Tool**: Maven

### Frontend Technologies
- **Framework**: React 18
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **File Upload**: React Dropzone
- **Icons**: React Icons
- **Build Tool**: Create React App

### DevOps
- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **Web Server**: Nginx (production)

## Database Schema

### Tables
1. **customers** - Event organizers
2. **packages** - Pricing packages
3. **events** - Event details
4. **guests** - Event attendees
5. **images** - Uploaded photos
6. **payments** - Payment records

### Relationships
- Customer → Events (1:N)
- Event → Package (N:1)
- Event → Guests (1:N)
- Event → Images (1:N)
- Event → Payment (1:1)
- Guest → Images (1:N)

## API Endpoints

### Public Endpoints
- `POST /api/auth/register` - Customer registration
- `POST /api/auth/login` - Customer login
- `GET /api/packages/list` - List packages
- `POST /api/guest/register` - Guest registration
- `POST /api/guest/{id}/upload` - Image upload
- `GET /api/events/qr/{code}` - Get QR code

### Protected Endpoints (Require JWT)
- `POST /api/events` - Create event
- `GET /api/events` - List my events
- `GET /api/events/{id}` - Get event details
- `GET /api/images/event/{id}` - Get event images
- `DELETE /api/images/{id}` - Delete image

## Configuration Files

### Backend Configuration
- **application.properties** - Main configuration
  - Database connection
  - AWS credentials
  - JWT settings
  - File upload limits

### Frontend Configuration
- **package.json** - Dependencies and scripts
- **.env** - Environment variables
  - API URL

### Docker Configuration
- **docker-compose.yml** - Multi-container setup
- **Dockerfile** (backend) - Spring Boot image
- **Dockerfile** (frontend) - React + Nginx image
- **nginx.conf** - Nginx web server config

## File Size Summary

### Backend
- **Models**: 9 files (~2.5KB each)
- **Repositories**: 6 files (~1KB each)
- **Services**: 8 files (~3-4KB each)
- **Controllers**: 5 files (~2-3KB each)
- **DTOs**: 10 files (~1-2KB each)
- **Config/Security**: 5 files (~2-3KB each)

### Frontend
- **Pages**: 7 files (~5-8KB each)
- **Styles**: 5 CSS files (~3-5KB each)
- **Services**: 1 file (~2KB)
- **Config**: 2 files (~1KB each)

### Documentation
- **README.md**: ~12KB
- **SETUP_GUIDE.md**: ~8KB
- **QUICK_START.md**: ~4KB
- **API_TESTING.md**: ~6KB
- **PROJECT_STRUCTURE.md**: ~4KB

**Total Lines of Code**: ~5,000+ lines
- Backend: ~3,000 lines
- Frontend: ~2,000 lines
- Configuration: ~500 lines

## Development Workflow

1. **Backend Development**
   - Modify models/entities
   - Update repositories if needed
   - Implement business logic in services
   - Create/update controllers
   - Test with curl/Postman

2. **Frontend Development**
   - Create/modify pages
   - Update API services
   - Style with CSS
   - Test in browser

3. **Testing**
   - Unit tests for services
   - Integration tests for APIs
   - End-to-end testing
   - Mobile responsiveness testing

4. **Deployment**
   - Build backend JAR
   - Build frontend production bundle
   - Deploy to cloud platform
   - Configure domain and SSL

## Features Implemented

✅ User authentication (JWT)
✅ Event management
✅ QR code generation
✅ Guest registration
✅ Multi-file upload
✅ Image gallery
✅ Package management
✅ Payment tracking
✅ Responsive design
✅ Security best practices
✅ Error handling
✅ CORS configuration
✅ Docker support
✅ Comprehensive documentation

## Future Enhancements

- Payment gateway integration
- Email notifications
- Image compression
- Video upload support
- Advanced analytics
- Social media sharing
- Mobile app (React Native)
- Admin dashboard
- Event templates
- Bulk download
- Photo albums/categories

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [AWS S3 Documentation](https://docs.aws.amazon.com/s3/)
- [JWT Documentation](https://jwt.io/)

## License

MIT License - Feel free to use and modify for your needs.
