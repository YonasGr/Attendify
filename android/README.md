# Attendify Android App

A fully offline native Android application for university attendance tracking. Built with Kotlin, Jetpack Compose, Room database, and modern Android architecture components.

## Features

### Student Features
- 📱 View enrolled courses
- 📷 Scan QR codes to mark attendance
- 📊 View attendance history for all courses
- 📅 See upcoming sessions
- 📴 **Full offline functionality**

### Instructor Features
- 📚 Manage courses and sessions
- 🎫 Generate QR codes for attendance sessions
- 👥 Track student attendance in real-time
- ➕ Enroll students in courses
- 📊 View course statistics

### Admin Features
- 👤 Manage users and assign roles
- 📖 Course management
- 📈 View analytics and attendance statistics
- 🔧 System administration
- 📊 Comprehensive reporting

## Tech Stack

- **Language**: Kotlin 1.9.20
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Local Database**: Room (SQLite)
- **Async Operations**: Kotlin Coroutines + Flow
- **QR Code Scanning**: ZXing
- **QR Code Generation**: ZXing
- **Local Storage**: DataStore (for authentication)

## Project Structure

```
app/src/main/kotlin/com/attendify/app/
├── data/
│   ├── local/            # Room database
│   │   ├── entity/       # Room entities
│   │   ├── dao/          # Data Access Objects
│   │   ├── AttendifyDatabase.kt
│   │   └── DatabaseSeeder.kt
│   ├── model/            # UI models
│   └── repository/       # Repository layer
├── di/                   # Dependency injection modules
├── ui/
│   ├── auth/            # Login screen & ViewModel
│   ├── student/         # Student dashboard
│   ├── instructor/      # Instructor dashboard
│   ├── admin/           # Admin dashboard
│   ├── components/      # Reusable UI components
│   └── theme/           # App theme and styling
├── utils/               # Utility classes
├── AttendifyApplication.kt
└── MainActivity.kt
```

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with API 24 (Android 7.0) minimum
- Target API 34 (Android 14)

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

## First Time Setup

On first launch, the app automatically creates a local database and seeds it with default users:

### Default Accounts

- **Admin**
  - Username: `admin`
  - Password: `admin123`
  - Full system access

- **Instructor**
  - Username: `instructor`
  - Password: `instructor123`
  - Can create courses, sessions, and manage attendance

- **Student**
  - Username: `student`
  - Password: `student123`
  - Can view courses and mark attendance

### Sample Data

The app also creates:
- 2 sample courses (CS101, CS201)
- 3 sample sessions
- Sample enrollments for the student account

## Authentication

The app uses local username/password authentication stored in Room database.

**⚠️ Security Note**: For production use, implement proper password hashing (e.g., BCrypt, Argon2) instead of plain text storage.

## Building for Production

### Create a release build

1. **Generate signing key** (first time only)
   ```bash
   keytool -genkey -v -keystore attendify-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias attendify
   ```

2. **Configure signing** in `app/build.gradle.kts`:
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("path/to/attendify-release-key.jks")
               storePassword = "your_password"
               keyAlias = "attendify"
               keyPassword = "your_password"
           }
       }
       buildTypes {
           release {
               signingConfig = signingConfigs.getByName("release")
               isMinifyEnabled = true
               proguardFiles(
                   getDefaultProguardFile("proguard-android-optimize.txt"),
                   "proguard-rules.pro"
               )
           }
       }
   }
   ```

3. **Build release APK**
   ```bash
   ./gradlew assembleRelease
   ```
   
   The APK will be generated in `app/build/outputs/apk/release/`

4. **Build release App Bundle (for Play Store)**
   ```bash
   ./gradlew bundleRelease
   ```
   
   The AAB will be generated in `app/build/outputs/bundle/release/`

## Data Management

### Local Database

All data is stored locally using Room (SQLite):

- **Users**: Local user accounts with roles
- **Courses**: Course information
- **Sessions**: Class sessions with QR codes
- **Enrollments**: Student-course relationships
- **Attendance Records**: Check-in records

### Database Location

The database file is located at:
```
/data/data/com.attendify.app/databases/attendify_database
```

### Backup and Export

To backup the database:
```bash
adb pull /data/data/com.attendify.app/databases/attendify_database backup.db
```

To restore:
```bash
adb push backup.db /data/data/com.attendify.app/databases/attendify_database
```

### Clearing Data

To reset the app and database:
- Settings → Apps → Attendify → Storage → Clear Data
- Or reinstall the app

## Development Guidelines

### Adding New Features

1. **Create Entity** (if new data type)
   - Add to `data/local/entity/`
   - Define table structure with `@Entity`
   
2. **Create DAO**
   - Add to `data/local/dao/`
   - Define database operations
   
3. **Update Database**
   - Add entity to `AttendifyDatabase`
   - Increment database version if needed
   
4. **Create Repository**
   - Add to `data/repository/`
   - Implement business logic
   
5. **Create ViewModel**
   - Handle UI state
   - Call repository methods
   
6. **Create UI**
   - Jetpack Compose screens
   - Observe ViewModel state

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable names
- Add KDoc comments for public APIs
- Keep functions small and focused
- Use data classes for models

### Testing

Run tests:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Troubleshooting

### Common Issues

**Issue**: "Database locked" or "Database corrupted"
- **Solution**: Clear app data or reinstall the app

**Issue**: "QR Scanner not working"
- **Solution**: Grant camera permissions in app settings

**Issue**: Build fails with "SDK not found"
- **Solution**: Install required SDK versions via Android Studio SDK Manager

**Issue**: "Failed to sync Gradle"
- **Solution**: Check internet connection and Gradle version compatibility

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is part of the Attendify system. See the main repository for license information.

## Support

For issues and questions:
- Open an issue on GitHub
- Contact the development team
- Check the main Attendify documentation

## Roadmap

### Phase 1 (Complete)
- [x] Local database with Room
- [x] Local authentication with username/password
- [x] Role-based navigation (Student, Instructor, Admin)
- [x] Dashboard layouts for all roles
- [x] Database seeding with default users
- [x] Complete offline functionality

### Phase 2 (Current)
- [ ] QR code scanning implementation
- [ ] QR code generation for sessions
- [ ] Course management UI
- [ ] Session management UI
- [ ] Enrollment management
- [ ] Attendance marking via QR scan

### Phase 3 (Planned)
- [ ] Analytics dashboards
- [ ] Attendance reports and export
- [ ] User management UI for admins
- [ ] Course statistics and insights
- [ ] Enhanced error handling

### Phase 4 (Future)
- [ ] Data export/import functionality
- [ ] Biometric authentication
- [ ] Push notifications for sessions
- [ ] Dark mode toggle
- [ ] Multi-language support
- [ ] Accessibility improvements
- [ ] Play Store release
