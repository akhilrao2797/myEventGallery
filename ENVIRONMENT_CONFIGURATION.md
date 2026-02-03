# Environment Configuration Guide

## Overview

The Event Gallery application now supports environment-specific storage backends:
- **Local File System** for development (default)
- **AWS S3** for production

This makes development easier and more cost-effective while maintaining production-ready S3 integration.

---

## Storage Backend Selection

The application automatically selects the storage backend based on the active Spring profile:

| Profile | Storage Backend | Use Case |
|---------|----------------|----------|
| `local` (default) | Local File System | Development |
| `dev` | Local File System | Development |
| `prod` | AWS S3 | Production |
| `production` | AWS S3 | Production |

---

## Configuration

### Local Development (Default)

**No S3 setup required!** The application will store files locally.

#### application.properties
```properties
spring.profiles.active=local
storage.local.base-path=./uploads
app.base-url=http://localhost:8080
```

#### What happens:
- Files are stored in `./uploads/` directory
- Files are served via `/api/files/` endpoint
- No AWS credentials needed
- Perfect for development and testing

### Production (AWS S3)

#### application-prod.properties
```properties
spring.profiles.active=prod
storage.type=s3

# AWS Configuration (use environment variables)
aws.s3.bucket-name=${AWS_S3_BUCKET}
aws.s3.region=${AWS_REGION:us-east-1}
aws.access-key-id=${AWS_ACCESS_KEY_ID}
aws.secret-access-key=${AWS_SECRET_ACCESS_KEY}

app.base-url=${APP_BASE_URL}
```

#### What happens:
- Files are uploaded to S3 bucket
- Presigned URLs are generated for access
- Full cloud scalability
- Production-ready

---

## How to Run

### Development Mode (Local Storage)

```bash
# Default - uses local storage
./build.sh run

# Or explicitly set profile
export SPRING_PROFILES_ACTIVE=local
./mvnw spring-boot:run
```

### Production Mode (S3 Storage)

```bash
# Set environment variables
export SPRING_PROFILES_ACTIVE=prod
export AWS_S3_BUCKET=your-bucket-name
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export APP_BASE_URL=https://yourdomain.com

# Run application
./mvnw spring-boot:run
```

### Docker Deployment

#### docker-compose.yml (Development)
```yaml
services:
  backend:
    environment:
      - SPRING_PROFILES_ACTIVE=local
      - STORAGE_LOCAL_BASE_PATH=/app/uploads
    volumes:
      - ./uploads:/app/uploads
```

#### docker-compose.yml (Production)
```yaml
services:
  backend:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - AWS_S3_BUCKET=${AWS_S3_BUCKET}
      - AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
      - AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
```

---

## Architecture

### Storage Service Interface
```java
public interface StorageService {
    String uploadFile(MultipartFile file, String folderPath);
    String getFileUrl(String key);
    void deleteFile(String key);
    boolean doesFileExist(String key);
}
```

### Implementation Classes

#### LocalStorageService (Development)
- Stores files in local directory
- Serves files via REST API endpoint
- Auto-activated with `@Profile({"local", "dev", "default"})`

#### S3StorageService (Production)
- Uploads to AWS S3
- Generates presigned URLs
- Auto-activated with `@Profile({"prod", "production"})`

### Automatic Selection
Spring's `@Profile` annotation ensures only the appropriate implementation is loaded based on the active profile.

---

## File Access

### Local Development
```
http://localhost:8080/api/files/events/{event-code}/{filename}
```

### Production
```
https://your-bucket.s3.amazonaws.com/events/{event-code}/{filename}?presigned-params
```

---

## Benefits

### Development Benefits
✅ No AWS setup required  
✅ Faster development cycle  
✅ No cloud costs during development  
✅ Easier debugging  
✅ Works offline  

### Production Benefits
✅ Scalable cloud storage  
✅ Global CDN availability  
✅ Automatic backups  
✅ High availability  
✅ Cost-effective at scale  

---

## Migration

### From Local to Production

1. **Export local files** (if needed):
```bash
aws s3 sync ./uploads/ s3://your-bucket/
```

2. **Update environment**:
```bash
export SPRING_PROFILES_ACTIVE=prod
```

3. **Deploy application**

### From S3 to Local (for testing)

1. **Download files**:
```bash
aws s3 sync s3://your-bucket/ ./uploads/
```

2. **Switch profile**:
```bash
export SPRING_PROFILES_ACTIVE=local
```

---

## Testing

### Test Local Storage
```bash
# Start application
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run

# Upload a file
curl -X POST http://localhost:8080/api/guest/1/upload \
  -F "files=@test-image.jpg"

# Check uploads directory
ls -la ./uploads/events/
```

### Test S3 Storage
```bash
# Start application with S3
SPRING_PROFILES_ACTIVE=prod \
AWS_S3_BUCKET=test-bucket \
AWS_ACCESS_KEY_ID=xxx \
AWS_SECRET_ACCESS_KEY=xxx \
./mvnw spring-boot:run

# Upload a file
curl -X POST http://localhost:8080/api/guest/1/upload \
  -F "files=@test-image.jpg"

# Check S3 bucket
aws s3 ls s3://test-bucket/events/
```

---

## Troubleshooting

### Issue: Files not found in local mode
**Solution**: Check `storage.local.base-path` and ensure directory exists
```bash
mkdir -p ./uploads
chmod 755 ./uploads
```

### Issue: S3 access denied
**Solution**: Verify AWS credentials and bucket permissions
```bash
aws s3 ls s3://your-bucket
```

### Issue: Wrong storage backend loaded
**Solution**: Check active profile
```bash
# In application
curl http://localhost:8080/actuator/env | grep spring.profiles.active

# Or check logs
grep "Profile" logs/spring.log
```

---

## Security Considerations

### Local Storage
- Files are accessible via API endpoint
- Implement proper authentication
- Use secure file paths (no directory traversal)

### S3 Storage
- Use presigned URLs (temporary access)
- Enable bucket encryption
- Use IAM roles instead of access keys when possible
- Implement bucket policies for access control

---

## Best Practices

1. **Always use environment variables** for sensitive data
2. **Never commit AWS credentials** to version control
3. **Use IAM roles** in production (EC2, ECS, Lambda)
4. **Enable S3 versioning** for backup
5. **Set up lifecycle policies** to manage storage costs
6. **Monitor storage usage** with CloudWatch
7. **Test locally first** before deploying

---

## Quick Reference

| Command | Purpose |
|---------|---------|
| `SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run` | Run with local storage |
| `SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run` | Run with S3 storage |
| `ls ./uploads/` | Check local files |
| `aws s3 ls s3://bucket/` | Check S3 files |

---

## Summary

The application is now **environment-agnostic**:
- 🏠 **Local development**: Fast, simple, no cloud setup
- ☁️ **Production**: Scalable, reliable, S3-powered
- 🔄 **Easy switching**: Just change the profile
- ✅ **Same codebase**: One application, multiple environments
