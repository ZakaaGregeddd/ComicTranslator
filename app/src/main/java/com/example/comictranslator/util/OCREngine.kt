package com.example.comictranslator.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.example.comictranslator.model.TextBlock
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.math.abs
import kotlin.math.atan2

/**
 * OCR Recognition Engine menggunakan Google ML Kit
 * Mendukung deteksi teks dengan rotasi (vertikal dan horizontal)
 */
object OCREngine {
    private const val TAG = "OCREngine"
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognizeText(bitmap: Bitmap): List<TextBlock> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val visionText = recognizer.process(image).addOnSuccessListener { }
                .let { task ->
                    task.result ?: return emptyList()
                }

            val textBlocks = mutableListOf<TextBlock>()

            for (block in visionText.textBlocks) {
                val boundingBox = block.boundingBox ?: continue
                val text = block.text

                // Hitung angle/rotasi dari bounding box
                val angle = calculateTextAngle(block.cornerPoints)
                val isVertical = abs(angle) > 45f && abs(angle) < 135f

                val textBlock = TextBlock(
                    text = text,
                    boundingBox = boundingBox,
                    confidence = 0.95f, // ML Kit tidak expose confidence untuk text recognition
                    angle = angle,
                    isVertical = isVertical
                )
                textBlocks.add(textBlock)

                Log.d(TAG, "Detected text: '$text' | Angle: ${angle}° | Vertical: $isVertical")
            }

            textBlocks
        } catch (e: Exception) {
            Log.e(TAG, "OCR Error: ${e.message}")
            emptyList()
        }
    }

    /**
     * Hitung angle/rotasi text dari corner points
     */
    private fun calculateTextAngle(cornerPoints: Array<android.graphics.PointF>?): Float {
        if (cornerPoints == null || cornerPoints.size < 2) return 0f

        val p0 = cornerPoints[0]
        val p1 = cornerPoints[1]

        val deltaX = p1.x - p0.x
        val deltaY = p1.y - p0.y

        return (Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble()))).toFloat()
    }
}
