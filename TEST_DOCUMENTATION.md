# 🧪 Comprehensive Test Documentation

## Overview
This document provides complete test coverage for both frontend and backend components of the Event Gallery application.

---

## 📊 Test Coverage Summary

### Frontend Tests
- **Total Test Files**: 9
- **Test Categories**:
  - App routing & authentication: 1 file
  - Page components: 7 files
  - API services: 1 file

### Backend Tests  
- **Total Test Files**: 16
- **Test Categories**:
  - Unit tests: 12 files
  - Integration tests: 4 files

---

## 🎯 Frontend Test Coverage

### 1. App Component Tests (`__tests__/App.test.js`)
**Purpose**: Test routing, authentication, and protected routes

**Test Cases**:
- ✅ Redirects to login by default
- ✅ Renders login for unauthenticated users
- ✅ Protects customer dashboard route
- ✅ Allows dashboard access when authenticated
- ✅ Protects guest dashboard route
- ✅ Allows guest dashboard when guest authenticated
- ✅ Protects admin dashboard route
- ✅ Allows admin dashboard when admin authenticated

**Expected Behavior**:
- Unauthenticated users redirected to login
- Each user type (customer/guest/admin) has separate authentication
- Protected routes check appropriate token

---

### 2. Login Page Tests (`__tests__/pages/Login.test.js`)
**Purpose**: Test customer login functionality

**Test Cases**:
- ✅ Renders login form with all fields
- ✅ Shows validation errors for empty fields
- ✅ Handles successful login
- ✅ Handles login failure
- ✅ Has link to register page
- ✅ Has links to guest and admin login

**Expected Behavior**:
- Email and password validation
- Successful login stores token and redirects to dashboard
- Failed login shows error message
- Navigation links work correctly

---

### 3. Register Page Tests (`__tests__/pages/Register.test.js`)
**Purpose**: Test customer registration functionality

**Test Cases**:
- ✅ Renders registration form with all fields
- ✅ Handles successful registration
- ✅ Handles registration failure
- ✅ Validates password strength
- ✅ Has link to login page

**Expected Behavior**:
- All required fields validated
- Successful registration redirects to login
- Email uniqueness checked
- Password strength requirements enforced

---

### 4. Dashboard Tests (`__tests__/pages/Dashboard.test.js`)
**Purpose**: Test customer dashboard

**Test Cases**:
- ✅ Renders dashboard with loading state
- ✅ Displays events when loaded successfully
- ✅ Displays empty state when no events
- ✅ Displays error message on API failure
- ✅ Has create new event button
- ✅ Displays welcome message with customer name

**Expected Behavior**:
- Shows loading state during API call
- Displays list of customer's events
- Empty state encourages event creation
- Error handling for API failures

---

### 5. CreateEvent Tests (`__tests__/pages/CreateEvent.test.js`)
**Purpose**: Test event creation functionality

**Test Cases**:
- ✅ Renders create event form
- ✅ Loads and displays packages
- ✅ Handles package selection
- ✅ Handles successful event creation
- ✅ Displays error on event creation failure
- ✅ Validates required fields

**Expected Behavior**:
- Packages loaded from API
- User can select package
- Form validates all required fields
- Successful creation redirects to dashboard
- Error messages displayed on failure

---

### 6. EventDetails Tests (`__tests__/pages/EventDetails.test.js`)
**Purpose**: Test event details and image management

**Test Cases**:
- ✅ Renders event details and loading state
- ✅ Displays event information
- ✅ Displays guest folders with images
- ✅ Allows folder expansion and collapse
- ✅ Handles bulk image selection
- ✅ Handles QR code download
- ✅ Displays error message on load failure
- ✅ Has back button navigation

**Expected Behavior**:
- Event details displayed clearly
- Images grouped by guest names
- Bulk operations available (select, download)
- QR code downloadable
- Folder structure collapsible

---

### 7. GuestLogin Tests (`__tests__/pages/GuestLogin.test.js`)
**Purpose**: Test guest authentication

**Test Cases**:
- ✅ Renders guest login form
- ✅ Handles successful guest login
- ✅ Handles login failure with invalid event code
- ✅ Has link to guest registration

**Expected Behavior**:
- Guest logs in with email + event code
- Successful login stores guest token
- Invalid event code shows error
- Registration link available

---

### 8. AdminDashboard Tests (`__tests__/pages/AdminDashboard.test.js`)
**Purpose**: Test admin dashboard functionality

**Test Cases**:
- ✅ Renders admin dashboard with loading state
- ✅ Displays dashboard statistics
- ✅ Displays recent events
- ✅ Allows tab navigation between sections
- ✅ Allows searching events
- ✅ Handles event deletion
- ✅ Displays error message on API failure

**Expected Behavior**:
- Comprehensive statistics displayed
- Event and customer management
- Search functionality works
- Delete operations require confirmation
- Error handling for unauthorized access

---

### 9. API Service Tests (`__tests__/services/api.test.js`)
**Purpose**: Test API service layer

