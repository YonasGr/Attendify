# Attendify

A fully offline native Android application for university attendance tracking with a modern, professional UI.

## 🎨 Recent Updates

**Core Feature Implementation (January 2025)**
- 🔐 **Biometric Authentication**: Secure fingerprint/face login after initial setup
- ⚙️ **Settings Screen**: User preferences and biometric management
- 📱 **Enhanced Student UI**: Profile avatars, upcoming sessions, settings access
- 👨‍🏫 **Instructor Tools**: Manual enrollment, attendance marking, comprehensive tracking
- 👤 **12+ Demo Accounts**: Realistic testing environment with students and instructors
- 📚 **12+ Demo Courses**: CS, Math, and Physics courses across multiple semesters
- 🌐 **Cloud Sync Ready**: REST API scaffold for online database integration
- 📖 **Comprehensive Documentation**: Professional setup guides and API docs

**Previous Major UI/UX Overhaul (January 2025)**
- ✨ Modern, professional color scheme (Indigo, Emerald, Violet)
- 🎯 Enhanced dashboards with welcome cards and statistics
- 🚀 Create Session screen with date picker and validation
- 💎 Improved cards with elevation, shadows, and better spacing
- 🎨 Semantic colors for status and roles
- 📱 Material 3 design throughout

## Overview

Attendify is a modern attendance management system designed for universities, built as a native Android application with complete offline functionality. It supports multiple user roles (Students, Instructors, Admins) and provides QR code-based attendance tracking, analytics, and comprehensive course management - all stored locally on the device.

## Project Structure

```
Attendify/
├── android/         # Native Android application (Kotlin + Jetpack Compose)
│   ├── app/
│   │   └── src/main/kotlin/com/attendify/app/
│   │       ├── data/          # Data layer with Room database
│   │       ├── di/            # Dependency injection (Hilt)
│   │       ├── ui/            # UI layer (Jetpack Compose)
│   │       └── utils/         # Utility classes
│   └── ...
└── server/          # REST API server for cloud synchronization (Node.js + Express)
    ├── routes/      # API endpoints
    ├── server.js    # Main server file
    └── README.md    # Server documentation
```

## Android Application

The native Android app is built with:
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Local Database**: Room (SQLite)
- **Async Operations**: Kotlin Coroutines + Flow
- **QR Code**: ZXing for scanning and generation
- **Authentication**: Local user management

**See [android/README.md](android/README.md) for detailed setup instructions.**

## Features

### For Students
- 📱 View enrolled courses
- 📷 Scan QR codes to mark attendance
- 📊 Track attendance history
- 📅 View upcoming sessions
- 🔐 Biometric login (fingerprint/face)
- ⚙️ Settings and preferences
- 📴 Full offline functionality

### For Instructors
- 📚 Create and manage courses
- 🎫 Generate QR codes for attendance sessions
- 👥 Track student attendance in real-time
- ➕ Manual student enrollment (for missed QR scans)
- ✏️ Manual attendance marking
- 📊 Comprehensive session management
- 📈 View attendance analytics
- ⚙️ Settings and preferences

### For Admins
- 👤 Manage users and assign roles
- 📖 Full course and session management
- 📊 System-wide analytics and reporting
- 🔧 System administration
- 👥 Comprehensive user management
- ⚙️ Settings and preferences

### Cloud Synchronization (Server)
- 🌐 REST API for data sync
- 📤 Upload local changes to cloud
- 📥 Download server updates
- 🔄 Conflict resolution
- 🔒 JWT authentication
- 🗄️ PostgreSQL database support
- ☁️ **Production Backend**: https://attendify-mpsw.onrender.com
- 🔄 **Automatic Sync**: Seamless integration with live server
- 💾 **Offline-First**: Local Room database with cloud backup

## Quick Start

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Gradle 8.5 (included via wrapper)
- Android SDK with API 24 (Android 7.0) minimum
- Target API 34 (Android 14)
- Network access to Google Maven repository for dependency downloads

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/YonasGr/Attendify.git
   cd Attendify/android
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to `Attendify/android` directory
   - Click "OK"

