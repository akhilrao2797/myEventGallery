# API Testing Guide

Test all endpoints using curl, Postman, or any REST client.

## Base URL
```
http://localhost:8080/api
```

## 1. Authentication

### Register Customer
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "phoneNumber": "+1234567890"
  }'
```

Response:
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "customerId": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }
}
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Save the token from response for subsequent requests!**

## 2. Packages

### Get All Packages
```bash
curl -X GET http://localhost:8080/api/packages/list
```

## 3. Events

### Create Event
```bash
TOKEN="your_token_here"

curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Sarah and Johns Wedding",
    "eventType": "MARRIAGE",
    "description": "Our special day",
    "eventDate": "2024-12-31",
    "venue": "Grand Ballroom",
    "expectedGuests": 150,
    "packageType": "PREMIUM"
  }'
```

### Get My Events
```bash
curl -X GET http://localhost:8080/api/events \
  -H "Authorization: Bearer $TOKEN"
```

### Get Event Details
```bash
EVENT_ID=1

curl -X GET http://localhost:8080/api/events/$EVENT_ID \
  -H "Authorization: Bearer $TOKEN"
```

### Get Event QR Code
```bash
EVENT_CODE="your-event-code-here"

curl -X GET http://localhost:8080/api/events/qr/$EVENT_CODE \
  -o qr-code.png
```

## 4. Guest Operations

### Register as Guest
```bash
curl -X POST http://localhost:8080/api/guest/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane@example.com",
    "phoneNumber": "+1234567890",
    "eventCode": "your-event-code-here"
  }'
```

Response includes `guestId` - save it for upload!

### Upload Images
```bash
GUEST_ID=1

curl -X POST http://localhost:8080/api/guest/$GUEST_ID/upload \
  -F "files=@/path/to/image1.jpg" \
  -F "files=@/path/to/image2.jpg" \
  -F "files=@/path/to/image3.jpg"
```

## 5. Images

### Get Event Images
```bash
curl -X GET http://localhost:8080/api/images/event/$EVENT_ID \
  -H "Authorization: Bearer $TOKEN"
```

### Get Paginated Images
```bash
curl -X GET "http://localhost:8080/api/images/event/$EVENT_ID/paginated?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### Delete Image
```bash
IMAGE_ID=1

curl -X DELETE http://localhost:8080/api/images/$IMAGE_ID \
  -H "Authorization: Bearer $TOKEN"
```

## Complete Test Flow

```bash
# 1. Register
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }')

# Extract token
TOKEN=$(echo $REGISTER_RESPONSE | jq -r '.data.token')
echo "Token: $TOKEN"

# 2. Get packages
curl -s http://localhost:8080/api/packages/list | jq

# 3. Create event
EVENT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Test Event",
    "eventType": "BIRTHDAY",
    "eventDate": "2024-12-31",
    "expectedGuests": 50,
    "packageType": "BASIC"
  }')

# Extract event code
EVENT_CODE=$(echo $EVENT_RESPONSE | jq -r '.data.eventCode')
EVENT_ID=$(echo $EVENT_RESPONSE | jq -r '.data.id')
echo "Event Code: $EVENT_CODE"
echo "Event ID: $EVENT_ID"

# 4. Get QR code
curl -s http://localhost:8080/api/events/qr/$EVENT_CODE -o test-qr.png
echo "QR code saved to test-qr.png"

# 5. Register as guest
GUEST_RESPONSE=$(curl -s -X POST http://localhost:8080/api/guest/register \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Guest User\",
    \"email\": \"guest@example.com\",
    \"eventCode\": \"$EVENT_CODE\"
  }")

GUEST_ID=$(echo $GUEST_RESPONSE | jq -r '.data.id')
echo "Guest ID: $GUEST_ID"

# 6. Upload images (create a test image first)
# Create a simple test image
convert -size 100x100 xc:blue test-image.jpg

curl -X POST http://localhost:8080/api/guest/$GUEST_ID/upload \
  -F "files=@test-image.jpg"

# 7. View uploaded images
curl -s http://localhost:8080/api/images/event/$EVENT_ID \
  -H "Authorization: Bearer $TOKEN" | jq

echo "Test flow complete!"
```

## Postman Collection

Import this JSON into Postman:

```json
{
  "info": {
    "name": "Event Gallery API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"John Doe\",\n  \"email\": \"john@example.com\",\n  \"password\": \"password123\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{baseUrl}}/auth/register",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "register"]
            }
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"john@example.com\",\n  \"password\": \"password123\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{baseUrl}}/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "login"]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api"
    }
  ]
}
```

## Testing Tips

1. **Save your token**: After login, save the token to use in subsequent requests
2. **Save event code**: You'll need it for guest operations
3. **Test with real images**: Use actual image files for upload testing
4. **Check response codes**: 
   - 200: Success
   - 400: Bad request (validation error)
   - 401: Unauthorized (invalid/missing token)
   - 404: Not found
   - 500: Server error

## Common Issues

**401 Unauthorized:**
- Token expired (login again)
- Token not included in header
- Invalid token format

**400 Bad Request:**
- Missing required fields
- Invalid data format
- Validation errors

**403 Forbidden:**
- Trying to access another user's resources

**500 Internal Server Error:**
- Database connection issues
- S3 access problems
- Check backend logs

## Automated Testing Script

Save as `test-api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/api"

echo "Testing Event Gallery API..."

# Test health
echo "\n1. Testing server..."
curl -s $BASE_URL/../actuator/health || echo "Server not responding"

# Test registration
echo "\n2. Testing registration..."
REGISTER=$(curl -s -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Auto Test User",
    "email": "autotest@example.com",
    "password": "test123"
  }')

if echo $REGISTER | jq -e '.success' > /dev/null; then
  echo "✓ Registration successful"
else
  echo "✗ Registration failed"
fi

# Test login
echo "\n3. Testing login..."
LOGIN=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "autotest@example.com",
    "password": "test123"
  }')

if echo $LOGIN | jq -e '.success' > /dev/null; then
  echo "✓ Login successful"
  TOKEN=$(echo $LOGIN | jq -r '.data.token')
else
  echo "✗ Login failed"
  exit 1
fi

# Test packages
echo "\n4. Testing packages endpoint..."
PACKAGES=$(curl -s $BASE_URL/packages/list)
if echo $PACKAGES | jq -e '.success' > /dev/null; then
  echo "✓ Packages retrieved"
else
  echo "✗ Failed to get packages"
fi

echo "\n✓ All basic tests passed!"
```

Make it executable:
```bash
chmod +x test-api.sh
./test-api.sh
```
