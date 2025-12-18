# Comic Translator - Quick Start Guide

Panduan cepat untuk mulai menggunakan Comic Translator dalam 5 menit.

## 60 Detik Setup

### 1. Install Prerequisites
- **JDK 11+**: https://adoptopenjdk.net/
- **Android Studio**: https://developer.android.com/studio
- **Set JAVA_HOME** environment variable

### 2. Open Project
```bash
cd ComicTranslator
# Open in Android Studio
```

### 3. Build & Run
```bash
./gradlew build
# Run button di Android Studio (atau tekan Shift+F10)
```

### 4. Grant Permissions
- Overlay: Settings → Display → Display over other apps → ON
- Camera/Internet: Tap Allow di dialog

### 5. Start Using
1. Select language (e.g., English → Indonesian)
2. Tap "Start Translation"
3. Select screen to capture
4. Terjemahan akan muncul overlay pada layar

## Common Tasks

### Change Translation Language
1. Main screen → Language spinners
2. Select source language (default: Auto)
3. Select target language (default: Indonesian)
4. Tap Start

### Enable/Disable Features
- **Auto Translate**: Checkbox di settings
- **Detect Orientation**: Support vertikal teks
- **Detect Bubbles**: Find speech bubbles

### Stop Translation
- Tap "Stop Translation" button
- Atau swipe down notification

### View Logs
- Android Studio → Logcat (View → Tool Windows → Logcat)
- Search: "ComicTranslator"

## File Structure Quick Reference

```
ComicTranslator/
├── MainActivity.kt          ← Main UI entry point
├── service/
│   ├── OverlayService.kt   ← Floating overlay logic
│   └── ScreenCaptureHandler.kt ← Screen capture
├── util/
│   ├── OCREngine.kt        ← Google ML Kit text recognition
│   ├── RetrofitClient.kt   ← Translation API client
│   └── ImageProcessor.kt   ← Speech bubble detection
├── ui/
│   └── OverlayView.kt      ← Custom drawing for overlays
└── model/Models.kt         ← Data classes
```

## Essential Modifications

### Change Supported Languages
File: `MainActivity.kt`
```kotlin
SUPPORTED_LANGUAGES = listOf(
    LanguageOption("Language Name", "code", "Native Name"),
    // Add more languages
)
```

### Change Translation API
File: `RetrofitClient.kt`
```kotlin
// Public API
private const val LIBRE_TRANSLATE_BASE_URL = "https://api.libretranslate.de/"

// Self-hosted
private const val LIBRE_TRANSLATE_BASE_URL = "http://localhost:5000/"
```

### Change Frame Rate
File: `ScreenCaptureHandler.kt`
```kotlin
private val frameInterval = 100L  // milliseconds
// 100L = 10fps, 50L = 20fps, 200L = 5fps
```

### Change Overlay Text Size
File: `OverlayView.kt`
```kotlin
paint.textSize = 14f  // Change this value
```

## Testing Checklist

- [ ] App launches without crashes
- [ ] Overlay permission dialog shows
- [ ] Camera/Internet permissions grant
- [ ] Language selection works
- [ ] Start Translation button works
- [ ] Overlay appears on screen
- [ ] Text detection shows (logcat)
- [ ] Translation appears in overlay
- [ ] Stop button works
- [ ] No permissions errors

## Quick Debugging

### App Not Starting?
```bash
# Check Java
java -version

# Clear build cache
./gradlew clean

# Rebuild
./gradlew build
```

### No Overlay?
```bash
# Check device logcat
adb logcat | grep ComicTranslator

# Common cause: Overlay permission not granted
```

### Translation API Error?
```bash
# Test API manually
curl https://api.libretranslate.de/translate -X POST \
  -d '{"q":"Hello","source":"en","target":"id"}' \
  -H "Content-Type: application/json"
```

### Memory Issues?
```bash
# Reduce frame rate in ScreenCaptureHandler.kt
private val frameInterval = 200L  // Lower fps

# Or disable speech bubble detection
// In MainActivity: detectBubblesCheckbox.isChecked = false
```

## Performance Tips

1. **Lower Resolution**:
   - Reduce `displayWidth/displayHeight` di screen capture
   - Trade-off: Less accurate OCR

2. **Reduce Frame Rate**:
   - Increase `frameInterval` di ScreenCaptureHandler
   - Default 100ms (10fps) is balanced

3. **Disable Features**:
   - Uncheck "Detect Orientation" jika tidak perlu
   - Uncheck "Detect Bubbles" untuk faster processing

4. **Use Self-Hosted Translation**:
   - Better latency than public API
   - No rate limiting

## Environment Variables

```bash
# Windows
set ANDROID_HOME=C:\Users\Username\AppData\Local\Android\Sdk
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.x

# macOS/Linux
export ANDROID_HOME=$HOME/Library/Android/sdk
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
```

## Important Notes

⚠️ **Android Version**: Minimum API 24 (Android 7.0)
⚠️ **Internet Required**: For translation API calls
⚠️ **Permissions Critical**: Without overlay permission, app won't work
⚠️ **Battery Drain**: ~300mAh/hour at 10fps (normal usage)

## Getting Help

1. Check **TECHNICAL_DOCS.md** untuk detailed architecture
2. Check **README.md** untuk features & configuration
3. Check **SETUP_GUIDE.md** untuk installation help
4. Enable logcat di Android Studio untuk debugging

## Next Level: Customization

### Add New Language
Edit `MainActivity.kt`:
```kotlin
SUPPORTED_LANGUAGES.add(LanguageOption("Thai", "th", "ไทย"))
```

### Change Color Scheme
Edit `res/values/colors.xml`:
```xml
<color name="overlay_background">#FFFFFFFF</color>  <!-- White -->
<color name="overlay_text">#FF000000</color>        <!-- Black -->
```

### Adjust Text Rendering
Edit `OverlayView.kt`:
```kotlin
// Change padding
val padding = 12  // was 8

// Change border width
borderPaint.strokeWidth = 3f  // was 2f
```

---

**Quick Start Version**: 1.0  
**Last Updated**: December 2025  
**Estimated Setup Time**: 5-10 minutes
