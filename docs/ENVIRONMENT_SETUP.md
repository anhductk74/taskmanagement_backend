# 🔧 Environment Variables Setup Guide

## 📋 Tạo file môi trường

### 1. Copy file example
```bash
cp .env.example .env
```

### 2. Hoặc tạo file `.env` mới với nội dung:
```bash
# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id-here
GOOGLE_CLIENT_SECRET=your-google-client-secret-here
GOOGLE_REDIRECT_URI=http://localhost:8080/api/auth/google/callback

# JWT Secret (phải ít nhất 32 ký tự)
JWT_SECRET=mySecretKey123456789012345678901234567890

# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/db_taskmanagement
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=123456789

# Application URLs
FRONTEND_URL=http://localhost:3000
BACKEND_URL=http://localhost:8080
```

## 🚀 Cách setup cho từng môi trường

### IntelliJ IDEA
1. **Run Configuration**:
   - Go to `Run` → `Edit Configurations`
   - Chọn Spring Boot application
   - Trong `Environment Variables`, add:
   ```
   GOOGLE_CLIENT_ID=your-value
   GOOGLE_CLIENT_SECRET=your-value
   JWT_SECRET=your-secret-key
   ```

2. **Hoặc sử dụng .env plugin**:
   - Install plugin "EnvFile"
   - Add `.env` file vào run configuration

### VS Code
1. Tạo file `.vscode/launch.json`:
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Spring Boot",
            "request": "launch",
            "mainClass": "com.example.taskmanagement_backend.TaskmanagementBackendApplication",
            "envFile": "${workspaceFolder}/.env"
        }
    ]
}
```

### Terminal/Command Line
```bash
# Export variables
export GOOGLE_CLIENT_ID=your-value
export GOOGLE_CLIENT_SECRET=your-value
export JWT_SECRET=your-secret-key

# Run application
./gradlew bootRun
```

### Docker
```dockerfile
# Dockerfile
ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
ENV JWT_SECRET=${JWT_SECRET}
```

```yaml
# docker-compose.yml
services:
  app:
    environment:
      - GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
      - GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
      - JWT_SECRET=${JWT_SECRET}
```

## 🔑 Lấy Google OAuth2 Credentials

### Bước 1: Google Cloud Console
1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project hiện có
3. Enable **Google+ API**

### Bước 2: Tạo OAuth 2.0 Credentials
1. Vào **APIs & Services** → **Credentials**
2. Click **Create Credentials** → **OAuth 2.0 Client IDs**
3. Chọn **Web application**
4. Cấu hình:
   ```
   Name: Task Management Backend
   
   Authorized JavaScript origins:
   http://localhost:3000
   
   Authorized redirect URIs:
   http://localhost:8080/api/auth/google/callback
   ```

### Bước 3: Copy credentials
- Copy **Client ID** → `GOOGLE_CLIENT_ID`
- Copy **Client Secret** → `GOOGLE_CLIENT_SECRET`

## 🔐 Generate JWT Secret

```bash
# Tạo random secret 32+ characters
openssl rand -base64 32

# Hoặc sử dụng online generator
# https://generate-secret.vercel.app/32
```

## ✅ Kiểm tra setup

### Test environment variables
```bash
# Kiểm tra biến đã được load
echo $GOOGLE_CLIENT_ID
echo $JWT_SECRET
```

### Test application
```bash
# Start application
./gradlew bootRun

# Check health
curl http://localhost:8080/api/auth/google/url
```

## 🚨 Security Notes

### ⚠️ QUAN TRỌNG:
- **KHÔNG** commit file `.env` vào Git
- **KHÔNG** share credentials trong code
- **SỬ DỤNG** environment variables cho production

### .gitignore
Đảm bảo file `.gitignore` có:
```
.env
.env.local
.env.*.local
```

## 🌍 Production Setup

### Heroku
```bash
heroku config:set GOOGLE_CLIENT_ID=your-value
heroku config:set GOOGLE_CLIENT_SECRET=your-value
heroku config:set JWT_SECRET=your-secret
```

### AWS/Azure/GCP
Sử dụng secrets manager hoặc environment variables của platform.

### Docker Production
```bash
docker run -e GOOGLE_CLIENT_ID=value -e JWT_SECRET=secret your-app
```

---

## 🔧 Troubleshooting

### Lỗi thường gặp:

1. **"Google Client ID not found"**
   - Kiểm tra biến `GOOGLE_CLIENT_ID` đã được set
   - Restart application sau khi thay đổi

2. **"Invalid JWT Secret"**
   - JWT_SECRET phải ít nhất 32 ký tự
   - Không chứa ký tự đặc biệt

3. **"Database connection failed"**
   - Kiểm tra MySQL đang chạy
   - Verify username/password

### Debug environment variables:
```java
@Value("${GOOGLE_CLIENT_ID:NOT_SET}")
private String clientId;

@PostConstruct
public void checkConfig() {
    log.info("Google Client ID: {}", clientId.substring(0, 10) + "...");
}
```