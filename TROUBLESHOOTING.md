# Comic Translator - Troubleshooting Guide

Panduan lengkap untuk mengatasi masalah umum yang mungkin Anda temui.

## Installation Issues

### Issue: "JAVA_HOME is not set"

**Error Message**:
```
Error: JAVA_HOME is not set and no 'java' command could be found in your PATH
```

**Solution**:
1. **Windows**:
   - Go to Control Panel → System → Advanced System Settings
   - Click "Environment Variables"
   - New System Variable:
     - Name: `JAVA_HOME`
     - Value: `C:\Program Files\Java\jdk-11.0.x` (adjust path)
   - Add to PATH: `%JAVA_HOME%\bin`
   - Restart terminal/IDE

2. **macOS**:
   ```bash
   # Add to ~/.zshrc or ~/.bash_profile
   export JAVA_HOME=$(/usr/libexec/java_home -v 11)
   
   # Reload
   source ~/.zshrc
   ```

3. **Linux**:
   ```bash
   # Add to ~/.bashrc
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
   
   # Reload
   source ~/.bashrc
   ```

**Verify**:
```bash
java -version
javac -version
```

### Issue: "Gradle Sync Failed"

**Error Message**:
```
ERROR: Could not find androidx.appcompat:appcompat:1.6.1
```

**Solution**:
1. Clear cache:
   ```bash
   ./gradlew clean
   ./gradlew build --refresh-dependencies
   ```

2. Check internet connection
3. Android Studio → File → Invalidate Caches
4. Rebuild project

### Issue: "SDK not installed"

**Error Message**:
```
ERROR: compileSdkVersion 34 requires Android API 34 to be installed
```

**Solution**:
1. Open SDK Manager:
   - Android Studio → Tools → SDK Manager
2. Install required SDK:
   - Select "Android 14 (API 34)"
   - Select "Build-Tools 34.x.x"
   - Click Install

## Runtime Issues

### Issue: App crashes on startup

**Error Message** (dari Logcat):
```
java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.comictranslator/.MainActivity}
```

**Solution**:

1. **Check Permissions**:
   ```bash
   adb logcat | grep ComicTranslator
   ```
   Look untuk permission errors

2. **Missing dependency**:
   - Check `build.gradle` untuk semua dependencies
   - Rebuild: `./gradlew clean build`

3. **ML Kit issue**:
   - Ensure ML Kit models downloaded
   - Go to device Settings → Apps → Comic Translator → Permissions
   - Grant all required permissions

4. **Full logcat dump**:
   ```bash
   adb logcat -c  # Clear
   adb logcat | grep -E "ComicTranslator|Exception|Error"
   ```

### Issue: "SecurityException: Permission denied"

**Error Message**:
```
android.system.ErrnoException: Permission denied
```

**Solution**:

1. **Overlay Permission**:
   - Settings → Display → Special app access → Display over other apps
   - Toggle ON for Comic Translator

2. **Camera Permission**:
   - Settings → Apps → Comic Translator → Permissions
   - Enable Camera

3. **Internet Permission**:
   - Settings → Apps → Comic Translator → Permissions
   - Enable Internet

4. **Reset permissions**:
   ```bash
   adb shell pm reset-permissions
   ```

### Issue: "Screen capture not working"

**Error Message**:
```
MediaProjection not started / surface already released
```

**Solution**:

1. **Restart device**
2. **Check API level**:
   - Require API 21+ for MediaProjection
   - Our minimum: API 24

3. **Clear app data**:
   ```bash
   adb shell pm clear com.example.comictranslator
   ```

4. **Check if service running**:
   ```bash
   adb shell ps | grep comictranslator
   ```

## Feature Issues

### Issue: Overlay not appearing

**Symptoms**:
- App runs, but no translation overlay visible
- Status shows "Translating..." but nothing on screen

**Solution**:

1. **Verify overlay permission**:
   ```bash
   adb shell dumpsys package | grep OVERLAY
   ```

2. **Check if service active**:
   - Android Studio → Debug → Check if OverlayService running
   - Logcat: Should show "Overlay started"

3. **Possible fixes**:
   - Restart device
   - Clear app data: `adb shell pm clear com.example.comictranslator`
   - Reinstall app

4. **Debug with verbose logging**:
   Edit `OverlayService.kt`:
   ```kotlin
   Log.d(TAG, "Overlay view added to window")  // Should appear in logcat
   ```

### Issue: Text not being detected (OCR)

**Symptoms**:
- App running, but no text detected
- Logcat shows: "Detected text: ''" (empty)

**Solution**:

1. **Improve text visibility**:
   - Increase text size in source comic
   - Ensure text is clear and high contrast
   - Try different comics

2. **Check ML Kit**:
   - Google Play Services updated?
   - ML Kit model downloaded?
   - Check logcat: "ML Kit loading model..."

3. **Adjust frame rate**:
   - Increase from 10fps to 20fps untuk better capture
   - Edit `ScreenCaptureHandler.kt`: `frameInterval = 50L`

4. **Enable detailed logging**:
   Edit `OCREngine.kt`:
   ```kotlin
   Log.d(TAG, "Found ${textBlocks.size} text blocks")
   ```

### Issue: Translation not working

**Symptoms**:
- Text detected, but not translated
- Overlay shows original text instead of translation

**Solution**:

1. **Check internet connection**:
   ```bash
   ping google.com  # or api.libretranslate.de
   ```

2. **Verify API endpoint**:
   - Open browser: `https://api.libretranslate.de/`
   - Should load translation page

3. **Check if public API is down**:
   - Use self-hosted option:
   ```bash
   docker run -d -p 5000:5000 libretranslate/libretranslate
   ```
   - Edit `RetrofitClient.kt`: Change URL to `http://localhost:5000/`