3. **Sync Gradle**
   - Click "Sync Project with Gradle Files" in Android Studio
   - Wait for dependencies to download

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift + F10

### First Time Setup

On first launch, the app will create a local database with demo users and content:

**Demo Accounts:**
- **Admin**: username: `admin`, password: `admin123`
- **Instructors**:
  - John Doe (CS): `instructor` / `instructor123`
  - Jane Smith (Math): `jane.smith` / `instructor123`
  - Robert Jones (Physics): `robert.jones` / `instructor123`
- **Students** (12 total):
  - Alice Johnson (CS): `student` / `student123`
  - Bob Williams (CS): `bob.williams` / `student123`
  - Carol Jones (Math): `carol.jones` / `student123`
  - David Brown (Physics): `david.brown` / `student123`
  - Emma Wilson (CS): `emma.wilson` / `student123`
  - Frank Taylor (Math): `frank.taylor` / `student123`
  - Grace Lee (Physics): `grace.lee` / `student123`
  - Henry Martin (CS): `henry.martin` / `student123`
  - Isabel Garcia (Math): `isabel.garcia` / `student123`
  - Jack Rodriguez (Physics): `jack.rodriguez` / `student123`
  - Kate Anderson (CS): `kate.anderson` / `student123`
  - Liam Thomas (Math): `liam.thomas` / `student123`

**Demo Content:**
- 12 courses across Computer Science, Mathematics, and Physics departments
- 12+ scheduled sessions with QR codes for attendance
- Sample attendance records showing realistic usage
- Student enrollments across multiple courses

See [android/README.md](android/README.md) for complete setup instructions and documentation.

## Backend Integration

The Attendify Android app integrates with the production backend at **https://attendify-mpsw.onrender.com**.

### Recent Backend Improvements (October 2025)

The backend API has been **completely implemented** with full database operations:

- ✅ **Complete Database Schema**: All tables (users, courses, sessions, enrollments, attendance_records) with proper constraints and relationships
- ✅ **Authentication System**: Full JWT-based authentication with bcrypt password hashing
  - Login with username/password validation
  - User registration with duplicate username checking
  - Token refresh mechanism
  - Secure password storage with bcrypt (10 salt rounds)
- ✅ **Role-Based Access Control**: Middleware for authentication and authorization
  - JWT token verification on protected routes
  - Role-based permissions (student, instructor, admin)
  - Granular access control per endpoint
- ✅ **Complete CRUD Operations**: All endpoints fully implemented with database operations
  - Users: Create, read, update, delete with validation
  - Courses: Full CRUD with instructor assignment and validation
  - Sessions: Complete session management with QR code support
  - Enrollments: Student enrollment with duplicate checking
  - Attendance: Recording and retrieval with multiple filters
- ✅ **Comprehensive Error Handling**: Proper HTTP status codes and error messages
  - 400 Bad Request for invalid input
  - 401 Unauthorized for missing authentication
  - 403 Forbidden for insufficient permissions
  - 404 Not Found for missing resources
  - 409 Conflict for duplicate entries
  - 500 Internal Server Error with error logging
- ✅ **Input Validation**: Request validation at every endpoint
- ✅ **Database Constraints**: Foreign keys, unique constraints, and cascading deletes

### Architecture

The app follows an **offline-first architecture**:

1. **Local Database (Room/SQLite)**: Primary data store for instant access and offline functionality
2. **Backend API (Render)**: Cloud storage for data synchronization across devices
3. **Automatic Sync**: Background synchronization when network is available
4. **Fallback Support**: Local data is used when backend is unreachable

### Network Features

- ✅ **Authentication**: 
  - Login and registration with JWT tokens
  - Password hashing with bcrypt for security
  - Token refresh for extended sessions
  - Secure token storage in Android DataStore
