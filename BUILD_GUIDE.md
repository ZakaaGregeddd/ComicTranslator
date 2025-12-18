# Build Instructions - Comic Translator

Panduan lengkap untuk build dan menjalankan aplikasi Comic Translator.

## ⚙️ Prerequisites

Sebelum build, pastikan sudah install:
- ✅ JDK 11 atau lebih baru
- ✅ Android Studio (2023.1+)
- ✅ Android SDK (API 34 target, API 24 minimum)
- ✅ Android device atau emulator

## 📋 Quick Build Commands

### 1. Debug Build (Development)

```bash
cd ComicTranslator

# Windows
gradlew.bat build

# macOS/Linux
./gradlew build
```

**Output**: `app/build/outputs/apk/debug/app-debug.apk`

### 2. Release Build (Production)

```bash
# Windows
gradlew.bat assembleRelease

# macOS/Linux
./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release-unsigned.apk`

### 3. Clean & Rebuild

```bash
# Windows
gradlew.bat clean build

# macOS/Linux
./gradlew clean build
```

## 🔨 Build via Android Studio

### Method 1: Using Run Button (Recommended)

1. **Connect Device/Open Emulator**
   - Physical device dengan USB debugging enabled
   - Atau start Android Emulator

2. **Select Build Variant**
   - Bottom left: "Build Variants" panel
   - Select "debug" untuk development

3. **Click Run Button**
   - Top toolbar green play button (or Shift+F10)
   - Wait untuk build & install

4. **Monitor Progress**
   - View → Tool Windows → Build
   - Shows compilation progress

### Method 2: Using Build Menu

1. **Build → Make Project** (Ctrl+F9)
   - Compiles semua modules

2. **Build → Build Bundle(s) / APK(s)**
   - Pilih "Build APK(s)"

3. **File → Sync Project with Gradle Files** (Ctrl+Shift+O)
   - Jika ada Gradle sync issues

## 📦 Installation Methods

### Method 1: Android Studio (Fastest)

1. Select device/emulator di toolbar
2. Click green Run button
3. App installs & launches automatically
4. Grant permissions when prompted

### Method 2: Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install to device
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Run app
adb shell am start -n com.example.comictranslator/.MainActivity

