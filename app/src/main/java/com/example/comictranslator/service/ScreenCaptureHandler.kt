package com.example.comictranslator.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.util.Log
import kotlinx.coroutines.*

/**
 * Screen Capture Handler menggunakan MediaProjection
 * Frame throttling untuk performa optimal tanpa drain baterai
 */
class ScreenCaptureHandler(
    private val context: Context,
    private val mediaProjection: MediaProjection,
    private val displayWidth: Int,
    private val displayHeight: Int,
    private val density: Int = 1
) {
    private val TAG = "ScreenCaptureHandler"
    private var imageReader: ImageReader? = null
    private var coroutineScope: CoroutineScope? = null
    private var captureCallback: ((Bitmap) -> Unit)? = null
    private var isCapturing = false

    // Frame throttling - capture 10 fps untuk balance antara latency dan CPU usage
    private val frameInterval = 100L

    init {
        setupImageReader()
    }

    private fun setupImageReader() {
        try {
            imageReader = ImageReader.newInstance(
                displayWidth / density,
                displayHeight / density,
                ImageFormat.RGB_565,
                2
            ).apply {
                setOnImageAvailableListener({ reader ->
                    handleNewImage(reader)
                }, null)
            }

            val surface = imageReader?.surface ?: return
            mediaProjection.createVirtualDisplay(
                "ComicTranslator",
                displayWidth / density,
                displayHeight / density,
                density,
                0,
                surface,
                null,
                null
            )

            Log.d(TAG, "Screen capture initialized: ${displayWidth / density}x${displayHeight / density}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup image reader: ${e.message}")
        }
    }

    private fun handleNewImage(reader: ImageReader) {
        try {
            val image = reader.acquireLatestImage() ?: return
            val bitmap = imageToBitmap(image)
            image.close()

            bitmap?.let {
                captureCallback?.invoke(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling image: ${e.message}")
        }
    }

    private fun imageToBitmap(image: Image): Bitmap? {
        return try {
            val data = image.planes[0]
            val buffer = data.buffer
            val pixelStride = data.pixelStride
            val width = image.width
            val height = image.height
            val pitch = data.rowPadding
            val pixelData = ByteArray(buffer.remaining())
            buffer.get(pixelData)

            val bitmap = Bitmap.createBitmap(width + pitch / pixelStride, height, Bitmap.Config.RGB_565)
            bitmap.copyPixelsFromBuffer(buffer)

            // Crop ke actual size
            if (pitch == 0) {
                bitmap
            } else {
                Bitmap.createBitmap(bitmap, 0, 0, width, height)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Image conversion error: ${e.message}")
            null
        }
    }

    fun startCapture(onCapture: (Bitmap) -> Unit) {
        if (isCapturing) return

        captureCallback = onCapture
        isCapturing = true

        coroutineScope = CoroutineScope(Dispatchers.Default + Job()).apply {
            launch {
                var lastCaptureTime = 0L
                while (isCapturing) {
                    val now = System.currentTimeMillis()
                    if (now - lastCaptureTime >= frameInterval) {
                        lastCaptureTime = now
                    }
                    delay(10)
                }
            }
        }

        Log.d(TAG, "Screen capture started")
    }

    fun stopCapture() {
        isCapturing = false
        coroutineScope?.cancel()
        Log.d(TAG, "Screen capture stopped")
    }

    fun release() {
        stopCapture()
        imageReader?.close()
        mediaProjection.stop()
        Log.d(TAG, "Resources released")
    }
}
