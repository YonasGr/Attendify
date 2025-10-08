# Attendify

A fully offline native Android application for university attendance tracking.

## Overview

Attendify is a modern attendance management system designed for universities, built as a native Android application with complete offline functionality. It supports multiple user roles (Students, Instructors, Admins) and provides QR code-based attendance tracking, analytics, and comprehensive course management - all stored locally on the device.

## Project Structure

```
Attendify/
└── android/         # Native Android application (Kotlin + Jetpack Compose)
    ├── app/
    │   └── src/main/kotlin/com/attendify/app/
    │       ├── data/          # Data layer with Room database
    │       ├── di/            # Dependency injection (Hilt)
    │       ├── ui/            # UI layer (Jetpack Compose)
    │       └── utils/         # Utility classes
    └── ...
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
- 📅 View session schedules
- 📴 Full offline functionality

### For Instructors
- 📚 Create and manage courses
- 🎫 Generate QR codes for attendance sessions
- 👥 Track student attendance in real-time
- ➕ Enroll students in courses
- 📈 View attendance analytics
- 📊 Session management

### For Admins
- 👤 Manage users and assign roles
- 📖 Full course and session management
- 📊 System-wide analytics and reporting
- 🔧 System administration
- 👥 Comprehensive user management

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

On first launch, the app will create a local database with default users:
- **Admin**: username: `admin`, password: `admin123`
- **Instructor**: username: `instructor`, password: `instructor123`
- **Student**: username: `student`, password: `student123`

See [android/README.md](android/README.md) for complete setup instructions and documentation.

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
- **Language**: Kotlin 1.9.22
- **Build System**: Gradle 8.5
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt 2.50
- **Code Generation**: KSP (Kotlin Symbol Processing)
- **Database**: Room (SQLite)
- **Async**: Kotlin Coroutines + Flow
- **QR Codes**: ZXing for scanning and generation
- **Local Storage**: DataStore for preferences
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Development

### Building the App
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

## Roadmap

### Phase 1 (Complete)
- ✅ Android app scaffold and architecture
- ✅ Room database integration
- ✅ Local authentication
- ✅ Role-based navigation
- ✅ QR code generation and scanning
- ✅ Complete offline functionality

### Phase 2 (Current)
- ✅ Course management
- ✅ Session management
- ✅ Attendance tracking
- ✅ User management
- ✅ Analytics and reporting

### Phase 3 (Future)
- 🔄 Data export/import functionality
- 🔄 Biometric authentication
- 🔄 Advanced analytics dashboards
- 🔄 Multi-language support
- 🔄 Dark mode enhancements
- 🔄 Backup and restore

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

## Screenshots

[Add screenshots when UI is complete]

---

Built with ❤️ for better attendance management
