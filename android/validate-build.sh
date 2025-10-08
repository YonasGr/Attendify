#!/bin/bash
# Build Validation Script for Attendify Android Project
# This script validates the build configuration and tests if the project can be built successfully.

set -e  # Exit on error

echo "======================================"
echo "Attendify Android Build Validation"
echo "======================================"
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ $2${NC}"
    else
        echo -e "${RED}✗ $2${NC}"
    fi
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "ℹ $1"
}

# Change to android directory
cd "$(dirname "$0")"

echo "Step 1: Checking prerequisites..."
echo "-----------------------------------"

# Check Java version
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        print_status 0 "Java $JAVA_VERSION detected (JDK 17+ required)"
    else
        print_status 1 "Java $JAVA_VERSION detected (JDK 17+ required)"
        echo "  Please install JDK 17 or later"
        exit 1
    fi
else
    print_status 1 "Java not found"
    echo "  Please install JDK 17 or later"
    exit 1
fi

# Check Gradle wrapper
if [ -f "gradlew" ]; then
    print_status 0 "Gradle wrapper found"
    chmod +x gradlew
else
    print_status 1 "Gradle wrapper not found"
    exit 1
fi

# Check Gradle wrapper jar
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    print_status 0 "Gradle wrapper jar found"
else
    print_status 1 "Gradle wrapper jar not found"
    print_warning "  You may need to regenerate the wrapper with: gradle wrapper --gradle-version 8.5"
    exit 1
fi

echo ""
echo "Step 2: Checking network connectivity..."
echo "-----------------------------------"

# Check Google Maven repository access
if curl -s --head --max-time 5 https://dl.google.com/ > /dev/null 2>&1; then
    print_status 0 "Google Maven repository accessible"
    NETWORK_OK=1
else
    print_status 1 "Google Maven repository NOT accessible (dl.google.com)"
    print_warning "  Build may fail without access to Google Maven repository"
    print_info "  See BUILD_REQUIREMENTS.md for mirror configuration options"
    NETWORK_OK=0
fi

# Check Maven Central access
if curl -s --head --max-time 5 https://repo.maven.apache.org/maven2/ > /dev/null 2>&1; then
    print_status 0 "Maven Central accessible"
else
    print_status 1 "Maven Central NOT accessible"
    print_warning "  Some dependencies may not be available"
fi

echo ""
echo "Step 3: Checking build configuration..."
echo "-----------------------------------"

# Check build files exist
if [ -f "build.gradle.kts" ] && [ -f "app/build.gradle.kts" ] && [ -f "settings.gradle.kts" ]; then
    print_status 0 "Build configuration files found"
else
    print_status 1 "Build configuration files missing"
    exit 1
fi

# Verify Gradle version
GRADLE_VERSION=$(./gradlew --version 2>/dev/null | grep "Gradle" | awk '{print $2}')
if [ -n "$GRADLE_VERSION" ]; then
    print_status 0 "Gradle version: $GRADLE_VERSION"
    if [ "$GRADLE_VERSION" != "8.5" ]; then
        print_warning "  Expected Gradle 8.5, got $GRADLE_VERSION"
    fi
else
    print_status 1 "Could not determine Gradle version"
fi

echo ""
if [ $NETWORK_OK -eq 0 ]; then
    echo "====================================="
    echo "⚠ Network Check Failed"
    echo "====================================="
    echo ""
    echo "The build requires access to Google's Maven repository (dl.google.com)."
    echo "Without this access, the build will likely fail when downloading dependencies."
    echo ""
    echo "Options:"
    echo "  1. Enable access to dl.google.com in your network/firewall"
    echo "  2. Use a VPN or proxy"
    echo "  3. Configure mirror repositories (see BUILD_REQUIREMENTS.md)"
    echo ""
    echo "Would you like to try building anyway? (y/N)"
    read -r response
    if [[ ! "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        echo "Build validation cancelled."
        exit 1
    fi
fi

echo ""
echo "Step 4: Attempting clean build..."
echo "-----------------------------------"

# Try to build
echo "Running: ./gradlew clean build --no-daemon"
echo ""

if ./gradlew clean build --no-daemon; then
    echo ""
    echo "======================================"
    echo -e "${GREEN}✓ Build Successful!${NC}"
    echo "======================================"
    echo ""
    echo "Build artifacts:"
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        echo "  - Debug APK: app/build/outputs/apk/debug/app-debug.apk"
        APK_SIZE=$(du -h "app/build/outputs/apk/debug/app-debug.apk" | cut -f1)
        echo "    Size: $APK_SIZE"
    fi
    echo ""
    echo "Next steps:"
    echo "  - Install on device: ./gradlew installDebug"
    echo "  - Run tests: ./gradlew test"
    echo "  - Build release: ./gradlew assembleRelease"
    echo ""
    exit 0
else
    echo ""
    echo "======================================"
    echo -e "${RED}✗ Build Failed${NC}"
    echo "======================================"
    echo ""
    echo "Common issues:"
    echo "  1. Network connectivity to Google Maven repository"
    echo "  2. JDK version mismatch (requires JDK 17+)"
    echo "  3. Android SDK not configured"
    echo ""
    echo "For detailed troubleshooting, see:"
    echo "  - BUILD_REQUIREMENTS.md"
    echo "  - README.md"
    echo ""
    exit 1
fi
