# .gitignore Summary - Space Saving Guide

## ✅ Files Created
1. **`.gitignore`** - Main ignore file
2. **`.gitattributes`** - Git attributes for proper line endings and diff handling

---

## 🗑️ What's Being Ignored (Space Saved)

### Large Directories (Critical to Ignore)

| Directory | Purpose | Typical Size | Ignored? |
|-----------|---------|--------------|----------|
| `target/` | Maven build output | **62 MB** | ✅ Yes |
| `node_modules/` | npm dependencies | **100-500 MB** | ✅ Yes |
| `frontend/node_modules/` | Frontend deps | **100-500 MB** | ✅ Yes |
| `frontend/build/` | Production build | **2-10 MB** | ✅ Yes |
| `uploads/` | Local storage | **Varies** | ✅ Yes |

**Estimated Space Saved: 264+ MB minimum**

---

## 📋 Categories of Ignored Files

### 1. Build Artifacts
```
target/              # Maven compiled output
*.class              # Compiled Java
*.jar, *.war         # Package files
build/, dist/        # Frontend builds
```

### 2. Dependencies (Largest!)
```
node_modules/        # npm packages (HUGE!)
.mvn/               # Maven wrapper
```

### 3. IDE Configuration
```
.idea/              # IntelliJ IDEA
.vscode/            # VS Code
*.iml               # IntelliJ module files
.settings/          # Eclipse
```

### 4. OS Files
```
.DS_Store           # macOS
Thumbs.db           # Windows
*~                  # Linux backup files
```

### 5. Logs & Temporary
```
*.log               # Log files
*.tmp               # Temp files
*.swp               # Vim swap files
coverage/           # Test coverage
```

### 6. Environment & Secrets
```
.env                # Environment variables
.env.local          # Local overrides
application-prod.properties  # Production config
*.key, *.pem        # Certificates
.aws/               # AWS credentials
```

### 7. Local Storage
```
uploads/            # User uploaded files
storage/            # Local file storage
local-storage/      # Development storage
```

### 8. Database Files
```
*.db                # SQLite
*.sqlite3           # SQLite3
postgres-data/      # Docker PostgreSQL data
```

---

## 🎯 Why Each Category is Ignored

### Build Artifacts (`target/`, `build/`)
- ❌ **Not needed**: Can be regenerated with `mvn clean package` or `npm run build`
- 💾 **Space**: ~62 MB for backend, ~10 MB for frontend
- ⚡ **Benefit**: Faster clones, smaller repo size

### Dependencies (`node_modules/`)
- ❌ **Not needed**: Restored with `npm install`
- 💾 **Space**: 100-500 MB typically
- ⚡ **Benefit**: Massive space savings, faster operations
- 📝 **Note**: `package.json` and `package-lock.json` define dependencies

### IDE Files (`.idea/`, `.vscode/`)
- ❌ **Not needed**: Personal preferences, auto-generated
- 💾 **Space**: 1-5 MB
- ⚡ **Benefit**: Prevents merge conflicts, respects team member preferences

### Environment Files (`.env`, `application-prod.properties`)
- ❌ **SECURITY**: May contain secrets, API keys, passwords
- 💾 **Space**: < 1 MB
- 🔐 **Benefit**: Prevents credential leaks
- 📝 **Note**: Use `.env.example` to document required variables

### Local Storage (`uploads/`)
- ❌ **Not needed**: Test/development data
- 💾 **Space**: Can be gigabytes
- ⚡ **Benefit**: Keeps test images out of repo
- 📝 **Note**: Production uses S3, not local storage

---

## ✅ What IS Committed (Keep in Git)

### Source Code
```
src/                # All Java source code
frontend/src/       # All React source code
```

### Configuration Templates
```
application.properties          # Development config
application-prod.properties.example  # Template (no secrets)
.env.example                   # Environment variable template
```

### Documentation
```
*.md                # All markdown files
README.md          # Project documentation
database/*.sql     # Database migrations
```

### Package Definitions
```
pom.xml            # Maven dependencies
package.json       # npm dependencies
package-lock.json  # Locked versions
```

### Docker & DevOps
```
Dockerfile         # Container definitions
docker-compose.yml # Multi-container setup
.github/           # GitHub Actions (if added)
```

---

## 🚀 Benefits of Proper .gitignore

### 1. Smaller Repository Size
- **Before**: Potentially 500+ MB with dependencies
- **After**: ~10-20 MB of actual source code
- **Clone Time**: 10x faster

### 2. Faster Git Operations
- `git status` - Instant (not scanning node_modules)
- `git add .` - Safe (won't accidentally add build files)
- `git push` - Faster uploads

### 3. Cleaner Diffs
- No noise from compiled files
- Focus on actual code changes
- Easier code reviews

### 4. Security
- Prevents committing:
  - Database credentials
  - API keys
  - AWS credentials
  - SSL certificates
  - Environment secrets

### 5. Team Collaboration
- No merge conflicts in:
  - IDE configuration
  - Build artifacts
  - Personal settings
- Everyone can use their preferred IDE

---

## 📝 Best Practices

### DO ✅
- ✅ Commit `.gitignore` itself
- ✅ Commit `.env.example` (template)
- ✅ Commit source code
- ✅ Commit package definition files
- ✅ Commit database migrations
- ✅ Commit documentation

### DON'T ❌
- ❌ Commit `node_modules/`
- ❌ Commit `target/`
- ❌ Commit `.env` with secrets
- ❌ Commit IDE configuration
- ❌ Commit log files
- ❌ Commit uploaded images (use S3)
- ❌ Commit compiled binaries

---

## 🔧 Verify Your .gitignore

### Check What Would Be Committed
```bash
# See all files Git will track
git status

# See ignored files
git status --ignored

# Test if a file is ignored
git check-ignore -v target/myEventGallery-0.0.1-SNAPSHOT.jar
```

### Clean Up Accidentally Committed Files
```bash
# If you already committed something by mistake
git rm -r --cached target/
git rm -r --cached node_modules/
git commit -m "Remove ignored files from repository"
```

### Repository Size Check
```bash
# Check current repo size
du -sh .git/

# After cleanup, garbage collect
git gc --aggressive --prune=now
```

---

## 📦 Expected Repository Sizes

### Without .gitignore
```
Total: ~500 MB
├── Source code: 10 MB
├── target/: 62 MB
├── node_modules/: 400+ MB
├── uploads/: Variable
└── logs/: 5 MB
```

### With .gitignore (Proper)
```
Total: ~15 MB
├── Source code: 10 MB
├── Documentation: 3 MB
├── Configuration: 1 MB
└── Database scripts: 1 MB
```

**Space Saved: ~485 MB (97% reduction!)**

---

## 🎯 Quick Reference

### Most Important to Ignore
1. **`node_modules/`** - Largest by far
2. **`target/`** - Maven output
3. **`.env`** - Secrets
4. **`uploads/`** - User files
5. **`.idea/`** - IDE config

### Create Template Files
```bash
# Create environment template
cp .env .env.example
# Remove secrets from .env.example, commit it
git add .env.example
```

---

## ✅ Status

- [x] `.gitignore` created
- [x] `.gitattributes` created
- [x] Major directories excluded
- [x] Secrets protected
- [x] IDE files ignored
- [x] Build artifacts ignored

**Estimated Space Saved: 485+ MB**

**Your repository is now clean and efficient!** 🎉

---

## 🔗 Additional Resources

- [GitHub .gitignore Templates](https://github.com/github/gitignore)
- [gitignore.io Generator](https://www.toptal.com/developers/gitignore)
- [Git Documentation](https://git-scm.com/docs/gitignore)
