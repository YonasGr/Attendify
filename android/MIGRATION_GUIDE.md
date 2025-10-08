# Migration Guide: Upgrading to New Build Configuration

This guide helps you upgrade your Attendify Android project to use the latest build configuration with Gradle 8.5, Kotlin 1.9.22, and KSP.

## Overview

This migration brings several improvements:
- **Gradle 8.5**: Latest stable Gradle with better performance
- **Kotlin 1.9.22**: Latest Kotlin 1.9.x with bug fixes
- **KAPT → KSP**: Up to 2x faster builds with better error messages
- **Hilt 2.50**: Latest stable Hilt with Kotlin 1.9.22 support
- **Updated dependencies**: Latest stable versions of all libraries

## Prerequisites

Before starting:
- ✅ Backup your current working code
- ✅ Commit all changes
- ✅ Ensure you have JDK 17+ installed
- ✅ Have network access to Google Maven repository

## Step-by-Step Migration

### Step 1: Update Gradle Wrapper

Update `gradle/wrapper/gradle-wrapper.properties`:

```diff
-distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
+distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

Regenerate wrapper:
```bash
cd android
gradle wrapper --gradle-version 8.5
```

### Step 2: Update Root build.gradle.kts

Replace the plugins block with buildscript block:

```diff
-plugins {
-    id("com.android.application") version "8.2.0" apply false
-    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
-    id("com.google.dagger.hilt.android") version "2.48" apply false
+buildscript {
+    repositories {
+        google()
+        mavenCentral()
+    }
+    dependencies {
+        classpath("com.android.tools.build:gradle:8.2.2")
+        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
+        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
+    }
 }

+plugins {
+    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
+}
```

### Step 3: Update app/build.gradle.kts - Plugins

Update plugin declarations:

```diff
 plugins {
     id("com.android.application")
-    id("org.jetbrains.kotlin.android")
-    id("com.google.dagger.hilt.android")
-    kotlin("kapt")
+    id("kotlin-android")
+    id("dagger.hilt.android.plugin")
+    id("com.google.devtools.ksp")
 }
```

### Step 4: Update app/build.gradle.kts - Android Block

Remove kapt configuration and enable BuildConfig:

```diff
 android {
     namespace = "com.attendify.app"
     compileSdk = 34

     defaultConfig {
         applicationId = "com.attendify.app"
         minSdk = 24
         targetSdk = 34
         versionCode = 1
         versionName = "1.0.0"

         testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
         vectorDrawables {
             useSupportLibrary = true
         }
-
-        // Schema export directory for Room
-        kapt {
-            arguments {
-                arg("room.schemaLocation", "$projectDir/schemas")
-            }
-        }
     }

     // ... buildTypes, compileOptions, kotlinOptions ...

     buildFeatures {
         compose = true
+        buildConfig = true
     }

     composeOptions {
-        kotlinCompilerExtensionVersion = "1.5.4"
+        kotlinCompilerExtensionVersion = "1.5.10"
     }
 }
```

### Step 5: Update app/build.gradle.kts - Dependencies

Replace all `kapt()` with `ksp()`:

```diff
 dependencies {
     // ... other dependencies ...
     
     // Room Database
+    val roomVersion = "2.6.1"
-    implementation("androidx.room:room-runtime:2.6.1")
-    implementation("androidx.room:room-ktx:2.6.1")
-    kapt("androidx.room:room-compiler:2.6.1")
+    implementation("androidx.room:room-runtime:$roomVersion")
+    implementation("androidx.room:room-ktx:$roomVersion")
+    ksp("androidx.room:room-compiler:$roomVersion")
     
-    // Dependency Injection
-    implementation("com.google.dagger:hilt-android:2.48")
-    kapt("com.google.dagger:hilt-compiler:2.48")
+    // Dependency Injection - Hilt
+    implementation("com.google.dagger:hilt-android:2.50")
+    ksp("com.google.dagger:hilt-compiler:2.50")
     implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
     
     // ... other dependencies ...
 }

-kapt {
-    correctErrorTypes = true
-}
```

### Step 6: Update Other Dependencies

Update to latest stable versions:

```diff
 dependencies {
     // Core Android
     implementation("androidx.core:core-ktx:1.12.0")
-    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
-    implementation("androidx.activity:activity-compose:1.8.1")
+    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
+    implementation("androidx.activity:activity-compose:1.8.2")

     // Jetpack Compose
-    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
+    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
     
     // Navigation
-    implementation("androidx.navigation:navigation-compose:2.7.5")
+    implementation("androidx.navigation:navigation-compose:2.7.7")
     
     // Lifecycle
-    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
-    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
+    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
+    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
     
     // QR Code Scanning - ZXing
     implementation("com.journeyapps:zxing-android-embedded:4.3.0")
-    implementation("com.google.zxing:core:3.5.2")
+    implementation("com.google.zxing:core:3.5.3")
     
     // QR Code Generation
-    implementation("com.google.zxing:javase:3.5.2")
+    implementation("com.google.zxing:javase:3.5.3")
     
     // Testing
-    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
+    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
 }
```

### Step 7: Update settings.gradle.kts (Optional)

Enhance repository configuration:

```diff
 pluginManagement {
     repositories {
-        google()
+        google {
+            content {
+                includeGroupByRegex("com\\.android.*")
+                includeGroupByRegex("com\\.google.*")
+                includeGroupByRegex("androidx.*")
+            }
+        }
         mavenCentral()
         gradlePluginPortal()
     }
 }

 dependencyResolutionManagement {
     repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
     repositories {
-        google()
+        google {
+            content {
+                includeGroupByRegex("com\\.android.*")
+                includeGroupByRegex("com\\.google.*")
+                includeGroupByRegex("androidx.*")
+            }
+        }
         mavenCentral()
     }
 }
```

### Step 8: Clean and Build

1. Clean the project:
   ```bash
   ./gradlew clean
   ```

2. Delete build directories:
   ```bash
   rm -rf .gradle/ build/ app/build/
   ```

3. Sync and build:
   ```bash
   ./gradlew build
   ```

4. Or use the validation script:
   ```bash
   ./validate-build.sh
   ```

## Troubleshooting

### Build fails with "Could not resolve dependencies"

**Cause**: Network cannot access Google Maven repository.

**Solution**:
1. Check network connectivity to https://dl.google.com/
2. Configure VPN/proxy if needed
3. See BUILD_REQUIREMENTS.md for mirror configuration

### "Task 'kapt' not found"

**Cause**: Old build cache referencing KAPT.

**Solution**:
```bash
./gradlew clean
./gradlew --stop  # Stop Gradle daemon
rm -rf .gradle/
./gradlew build
```

### Annotation processing errors

**Cause**: KSP generated files not recognized.

**Solution**:
1. In Android Studio: File → Invalidate Caches / Restart
2. Clean and rebuild: `./gradlew clean build`
3. Check that all `kapt()` references are changed to `ksp()`

### Compose compilation errors

**Cause**: Compose compiler version mismatch.

**Solution**: Ensure `kotlinCompilerExtensionVersion = "1.5.10"` matches Kotlin 1.9.22.

### Hilt injection failures

**Cause**: Hilt annotation processor not running correctly.

**Solution**:
1. Verify using `ksp("com.google.dagger:hilt-compiler:2.50")`
2. Check `@HiltAndroidApp` annotation on Application class
3. Ensure `dagger.hilt.android.plugin` is applied
4. Clean and rebuild

## Verification Checklist

After migration, verify:

- [ ] Project syncs successfully in Android Studio
- [ ] `./gradlew build` completes without errors
- [ ] Debug APK builds: `./gradlew assembleDebug`
- [ ] Unit tests pass: `./gradlew test`
- [ ] App installs and runs on device/emulator
- [ ] Dependency injection works (no Hilt errors)
- [ ] Room database operations work
- [ ] No compilation warnings about KAPT

## Reverting if Needed

If you need to revert:

1. Checkout your backup branch:
   ```bash
   git checkout your-backup-branch
   ```

2. Or revert specific files:
   ```bash
   git checkout HEAD~1 -- android/build.gradle.kts
   git checkout HEAD~1 -- android/app/build.gradle.kts
   git checkout HEAD~1 -- android/gradle/wrapper/gradle-wrapper.properties
   ```

## Benefits After Migration

You should see:

✅ **Faster builds**: KSP is significantly faster than KAPT  
✅ **Better error messages**: KSP provides clearer compilation errors  
✅ **Latest features**: Access to newest Kotlin and Compose features  
✅ **Better IDE support**: Improved code completion and inspection  
✅ **Future-proof**: Modern configuration compatible with upcoming versions

## Getting Help

If you encounter issues:

1. Check [BUILD_REQUIREMENTS.md](BUILD_REQUIREMENTS.md) - Comprehensive troubleshooting
2. Check [BUILD_QUICK_REF.md](BUILD_QUICK_REF.md) - Quick reference
3. Review the validation script output: `./validate-build.sh`
4. Check the Android Gradle Plugin release notes
5. Open an issue on GitHub with:
   - Your environment details (OS, JDK version, etc.)
   - Full build error log
   - Steps you've already tried

## Additional Resources

- [Migrating from kapt to KSP](https://developer.android.com/studio/build/migrate-to-ksp)
- [AGP 8.2 Release Notes](https://developer.android.com/build/releases/past-releases/agp-8-2-0-release-notes)
- [Kotlin 1.9.22 Release Notes](https://kotlinlang.org/docs/whatsnew1922.html)
- [Hilt Documentation](https://dagger.dev/hilt/)

---

Migration guide version: 1.0  
Target configuration: Gradle 8.5, Kotlin 1.9.22, AGP 8.2.2, Hilt 2.50  
Last updated: January 2025
