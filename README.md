# Event Photo Gallery

Event photo sharing app with **Customer** (organizer), **Guest** (photo uploader), and **Admin** (placeholder) roles.

## Tech Stack

- **Backend:** Spring Boot 3.2.2, Java 17, PostgreSQL, JWT, BCrypt, S3/Local storage, ZXing QR, Razorpay, JImageHash (pHash), Apache PDFBox
- **Frontend:** React 18, React Router v6, Axios, React Dropzone, React Icons, Create React App

## Prerequisites

- Java 17, Maven
- PostgreSQL (create DB: `eventphoto`)
- Node.js 18+ (for frontend)

## Backend

```bash
# Create DB (PostgreSQL)
createdb eventphoto

# Configure (optional) in src/main/resources/application.properties
# - spring.datasource.url, username, password
# - jwt.secret, app.frontend-base-url, app.api-base-url
# - razorpay.key-id, razorpay.key-secret (optional)

mvn spring-boot:run
```

Runs at `http://localhost:8080`. Non-prod profile uses local file storage under `./uploads`.

## Frontend

```bash
cd frontend
npm install
npm start
```

Runs at `http://localhost:3000`. Proxy is set to `http://localhost:8080` for API calls.

## Main Flows

1. **Customer:** Register → Login → Dashboard → Create event (guided steps) → Event detail: view photos by guest, bulk delete/download ZIP, create share link (optional password/expiry), generate album PDF.
2. **Guest:** Enter event code → Register or Login → Upload photos (drag & drop). Dashboard shows events they uploaded to; modify/delete only within configured days after event.
3. **Share link:** Public page at `/shared/:shareCode`; optional password and expiry.

## Configuration

- **Duplicate detection:** Perceptual hash (JImageHash); duplicates per guest/event are skipped.
- **Content moderation:** Placeholder (allow all); replace `ContentModerationServiceImpl` for real checks.
- **Guest modify window:** `app_properties.guest.modify.days.after.event` (default 1).
- **Upload batch size:** `app_properties.guest.upload.max.images.per.batch` (default 20).

See `REQUIREMENTS_UNDERSTANDING.txt` for full requirements and clarifications.