# View logs
adb logcat | grep ComicTranslator
```

### Method 3: Android Studio Device Manager

1. Tools → Device Manager
2. Select device
3. Right-click → Cold Boot / Run App

## 🎯 Build Configuration Options

### Change Target Device

Edit `app/build.gradle`:
```gradle
android {
    ...
    defaultConfig {
        targetSdk 34  // Change here
        minSdk 24     // Minimum supported
    }
}
```

### Enable ProGuard/R8 Minification

Edit `app/build.gradle`:
```gradle
buildTypes {
    release {
        minifyEnabled true  // Enable code shrinking
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

### Change App ID

Edit `app/build.gradle`:
```gradle
defaultConfig {
    applicationId "com.yourcompany.comictranslator"
}
```

## 🧪 Testing Build

### Verify Build Success

```bash
# Check if APK created
ls app/build/outputs/apk/debug/app-debug.apk

# Or on Windows
dir app\build\outputs\apk\debug\app-debug.apk
```

### Test on Device

```bash
# Check if app installed
adb shell pm list packages | grep comictranslator

# Launch app
adb shell am start -n com.example.comictranslator/.MainActivity

# Check for crashes
adb logcat | grep -E "CRASH|Exception|ERROR"
```

### Check APK Info

```bash
# List APK contents
aapt dump badging app/build/outputs/apk/debug/app-debug.apk

# Check permissions
aapt dump permissions app/build/outputs/apk/debug/app-debug.apk
```

## 🐛 Build Troubleshooting

### Build Fails: "Gradle Sync Failed"

**Solution**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Build Fails: "Compilation Error"

**Solution**:
1. Check Java version: `java -version` (should be 11+)
2. File → Invalidate Caches → Invalidate and Restart
3. Delete `.gradle` folder dan rebuild

### Build Slow

**Solution**:
```bash
# Parallel build
./gradlew build -x test --parallel

# Use daemon
./gradlew --daemon build

# Offline mode (if dependencies cached)
./gradlew build --offline
```

### APK Not Generated

**Solution**:
1. Check if build succeeded (no ERROR in output)
2. Verify path: `app/build/outputs/apk/debug/`
3. Check disk space (need ~1-2GB)

## 📊 Build Output Analysis

### APK Size Analysis

```bash
# Analyze APK
./gradlew build bundleDebug

# View size report
unzip -l app/build/outputs/apk/debug/app-debug.apk | tail -20

# Expected size: ~50-100MB (dengan semua dependencies)
```

### Build Time Optimization

| Optimization | Command |
|--------------|---------|
| Clean build | `./gradlew clean build` |
| Incremental | `./gradlew build` |
| Parallel | `./gradlew build --parallel` |
| Daemon | `./gradlew --daemon build` |
| Offline | `./gradlew build --offline` |
| Combine | `./gradlew --daemon build --parallel -x test` |

## 🚀 Release Build Steps

### 1. Sign APK

Edit `app/build.gradle`:
```gradle
signingConfigs {
    release {
        storeFile file("keystore.jks")
        storePassword System.getenv("KEYSTORE_PASSWORD")
        keyAlias System.getenv("KEY_ALIAS")
        keyPassword System.getenv("KEY_PASSWORD")
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
    }
}
```

### 2. Generate Release APK

```bash
# Windows
set KEYSTORE_PASSWORD=your_password
set KEY_ALIAS=your_alias
set KEY_PASSWORD=your_key_password
gradlew.bat assembleRelease

# macOS/Linux
export KEYSTORE_PASSWORD=your_password
export KEY_ALIAS=your_alias
export KEY_PASSWORD=your_key_password
./gradlew assembleRelease
```

### 3. Verify Release APK

```bash
aapt dump badging app/build/outputs/apk/release/app-release.apk
```

## 📱 Install APK Manually

### Via ADB

```bash
# Install APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Uninstall before install
adb shell pm uninstall com.example.comictranslator
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Via Android Studio

1. Build → Analyze APK
2. Select APK file
3. View structure & contents
4. Right-click device → Install APK

### Via File Manager

1. Copy APK ke device
2. Open file manager
3. Select APK → Install

## ✅ Build Verification Checklist

- [ ] JAVA_HOME set correctly
- [ ] Android SDK installed (API 24 & 34)
- [ ] Gradle build.gradle updated
- [ ] AndroidManifest.xml valid
- [ ] All source files compile (no errors)
- [ ] All resources reference valid
- [ ] APK generated successfully
- [ ] APK size < 150MB
- [ ] APK installs without error
- [ ] App launches without crash
- [ ] Permissions request shows
- [ ] Features functional

## 📈 Build Statistics

| Metric | Value |
|--------|-------|
| Compilation Time | 1-3 minutes |
| APK Size | 50-100MB |
| Min RAM for Build | 2GB |
| Disk Space Needed | 2GB |
| Gradle Daemon | Recommended |
| Parallel Builds | Supported |
| Incremental Compile | Supported |

## 🔗 Useful Build Commands

```bash
# List tasks
./gradlew tasks

# Build with verbose output
./gradlew build --stacktrace

# Build with daemon
./gradlew --daemon assembleDebug

# Stop gradle daemon
./gradlew --stop

# Check dependencies
./gradlew dependencies

# Export APK to specific location
./gradlew build -PoutputDir=../output

# Disable tests
./gradlew build -x test

# Force rebuild without cache
./gradlew cleanBuildCache build
```

## 📞 Build Support

If build fails after trying solutions above:

1. Check **TROUBLESHOOTING.md** section "Installation Issues"
2. Check Android documentation: https://developer.android.com/studio/build
3. Check Gradle documentation: https://gradle.org/
4. Report issue dengan output dari:
   ```bash
   ./gradlew build --stacktrace > build.log
   ```

---

**Build Guide Version**: 1.0  
**Last Updated**: December 2025  
**Tested On**: Windows 10, macOS 12+, Ubuntu 20.04+