- ✅ **User Management**: 
  - Sync user profiles and roles
  - Role-based access control (student, instructor, admin)
  - Admin-only user creation and management
  - Profile updates with password change support
- ✅ **Course Operations**: 
  - Create, read, update, delete courses with backend
  - Instructor assignment and validation
  - Duplicate course code prevention
  - Course filtering by instructor or enrollment
- ✅ **Session Management**: 
  - Sync attendance sessions and QR codes
  - Session creation with date/time validation
  - Active/inactive session status
  - Instructor-only session management
- ✅ **Attendance Tracking**: 
  - Real-time attendance submission to server
  - Multiple retrieval options (by session, by student, all)
  - Attendance status tracking (present, late, absent)
  - Duplicate attendance prevention
- ✅ **Enrollment Sync**: 
  - Student course enrollments synchronized
  - Enrollment validation (student exists, course exists, not already enrolled)
  - Instructor-controlled enrollment management
- ✅ **Error Handling**: 
  - Robust retry logic and offline fallback
  - Comprehensive error messages
  - Proper HTTP status codes
  - Detailed error logging
- ✅ **Token Management**: 
  - Automatic token refresh and storage
  - Secure authentication headers
  - Token expiration handling (7-day default)

### API Configuration

The app is pre-configured to use the production backend. The base URL is set in `android/app/build.gradle.kts`:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://attendify-mpsw.onrender.com/api/\"")
```

To change the backend URL (e.g., for local development):

1. Edit `android/app/build.gradle.kts`
2. Update the `API_BASE_URL` value
3. Sync Gradle and rebuild

**Development URLs:**
- **Local backend**: `"http://10.0.2.2:3000/api/"` (Android emulator)
- **Network device**: `"http://192.168.x.x:3000/api/"` (replace with your IP)
- **Production**: `"https://attendify-mpsw.onrender.com/api/"` (default)

### Data Synchronization

The app automatically syncs data when:
- User logs in
- User manually triggers sync (Settings screen)
- App returns to foreground after being offline

**Sync Process:**
1. Upload local changes to server
2. Download updates from server
3. Merge changes with conflict resolution (last-write-wins)
4. Update local database

**Manual Sync:**
Navigate to Settings → Sync Data to manually trigger synchronization.

### Offline Functionality

All features work offline using the local database:
- View courses and sessions
- Mark attendance (synced when online)
- Access user profiles
- View attendance history

Data is queued and automatically synchronized when network connectivity is restored.

## Architecture

The application follows modern Android architecture with MVVM pattern:

```
┌─────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                   │
│                    (Jetpack Compose)                     │
├─────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Student    │  │  Instructor  │  │    Admin     │  │
│  │  Dashboard   │  │  Dashboard   │  │  Dashboard   │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                 │                  │          │
│         └─────────────────┼──────────────────┘          │
│                           │                             │
│                  ┌────────▼────────┐                    │
│                  │   ViewModels    │                    │
│                  │    (MVVM)       │                    │
│                  └────────┬────────┘                    │
├───────────────────────────┼─────────────────────────────┤
│                  ┌────────▼────────┐                    │
│                  │  Repositories   │                    │
│                  └────────┬────────┘                    │
├───────────────────────────┼─────────────────────────────┤
│                  ┌────────▼────────┐                    │
│                  │  Room Database  │                    │
│                  │    (SQLite)     │                    │
│                  └─────────────────┘                    │
└─────────────────────────────────────────────────────────┘
```

### Key Components
- **UI Layer**: Jetpack Compose with Material 3
- **ViewModel Layer**: Business logic and state management
- **Repository Layer**: Data access abstraction
- **Database Layer**: Room (SQLite) for local persistence

## Technology Stack

### Android Application
- **Language**: Kotlin 1.9.23
- **Build System**: Gradle 8.5
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt 2.50
- **Code Generation**: KSP (Kotlin Symbol Processing)
- **Database**: Room (SQLite) for offline storage
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0
- **JSON Parsing**: Gson 2.10.1
- **Async**: Kotlin Coroutines + Flow
- **QR Codes**: ZXing for scanning and generation
- **Biometric Auth**: AndroidX Biometric library
- **Local Storage**: DataStore for preferences and tokens
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### REST API Server
- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: PostgreSQL (cloud-ready)
- **Authentication**: JWT (JSON Web Tokens)
- **Password Hashing**: bcrypt
- **CORS**: Enabled for cross-origin requests
- **Hosting**: Render.com (Production)
- **URL**: https://attendify-mpsw.onrender.com

## Development

### Building the Android App
```bash
# In Android Studio, or via command line:
cd android
./gradlew assembleDebug
```

### Running Tests
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### Building for Production
```bash
./gradlew assembleRelease
```

### Running the REST API Server

**Prerequisites:**
- Node.js v16 or higher
- PostgreSQL database

**Installation:**
```bash
cd server
npm install
```

**Configuration:**
```bash
cp .env.example .env
# Edit .env with your database URL and JWT secret
```

**Development:**
```bash
npm run dev
```

**Production:**
```bash
npm start
```

**See [server/README.md](server/README.md) for detailed server documentation.**

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly on Android devices/emulators
5. Follow Kotlin coding conventions
6. Submit a pull request

## License

[Add license information]

## Support

For issues and questions:
- Open an issue on GitHub
- Check documentation in respective directories
- Contact the development team

## Troubleshooting

### Backend API Issues

**Course Creation Returns 400/500 Error:**
- ✅ **Fixed**: Backend now validates all required fields (code, name, instructorId, semester, year)
- ✅ **Fixed**: Proper error messages for missing or invalid fields
- ✅ **Fixed**: Duplicate course code detection returns 409 Conflict
- ✅ **Fixed**: Instructor validation ensures user exists and has instructor role

**Missing Course Lists for Instructors:**
- ✅ **Fixed**: Instructors now see only their assigned courses
- ✅ **Fixed**: Course filtering by instructorId implemented
- ✅ **Fixed**: Proper JOIN queries return instructor details with courses

**Admin/Instructor Panels Not Functional:**
- ✅ **Fixed**: Role-based access control properly enforced
- ✅ **Fixed**: Authentication middleware validates JWT tokens
- ✅ **Fixed**: All CRUD operations fully implemented with database queries
- ✅ **Fixed**: Proper permissions for create/update/delete operations

**Authentication Failures:**
- ✅ **Fixed**: Login properly validates username and password with bcrypt
- ✅ **Fixed**: JWT tokens generated with configurable expiration (7 days default)
- ✅ **Fixed**: Token refresh endpoint for extending sessions
- ✅ **Fixed**: Secure password hashing with 10 salt rounds

**Database Connection Errors:**
- Ensure `DATABASE_URL` environment variable is set correctly
- For local development, PostgreSQL must be running
- For production (Render), database URL is automatically provided
- Check server logs for specific connection errors

**Authorization Issues:**
- Ensure JWT token is included in `Authorization: Bearer <token>` header
- Token must be valid and not expired
- User role must match required permissions for endpoint
- Students can only access their own data unless admin/instructor

### Android App Issues

**App Won't Build:**
- Ensure JDK 17+ is installed
- Check Android SDK is updated
- Network access to Google Maven may be restricted
- Run `./gradlew clean` then rebuild

**Network Connectivity:**
- For emulator, use `http://10.0.2.2:3000/api/` for local backend
- For physical device, use `http://192.168.x.x:3000/api/` with your computer's IP
- Production backend is at `https://attendify-mpsw.onrender.com/api/`
- Check API_BASE_URL in `android/app/build.gradle.kts`

