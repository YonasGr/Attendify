# Attendify Android - Build Configuration Quick Reference

## Current Versions (January 2025)

| Component | Version | Notes |
|-----------|---------|-------|
| Gradle | 8.5 | Via wrapper |
| Android Gradle Plugin (AGP) | 8.2.2 | Minimum for Gradle 8.5 |
| Kotlin | 1.9.22 | Latest stable 1.9.x |
| Kotlin Symbol Processing (KSP) | 1.9.22-1.0.17 | Replaces KAPT |
| Hilt | 2.50 | Latest stable |
| Jetpack Compose BOM | 2024.02.00 | |
| Compose Compiler | 1.5.10 | Must match Kotlin 1.9.22 |
| Room | 2.6.1 | |
| Navigation Compose | 2.7.7 | |
| Min SDK | 24 | Android 7.0 |
| Target SDK | 34 | Android 14 |
| Compile SDK | 34 | Android 14 |
| JDK | 17+ | Required |

## Quick Commands

```bash
# Validate build configuration
./validate-build.sh

# Clean build
./gradlew clean build

# Debug build only
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Install debug APK on connected device
./gradlew installDebug

# Check dependencies
./gradlew dependencies

# Check for outdated dependencies  
./gradlew dependencyUpdates
```

## File Structure

```
android/
├── build.gradle.kts          # Root build file (buildscript config)
├── settings.gradle.kts        # Repository configuration
├── gradle.properties          # Gradle JVM settings
├── gradle/wrapper/
│   ├── gradle-wrapper.jar     # Wrapper executable
│   └── gradle-wrapper.properties  # Wrapper version
├── app/
│   └── build.gradle.kts       # App module configuration
├── BUILD_REQUIREMENTS.md      # Comprehensive build guide
├── README.md                  # Project documentation
└── validate-build.sh          # Build validation script
```

## Key Configuration Points

### Root build.gradle.kts
- Uses `buildscript` block for plugin dependencies
- Declares AGP, Kotlin, and Hilt plugins
- KSP plugin in `plugins` block

### app/build.gradle.kts
- Applies plugins using legacy syntax for compatibility
- Uses KSP instead of KAPT
- Explicit `buildConfig = true`
- Compose compiler version matches Kotlin version

### settings.gradle.kts
- Google Maven repository first
- Maven Central as fallback
- Content filters for faster resolution

## Migration Notes

### KAPT → KSP
Changed all annotation processors to use KSP:
- ✅ Room compiler: `ksp("androidx.room:room-compiler:...")`
- ✅ Hilt compiler: `ksp("com.google.dagger:hilt-compiler:...")`
- ❌ Removed: `kapt { correctErrorTypes = true }`
- ❌ Removed: Room schema arguments from defaultConfig

Benefits:
- Up to 2x faster build times
- Better error messages
- Native Kotlin support

## Network Requirements

**Critical**: Build requires access to Google Maven repository
- URL: https://dl.google.com/dl/android/maven2/
- Alternative: https://maven.google.com/ (redirects to above)

If blocked:
1. Request firewall/network access
2. Use VPN/proxy
3. Configure mirrors (see BUILD_REQUIREMENTS.md)

## Troubleshooting Checklist

- [ ] JDK 17+ installed and configured
- [ ] Gradle wrapper jar present (gradle/wrapper/gradle-wrapper.jar)
- [ ] Network access to dl.google.com
- [ ] Android SDK installed (if using Android Studio)
- [ ] ANDROID_HOME environment variable set (if building from CLI)
- [ ] Gradle daemon not corrupted (try: `./gradlew --stop`)

## Common Errors & Solutions

| Error | Solution |
|-------|----------|
| Could not resolve com.android.tools.build:gradle | Check network access to dl.google.com |
| Unsupported class file major version | Verify JDK 17+ |
| Could not find method kapt() | Update to use ksp() |
| Compose compiler version mismatch | Update kotlinCompilerExtensionVersion |
| Could not create task ':kaptGenerateStubs' | Plugin migration incomplete, verify no kapt references |

## Upgrading

### To AGP 8.5+
1. Update AGP version in build.gradle.kts
2. Ensure Gradle 8.4+ (or keep 8.5)
3. Review AGP 8.5 release notes
4. Test thoroughly

### To Kotlin 1.9.23+
1. Update Kotlin version
2. Update KSP version (format: kotlin-version-ksp-version)
3. Update Compose compiler extension
4. Test all annotation processors

### To Hilt 2.51+
1. Update Hilt version in buildscript dependencies
2. Update Hilt runtime dependency
3. Verify compatibility with current Kotlin version
4. Clean and rebuild

## CI/CD Integration

### GitHub Actions Example
```yaml
- uses: actions/setup-java@v3
  with:
    distribution: 'temurin'
    java-version: '17'
    
- uses: gradle/gradle-build-action@v2
  with:
    gradle-version: 8.5
    
- run: |
    cd android
    chmod +x gradlew
    ./gradlew assembleDebug --no-daemon
```

### Environment Variables
```bash
export JAVA_HOME=/path/to/jdk17
export ANDROID_HOME=/path/to/android-sdk
export GRADLE_OPTS="-Xmx2048m -Dfile.encoding=UTF-8"
```

## Build Performance Tips

1. **Enable Gradle Daemon** (default)
   ```properties
   # gradle.properties
   org.gradle.daemon=true
   ```

2. **Enable Parallel Builds** (already enabled)
   ```properties
   org.gradle.parallel=true
   ```

3. **Enable Build Cache** (already enabled)
   ```properties
   org.gradle.caching=true
   ```

4. **Increase Heap Size** (already configured)
   ```properties
   org.gradle.jvmargs=-Xmx2048m
   ```

5. **Use KSP instead of KAPT** ✅ (already done)

6. **Enable Configuration Cache** (Gradle 8.5+)
   ```bash
   ./gradlew build --configuration-cache
   ```

## Resources

- [BUILD_REQUIREMENTS.md](BUILD_REQUIREMENTS.md) - Comprehensive guide
- [Android AGP Releases](https://developer.android.com/build/releases/gradle-plugin)
- [Kotlin Releases](https://kotlinlang.org/docs/releases.html)
- [KSP Documentation](https://kotlinlang.org/docs/ksp-overview.html)
- [Compose-Kotlin Compatibility](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)

---

Last updated: January 2025  
Build config version: 1.0