**Test Cases**:
- ✅ customerLogin sends correct request
- ✅ customerRegister sends correct data
- ✅ getImageUrl returns S3 URL for http URLs
- ✅ getImageUrl constructs local URL for non-http paths
- ✅ getImageUrl returns empty string for null/undefined
- ✅ Token interceptor adds customer token
- ✅ Token interceptor adds guest token
- ✅ Token interceptor adds admin token

**Expected Behavior**:
- Correct API endpoints called
- Image URLs constructed properly (local vs S3)
- Token interceptor adds correct token based on endpoint
- Error handling in place

---

## 🔧 Backend Test Coverage

### Unit Tests

#### 1. AdminServiceTest
- Admin authentication
- Dashboard statistics calculation
- Event management operations
- Customer management operations

#### 2. GuestAuthServiceTest
- Guest registration
- Guest login
- Guest dashboard data retrieval
- Image deletion with time restrictions

#### 3. SharedFolderServiceTest
- Shared folder creation
- Password protection
- Access control
- Image management in folders

#### 4. EventServiceTest
- Event creation with QR code
- Event retrieval
- Event update operations
- QR code generation

#### 5. PackageServiceTest
- Package listing
- Active package filtering
- Package details retrieval

#### 6. GuestServiceTest
- Guest registration validation
- Image upload handling
- Guest-event association

#### 7. CustomerServiceTest
- Customer registration
- Profile management
- Event ownership verification

#### 8. JwtUtilTest
- Token generation
- Token validation
- Token expiration handling
- Claims extraction

#### 9. EventQRValidationTest
- QR code time validation
- Event date checking
- Upload window enforcement

#### 10. EventTest
- Event model validation
- QR validity checking
- Date calculations

---

### Integration Tests

#### 1. EventControllerIntegrationTest
**Endpoints Tested**:
- `POST /api/events` - Create event
  - ✅ Success case
  - ✅ Unauthorized access
  - ✅ Invalid data validation
- `GET /api/events` - Get customer events
  - ✅ Success case
  - ✅ Authentication required
- `GET /api/events/{id}` - Get event by ID
  - ✅ Success case
  - ✅ Event ownership verification
- `GET /api/events/qr/{eventCode}` - Get QR code
  - ✅ Success case
  - ✅ Not found case

**Expected Behavior**:
- Authentication required for all operations
- Events created successfully with valid data
- QR codes generated automatically
- Proper error messages for invalid requests

---

#### 2. GuestControllerIntegrationTest
**Endpoints Tested**:
- `POST /api/guest/register` - Register guest
  - ✅ Success case
  - ✅ Invalid event code
- `POST /api/guest/{id}/upload` - Upload images
  - ✅ Success case
  - ✅ QR code expiration validation
  - ✅ Event date restrictions

**Expected Behavior**:
- Guest registration validates event code
- Image uploads only during valid time window
- QR code expiration enforced (event start to +3 days)
- File upload handled correctly

---

#### 3. PackageControllerIntegrationTest
**Endpoints Tested**:
- `GET /api/packages/list` - Get all packages
  - ✅ Success case
  - ✅ Returns active packages only
  - ✅ Contains all required fields

**Expected Behavior**:
- All active packages returned
- Package details complete (price, storage, guests, etc.)
- No authentication required (public endpoint)

---

#### 4. AdminControllerIntegrationTest
**Endpoints Tested**:
- `POST /api/admin/login` - Admin login
  - ✅ Success case
  - ✅ Invalid credentials
- `GET /api/admin/dashboard/stats` - Dashboard statistics
  - ✅ Success case
  - ✅ Unauthorized access
- `GET /api/admin/events` - Get all events
  - ✅ Success case
  - ✅ Requires admin token
- `GET /api/admin/events/search` - Search events
  - ✅ Success case
  - ✅ Query parameter handling
- `GET /api/admin/customers` - Get all customers
  - ✅ Success case
  - ✅ Admin authorization required

**Expected Behavior**:
- Admin authentication separate from customer
- Dashboard provides comprehensive statistics
- Admin can view all events and customers
- Search functionality works correctly
- All operations require admin token

---

## 🚀 Running Tests

### Frontend Tests

#### Run All Tests
```bash
cd frontend
npm test
```

#### Run Tests in Watch Mode
```bash
npm test -- --watch
```

#### Run Tests with Coverage
```bash
npm test -- --coverage --watchAll=false
```

#### Run Specific Test File
```bash
npm test Login.test.js
```

### Backend Tests

#### Run All Tests
```bash
./mvnw test
```

#### Run Specific Test Class
```bash
./mvnw test -Dtest=EventControllerIntegrationTest
```

#### Run Tests with Coverage
```bash
./mvnw test jacoco:report
```

#### Run Only Unit Tests
```bash
./mvnw test -Dtest="*ServiceTest,*Test"
```

#### Run Only Integration Tests
```bash
./mvnw test -Dtest="*IntegrationTest"
```

---

## 🎯 Test Scenarios by User Flow

### Customer Flow
1. **Registration** → Register.test.js
2. **Login** → Login.test.js
3. **View Dashboard** → Dashboard.test.js
4. **Create Event** → CreateEvent.test.js
5. **View Event Details** → EventDetails.test.js
6. **Download QR Code** → EventDetails.test.js
7. **Manage Images** → EventDetails.test.js