**Camera Not Working:**
- Grant camera permissions in Settings
- Restart the app after granting permission

**Biometric Not Available:**
- Check device has fingerprint/face sensor
- Enable biometric auth in device settings
- May not work on emulators

**QR Code Won't Scan:**
- Ensure good lighting
- Hold camera steady
- QR code should be clear and focused

## Roadmap

### Phase 1 (Complete) ✅
- ✅ Android app scaffold and architecture
- ✅ Room database integration
- ✅ Local authentication
- ✅ Role-based navigation
- ✅ QR code generation and scanning
- ✅ Complete offline functionality
- ✅ Modern UI/UX with Material 3 design
- ✅ Enhanced color scheme and theming
- ✅ Rich demo content for onboarding

### Phase 2 (Complete) ✅
- ✅ Course management (Create, View, Manage)
- ✅ Session management (Create, View, QR Display)
- ✅ Attendance tracking (Scan, Record, History)
- ✅ User management (Create, View, Filter by role)
- ✅ Analytics and reporting dashboards
- ✅ Biometric authentication support
- ✅ Dark mode support
- ✅ Professional dashboard designs

### Phase 3 (In Progress) 🔄
- ✅ Create Session screen with validation
- 🔄 Enrollment Management interface
- 🔄 Course Details with session list
- 🔄 Session Details with attendance list
- 🔄 Edit functionality for entities
- 🔄 Delete with confirmation dialogs

