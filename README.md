# Attendify

A fully offline native Android application for university attendance tracking with a modern, professional UI.

## 🎨 Recent Updates

**Major UI/UX Overhaul (January 2025)**
- ✨ Modern, professional color scheme (Indigo, Emerald, Violet)
- 🎯 Enhanced dashboards with welcome cards and statistics
- 📊 Rich demo content with 8 students, 3 instructors, 8 courses
- 🚀 Create Session screen with date picker and validation
- 💎 Improved cards with elevation, shadows, and better spacing
- 🎨 Semantic colors for status and roles
- 📱 Material 3 design throughout

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

On first launch, the app will create a local database with demo users and content:

**Demo Accounts:**
- **Admin**: username: `admin`, password: `admin123`
- **Instructors**:
  - John Smith (CS): `instructor` / `instructor123`
  - Sarah Williams (Math): `dr.williams` / `instructor123`
  - Robert Anderson (Physics): `prof.anderson` / `instructor123`
- **Students**:
  - Alice Johnson (CS): `student` / `student123`
  - Bob Miller (CS): `bob.miller` / `student123`
  - Carol Davis (Math): `carol.davis` / `student123`
  - David Brown (Physics): `david.brown` / `student123`
  - Emma Wilson (CS): `emma.wilson` / `student123`
  - Frank Taylor (Math): `frank.taylor` / `student123`
  - Grace Lee (Physics): `grace.lee` / `student123`
  - Henry Martin (CS): `henry.martin` / `student123`

**Demo Content:**
- 8 courses across Computer Science, Mathematics, and Physics departments
- 12 scheduled sessions with QR codes for attendance
- Sample attendance records showing realistic usage
- Student enrollments across multiple courses

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
- **Language**: Kotlin 1.9.23
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
