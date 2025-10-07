# Quick Start Guide - Attendify Android Development

This guide will help you get the Attendify Android app running on your development machine in minutes.

## Prerequisites Checklist

- [ ] Android Studio Hedgehog (2023.1.1) or later installed
- [ ] JDK 17 or later configured
- [ ] Android device or emulator available
- [ ] Backend API running and accessible
- [ ] Git installed

## 5-Minute Setup

### Step 1: Clone and Open (1 min)

```bash
# Clone the repository
git clone https://github.com/YonasGr/Attendify.git
cd Attendify/android

# Open in Android Studio
# File → Open → Select 'android' directory
```

### Step 2: Configure API URL (1 min)

Edit `app/build.gradle.kts` and update the API base URL:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://your-backend-url.com/api/\"")
```

**Common configurations:**
- **Local development**: `"http://10.0.2.2:5000/api/"` (emulator)
- **Local network**: `"http://192.168.x.x:5000/api/"` (real device)
- **Replit**: `"https://your-repl.replit.app/api/"`

### Step 3: Sync Gradle (1 min)

1. Click "Sync Project with Gradle Files" in Android Studio
2. Wait for dependencies to download
3. Fix any SDK version issues if prompted

### Step 4: Run the App (2 min)

1. Connect a device or start an emulator
2. Click the green "Run" button (or press Shift + F10)
3. Wait for build and installation

## First Login

### Option 1: Get Session Token from Web App

1. Open the web app in a browser
2. Login with your credentials
3. Open Developer Tools (F12)
4. Go to Application → Cookies
5. Find the session cookie (usually named "connect.sid")
6. Copy the entire cookie value
7. Paste it in the Android app login screen

### Option 2: Mock Authentication (Development)

For testing without a backend, you can temporarily modify the `LoginViewModel`:

```kotlin
// In LoginViewModel.kt, modify the login function for testing:
fun login(sessionToken: String) {
    viewModelScope.launch {
        _loginState.value = Resource.Loading()
        
        // Mock user for testing
        val mockUser = User(
            id = "test-user-id",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            role = "student", // Change to "instructor" or "admin" to test other roles
            profileImageUrl = null,
            studentId = "S12345",
            department = "Computer Science",
            createdAt = null,
            updatedAt = null
        )
        
        authRepository.saveAuthToken(sessionToken)
        authRepository.saveUser(mockUser)
        _loginState.value = Resource.Success(mockUser)
        _authState.value = AuthState(
            isAuthenticated = true,
            isLoading = false,
            user = mockUser
        )
    }
}
```

## Troubleshooting

### "Cannot connect to API"
- Check API_BASE_URL is correct
- Ensure backend is running
- For emulator, use `10.0.2.2` instead of `localhost`
- Check device/emulator has internet access

### "SDK not found"
1. Open Tools → SDK Manager
2. Install required SDK versions:
   - Android SDK Platform 34
   - Android SDK Build-Tools
   - Android SDK Platform-Tools

### Build errors
```bash
# Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies
```

### Gradle sync failed
1. File → Invalidate Caches → Invalidate and Restart
2. Check internet connection
3. Update Android Studio to latest version

### App crashes on startup
1. Check logcat for error messages
2. Verify all permissions in AndroidManifest
3. Ensure Hilt is properly configured

## Development Workflow

### Testing Changes

1. Make code changes
2. Use "Run" (Shift + F10) for quick testing
3. Use "Debug" for breakpoints and inspection
4. Check Logcat for debugging output

### Hot Reload

For Compose UI changes:
1. Enable "Live Edit" in Android Studio
2. Changes apply immediately without rebuild
3. Speeds up UI development significantly

### Viewing Logs

1. Open Logcat at bottom of Android Studio
2. Filter by package: `com.attendify.app`
3. Look for custom tags:
   - "AttendifyApi" for network calls
   - "Auth" for authentication
   - Error logs will show in red

## Project Structure Quick Reference

```
Key Files to Know:
├── app/build.gradle.kts          → Dependencies & config
├── AndroidManifest.xml           → Permissions & components
├── data/
│   ├── api/AttendifyApiService   → API endpoints
│   ├── model/Models              → Data classes
│   └── repository/               → Data access layer
├── ui/
│   ├── auth/LoginScreen          → Login UI
│   ├── student/                  → Student features
│   ├── instructor/               → Instructor features
│   └── admin/                    → Admin features
└── di/AppModule                  → Dependency injection
```

## Common Tasks

### Add a New Screen

1. Create Composable in appropriate package:
   ```kotlin
   @Composable
   fun NewScreen() {
       // UI code
   }
   ```

2. Add to Navigation.kt:
   ```kotlin
   object NewScreen : Screen("new_screen")
   ```

3. Add route in AttendifyApp.kt:
   ```kotlin
   composable(Screen.NewScreen.route) {
       NewScreen()
   }
   ```

### Add New API Endpoint

1. Add to `AttendifyApiService`:
   ```kotlin
   @GET("new/endpoint")
   suspend fun getNewData(): Response<NewData>
   ```

2. Add repository method:
   ```kotlin
   fun getNewData(): Flow<Resource<NewData>> = flow {
       emit(Resource.Loading())
       try {
           val response = api.getNewData()
           emit(handleResponse(response))
       } catch (e: Exception) {
           emit(Resource.Error(e.message ?: "Failed"))
       }
   }
   ```

3. Use in ViewModel:
   ```kotlin
   fun loadData() {
       viewModelScope.launch {
           repository.getNewData().collect { result ->
               // Handle result
           }
       }
   }
   ```

### Test Different User Roles

Change the mock user role in LoginViewModel:
- `role = "student"` → Student Dashboard
- `role = "instructor"` → Instructor Dashboard  
- `role = "admin"` → Admin Dashboard

## Next Steps

Once you have the app running:

1. **Explore the UI**
   - Test all three role dashboards
   - Navigate between screens
   - Test logout functionality

2. **Connect to Real API**
   - Update API_BASE_URL
   - Get a real session token
   - Test actual data loading

3. **Start Development**
   - Pick a feature from the roadmap
   - Follow the architecture patterns
   - Refer to IMPLEMENTATION_SUMMARY.md

4. **Read Documentation**
   - Check android/README.md for details
   - Review backend API in server/routes.ts
   - Study existing code patterns

## Getting Help

- **Android Issues**: Check [Android Developer Documentation](https://developer.android.com/)
- **Compose Questions**: See [Compose Documentation](https://developer.android.com/jetpack/compose)
- **Project Issues**: Open GitHub issue
- **Architecture Questions**: Review IMPLEMENTATION_SUMMARY.md

## Useful Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Check for dependency updates
./gradlew dependencyUpdates

# Clean build
./gradlew clean

# Generate release APK
./gradlew assembleRelease
```

## IDE Tips

- **Quick Fix**: Alt + Enter
- **Search Everywhere**: Double Shift
- **Recent Files**: Ctrl/Cmd + E
- **Find Usages**: Alt + F7
- **Reformat Code**: Ctrl/Cmd + Alt + L
- **Optimize Imports**: Ctrl/Cmd + Alt + O

Happy coding! 🚀
