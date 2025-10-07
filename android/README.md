# Attendify Android App

A native Android application for the Attendify university attendance tracking system. Built with Kotlin, Jetpack Compose, and modern Android architecture components.

## Features

### Student Features
- 📱 View enrolled courses
- 📷 Scan QR codes to mark attendance
- 📊 View attendance history for all courses
- 📅 See upcoming sessions

### Instructor Features
- 📚 Manage courses and sessions
- 🎫 Generate QR codes for attendance sessions
- 👥 Track student attendance in real-time
- ➕ Enroll students in courses

### Admin Features
- 👤 Manage users and assign roles
- 📖 Course management
- 📈 View analytics and attendance statistics
- 🔧 System administration

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Async Operations**: Kotlin Coroutines + Flow
- **QR Code Scanning**: ZXing
- **Local Storage**: DataStore (for authentication)

## Project Structure

```
app/src/main/kotlin/com/attendify/app/
├── data/
│   ├── api/              # Retrofit API service
│   ├── model/            # Data models
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

3. **Configure API Base URL**
   
   Update the API base URL in `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "API_BASE_URL", "\"https://your-backend-url.com/api/\"")
   ```
   
   Replace `your-backend-url.com` with your actual backend URL.

4. **Sync Gradle**
   - Click "Sync Project with Gradle Files" in Android Studio
   - Wait for dependencies to download

5. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift + F10

## Configuration

### Backend Integration

The app connects to the Attendify Express.js backend. Ensure your backend is running and accessible from your device/emulator.

**For development with local backend:**
- If using an emulator, use `10.0.2.2` instead of `localhost`
- Example: `http://10.0.2.2:5000/api/`

**For Replit deployments:**
- Use the Replit URL: `https://your-repl.replit.app/api/`

### Authentication

The current implementation uses session token authentication:

1. Log in to the web app
2. Open browser developer tools (F12)
3. Navigate to Application → Cookies
4. Copy the session cookie value
5. Paste it in the Android app login screen

**Future Enhancement**: Implement proper OAuth flow with Replit Auth or another provider.

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
               // ... other settings
           }
       }
   }
   ```

3. **Build release APK**
   ```bash
   ./gradlew assembleRelease
   ```
   
   Output: `app/build/outputs/apk/release/app-release.apk`

4. **Build Android App Bundle** (recommended for Play Store)
   ```bash
   ./gradlew bundleRelease
   ```
   
   Output: `app/build/outputs/bundle/release/app-release.aab`

## Development Guidelines

### Adding New Features

1. **Create data models** in `data/model/`
2. **Add API endpoints** to `AttendifyApiService`
3. **Update repository** methods in `AttendifyRepository`
4. **Create ViewModel** for the feature
5. **Build UI** with Composables
6. **Add navigation** routes if needed

### Code Style

- Follow Kotlin coding conventions
- Use Jetpack Compose best practices
- Implement proper error handling
- Write meaningful comments for complex logic
- Use `Resource<T>` for API responses

### Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## Troubleshooting

### Common Issues

**Issue**: "Unable to connect to API"
- **Solution**: Check API_BASE_URL configuration and network connectivity

**Issue**: "Authentication failed"
- **Solution**: Ensure session token is valid and not expired

**Issue**: "QR Scanner not working"
- **Solution**: Grant camera permissions in app settings

**Issue**: Build fails with "SDK not found"
- **Solution**: Install required SDK versions via Android Studio SDK Manager

## API Documentation

The Android app integrates with these backend endpoints:

### Authentication
- `GET /api/auth/user` - Get current user

### Users
- `GET /api/users` - Get all users (admin)
- `GET /api/users/students` - Get all students
- `GET /api/users/instructors` - Get all instructors
- `PATCH /api/users/{id}` - Update user

### Courses
- `GET /api/courses` - Get all courses
- `GET /api/courses/{id}` - Get course by ID
- `POST /api/courses` - Create course
- `GET /api/courses/instructor/{instructorId}` - Get instructor's courses

### Enrollments
- `GET /api/enrollments/course/{courseId}` - Get course enrollments
- `GET /api/enrollments/student/{studentId}` - Get student enrollments
- `POST /api/enrollments` - Create enrollment
- `DELETE /api/enrollments/{id}` - Delete enrollment

### Sessions
- `GET /api/sessions/course/{courseId}` - Get course sessions
- `POST /api/sessions` - Create session
- `GET /api/sessions/{id}/qrcode` - Get session QR code
- `PATCH /api/sessions/{id}` - Update session

### Attendance
- `POST /api/attendance/checkin` - Check in with QR code
- `GET /api/attendance/session/{sessionId}` - Get session attendance
- `GET /api/attendance/student/{studentId}` - Get student attendance
- `GET /api/attendance/course/{courseId}/student/{studentId}` - Get attendance for student in course

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

### Phase 1 (Current - MVP)
- [x] Basic authentication with session tokens
- [x] Role-based navigation (Student, Instructor, Admin)
- [x] Dashboard layouts for all roles
- [x] API integration foundation
- [ ] QR code scanning implementation
- [ ] Course list display
- [ ] Attendance history view

### Phase 2
- [ ] Full OAuth integration
- [ ] Course details screens
- [ ] Session management UI
- [ ] Real-time attendance updates
- [ ] QR code generation for instructors
- [ ] Enhanced error handling

### Phase 3
- [ ] Offline support with local database
- [ ] Push notifications for sessions
- [ ] Biometric authentication
- [ ] Advanced analytics dashboard
- [ ] Export attendance reports
- [ ] Dark mode toggle

### Phase 4
- [ ] Multi-language support
- [ ] Accessibility improvements
- [ ] Performance optimizations
- [ ] Comprehensive testing coverage
- [ ] Play Store release