### Guest Flow
1. **Scan QR Code** → (Mobile/Web)
2. **Register** → GuestRegistration (not tested yet - needs creation)
3. **Login** → GuestLogin.test.js
4. **Upload Images** → GuestControllerIntegrationTest
5. **View Dashboard** → (Needs frontend test)
6. **Delete Images** → GuestAuthServiceTest

### Admin Flow
1. **Login** → AdminControllerIntegrationTest
2. **View Dashboard** → AdminDashboard.test.js
3. **Manage Events** → AdminControllerIntegrationTest
4. **Manage Customers** → AdminControllerIntegrationTest
5. **Search/Filter** → AdminDashboard.test.js

---

## 📋 Test Checklist

### ✅ Completed
- [x] Frontend: App routing and authentication
- [x] Frontend: Login page
- [x] Frontend: Register page
- [x] Frontend: Dashboard
- [x] Frontend: Create Event
- [x] Frontend: Event Details
- [x] Frontend: Guest Login
- [x] Frontend: Admin Dashboard
- [x] Frontend: API service
- [x] Backend: Event controller integration
- [x] Backend: Guest controller integration
- [x] Backend: Package controller integration
- [x] Backend: Admin controller integration
- [x] Backend: All service unit tests
- [x] Backend: Model validation tests

### 🔄 Additional Tests Recommended
- [ ] Frontend: Guest Registration page
- [ ] Frontend: Guest Dashboard page
- [ ] Frontend: Guest Upload page
- [ ] Frontend: Shared Folder view
- [ ] Backend: Image controller integration
- [ ] Backend: Shared folder controller integration
- [ ] Backend: File controller integration
- [ ] End-to-end tests with real database
- [ ] Performance tests for image uploads
- [ ] Security tests for authentication

---

## 🐛 Common Test Failures & Solutions

### Frontend

**Issue**: `Cannot find module 'axios'`
**Solution**: Run `npm install` in frontend directory

**Issue**: `localStorage is not defined`
**Solution**: Already mocked in setupTests.js

**Issue**: `window.matchMedia is not a function`
**Solution**: Already mocked in setupTests.js

### Backend

**Issue**: `ApplicationContext failed to load`
**Solution**: Ensure database is configured or use `@TestConfiguration`

**Issue**: `JwtUtil cannot be autowired`
**Solution**: Use `@MockBean` or ensure proper Spring context

**Issue**: `Multipart upload fails`
**Solution**: Use `MockMultipartFile` with correct content type

---

## 📊 Expected Test Results

### Frontend
- **Total Tests**: ~50+ test cases
- **Expected Pass Rate**: 100% (after running `npm install`)
- **Coverage Goal**: >80%

### Backend
- **Total Tests**: ~60+ test cases
- **Expected Pass Rate**: 100%
- **Coverage Goal**: >75%

---

## 🔍 Debugging Tests

### Frontend Debugging
```bash
# Run with verbose output
npm test -- --verbose

# Debug specific test
node --inspect-brk node_modules/.bin/jest --runInBand Login.test.js
```

### Backend Debugging
```bash
# Run with debug output
./mvnw test -X

# Run specific test with debug port
./mvnw test -Dtest=EventServiceTest -Dmaven.surefire.debug
```

---

## 📈 Continuous Integration

### Recommended CI Pipeline
1. Install dependencies
2. Run linter
3. Run unit tests
4. Run integration tests
5. Generate coverage report
6. Fail build if coverage < threshold

### GitHub Actions Example
```yaml
- name: Run Frontend Tests
  run: |
    cd frontend
    npm install
    npm test -- --coverage --watchAll=false

- name: Run Backend Tests
  run: |
    ./mvnw clean test
    ./mvnw jacoco:report
```

---

## ✅ Test Quality Standards

### All Tests Must:
- ✅ Have clear, descriptive names
- ✅ Test one specific behavior
- ✅ Be independent (no shared state)
- ✅ Be fast (<1s per test ideally)
- ✅ Be deterministic (same input = same output)
- ✅ Clean up after themselves
- ✅ Have assertions that verify behavior

### Test Documentation Must Include:
- Purpose of test
- Expected behavior
- Prerequisites/setup
- Cleanup requirements

---

## 🎓 Next Steps

1. **Install Frontend Dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Run All Tests**:
   ```bash
   # Frontend
   npm test -- --coverage --watchAll=false
   
   # Backend
   cd ..
   ./mvnw test
   ```

3. **Review Coverage Reports**:
   - Frontend: `frontend/coverage/lcov-report/index.html`
   - Backend: `target/site/jacoco/index.html`

4. **Fix Any Failing Tests**

5. **Add Missing Tests** (see checklist above)

---

## 📞 Support

If tests fail:
1. Check error messages carefully
2. Verify all dependencies installed
3. Ensure database is running (for integration tests)
4. Check environment configuration
5. Review test documentation above

**Test coverage is critical for maintaining code quality and preventing regressions!** 🎯
