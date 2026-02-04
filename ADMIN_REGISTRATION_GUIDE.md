# 🔐 Admin Registration Guide

## Overview
Admin registration functionality has been added to allow authorized personnel to create new admin accounts with proper security measures.

---

## 🎯 Features Added

### Backend
1. **New API Endpoint**: `POST /api/admin/register`
2. **Admin Registration DTO**: `AdminRegistrationRequest.java`
3. **Registration Service Method**: `AdminService.registerAdmin()`
4. **Security**: Super admin key validation required

### Frontend
1. **Admin Registration Page**: `/admin/register`
2. **Form Fields**: Username, Full Name, Email, Password, Role, Super Admin Key
3. **Role Selection**: ADMIN, SUPER_ADMIN, SUPPORT
4. **Navigation**: Links between login and registration pages

---

## 🔑 How to Register a New Admin

### Step 1: Access Registration Page
Navigate to: `http://localhost:3000/admin/register`

Or click "Register here" link on the Admin Login page.

### Step 2: Fill in the Form

**Required Fields:**
- **Username**: Unique username for the admin
- **Full Name**: Admin's full name
- **Email**: Valid email address
- **Password**: Minimum 6 characters
- **Role**: Select from:
  - `ADMIN` - Regular admin access
  - `SUPER_ADMIN` - Full administrative access
  - `SUPPORT` - Support staff access
- **Super Admin Key**: Security key (see below)

### Step 3: Enter Super Admin Key

**Default Super Admin Key**: `SUPER_ADMIN_SECRET_KEY_2026`

⚠️ **IMPORTANT**: This key is required for security. Only authorized personnel should have access to this key.

### Step 4: Submit
Click "Register Admin" button to create the account.

---

## 🔒 Security Features

### 1. Super Admin Key Validation
- Every admin registration requires a valid super admin key
- Prevents unauthorized admin account creation
- Key should be kept confidential and changed periodically

### 2. Duplicate Prevention
- Username must be unique
- Email must be unique
- System checks before creating account

### 3. Password Encryption
- Passwords are hashed using BCrypt
- Never stored in plain text

### 4. JWT Token Authentication
- Admin receives JWT token upon successful registration
- Token used for subsequent API calls

---

## 🚀 API Details

### Endpoint
```
POST /api/admin/register
```

### Request Body
```json
{
  "username": "admin1",
  "email": "admin@example.com",
  "password": "securePassword123",
  "fullName": "John Admin",
  "role": "ADMIN",
  "superAdminKey": "SUPER_ADMIN_SECRET_KEY_2026"
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "message": "Admin registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "adminId": 1,
    "name": "John Admin",
    "email": "admin@example.com"
  }
}
```

### Error Response (400 Bad Request)
```json
{
  "success": false,
  "message": "Invalid super admin key. Only authorized super admins can create admin accounts.",
  "data": null
}
```

---

## 📝 Admin Roles Explained

### SUPER_ADMIN
- Full system access
- Can create other admins
- Can modify all settings
- Cannot be deleted

### ADMIN
- View and manage events
- View and manage customers
- View dashboard statistics
- Moderate content

### SUPPORT
- View events and customers
- Limited modification access
- Handle support tickets
- View analytics

---

## 🔧 Configuration

### Changing Super Admin Key

**Production Setup** (Recommended):
1. Set environment variable: `SUPER_ADMIN_KEY`
2. Update `AdminService.java`:

```java
@Value("${admin.super.key}")
private String superAdminKey;

public AuthResponse registerAdmin(AdminRegistrationRequest request) {
    if (request.getSuperAdminKey() == null || 
        !request.getSuperAdminKey().equals(superAdminKey)) {
        throw new RuntimeException("Invalid super admin key");
    }
    // ... rest of the code
}
```

3. Add to `application.properties`:
```properties
admin.super.key=${SUPER_ADMIN_KEY:SUPER_ADMIN_SECRET_KEY_2026}
```

### Database Table
Admin accounts are stored in the `admins` table:

```sql
CREATE TABLE admins (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🧪 Testing Admin Registration

### Test Case 1: Successful Registration
```bash
curl -X POST http://localhost:8080/api/admin/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testadmin",
    "email": "test@admin.com",
    "password": "password123",
    "fullName": "Test Admin",
    "role": "ADMIN",
    "superAdminKey": "SUPER_ADMIN_SECRET_KEY_2026"
  }'
