# Comic Translator - Setup & Installation Guide

## Prerequisites

Sebelum memulai, pastikan Anda sudah install:

1. **Android Studio** (Recommended version: 2023.1+)
   - Download: https://developer.android.com/studio
   - Include: Android SDK, Android Emulator

2. **Java Development Kit (JDK)**
   - Version: JDK 11 atau lebih baru
   - Download: https://adoptopenjdk.net/
   - Set JAVA_HOME environment variable

3. **Android SDK**
   - Target SDK: 34 (Android 14)
   - Minimum SDK: 24 (Android 7.0)
   - Install via Android Studio SDK Manager

4. **Android Device atau Emulator**
   - Real device: Android 7.0+ dengan USB debugging enabled
   - Emulator: Android 7.0+ dengan minimal 4GB RAM

## Environment Setup

### Windows Setup

1. **Set JAVA_HOME**:
   ```
   Control Panel → System → Advanced System Settings → Environment Variables
   → New System Variable
   Variable name: JAVA_HOME
   Variable value: C:\Program Files\Java\jdk-11.0.x (adjust path)
   ```

2. **Add to PATH**:
   ```
   Edit System Variable "Path"
   Add: %JAVA_HOME%\bin
   ```

3. **Verify**:
   ```cmd
   java -version
   javac -version
   ```

### macOS Setup

1. **Install via Homebrew**:
   ```bash
   brew install openjdk@11
   ```

2. **Set JAVA_HOME** (add to `~/.zshrc` atau `~/.bash_profile`):
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 11)
   export PATH=$JAVA_HOME/bin:$PATH
   ```

3. **Verify**:
   ```bash
   java -version
   ```

### Linux Setup

1. **Install JDK**:
   ```bash
   # Ubuntu/Debian
   sudo apt-get install openjdk-11-jdk
   
   # Fedora/RHEL
   sudo dnf install java-11-openjdk-devel
   ```

2. **Set JAVA_HOME**:
   ```bash
   echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk' >> ~/.bashrc
   source ~/.bashrc
   ```

## Project Setup

### 1. Download/Clone Project

```bash
# Copy project folder dari d:\User\Documents\2025\SEM5\TEST\Translate\ComicTranslator
# atau clone jika sudah di Git
cd ComicTranslator
```

### 2. Open di Android Studio

1. Launch Android Studio
2. File → Open
3. Select folder `ComicTranslator`
4. Wait untuk Gradle sync

### 3. Configure Android SDK

1. Tools → SDK Manager
2. Pastikan terinstall:
   - **SDK Platforms**: Android 14 (API 34), Android 13 (API 33)
   - **SDK Tools**:
     - Android SDK Build-Tools (latest)
     - Android SDK Platform-Tools
     - Android Emulator (untuk testing)

### 4. Build Project

**Via Android Studio**:
1. Build → Make Project
2. Wait untuk build selesai

**Via Terminal**:
```bash
# Windows
gradlew.bat build

# macOS/Linux
./gradlew build
```

## Dependency Management

### Auto-Download (Recommended)

Gradle akan otomatis download dependencies saat build:
- Google ML Kit Vision
- Retrofit + OkHttp
- OpenCV Android
- Room Database
- Coroutines

### Manual Install (Optional)

Jika ada offline development:

1. **Google ML Kit**:
   ```gradle
   implementation 'com.google.mlkit:text-recognition:16.0.0'
   ```

2. **OpenCV**:
   - Download: https://opencv.org/releases/
   - Extract ke folder: `app/src/main/libs/`

## Device/Emulator Setup

### USB Connected Device

1. **Enable Developer Mode**:
   - Settings → About Phone → Tap "Build Number" 7 times
   - Back to Settings → Developer Options → Enable USB Debugging

2. **Connect Device**:
   ```bash
   adb devices
   # Should show device in list
   ```

### Android Emulator

1. **Create AVD** (Android Virtual Device):
   - Android Studio → Tools → AVD Manager
   - Create Virtual Device
   - Select device profile (e.g., Pixel 6)
   - Select system image (e.g., Android 13, API 33)
   - Finish

2. **Run AVD**:
   - Double-click device di AVD Manager

## Installation & Running

### Method 1: Android Studio (Recommended)

1. **Select Device/Emulator**:
   - Top toolbar → Select device dropdown
   - Choose connected device atau emulator

2. **Run App**:
   - Click green "Run" button (or Shift+F10)
   - Wait untuk build dan install

3. **First Run**:
   - App akan launch automatically
   - Grant permissions:
     - Overlay Permission (tap Settings, enable)
     - Camera Permission (tap Allow)
     - Internet Permission (tap Allow)

### Method 2: Command Line

```bash
# Build APK
./gradlew assembleDebug  # Windows: gradlew.bat