4. **Check logcat for API errors**:
   ```bash
   adb logcat | grep "TranslationService\|Retrofit\|HTTP"
   ```

5. **Test API manually**:
   ```bash
   curl -X POST https://api.libretranslate.de/translate \
     -H "Content-Type: application/json" \
     -d '{
       "q": "Hello world",
       "source": "en",
       "target": "id"
     }'
   ```

### Issue: High memory usage / App crashes

**Symptoms**:
- App consumes >300MB RAM
- Crashes after 5-10 minutes
- Logcat: "OutOfMemoryError"

**Solution**:

1. **Reduce frame rate**:
   Edit `ScreenCaptureHandler.kt`:
   ```kotlin
   private val frameInterval = 200L  // 5fps instead of 10fps
   ```

2. **Reduce image resolution**:
   Edit `ScreenCaptureHandler.kt`:
   ```kotlin
   private val displayWidth = width / 2  // Half resolution
   ```

3. **Disable unnecessary features**:
   - Uncheck "Detect Speech Bubbles"
   - Reduces OpenCV processing

4. **Monitor memory**:
   ```bash
   adb shell dumpsys meminfo com.example.comictranslator
   ```

### Issue: Speech bubble detection not working

**Symptoms**:
- Checkbox enabled, but no visible difference
- Translations appear anyway (without bubble targeting)

**Solution**:

1. **Current limitation**:
   - Speech bubble detection is basic (contour-based)
   - Only works with clear outlined bubbles
   - Fails with complex/gradient backgrounds

2. **Workaround**:
   - Disable for now (uncheck checkbox)
   - Will improve in future updates with ML model

3. **Debug detection**:
   Edit `ImageProcessor.kt`:
   ```kotlin
   Log.d(TAG, "Found ${bubbles.size} speech bubbles")
   ```

## Performance Issues

### Issue: Slow translation / High latency

**Symptoms**:
- 2-5 second delay between text detection and overlay
- Battery draining quickly

**Solution**:

1. **Public API is slow?**:
   - Use self-hosted LibreTranslate
   - Response time: 100-200ms (vs 300-500ms public)

2. **Reduce frame processing load**:
   - Increase `frameInterval` dari 100L ke 150L atau 200L
   - Fewer frames = less frequent OCR calls

3. **Optimize ORC**:
   - Some text blocks unnecessary
   - Filter small text (< 10 pixels)
   Edit `OCREngine.kt`:
   ```kotlin
   if (boundingBox.width() < 10 || boundingBox.height() < 10) continue
   ```

4. **Use profiler**:
   - Android Studio → Profiler (Ctrl+Alt+6)
   - Monitor CPU, Memory, Network

### Issue: Battery draining quickly

**Symptoms**:
- 5-10% battery per hour
- Device getting hot
- CPU usage 50%+

**Solution**:

1. **Reduce frequency**:
   ```kotlin
   private val frameInterval = 200L  // 5fps (was 10fps)
   ```

2. **Lower quality**:
   ```kotlin
   ImageReader.newInstance(width/2, height/2, ...)  // Half resolution
   ```

3. **Disable features**:
   - Uncheck "Detect Speech Bubbles"
   - Uncheck "Auto Translate"

4. **Monitor battery**:
   ```bash
   adb shell dumpsys batterystats | grep -A 10 "com.example.comictranslator"
   ```

## Device-Specific Issues

### Issue: Emulator too slow

**Symptoms**:
- App runs but very laggy
- Overlay updates every 2-3 seconds

**Solution**:

1. **Enable GPU acceleration**:
   - Create new AVD
   - Graphics: "Hardware - GLES 2.0"

2. **Allocate more RAM to emulator**:
   - AVD Config → Show Advanced Settings
   - RAM: 4GB atau lebih

3. **Use real device untuk testing**

### Issue: Older device support

**Symptoms**:
- App crashes on API 24-25 devices
- OCR/Translation fails

**Solution**:

1. **Test on device**:
   - Older devices might have less resources
   - Reduce frame rate: `frameInterval = 300L` (3fps)

2. **Reduce image quality**:
   ```kotlin
   ImageFormat.RGB_565  // Already using (good for older devices)
   ```

3. **Check ML Kit compatibility**:
   - ML Kit v16+ supports API 21+
   - Ensure Google Play Services updated

## Logcat Debugging

### Enable detailed logging

1. Android Studio → Logcat
2. Filter by app:
   ```
   package:com.example.comictranslator
   ```

3. Common log tags:
   - `MainActivity`
   - `OverlayService`
   - `ScreenCaptureHandler`
   - `OCREngine`
   - `TranslationService`
   - `ImageProcessor`

### Example logcat inspection

```bash
# Start logcat
adb logcat -c
adb logcat

# Watch for errors
adb logcat | grep -E "ERROR|Exception|ComicTranslator"

# Save to file
adb logcat > logcat.log
```

## Getting More Help

1. **Check Project Files**:
   - `README.md` - Features & overview
   - `TECHNICAL_DOCS.md` - Architecture details
   - `SETUP_GUIDE.md` - Installation help

2. **Android Documentation**:
   - MediaProjection: https://developer.android.com/reference/android/media/projection/MediaProjection
   - ML Kit: https://developers.google.com/ml-kit
   - WindowManager: https://developer.android.com/reference/android/view/WindowManager

3. **Common Android Issues**:
   - StackOverflow: Tag "android"
   - Check official Android docs

## Report a Bug

Jika sudah mencoba solusi di atas, report bug dengan:
1. Error message dan stacktrace
2. Device/emulator specs (model, Android version)
3. Steps to reproduce
4. Logcat output
5. Screenshots/video

---

**Troubleshooting Guide Version**: 1.0  
**Last Updated**: December 2025  
**Last Verified**: December 2025