### Phase 4 (Future) 📋
- 📋 Data export/import functionality
- 📋 Advanced analytics with charts
- 📋 Backup and restore
- 📋 Multi-language support
- 📋 Push notifications
- 📋 Tablet optimization

## Data Management

All data is stored locally on the device using Room (SQLite):

### Database Entities
- **Users**: Student, Instructor, Admin accounts
- **Courses**: Course information and instructors
- **Sessions**: Class sessions with dates and times
- **Enrollments**: Student-course relationships
- **Attendance Records**: Check-in records with timestamps

### Data Persistence
- Automatic database creation on first launch
- Seed data for default users
- Transaction-based operations
- Data integrity with foreign keys
- Cascading deletes where appropriate

## Customization

### Profile Logo

The app includes a default profile logo displayed in the top app bar. To customize it with your own logo:

1. **Prepare your image**:
   - Create an image file in PNG format (recommended size: 48x48dp or larger)
   - Or create a vector drawable XML file for scalability

2. **Replace the default logo**:
   - Navigate to `android/app/src/main/res/drawable/`
   - Replace `ic_profile_logo.xml` with your custom image file
   - If using PNG: name it `ic_profile_logo.png`
   - If using XML: keep the name as `ic_profile_logo.xml`

3. **Using Android Studio**:
   - Right-click on `res/drawable` folder
   - Select "New → Image Asset" or "New → Vector Asset"
   - Configure your asset and name it `ic_profile_logo`
   - The new asset will automatically replace the default

The profile logo appears on all dashboard screens (Student, Instructor, and Admin) in the top app bar next to the user's name.

### App Launcher Icon

The Attendify app comes with a professional launcher icon. To customize it with your own branding:

1. **Using Android Studio** (Easiest):
   - Right-click on `android/app/src/main/res`
   - Select "New → Image Asset"
   - Choose "Launcher Icons (Adaptive and Legacy)"
   - Upload your icon and configure settings
   - Android Studio generates all required densities automatically

2. **Manual Replacement**:
   - Prepare PNG icons for all densities (mdpi: 48x48, hdpi: 72x72, xhdpi: 96x96, xxhdpi: 144x144, xxxhdpi: 192x192)
   - Replace files in `android/app/src/main/res/mipmap-*/ic_launcher.png`
   - For adaptive icons (Android 8.0+), also create foreground layers in `mipmap-*/ic_launcher_foreground.png`

3. **Using Online Tools**:
   - Use Android Asset Studio (https://romannurik.github.io/AndroidAssetStudio/)
   - Upload your icon and download generated assets
   - Copy files to the appropriate mipmap directories

**For detailed instructions, see [android/README.md](android/README.md#app-launcher-icon)**.

## Screenshots

[Add screenshots when UI is complete]

---

Built with ❤️ for better attendance management
