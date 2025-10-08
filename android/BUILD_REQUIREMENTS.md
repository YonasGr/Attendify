# Build Requirements and Configuration Guide

## Overview

This document outlines the build configuration, tooling requirements, and known issues for the Attendify Android project as of January 2025.

## Required Tooling Versions

### Build Tools
- **Gradle**: 8.5
- **Android Gradle Plugin (AGP)**: 8.2.2+
- **Kotlin**: 1.9.22
- **Java**: JDK 17 or later

### Key Dependencies
- **Hilt (Dependency Injection)**: 2.50
- **KSP (Kotlin Symbol Processing)**: 1.9.22-1.0.17
- **Jetpack Compose BOM**: 2024.02.00
- **Room Database**: 2.6.1

## Build Configuration

### Gradle Wrapper

The project uses Gradle 8.5. The wrapper is configured in `gradle/wrapper/gradle-wrapper.properties`:

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

### Root build.gradle.kts

The root build file uses the `buildscript` block for plugin dependencies due to Kotlin DSL compatibility:

```kotlin
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
    }
}

plugins {
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}
```

### Module build.gradle.kts

The app module applies plugins using the legacy syntax for compatibility:

```kotlin
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}
```

## Migration from KAPT to KSP

### Why KSP?

The project has been migrated from KAPT (Kotlin Annotation Processing Tool) to KSP (Kotlin Symbol Processing) for better build performance:

- **KAPT**: Slower compilation, generates Java stubs
- **KSP**: Up to 2x faster, native Kotlin support, better for large projects

### Changes Made

1. **Plugin Declaration**: Replaced `kotlin("kapt")` with `id("com.google.devtools.ksp")`

2. **Dependency Configuration**: Changed `kapt()` calls to `ksp()`:
   ```kotlin
   // Old (KAPT)
   kapt("androidx.room:room-compiler:2.6.1")
   kapt("com.google.dagger:hilt-compiler:2.50")
   
   // New (KSP)
   ksp("androidx.room:room-compiler:2.6.1")
   ksp("com.google.dagger:hilt-compiler:2.50")
   ```

3. **Room Schema Location**: Removed from `defaultConfig` (KSP handles this differently if needed)

4. **KAPT correctErrorTypes**: No longer needed with KSP

## Kotlin DSL Conventions

### BuildConfig Feature

Enable BuildConfig explicitly in `android` block:

```kotlin
buildFeatures {
    compose = true
    buildConfig = true  // Required for BuildConfig.API_BASE_URL
}
```

### Compose Compiler Version

Ensure Kotlin compiler extension matches Kotlin version:

```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.10"  // Compatible with Kotlin 1.9.22
}
```

## Network Requirements

### Google Maven Repository

The Android Gradle Plugin and many Android dependencies are hosted on Google's Maven repository:
- Primary URL: `https://dl.google.com/dl/android/maven2/`
- Mirror: `https://maven.google.com/` (redirects to primary)

**Important**: Some network environments may block access to `dl.google.com`. If you encounter build failures with "Could not resolve" errors for Android dependencies, ensure your network allows access to Google's Maven repository.

### Alternative Mirrors (For Restricted Environments)

If Google Maven is blocked, consider using mirror repositories:

```kotlin
buildscript {
    repositories {
        // Aliyun Mirror (China)
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        
        // Fallbacks
        google()
        mavenCentral()
    }
}
```

**Note**: Mirror availability and completeness may vary. Always verify artifacts are up-to-date.

## Building the Project

### Clean Build

```bash
cd android
./gradlew clean build
```

### Debug Build

```bash
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

## Troubleshooting

### Issue: "Could not resolve com.android.tools.build:gradle"

**Cause**: Network cannot access Google Maven repository.

**Solutions**:
1. Verify network access to `https://dl.google.com/`
2. Check corporate firewall/proxy settings
3. Use a VPN if in a restricted region
4. Configure mirror repositories (see Alternative Mirrors section)

