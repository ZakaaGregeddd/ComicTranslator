# Comic Translator - Real-Time Screen Translation App

Aplikasi Android untuk menerjemahkan teks di layar secara real-time, khususnya dioptimalkan untuk komik dan manga dengan support teks vertikal/horizontal.

## Fitur Utama

✅ **Real-Time Screen Translation**: Capture dan terjemahkan teks dari layar secara live
✅ **OCR dengan Deteksi Orientasi**: Support teks vertikal dan horizontal menggunakan Google ML Kit
✅ **Multiple Languages**: 14+ bahasa yang didukung
✅ **Overlay Translasi**: Terjemahan ditampilkan di atas teks asli dengan latar putih
✅ **Speech Bubble Detection**: Deteksi balon obrolan menggunakan contour analysis
✅ **Translation Caching**: Caching untuk efisiensi dan performa
✅ **Lightweight**: Frame throttling (10fps) untuk CPU/battery efficient
✅ **Free Resources**: Google ML Kit + LibreTranslate API (gratis)

## Teknologi yang Digunakan

- **OCR Engine**: Google ML Kit Vision (Text Recognition v2)
  - Unlimited free usage
  - On-device processing (privacy-friendly)
  - Multi-language support
  - Rotation detection

- **Translation API**: LibreTranslate
  - Completely free
  - Unlimited API calls (self-hosted atau public instance)
  - 25+ languages
  - Non-formal language support

- **Image Processing**: OpenCV
  - Speech bubble detection
  - Contour analysis
  - Image preprocessing (contrast enhancement, deskewing)

- **Framework**: Kotlin + Coroutines
  - Async screen capture
  - Non-blocking OCR/translation
  - Efficient threading

## Requirements

- Android 7.0+ (API Level 24)
- Permission: SYSTEM_ALERT_WINDOW (overlay display)
- Permission: MEDIA_PROJECTION (screen capture)
- Internet connection (untuk translation API)

## Setup & Installation

### 1. Clone/Setup Project
```bash
cd ComicTranslator
```

### 2. Build Project
```bash
./gradlew build
```

### 3. Run di Android Device
```bash
./gradlew installDebug
```

### 4. Permissions
App akan meminta 3 permission:
1. **Overlay Permission** - untuk menampilkan terjemahan di atas apps lain
2. **Camera Permission** - untuk screen capture
3. **Internet Permission** - untuk API translation

## Configuration

### Source Code Locations

| Component | File Path |
|-----------|-----------|
| Main Activity | `app/src/main/java/.../MainActivity.kt` |
| Overlay Service | `app/src/main/java/.../service/OverlayService.kt` |
| Screen Capture | `app/src/main/java/.../service/ScreenCaptureHandler.kt` |
| OCR Engine | `app/src/main/java/.../util/OCREngine.kt` |
| Translation Service | `app/src/main/java/.../util/RetrofitClient.kt` |
| Image Processor | `app/src/main/java/.../util/ImageProcessor.kt` |
| Overlay View | `app/src/main/java/.../ui/OverlayView.kt` |

### Language Options

```kotlin
SUPPORTED_LANGUAGES = listOf(
    "Auto Detect" (auto),
    "English" (en),
    "Indonesian" (id),
    "Spanish" (es),
    "French" (fr),
    "German" (de),
    "Japanese" (ja),
    "Chinese" (zh),
    "Korean" (ko),
    "Portuguese" (pt),
    "Russian" (ru),
    "Arabic" (ar),
    "Thai" (th),
    "Vietnamese" (vi)
)
```

### Translation API Configuration

**Default**: Public LibreTranslate API (api.libretranslate.de)

**Untuk Self-Hosted LibreTranslate**:
Edit `app/src/main/java/.../util/RetrofitClient.kt`:
```kotlin
private const val LIBRE_TRANSLATE_BASE_URL = "http://localhost:5000/"
```

Kemudian jalankan LibreTranslate server lokal:
```bash
docker run -d -p 5000:5000 libretranslate/libretranslate
```

