# Attendify

A comprehensive university attendance tracking system with web and native Android applications.

## Overview

Attendify is a modern attendance management system designed for universities. It supports multiple user roles (Students, Instructors, Admins) and provides QR code-based attendance tracking, real-time analytics, and comprehensive course management.

## Project Structure

```
Attendify/
├── client/          # React web application
├── server/          # Express.js/Node.js backend API
├── shared/          # Shared TypeScript types and schemas
├── android/         # Native Android application (Kotlin + Jetpack Compose)
└── ...
```

## Applications

### Web Application
The web client is built with:
- React + TypeScript
- Vite
- TanStack Query
- Tailwind CSS
- Replit Auth

See the `client/` directory for web app setup.

### Android Application
The native Android app is built with:
- Kotlin
- Jetpack Compose (Material 3)
- Hilt for DI
- Retrofit for networking
- ZXing for QR code scanning

**See [android/README.md](android/README.md) for detailed Android setup instructions.**

### Backend API
The backend is built with:
- Express.js
- Node.js
- PostgreSQL (via Drizzle ORM)
- Replit Auth

## Features

### For Students
- 📱 View enrolled courses (web & mobile)
- 📷 Scan QR codes to mark attendance (web & mobile)
- 📊 Track attendance history
- 📅 View session schedules

### For Instructors
- 📚 Create and manage courses
- 🎫 Generate QR codes for attendance sessions
- 👥 Track student attendance in real-time
- ➕ Enroll students in courses
- 📈 View attendance analytics

### For Admins
- 👤 Manage users and assign roles
- 📖 Full course management
- 📊 System-wide analytics
- 🔧 System administration

## Quick Start

### Web Application

1. Install dependencies:
   ```bash
   npm install
   ```

2. Set up environment variables (see server configuration)

3. Run database migrations:
   ```bash
   npm run db:push
   ```

4. Start development server:
   ```bash
   npm run dev
   ```

### Android Application

See [android/README.md](android/README.md) for complete setup instructions.

Quick steps:
1. Open `android/` in Android Studio
2. Configure API base URL in `app/build.gradle.kts`
3. Sync Gradle and run

## Environment Variables

Required environment variables for the backend:
- `DATABASE_URL` - PostgreSQL connection string
- `SESSION_SECRET` - Secret for session encryption
- `REPLIT_DOMAINS` - Comma-separated list of domains
- `REPL_ID` - Replit application ID (for OAuth)

## API Documentation

The backend provides a RESTful API for both web and mobile clients:

### Authentication
- `GET /api/auth/user` - Get current authenticated user
- `GET /api/login` - Initiate login flow
- `GET /api/logout` - Logout

### Users
- `GET /api/users` - List all users (admin)
- `PATCH /api/users/:id` - Update user role

### Courses
- `GET /api/courses` - List all courses
- `POST /api/courses` - Create course
- `GET /api/courses/instructor/:id` - Get instructor's courses

### Sessions
- `GET /api/sessions/course/:courseId` - Get course sessions
- `POST /api/sessions` - Create new session
- `GET /api/sessions/:id/qrcode` - Get QR code for session

### Attendance
- `POST /api/attendance/checkin` - Mark attendance with QR code
- `GET /api/attendance/student/:studentId` - Get student's attendance
- `GET /api/attendance/session/:sessionId` - Get session attendance

See `server/routes.ts` for complete API reference.

## Technology Stack

### Frontend (Web)
- React 18
- TypeScript
- Vite
- TanStack Query
- Tailwind CSS + shadcn/ui
- html5-qrcode for scanning

### Mobile (Android)
- Kotlin
- Jetpack Compose
- Material 3 Design
- Hilt (Dependency Injection)
- Retrofit + OkHttp
- Kotlin Coroutines + Flow
- ZXing (QR Code)

### Backend
- Node.js + Express
- TypeScript
- PostgreSQL
- Drizzle ORM
- Passport.js (Auth)
- QRCode generation

## Development

### Running Tests
```bash
npm test
```

### Type Checking
```bash
npm run check
```

### Building for Production
```bash
npm run build
npm start
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly (web and/or mobile)
5. Submit a pull request

## License

[Add license information]

## Support

For issues and questions:
- Open an issue on GitHub
- Check documentation in respective directories
- Contact the development team

## Roadmap

### Phase 1 (Complete)
- ✅ Web application with all role dashboards
- ✅ Backend API
- ✅ Basic authentication
- ✅ QR code generation and scanning (web)
- ✅ Android app scaffold and architecture

### Phase 2 (In Progress)
- 🔄 Android app - Core features implementation
- 🔄 Enhanced authentication
- 🔄 Real-time updates

### Phase 3 (Planned)
- 📱 iOS application
- 🔔 Push notifications
- 📊 Advanced analytics
- 🌐 Multi-language support
- 📴 Offline support

## Architecture

The system follows a client-server architecture:

```
┌─────────────┐     ┌─────────────┐
│  Web Client │────▶│             │
└─────────────┘     │             │
                    │   Backend   │
┌─────────────┐     │   API       │
│Android App  │────▶│  (Express)  │
└─────────────┘     │             │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  PostgreSQL │
                    └─────────────┘
```

- **Frontend clients** (web/mobile) communicate with the backend via REST API
- **Backend** handles business logic, authentication, and data persistence
- **Database** stores all application data
- **Authentication** is handled via Replit Auth (web) and session tokens (mobile)

## Screenshots

[Add screenshots when UI is complete]

---

Built with ❤️ for better attendance management