### Issue: "Unsupported class file major version"

**Cause**: JDK version mismatch.

**Solution**: Ensure you're using JDK 17 or later:
```bash
java -version  # Should show version 17+
```

### Issue: "Kotlin version mismatch"

**Cause**: Compose compiler version incompatible with Kotlin version.

**Solution**: Update `kotlinCompilerExtensionVersion` in `composeOptions` to match your Kotlin version. For Kotlin 1.9.22, use 1.5.10.

### Issue: KSP compilation errors

**Cause**: Generated code not being recognized.

**Solution**:
1. Clean and rebuild: `./gradlew clean build`
2. Invalidate caches in Android Studio: File → Invalidate Caches / Restart
3. Ensure KSP plugin version matches Kotlin version (1.9.22-1.0.17)

## Version Update Guidelines

### Updating AGP

When updating Android Gradle Plugin:
1. Check compatibility matrix: https://developer.android.com/build/releases/gradle-plugin#updating-gradle
2. Update Gradle wrapper if needed
3. Update Kotlin version if required
4. Update Compose compiler extension version

### Updating Kotlin

When updating Kotlin:
1. Update in root `build.gradle.kts`
2. Update KSP plugin version (format: `{kotlin-version}-{ksp-version}`)
3. Update Compose compiler extension version
4. Check Hilt compatibility

### Updating Hilt

When updating Hilt:
1. Ensure compatibility with current Kotlin version
2. Update both the Hilt Android Gradle Plugin and runtime dependency
3. Test dependency injection thoroughly

## Android Studio Configuration

### Recommended Version
- Android Studio Hedgehog (2023.1.1) or later
- Android Studio Iguana (2023.2.1) recommended for best Kotlin 1.9.22 support

### SDK Requirements
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)
- Compile SDK: API 34

### Gradle JVM
Set Gradle JVM to JDK 17 in Android Studio:
1. File → Settings → Build, Execution, Deployment → Build Tools → Gradle
2. Set "Gradle JDK" to "17" or "Embedded JDK 17"

## CI/CD Considerations

### GitHub Actions

If building on GitHub Actions or similar CI:

```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v3
  with:
    distribution: 'temurin'
    java-version: '17'
    
- name: Setup Gradle
  uses: gradle/gradle-build-action@v2
  with:
    gradle-version: 8.5
    
- name: Build with Gradle
  run: |
    cd android
    chmod +x gradlew
    ./gradlew assembleDebug --no-daemon
```

### Docker Builds

For Docker-based builds, ensure the base image includes:
- JDK 17+
- Android SDK with platform-tools and build-tools
- Network access to Google Maven

## Future Upgrades

### To Android Gradle Plugin 8.5+

When upgrading to AGP 8.5.0 or later:
1. Upgrade Gradle to 8.4 minimum (8.5+ recommended)
2. Review AGP 8.5 release notes for breaking changes
3. Test build configuration changes
4. Update this documentation

### To Kotlin 2.0+

Kotlin 2.0 introduces a new K2 compiler:
1. Review compatibility with Compose
2. Check Hilt and KSP compatibility
3. Update Compose compiler extension version
4. Thoroughly test annotation processing

## Resources

- [Android Gradle Plugin Release Notes](https://developer.android.com/build/releases/gradle-plugin)
- [Kotlin Releases](https://kotlinlang.org/docs/releases.html)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [KSP Documentation](https://kotlinlang.org/docs/ksp-overview.html)
- [Jetpack Compose Kotlin Compatibility](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)

## Support

For build issues:
1. Check this document first
2. Review the project's GitHub Issues
3. Consult the troubleshooting section in `/android/README.md`
4. Open a new issue with:
   - Your build environment details
   - Full error log
   - Steps to reproduce

---

Last Updated: January 2025  
Gradle Version: 8.5  
AGP Version: 8.2.2  
Kotlin Version: 1.9.22  
Hilt Version: 2.50