## How to Use

1. **Buka Aplikasi** - Tap icon Comic Translator
2. **Pilih Bahasa**:
   - Source Language: Pilih bahasa source (default: Auto Detect)
   - Target Language: Pilih bahasa target (default: Indonesian)
3. **Konfigurasi Settings**:
   - Auto Translate: Aktif untuk continuous translation
   - Detect Orientation: Support teks vertikal/horizontal
   - Detect Bubbles: Deteksi speech bubbles (optional)
4. **Tap Start Translation**
5. **Grant Permission** - Izinkan overlay dan screen capture
6. **Baca Komik** - Aplikasi akan menampilkan terjemahan di atas panel
7. **Tap Stop** untuk menghentikan

## Performance Tuning

### Frame Rate
Default: 10fps (100ms interval)
Untuk mengubah, edit `ScreenCaptureHandler.kt`:
```kotlin
private val frameInterval = 100L  // milliseconds
```

### Deteksi Speech Bubble
Sensitivity dapat disesuaikan di `ImageProcessor.kt`:
```kotlin
if (area < 100 || area > bitmap.width * bitmap.height * 0.5) continue
```

### Translation Caching
Cache akan clear otomatis setiap session, atau manual:
```kotlin
TranslationService.clearCache()
```

## Troubleshooting

### Overlay tidak muncul?
- Check SYSTEM_ALERT_WINDOW permission di Settings > Apps > Comic Translator > Permissions
- Android 6.0+ membutuhkan grant manual dari user

### Translation lambat?
- Check internet connection
- Kurangi ukuran text (more text = slower translation)
- Gunakan self-hosted LibreTranslate untuk response time lebih cepat

### OCR tidak akurat?
- Pastikan text cukup besar dan jelas di layar
- Gunakan Auto Detect untuk source language
- Untuk comic dengan stylized text, akurasi bisa berkurang

### Overlay tidak ada di atas apps?
- Check WindowManager.LayoutParams.type:
  - API 26+: TYPE_APPLICATION_OVERLAY
  - API <26: TYPE_SYSTEM_ALERT

## Future Improvements

🔄 **Planned Features**:
1. **PaddleOCR Integration** - Better accuracy untuk comic-specific text
2. **Custom ML Model** - Fine-tuned untuk manga/non-formal language
3. **User Dictionary** - Common comic terms untuk better translation
4. **Text-to-Speech** - Audio output untuk translated text
5. **Color-coded Translation** - Different colors untuk berbagai tipe bubbles
6. **Local Translation** - Offline translation dengan lightweight models
7. **Settings Persistence** - Save user preferences
8. **Translation History** - Keep log of translated texts

## Architecture

```
MainActivity (UI Control)
    ↓
OverlayService (Background Service)
    ├→ ScreenCaptureHandler (MediaProjection)
    │   └→ Bitmap frames (10fps)
    │
    ├→ OCREngine (ML Kit)
    │   └→ List<TextBlock>
    │
    ├→ ImageProcessor (OpenCV)
    │   └→ List<SpeechBubble>
    │
    ├→ TranslationService (LibreTranslate API)
    │   └→ List<TranslationResult>
    │
    └→ OverlayView (WindowManager)
        └→ Render translations with white background
```

## Dependencies

- **Android Support**: API 24+
- **Google ML Kit Vision**: 16.0.0
- **Retrofit**: 2.10.0 (HTTP client)
- **OkHttp**: 4.11.0 (HTTP library)
- **OpenCV Android**: 4.5.2 (Optional, untuk speech bubble detection)
- **Room**: 2.6.1 (Local caching)
- **Coroutines**: 1.7.3 (Async programming)

## License

MIT License - Free to use and modify

## Support

Untuk issue/pertanyaan, silakan buat issue di repository atau hubungi developer.

---

**Version**: 1.0.0  
**Last Updated**: December 2025  
**Status**: Beta (Production Ready untuk testing)
