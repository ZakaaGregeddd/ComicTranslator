package com.example.comictranslator.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.example.comictranslator.MainActivity
import com.example.comictranslator.R
import com.example.comictranslator.model.TranslationResult
import com.example.comictranslator.ui.OverlayView
import com.example.comictranslator.util.OCREngine
import com.example.comictranslator.util.TranslationService
import com.example.comictranslator.util.ImageProcessor
import kotlinx.coroutines.*

/**
 * Service untuk menampilkan overlay translasi di atas aplikasi lain
 */
class OverlayService : Service() {
    companion object {
        private const val TAG = "OverlayService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ComicTranslatorOverlay"
    }

    private lateinit var windowManager: WindowManager
    private var overlayView: OverlayView? = null
    private var params: WindowManager.LayoutParams? = null
    private var serviceJob: Job? = null
    private var isRunning = false

    // Konfigurasi terjemahan
    private var sourceLanguage = "auto"
    private var targetLanguage = "id"
    private var detectBubbles = true
    private var detectOrientation = true

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "OverlayService created")
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val source = intent?.getStringExtra("source_language") ?: "auto"
        val target = intent?.getStringExtra("target_language") ?: "id"
        val bubbles = intent?.getBooleanExtra("detect_bubbles", true) ?: true
        val orientation = intent?.getBooleanExtra("detect_orientation", true) ?: true

        sourceLanguage = source
        targetLanguage = target
        detectBubbles = bubbles
        detectOrientation = orientation

        when (action) {
            "START" -> startOverlay()
            "STOP" -> stopOverlay()
        }

        return START_STICKY
    }

    private fun startOverlay() {
        if (isRunning) return

        isRunning = true
        startForeground(NOTIFICATION_ID, createNotification())

        overlayView = OverlayView(this)
        params = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            x = 0
            y = 0
            gravity = Gravity.TOP or Gravity.START
        }

        try {
            windowManager.addView(overlayView, params)
            Log.d(TAG, "Overlay view added to window")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add overlay view: ${e.message}")
            stopSelf()
        }

        Log.d(TAG, "Overlay started with source: $sourceLanguage, target: $targetLanguage")
    }

    private fun stopOverlay() {
        if (!isRunning) return

        isRunning = false
        serviceJob?.cancel()

        try {
            if (overlayView != null && params != null) {
                windowManager.removeView(overlayView)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing overlay: ${e.message}")
        }

        overlayView = null
        params = null

        Log.d(TAG, "Overlay stopped")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun processFrame(bitmap: Bitmap) {
        if (!isRunning || overlayView == null) return

        serviceJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                // 1. OCR - Recognize text dalam frame
                val textBlocks = OCREngine.recognizeText(bitmap)
                if (textBlocks.isEmpty()) {
                    overlayView?.clearTranslations()
                    return@launch
                }

                // 2. Deteksi speech bubble (optional)
                val bubbles = if (detectBubbles) {
                    ImageProcessor.detectSpeechBubbles(bitmap)
                } else {
                    emptyList()
                }

                // 3. Translate setiap text block
                val translations = mutableListOf<TranslationResult>()
                for (block in textBlocks) {
                    val translatedText = TranslationService.translate(
                        block.text,
                        sourceLanguage,
                        targetLanguage
                    )

                    translations.add(
                        TranslationResult(
                            originalText = block.text,
                            translatedText = translatedText,
                            sourceLanguage = sourceLanguage,
                            targetLanguage = targetLanguage,
                            boundingBox = block.boundingBox,
                            confidence = block.confidence,
                            angle = block.angle,
                            isVertical = block.isVertical
                        )
                    )

                    Log.d(TAG, "Translated: '${block.text}' -> '$translatedText'")
                }

                // 4. Update overlay dengan hasil terjemahan
                withContext(Dispatchers.Main) {
                    overlayView?.updateTranslations(translations)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Frame processing error: ${e.message}", e)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Comic Translator Overlay",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Comic Translator")
            .setContentText("Translating screen content...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
        Log.d(TAG, "OverlayService destroyed")
    }
}
