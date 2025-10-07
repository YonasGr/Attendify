# Attendify - Current State

## Project Type
**Fully Offline Native Android Application**

## Repository Structure

```
Attendify/
├── android/                    # Native Android application
│   ├── app/
│   │   ├── src/main/kotlin/com/attendify/app/
│   │   │   ├── data/
│   │   │   │   ├── local/     # Room database
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   ├── UserEntity.kt
│   │   │   │   │   │   ├── CourseEntity.kt
│   │   │   │   │   │   ├── SessionEntity.kt
│   │   │   │   │   │   ├── EnrollmentEntity.kt
│   │   │   │   │   │   └── AttendanceRecordEntity.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── UserDao.kt
│   │   │   │   │   │   ├── CourseDao.kt
│   │   │   │   │   │   ├── SessionDao.kt
│   │   │   │   │   │   ├── EnrollmentDao.kt
│   │   │   │   │   │   └── AttendanceRecordDao.kt
│   │   │   │   │   ├── AttendifyDatabase.kt
│   │   │   │   │   └── DatabaseSeeder.kt
│   │   │   │   ├── model/     # UI models
│   │   │   │   │   ├── Models.kt
│   │   │   │   │   └── Mappers.kt
│   │   │   │   └── repository/
│   │   │   │       ├── AuthRepository.kt
│   │   │   │       ├── UserRepository.kt
│   │   │   │       ├── CourseRepository.kt
│   │   │   │       ├── SessionRepository.kt
│   │   │   │       ├── EnrollmentRepository.kt
│   │   │   │       └── AttendanceRepository.kt
│   │   │   ├── di/
│   │   │   │   └── AppModule.kt
│   │   │   ├── ui/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   └── LoginViewModel.kt
│   │   │   │   ├── student/
│   │   │   │   │   └── StudentDashboardScreen.kt
│   │   │   │   ├── instructor/
│   │   │   │   │   └── InstructorDashboardScreen.kt
│   │   │   │   ├── admin/
│   │   │   │   │   └── AdminDashboardScreen.kt
│   │   │   │   ├── theme/
│   │   │   │   ├── AttendifyApp.kt
│   │   │   │   └── Navigation.kt
│   │   │   ├── utils/
│   │   │   ├── AttendifyApplication.kt
│   │   │   └── MainActivity.kt
│   │   └── build.gradle.kts
│   ├── README.md
│   ├── ARCHITECTURE.md
│   ├── IMPLEMENTATION_SUMMARY.md
│   └── FEATURE_GUIDE.md
├── README.md
└── CONVERSION_SUMMARY.md
```

## Technology Stack

### Language & Framework
- **Kotlin** 1.9.20
- **Jetpack Compose** (Material 3)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **DI**: Hilt 2.48
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Jetpack Navigation Compose

### Data & Storage
- **Database**: Room 2.6.1 (SQLite)
- **Preferences**: DataStore
- **Local-only**: No network connectivity

### Features
- **QR Codes**: ZXing (scanning and generation)
- **Images**: Coil
- **UI**: Material 3 components

## Key Features

### User Roles
1. **Student**
   - View enrolled courses
   - Scan QR codes for attendance
   - View attendance history
   - See session schedules

2. **Instructor**
   - Manage courses
   - Create and manage sessions
   - Generate QR codes
   - Track student attendance
   - Enroll students

3. **Admin**
   - Manage users
   - System-wide course management
   - Analytics and reporting
   - Full system access

### Data Models

#### User
- Credentials: username, password
- Profile: firstName, lastName, email
- Role: student, instructor, admin
- Additional: studentId, department

#### Course
- Basic info: code, name, description
- Semester and year tracking
- Instructor assignment

#### Session
- Linked to course
- Schedule: date, start/end time
- QR code for attendance
- Active/inactive status

#### Enrollment
- Student-course relationship
- Enrollment tracking

#### Attendance Record
- Session check-in
- Student identification
- Timestamp
- Status: present, late, absent

## Default Data

### Users (Seeded on First Launch)
```
Admin:
  Username: admin
  Password: admin123
  Role: admin

Instructor:
  Username: instructor
  Password: instructor123
  Role: instructor
  Department: Computer Science

Student:
  Username: student
  Password: student123
  Role: student
  Student ID: STU001
  Department: Computer Science
```

### Sample Courses
1. CS101 - Introduction to Programming
2. CS201 - Data Structures and Algorithms

### Sample Sessions
- 3 sessions created with QR codes
- Mix of active and inactive sessions
- Scheduled for upcoming dates

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with API 24+

### Installation
1. Clone the repository
2. Open `android/` in Android Studio
3. Sync Gradle
4. Run on device/emulator

### First Launch
1. App creates local database
2. Seeds default users and sample data
3. Login with default credentials
4. Explore role-based dashboards

## Development Status

### Completed ✅
- [x] Room database setup
- [x] All entities and DAOs
- [x] Repository layer
- [x] Local authentication
- [x] Database seeding
- [x] Login UI
- [x] Dashboard scaffolds
- [x] Navigation system
- [x] Dependency injection
- [x] Documentation

### In Progress 🔄
- [ ] QR code scanning implementation
- [ ] QR code generation UI
- [ ] Course management screens
- [ ] Session management screens
- [ ] Attendance marking flow
- [ ] ViewModels for dashboards

### Planned 📋
- [ ] Analytics dashboards
- [ ] User management UI
- [ ] Report generation
- [ ] Data export/import
- [ ] Biometric auth
- [ ] Dark mode

## Building

### Debug Build
```bash
cd android
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Run Tests
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Important Notes

### Security
⚠️ **Passwords are currently stored in plain text**
- For production: implement BCrypt or Argon2 hashing
- Consider adding encryption for sensitive data

### Data Persistence
- All data stored locally on device
- No cloud backup by default
- Users should manually backup important data

### Offline-First
- No internet connection required
- All features work offline
- No API calls or network requests

### Scalability
- Data limited to device storage
- No cross-device synchronization
- Each installation is independent

## Documentation

- **README.md** - Project overview
- **android/README.md** - Android setup guide
- **android/ARCHITECTURE.md** - Architecture details
- **android/IMPLEMENTATION_SUMMARY.md** - Implementation status
- **CONVERSION_SUMMARY.md** - Web-to-Android conversion details

## Support

For issues, questions, or contributions:
- Open an issue on GitHub
- Check documentation in android/ directory
- Contact development team

## License

See LICENSE file in repository root.

---

**Last Updated**: January 2025  
**Version**: 1.0.0 (Offline Android App)  
**Status**: Development - Core functionality complete, UI features in progress