```

### Test Case 2: Invalid Super Admin Key
```bash
curl -X POST http://localhost:8080/api/admin/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testadmin",
    "email": "test@admin.com",
    "password": "password123",
    "fullName": "Test Admin",
    "role": "ADMIN",
    "superAdminKey": "WRONG_KEY"
  }'
```

Expected: 400 Bad Request with error message

### Test Case 3: Duplicate Username
```bash
# Register same username twice
curl -X POST http://localhost:8080/api/admin/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin1",
    "email": "another@admin.com",
    "password": "password123",
    "fullName": "Another Admin",
    "role": "ADMIN",
    "superAdminKey": "SUPER_ADMIN_SECRET_KEY_2026"
  }'
```

Expected: 400 Bad Request - "Username already exists"

---

## 🎨 Frontend Features

### Form Validation
- All fields are required
- Email format validation
- Password minimum length: 6 characters
- Super admin key required

### User Experience
- Clear error messages
- Loading state during registration
- Auto-redirect to dashboard on success
- Links to login page
- Links to customer and guest logins

### Styling
- Modern, consistent with Auth.css
- Icons for each field
- Responsive design
- Security notice displayed

---

## 🔍 Troubleshooting

### Issue: "Invalid super admin key"
**Solution**: Ensure you're using the correct key: `SUPER_ADMIN_SECRET_KEY_2026`

### Issue: "Username already exists"
**Solution**: Choose a different username

### Issue: "Email already exists"
**Solution**: Use a different email address

### Issue: Registration successful but not redirected
**Solution**: Check browser console for errors. Ensure JWT token is being stored correctly.

### Issue: Can't access admin dashboard after registration
**Solution**: 
1. Check if adminToken is in localStorage
2. Verify token is being sent in API requests
3. Check backend logs for authentication errors

---

## 📊 Default Admin Account

A default admin account is created during database initialization:

**Username**: `admin`  
**Password**: `Admin@123`  
**Role**: SUPER_ADMIN

This account can be used to create other admin accounts.

---

## 🚨 Security Best Practices

1. **Change the Super Admin Key** immediately in production
2. **Use Environment Variables** for sensitive configuration
3. **Rotate Keys Periodically** (every 3-6 months)
4. **Limit Super Admin Access** to authorized personnel only
5. **Audit Admin Actions** through logging
6. **Use Strong Passwords** (minimum 12 characters, mix of characters)
7. **Enable 2FA** (future enhancement)
8. **Monitor Failed Login Attempts**
9. **Disable Inactive Accounts** regularly
10. **Keep Super Admin Key Secret** - never commit to version control

---

## 📈 Future Enhancements

- [ ] Two-factor authentication (2FA)
- [ ] Password reset functionality
- [ ] Email verification on registration
- [ ] Admin activity logging
- [ ] Account lockout after failed attempts
- [ ] Role-based permissions matrix
- [ ] Admin approval workflow
- [ ] Session management
- [ ] IP whitelisting for admin access
- [ ] Audit trail for admin actions

---

## ✅ Files Modified/Created

### Backend
1. ✅ `dto/AdminRegistrationRequest.java` - NEW
2. ✅ `service/AdminService.java` - Updated with registerAdmin()
3. ✅ `controller/AdminController.java` - Added /register endpoint

### Frontend
1. ✅ `pages/AdminRegister.js` - NEW
2. ✅ `pages/AdminLogin.js` - Added registration link
3. ✅ `services/api.js` - Added adminRegister() function
4. ✅ `App.js` - Added /admin/register route

### Documentation
1. ✅ `ADMIN_REGISTRATION_GUIDE.md` - This file

---

## 📞 Support

For issues or questions about admin registration:
1. Check this guide first
2. Review console logs (frontend and backend)
3. Verify database connection
4. Ensure all dependencies are installed
5. Check API endpoint is accessible

**Admin registration is now fully functional and secure!** 🎉

---

**Last Updated**: February 4, 2026  
**Status**: ✅ Complete and Ready for Use
