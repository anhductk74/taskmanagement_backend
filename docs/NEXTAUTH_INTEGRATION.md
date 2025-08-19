# NextAuth.js Integration Guide

## 🚨 Problem: Duplicate API Calls

Your current setup is making multiple duplicate calls:
```
NextAuth.js: /api/auth/session, /api/auth/providers, /api/auth/csrf
useUserData: /api/auth/me
authService: /api/auth/me  
usersService: /api/auth/me
```

## ✅ Solution: Consolidated Authentication

### Backend Changes (✅ Implemented)

New consolidated endpoints in `NextAuthController.java`:

```java
GET /api/auth/me              // Single user data endpoint
GET /api/auth/session         // NextAuth session (includes user data)
GET /api/auth/user-data       // Comprehensive data (replaces all calls)
GET /api/auth/providers       // Auth providers
GET /api/auth/csrf           // CSRF token
GET /api/auth/health         // Health check
```

### Frontend Integration

#### 1. Update NextAuth Configuration

```javascript
// pages/api/auth/[...nextauth].js or app/api/auth/[...nextauth]/route.js
import NextAuth from 'next-auth'
import GoogleProvider from 'next-auth/providers/google'

export default NextAuth({
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET,
    })
  ],
  
  callbacks: {
    async jwt({ token, account, user }) {
      // Store backend access token
      if (account) {
        token.backendAccessToken = account.access_token
      }
      return token
    },
    
    async session({ session, token }) {
      // Get user data from your backend (single call)
      try {
        const response = await fetch(`${process.env.BACKEND_URL}/api/auth/user-data`, {
          headers: {
            'Authorization': `Bearer ${token.backendAccessToken}`
          }
        })
        
        if (response.ok) {
          const userData = await response.json()
          session.user = {
            ...session.user,
            ...userData.user,
            backendData: userData
          }
        }
      } catch (error) {
        console.error('Failed to fetch backend user data:', error)
      }
      
      return session
    }
  },
  
  // Use backend session endpoint
  session: {
    strategy: 'jwt',
    maxAge: 24 * 60 * 60, // 24 hours
  }
})
```

#### 2. Create Consolidated Auth Hook

```javascript
// hooks/useAuth.js
import { useSession } from 'next-auth/react'
import { useState, useEffect } from 'react'

export function useAuth() {
  const { data: session, status } = useSession()
  const [userData, setUserData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (status === 'loading') return

    if (session?.user?.backendData) {
      // Use data from NextAuth session (no additional API call needed)
      setUserData(session.user.backendData)
      setLoading(false)
    } else if (session) {
      // Fallback: single API call if session doesn't have backend data
      fetchUserData()
    } else {
      setUserData(null)
      setLoading(false)
    }
  }, [session, status])

  const fetchUserData = async () => {
    try {
      const response = await fetch('/api/auth/user-data')
      if (response.ok) {
        const data = await response.json()
        setUserData(data)
      }
    } catch (error) {
      console.error('Failed to fetch user data:', error)
    } finally {
      setLoading(false)
    }
  }

  return {
    user: userData?.user || session?.user,
    session: userData?.session || session,
    authenticated: userData?.authenticated || !!session,
    loading: loading || status === 'loading',
    permissions: userData?.permissions || [],
    refetch: fetchUserData
  }
}
```

#### 3. Replace Multiple Services with Single Hook

**❌ Before (Multiple duplicate calls):**
```javascript
// Multiple services making duplicate calls
const { data: userData } = useUserData()        // /api/auth/me
const { user } = useAuthService()               // /api/auth/me  
const { profile } = useUsersService()           // /api/auth/me
const { data: session } = useSession()         // /api/auth/session
```

**✅ After (Single consolidated call):**
```javascript
// Single hook with all data
const { user, session, authenticated, loading, permissions } = useAuth()
```

#### 4. Update Components

```javascript
// components/UserProfile.jsx
import { useAuth } from '@/hooks/useAuth'

export default function UserProfile() {
  const { user, authenticated, loading } = useAuth()

  if (loading) return <div>Loading...</div>
  if (!authenticated) return <div>Please login</div>

  return (
    <div>
      <h1>Welcome {user.profile?.firstName || user.email}</h1>
      <p>Organization: {user.organization?.name}</p>
      <p>Roles: {user.roles?.map(r => r.name).join(', ')}</p>
    </div>
  )
}
```

#### 5. API Route for Backend Communication

```javascript
// pages/api/auth/user-data.js or app/api/auth/user-data/route.js
import { getServerSession } from 'next-auth'
import { authOptions } from './[...nextauth]'

export async function GET(request) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return Response.json({ authenticated: false })
    }

    // Forward to Spring Boot backend
    const response = await fetch(`${process.env.BACKEND_URL}/api/auth/user-data`, {
      headers: {
        'Authorization': `Bearer ${session.backendAccessToken}`,
        'Content-Type': 'application/json'
      }
    })

    if (response.ok) {
      const data = await response.json()
      return Response.json(data)
    }

    return Response.json({ authenticated: false })
  } catch (error) {
    console.error('Auth API error:', error)
    return Response.json({ authenticated: false })
  }
}
```

## 🚀 Performance Benefits

### Before (Multiple Calls)
```
Page Load:
├── NextAuth session call     → /api/auth/session
├── NextAuth providers call   → /api/auth/providers  
├── NextAuth CSRF call        → /api/auth/csrf
├── useUserData call          → /api/auth/me
├── authService call          → /api/auth/me
└── usersService call         → /api/auth/me

Total: 6 API calls (3 duplicate /api/auth/me calls)
```

### After (Consolidated)
```
Page Load:
├── NextAuth session call     → /api/auth/session (includes user data)
└── Optional: user-data call  → /api/auth/user-data (if needed)

Total: 1-2 API calls (no duplicates)
```

## 🔧 Migration Steps

1. **✅ Backend**: NextAuthController.java implemented
2. **✅ Backend**: UserService.getCurrentUserData() added
3. **🔄 Frontend**: Update NextAuth configuration
4. **🔄 Frontend**: Create useAuth hook
5. **🔄 Frontend**: Replace multiple services with single hook
6. **🔄 Frontend**: Update components to use useAuth
7. **🔄 Frontend**: Test and verify no duplicate calls

## 📊 Monitoring

Check browser Network tab to verify:
- ✅ Only 1-2 auth-related API calls on page load
- ✅ No duplicate /api/auth/me calls
- ✅ Faster page load times
- ✅ Reduced server load

## 🎯 Expected Results

- **67% fewer API calls** (6 → 2 calls)
- **Faster page loads** (less network overhead)
- **Better UX** (single loading state)
- **Cleaner code** (one auth hook vs multiple services)
- **Reduced server load** (fewer duplicate requests)