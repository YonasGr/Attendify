# Attendify Quick Start Guide

Welcome to Attendify! This guide will help you get started quickly with the attendance management system.

## 🚀 Quick Setup (5 Minutes)

### Android App

1. **Clone and Open**
   ```bash
   git clone https://github.com/YonasGr/Attendify.git
   cd Attendify/android
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open Project" → Navigate to `Attendify/android`
   - Wait for Gradle sync to complete

3. **Run the App**
   - Click the Run button (▶️) or press Shift+F10
   - Select your device/emulator
   - App will install and launch automatically

4. **Try Demo Accounts**
   - **Student**: `student` / `student123`
   - **Instructor**: `instructor` / `instructor123`
   - **Admin**: `admin` / `admin123`

## 📱 Using the App

### As a Student

1. **Login**
   - Use: `student` / `student123`
   - First-time users will see the main dashboard

2. **View Courses**
   - Tap "Enrolled Courses" to see your classes
   - View CS101, CS201, and other enrolled courses

3. **Check Attendance**
   - Tap "Attendance History" to see your records
   - View attendance status for all sessions

4. **Scan QR Code**
   - Tap the floating "Scan QR Code" button
   - Point camera at instructor's QR code
   - Attendance marked automatically!

5. **View Upcoming Sessions**
   - Tap "Upcoming Sessions" to see scheduled classes
   - Plan ahead for your courses

6. **Enable Biometric Login**
   - Tap the settings icon (⚙️) in top right
   - Toggle "Biometric Login"
   - Complete fingerprint/face setup
   - Next login will use biometrics!

### As an Instructor

1. **Login**
   - Use: `instructor` / `instructor123`

2. **Create a Course**
   - Tap the floating "New Course" button
   - Fill in course details (code, name, semester, year)
   - Tap "Create Course"

3. **Create a Session**
   - Go to "Sessions" from dashboard
   - Tap the + button
   - Select course, set date/time
   - Tap "Create Session"

4. **Generate QR Code**
   - Go to "Sessions" from dashboard
   - Tap on any session
   - View and display the QR code
   - Students scan this to mark attendance

5. **Enroll Students Manually**
   - Navigate to a course
   - Tap "Enroll Students"
   - Toggle students to add/remove them
   - Perfect for students who missed QR scan!

6. **Mark Attendance Manually**
   - Navigate to a session
   - Tap "Mark Attendance"
   - Check/uncheck students
   - Quick way to handle missed scans

### As an Admin

1. **Login**
   - Use: `admin` / `admin123`

2. **View System Stats**
   - Dashboard shows total users, courses, sessions, attendance
   - Monitor system health at a glance

3. **Manage Users**
   - Tap "User Management"
   - Filter by role (Students, Instructors, Admins)
   - Create new users with the + button

4. **Manage Courses**
   - View and manage all courses in the system
   - Oversee instructor course assignments

## 🔐 Biometric Authentication

### First-Time Setup

1. Login with username/password first
2. Go to Settings (⚙️ icon)
3. Enable "Biometric Login"
4. Authenticate with fingerprint/face
5. Done! Next login uses biometrics

### Using Biometric Login

1. Open app
2. Instead of typing password, tap "Sign In with Biometrics"
3. Authenticate with fingerprint/face
4. Instant login!

## 🎯 Key Features to Try

### QR Code Attendance
- **Instructors**: Generate QR codes for sessions
- **Students**: Scan QR codes to mark attendance
- **Real-time**: Instant attendance recording

### Manual Operations
- **Enrollment**: Add students who couldn't scan QR
- **Attendance**: Mark attendance for missed scans
- **Flexible**: Handle any situation

### Offline-First
- Works completely offline
- No internet required
- All data stored locally
- Future: Cloud sync available

## 📊 Demo Data

The app comes pre-loaded with:
- **12 Students** across CS, Math, and Physics
- **12 Courses** covering multiple semesters
- **3 Instructors** teaching different subjects
- **Multiple Sessions** with attendance records

Explore the data to understand the system!

## 🔍 Troubleshooting

### App Won't Build
- Ensure JDK 17+ is installed
- Check Android SDK is updated
- Run `./gradlew clean` then rebuild

### Camera Not Working
- Grant camera permissions in Settings
- Restart the app after granting permission

### Biometric Not Available
- Check device has fingerprint/face sensor
- Enable biometric auth in device settings
- May not work on emulators

### QR Code Won't Scan
- Ensure good lighting
- Hold camera steady
- QR code should be clear and focused

## 📚 Next Steps

1. **Explore Different Roles**
   - Try logging in as student, instructor, and admin
   - See how each role has different capabilities

2. **Create Your Own Data**
   - Add new courses as instructor
   - Enroll students in courses
   - Create sessions and mark attendance

3. **Test Workflows**
   - Complete attendance flow: Create session → Generate QR → Student scans
   - Try manual enrollment and attendance marking

4. **Customize**
   - Enable biometric login for faster access
   - Explore settings and preferences

## 🌐 Cloud Sync (Optional)

See [server/README.md](../server/README.md) for setting up the REST API server for cloud synchronization.

## 💡 Tips

- **QR Codes**: Generate fresh QR codes for each session
- **Manual Marking**: Use for students with technical issues
- **Reports**: Check attendance history for insights
- **Biometrics**: Enable for quick, secure access

## 🆘 Need Help?

- Check [android/README.md](../android/README.md) for detailed docs
- Review [server/README.md](../server/README.md) for API docs
- Open an issue on GitHub for support

---

**Enjoy using Attendify! 🎓✨**
