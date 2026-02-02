# Event Gallery - Photo Sharing Platform

A comprehensive web application for event organizers to collect photos from guests via QR codes. Guests can easily upload photos by scanning a QR code, and all images are stored securely in AWS S3.

## Features

### For Event Organizers (Customers)
- **User Authentication**: Secure registration and login system
- **Event Management**: Create and manage multiple events
- **Unique QR Codes**: Each event gets a unique QR code for easy guest access
- **Package Selection**: Choose from different pricing packages based on needs
- **Photo Gallery**: View all uploaded photos in a beautiful gallery
- **Analytics**: Track guest count, photo uploads, and storage usage

### For Guests
- **Easy Registration**: Quick registration with minimal details
- **Multi-Photo Upload**: Upload multiple photos at once via drag-and-drop
- **Mobile Optimized**: Fully responsive design for mobile devices
- **Real-time Progress**: See upload progress in real-time

### Additional Features
- **Scalable Architecture**: Built for high performance and scalability
- **Secure Storage**: All images stored in AWS S3 with presigned URLs
- **Payment Tracking**: Built-in payment management system
- **Package-based Pricing**: Flexible pricing with profit margins
- **Event Expiration**: Automatic event expiration based on package

## Technology Stack

### Backend
- **Spring Boot 4.0.2**: Latest Spring framework
- **PostgreSQL**: Robust relational database
- **AWS S3**: Cloud storage for images
- **JWT Authentication**: Secure token-based authentication
- **Spring Security**: Comprehensive security framework
- **QR Code Generation**: Using ZXing library

### Frontend
- **React 18**: Modern React with hooks
- **React Router**: Client-side routing
- **Axios**: HTTP client
- **React Dropzone**: File upload with drag-and-drop
- **React Icons**: Beautiful icon library
- **Responsive CSS**: Mobile-first design

## Project Structure

```
myEventGallery/
├── src/main/java/com/example/myeventgallery/
│   ├── config/              # Configuration classes (S3, CORS, Security)
│   ├── controller/          # REST API controllers
│   ├── dto/                 # Data Transfer Objects
│   ├── exception/           # Exception handlers
│   ├── model/               # JPA entities
│   ├── repository/          # JPA repositories
│   ├── security/            # JWT and security components
│   └── service/             # Business logic services
├── src/main/resources/
│   └── application.properties
└── frontend/
    ├── public/
    ├── src/
    │   ├── pages/          # React page components
    │   ├── services/       # API service layer
    │   ├── App.js
    │   └── index.js
    └── package.json
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL 12 or higher
- Maven 3.6+
- AWS Account (for S3)

### Backend Setup

1. **Create PostgreSQL Database**
```sql
CREATE DATABASE event_gallery_db;
```

2. **Configure Application Properties**

Edit `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/event_gallery_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# AWS S3
aws.s3.bucket-name=your-bucket-name
aws.s3.region=us-east-1
aws.access-key-id=your-access-key
aws.secret-access-key=your-secret-key

# JWT
jwt.secret=your-secret-key-here
```

3. **Run Backend**
```bash
cd myEventGallery
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Install Dependencies**
```bash
cd frontend
npm install
```

2. **Start Development Server**
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## API Documentation

### Authentication Endpoints

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "+1234567890"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

### Event Endpoints

#### Create Event
```http
POST /api/events
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Sarah's Wedding",
  "eventType": "MARRIAGE",
  "description": "Wedding celebration",
  "eventDate": "2024-12-31",
  "venue": "Grand Hotel",
  "expectedGuests": 150,
  "packageType": "PREMIUM"
}
```

#### Get My Events
```http
GET /api/events
Authorization: Bearer {token}
```

#### Get Event Details
```http
GET /api/events/{eventId}
Authorization: Bearer {token}
```

#### Get Event QR Code
```http
GET /api/events/qr/{eventCode}
```

### Guest Endpoints

#### Register Guest
```http
POST /api/guest/register
Content-Type: application/json

{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "phoneNumber": "+1234567890",
  "eventCode": "event-uuid"
}
```

#### Upload Images
```http
POST /api/guest/{guestId}/upload
Content-Type: multipart/form-data

files: [image1.jpg, image2.jpg, ...]
```

### Image Endpoints

#### Get Event Images
```http
GET /api/images/event/{eventId}
Authorization: Bearer {token}
```

#### Delete Image
```http
DELETE /api/images/{imageId}
Authorization: Bearer {token}
```

### Package Endpoints

#### Get All Packages
```http
GET /api/packages/list
```

## Database Schema

### Key Tables
- **customers**: Event organizers
- **events**: Event details with QR codes
- **packages**: Pricing packages
- **guests**: Event guests who upload photos
- **images**: Uploaded photo metadata
- **payments**: Payment tracking

## Packages & Pricing

### BASIC
- Up to 50 guests
- Up to 500 images
- 30 days storage
- 5 GB storage
- $29.99 base price

### STANDARD
- Up to 150 guests
- Up to 2000 images
- 90 days storage
- 20 GB storage
- $79.99 base price

### PREMIUM
- Up to 300 guests
- Up to 5000 images
- 180 days storage
- 50 GB storage
- $149.99 base price

### ENTERPRISE
- Unlimited guests
- Unlimited images
- 365 days storage
- 200 GB storage
- $299.99 base price

## Security Features

- **JWT Authentication**: Secure token-based auth
- **Password Encryption**: BCrypt hashing
- **CORS Configuration**: Controlled cross-origin requests
- **Presigned URLs**: Temporary S3 access URLs
- **Input Validation**: Comprehensive validation on all inputs

## Deployment

### Backend (Spring Boot)

**Using JAR:**
```bash
./mvnw clean package
java -jar target/myEventGallery-0.0.1-SNAPSHOT.jar
```

**Using Docker:**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/myEventGallery-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Frontend (React)

**Build for Production:**
```bash
cd frontend
npm run build
```

Deploy the `build` folder to any static hosting service (Netlify, Vercel, AWS S3 + CloudFront, etc.)

## Environment Variables

### Backend
- `AWS_ACCESS_KEY_ID`: AWS access key
- `AWS_SECRET_ACCESS_KEY`: AWS secret key
- `JWT_SECRET`: JWT signing secret

### Frontend
- `REACT_APP_API_URL`: Backend API URL

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Future Enhancements

- [ ] Payment gateway integration (Stripe/PayPal)
- [ ] Email notifications for event organizers
- [ ] Image compression and optimization
- [ ] Thumbnail generation
- [ ] Event templates
- [ ] Social media sharing
- [ ] Advanced analytics dashboard
- [ ] Multi-language support
- [ ] Mobile app (React Native)
- [ ] Video upload support
- [ ] Facial recognition for photo tagging

## License

This project is licensed under the MIT License.

## Support

For support, email support@eventgallery.com or open an issue in the repository.

## Acknowledgments

- Spring Boot team for the excellent framework
- React team for the UI library
- AWS for reliable cloud storage
- ZXing for QR code generation
