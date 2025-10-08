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

- **Language**: Kotlin 1.9.22
- **Build System**: Gradle 8.5
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt 2.50
- **Code Generation**: KSP (Kotlin Symbol Processing)
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

- Android Studio Hedgehog (2023.1.1) or later (Iguana 2023.2.1+ recommended)
- JDK 17 or later
- Gradle 8.5 (included via wrapper)
- Android SDK with API 24 (Android 7.0) minimum
- Target API 34 (Android 14)
- Network access to Google Maven repository (https://dl.google.com) for dependency downloads

> **Note**: If you're building in a restricted network environment, see [BUILD_REQUIREMENTS.md](BUILD_REQUIREMENTS.md) for mirror configuration options.

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

## Customization

### Profile Logo

The app displays a profile logo in the top app bar of all dashboard screens. To customize it:

1. **Prepare Your Logo**:
   - Create a vector drawable (XML) for best scalability - recommended
   - Or use PNG format (48x48dp recommended, but higher resolution is better)
   - Ensure good contrast with the primary color background

2. **Replace the Default Logo**:
   ```
   android/app/src/main/res/drawable/ic_profile_logo.xml
   ```
   - Option A: Replace the XML file with your own vector drawable
   - Option B: Delete the XML and add `ic_profile_logo.png` (or other image format)

3. **Using Android Studio** (Easiest Method):
   - Right-click on `app/src/main/res/drawable`
   - Select **"New → Image Asset"** for PNG/JPG images
   - Or select **"New → Vector Asset"** for SVG files
   - Set the name as `ic_profile_logo`
   - Configure colors and sizing as needed
   - Click "Finish" to generate the asset

4. **Manual XML Vector Drawable** (For Custom Design):
   - Edit `ic_profile_logo.xml` directly
   - Modify the `pathData` to create custom shapes
   - Change `android:fillColor` for custom colors
   - Adjust `android:viewportWidth` and `android:viewportHeight` as needed

The logo appears as a circular icon on the left side of the top app bar across all user roles (Student, Instructor, Admin).

### App Launcher Icon

The app includes a professional launcher icon with a purple background and white "A" letter. To replace it with your own custom icon:

#### Method 1: Using Android Studio (Recommended)

1. **Open Android Studio** and load the project
2. **Right-click** on `app/src/main/res` in the Project view
3. Select **"New → Image Asset"**
4. In the Asset Studio window:
   - **Asset Type**: Choose "Launcher Icons (Adaptive and Legacy)"
   - **Name**: Keep as `ic_launcher`
   - **Foreground Layer**: Upload your icon image or use clipart
   - **Background Layer**: Set a solid color or upload a background image
   - **Trim**: Adjust padding as needed
5. **Preview** your icon across different densities and shapes
6. Click **"Next"** and then **"Finish"**

The Asset Studio automatically generates:
- Standard icons for all densities (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- Adaptive icons for Android 8.0+ (API 26+)
- Round icons for devices that support them
- Preview images showing how it looks on different Android versions

#### Method 2: Manual Replacement

If you have pre-made icons for all densities:

1. **Prepare your icons** with these exact sizes:
   - `mipmap-mdpi/ic_launcher.png`: 48x48 pixels
   - `mipmap-hdpi/ic_launcher.png`: 72x72 pixels
   - `mipmap-xhdpi/ic_launcher.png`: 96x96 pixels
   - `mipmap-xxhdpi/ic_launcher.png`: 144x144 pixels
   - `mipmap-xxxhdpi/ic_launcher.png`: 192x192 pixels

2. **For round icons** (optional but recommended):
   - Create circular versions with the same sizes
   - Name them `ic_launcher_round.png`
   - Place in the same mipmap folders

3. **For adaptive icons** (Android 8.0+):
   - Create foreground layer images (108x108dp safe zone):
     - `mipmap-mdpi/ic_launcher_foreground.png`: 108x108 pixels
     - `mipmap-hdpi/ic_launcher_foreground.png`: 162x162 pixels
     - `mipmap-xhdpi/ic_launcher_foreground.png`: 216x216 pixels
     - `mipmap-xxhdpi/ic_launcher_foreground.png`: 324x324 pixels
     - `mipmap-xxxhdpi/ic_launcher_foreground.png`: 432x432 pixels
   - Edit `res/drawable/ic_launcher_background.xml` to set your background color:
     ```xml
     <?xml version="1.0" encoding="utf-8"?>
     <shape xmlns:android="http://schemas.android.com/apk/res/android"
         android:shape="rectangle">
         <solid android:color="#YourColorHere" />
     </shape>
     ```

4. **Replace the files** in the respective mipmap directories under:
   ```
   android/app/src/main/res/mipmap-*/
   ```

#### Method 3: Using Online Icon Generators

Several free tools can generate all required densities:

1. **Android Asset Studio** (https://romannurik.github.io/AndroidAssetStudio/):
   - Upload your icon image
   - Configure padding, shape, and colors
   - Download generated assets
   - Extract and copy to your project's res folder

2. **App Icon Generator** (https://www.appicon.co/):
   - Upload your icon
   - Select "Android" as platform
   - Download and extract files
   - Copy to the appropriate mipmap directories

#### Important Notes

- **Icon Guidelines**: Follow Material Design icon guidelines for best results
- **Size**: Original artwork should be at least 512x512 pixels
- **Safe Zone**: For adaptive icons, keep important content within the center 66dp circle
- **Testing**: Test on different devices to ensure the icon looks good across various shapes (circle, rounded square, squircle)
- **No Alpha Channel**: Launcher icons should not have transparency in the background layer

The manifest already references the icons as `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`, so no changes to `AndroidManifest.xml` are needed after replacing the icon files.

## Troubleshooting

### Common Issues

**Issue**: "Database locked" or "Database corrupted"
- **Solution**: Clear app data or reinstall the app

**Issue**: "QR Scanner not working"
- **Solution**: Grant camera permissions in app settings

**Issue**: Build fails with "SDK not found"
- **Solution**: Install required SDK versions via Android Studio SDK Manager

**Issue**: "Failed to sync Gradle" or "Could not resolve dependencies"
- **Solution**: 
  - Check internet connection and network access to Google Maven repository
  - Verify JDK 17+ is configured
  - See [BUILD_REQUIREMENTS.md](BUILD_REQUIREMENTS.md) for detailed troubleshooting
  - If behind a firewall, configure repository mirrors

**Issue**: KSP compilation errors
- **Solution**: Clean and rebuild project (`./gradlew clean build`)

## Build Configuration

For detailed information about build configuration, version requirements, and troubleshooting:
- See [BUILD_REQUIREMENTS.md](BUILD_REQUIREMENTS.md) - Comprehensive build guide
- Check current versions: Gradle 8.5, Kotlin 1.9.22, AGP 8.2.2, Hilt 2.50

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