# Install ke device
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Run app
adb shell am start -n com.example.comictranslator/.MainActivity
```

### Method 3: Create Release APK

```bash
# Build release APK
./gradlew assembleRelease

# APK location: app/build/outputs/apk/release/app-release.apk
```

## Initial Configuration

### Grant Required Permissions

Saat pertama run, app akan minta permissions:

1. **Overlay Permission**:
   - Alert dialog → "Open Settings"
   - Settings → Display → Special app access → Display over other apps
   - Toggle ON untuk Comic Translator

2. **Camera Permission**:
   - Dialog → "Allow"
   - Untuk screen capture (tidak benar-benar access camera)

3. **Internet Permission**:
   - Automatic
   - Required untuk translation API

### Configure LibreTranslate

**Default (Public API)**:
```
Endpoint: https://api.libretranslate.de/
Status: Free tier dengan rate limiting
```

**Self-Hosted Option**:

1. Install Docker: https://www.docker.com/

2. Run LibreTranslate server:
   ```bash
   docker run -d -p 5000:5000 libretranslate/libretranslate
   ```

3. Edit `RetrofitClient.kt`:
   ```kotlin
   private const val LIBRE_TRANSLATE_BASE_URL = "http://localhost:5000/"
   ```

4. Rebuild project

## Troubleshooting

### Gradle Sync Fails

**Solution**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### "ANDROID_HOME not set"

**Solution**:
```bash
# Windows
set ANDROID_HOME=C:\Users\YourName\AppData\Local\Android\Sdk

# macOS/Linux
export ANDROID_HOME=$HOME/Library/Android/sdk
```

### "No Connected Device"

**Solution**:
```bash
adb devices -l  # List devices
adb kill-server
adb start-server
adb devices
```

### App Crashes on Startup

**Solution**:
1. Check logcat: View → Tool Windows → Logcat
2. Look untuk stack trace
3. Common issues:
   - Permission not granted
   - ML Kit model not loaded
   - Missing dependency

### Overlay not showing

**Solution**:
1. Go to Settings → Apps → Comic Translator → Permissions
2. Toggle "Overlay over other apps" ON
3. Restart app

### Translation not working

**Solution**:
1. Check internet connection
2. Verify API endpoint: Open browser → https://api.libretranslate.de/
3. If public API down, use self-hosted option
4. Check logcat untuk API errors

### High CPU/Battery usage

**Solution**:
1. Reduce frame rate:
   - Edit `ScreenCaptureHandler.kt`: `frameInterval = 200L` (5fps)
2. Disable speech bubble detection:
   - Uncheck "Detect Speech Bubbles" di UI
3. Use lower resolution:
   - Edit `createVirtualDisplay()`: reduce width/height

## Development Workflow

### Make Changes

1. Edit file (e.g., `.kt`, `.xml`)
2. Android Studio auto-saves
3. Hot Reload (Ctrl+Shift+F10) untuk quick test

### Build Variants

```bash
# Debug build (fast, for development)
./gradlew assembleDebug

# Release build (optimized, smaller size)
./gradlew assembleRelease

# Install specific variant
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Debugging

1. Set breakpoint: Click line number
2. Run with debugger: Ctrl+D
3. Step through code: F8/F9
4. View variables: View panel di bottom

### Logging

```kotlin
// In code
Log.d("TAG", "Debug message")
Log.e("TAG", "Error message")

// View in logcat
View → Tool Windows → Logcat
```

## Performance Testing

### Monitor Performance

```bash
# Check CPU/Memory
adb shell dumpsys meminfo com.example.comictranslator

# Monitor frame rate
adb shell dumpsys gfxinfo com.example.comictranslator
```

### Profiling

1. Android Studio → Profiler (Ctrl+Alt+6)
2. Monitor:
   - CPU usage
   - Memory allocation
   - Network traffic
   - Battery drain

## Distribution

### Generate Release APK

1. **Configure Signing**:
   - Edit `build.gradle` (app level)
   - Add signing configuration

2. **Build**:
   ```bash
   ./gradlew assembleRelease
   ```

3. **Output**:
   - File: `app/build/outputs/apk/release/app-release.apk`
   - Size: ~50-100MB (tergantung dependencies)

### Upload to Google Play

1. Create Google Play Developer account
2. Create app
3. Upload signed APK
4. Fill metadata (description, screenshots, etc.)
5. Submit for review

## Next Steps

1. **Test dengan berbagai device**:
   - Different screen sizes
   - Different Android versions
   - Different device specs

2. **Collect feedback**:
   - Translation accuracy
   - Performance on older devices
   - UI/UX improvements

3. **Iterate & Improve**:
   - Fix bugs
   - Optimize performance
   - Add new features

---

**Version**: 1.0  
**Last Updated**: December 2025  
**Status**: Ready for Development & Testing
