# Google OAuth2 Setup Guide

## 🚀 Hướng dẫn triển khai Google OAuth2 Login

### 1. Google Cloud Console Setup

#### Bước 1: Tạo Project
1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project hiện có
3. Enable Google+ API và Google Identity API

#### Bước 2: Tạo OAuth 2.0 Credentials
1. Vào **APIs & Services** → **Credentials**
2. Click **Create Credentials** → **OAuth 2.0 Client IDs**
3. Chọn **Web application**
4. Cấu hình:
   ```
   Name: Task Management Backend
   Authorized JavaScript origins:
   - http://localhost:3000 (Development)
   - https://yourdomain.com (Production)
   
   Authorized redirect URIs:
   - http://localhost:8080/api/auth/google/callback (Development)
   - https://api.yourdomain.com/api/auth/google/callback (Production)
   ```

#### Bước 3: Lấy Client ID và Client Secret
- Copy **Client ID** và **Client Secret**
- Lưu vào environment variables

### 2. Environment Variables

Tạo file `.env` hoặc set environment variables:

```bash
# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id-here
GOOGLE_CLIENT_SECRET=your-google-client-secret-here
GOOGLE_REDIRECT_URI=http://localhost:8080/api/auth/google/callback

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-at-least-32-characters-long

# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/db_taskmanagement
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your-password

# Redis (Optional - for refresh token storage)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Application URLs
FRONTEND_URL=http://localhost:3000
BACKEND_URL=http://localhost:8080
```

### 3. Database Migration

Chạy application để tự động tạo tables:
```bash
./gradlew bootRun
```

Hoặc tạo tables manually:
```sql
-- OAuth Providers table
CREATE TABLE oauth_providers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider_name VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    display_name VARCHAR(255),
    avatar_url TEXT,
    access_token TEXT,
    refresh_token TEXT,
    token_expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_provider_user (provider_name, provider_user_id)
);

-- Refresh Tokens table
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_revoked BOOLEAN DEFAULT FALSE,
    device_info TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_token (token(255))
);
```

### 4. Frontend Integration

#### Bước 1: Lấy Authorization URL
```javascript
const response = await fetch('/api/auth/google/url');
const { authUrl, state } = await response.json();

// Lưu state vào localStorage để verify sau
localStorage.setItem('oauth_state', state);

// Redirect user to Google
window.location.href = authUrl;
```

#### Bước 2: Handle Callback
```javascript
// Trong callback page (ví dụ: /auth/callback)
const urlParams = new URLSearchParams(window.location.search);
const code = urlParams.get('code');
const state = urlParams.get('state');
const savedState = localStorage.getItem('oauth_state');

if (state !== savedState) {
    throw new Error('Invalid state parameter');
}

// Exchange code for tokens
const response = await fetch('/api/auth/google/callback', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({ code, state })
});

const { accessToken, refreshToken, userInfo } = await response.json();

// Lưu tokens
localStorage.setItem('access_token', accessToken);
localStorage.setItem('refresh_token', refreshToken);

// Redirect to dashboard
window.location.href = '/dashboard';
```

#### Bước 3: Token Refresh
```javascript
async function refreshAccessToken() {
    const refreshToken = localStorage.getItem('refresh_token');
    
    const response = await fetch('/api/auth/refresh', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ refreshToken })
    });
    
    if (response.ok) {
        const { accessToken, refreshToken: newRefreshToken } = await response.json();
        localStorage.setItem('access_token', accessToken);
        localStorage.setItem('refresh_token', newRefreshToken);
        return accessToken;
    } else {
        // Redirect to login
        window.location.href = '/login';
    }
}
```

### 5. API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/auth/google/url` | Lấy Google authorization URL |
| POST | `/api/auth/google/callback` | Xử lý callback từ Google |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/logout` | Logout và revoke tokens |

### 6. Security Best Practices

✅ **Implemented:**
- HTTPS bắt buộc trong production
- State parameter chống CSRF
- JWT với short expiration (15 phút)
- Refresh token rotation
- Device tracking
- ID token verification

✅ **Recommended:**
- Rate limiting cho auth endpoints
- IP whitelist cho sensitive operations
- Audit logging
- Multi-factor authentication
- Session management

### 7. Testing

#### Test OAuth2 Flow:
```bash
# 1. Get auth URL
curl -X GET http://localhost:8080/api/auth/google/url

# 2. Manual browser test:
# - Copy authUrl và mở trong browser
# - Login với Google account
# - Copy authorization code từ callback URL

# 3. Test callback
curl -X POST http://localhost:8080/api/auth/google/callback \
  -H "Content-Type: application/json" \
  -d '{"code":"your-auth-code","state":"your-state"}'

# 4. Test token refresh
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"your-refresh-token"}'
```

### 8. Troubleshooting

#### Common Issues:

1. **"Invalid redirect URI"**
   - Kiểm tra redirect URI trong Google Console
   - Đảm bảo exact match với config

2. **"Invalid client ID"**
   - Verify GOOGLE_CLIENT_ID environment variable
   - Kiểm tra project trong Google Console

3. **"Token verification failed"**
   - Kiểm tra system time
   - Verify JWT_SECRET configuration

4. **Database connection issues**
   - Kiểm tra MySQL connection
   - Verify database credentials

### 9. Production Deployment

#### Environment Variables for Production:
```bash
GOOGLE_CLIENT_ID=prod-client-id
GOOGLE_CLIENT_SECRET=prod-client-secret
GOOGLE_REDIRECT_URI=https://api.yourdomain.com/api/auth/google/callback
JWT_SECRET=super-secure-production-secret-key
FRONTEND_URL=https://yourdomain.com
BACKEND_URL=https://api.yourdomain.com
```

#### SSL/HTTPS Setup:
- Configure reverse proxy (Nginx/Apache)
- Use Let's Encrypt for SSL certificates
- Update Google Console with HTTPS URLs

#### Monitoring:
- Setup logging for auth events
- Monitor failed login attempts
- Track token refresh patterns