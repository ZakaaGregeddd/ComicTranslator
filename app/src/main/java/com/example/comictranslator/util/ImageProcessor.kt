package com.example.comictranslator.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.example.comictranslator.model.SpeechBubble
import com.example.comictranslator.model.BubbleType
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

/**
 * Image Processing untuk deteksi speech bubble dan preprocessing
 */
object ImageProcessor {
    private const val TAG = "ImageProcessor"

    /**
     * Deteksi speech bubble menggunakan contour detection
     * Ini adalah basic implementation, bisa di-improve dengan ML model
     */
    fun detectSpeechBubbles(bitmap: Bitmap): List<SpeechBubble> {
        return try {
            val mat = Mat()
            val grayMat = Mat()
            val blurredMat = Mat()
            val binaryMat = Mat()
            val cannyMat = Mat()
            val contours = mutableListOf<MatOfPoint>()
            val hierarchy = Mat()

            // Convert Bitmap to Mat
            org.opencv.android.Utils.bitmapToMat(bitmap, mat)

            // Convert to grayscale
            Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)

            // Apply blur untuk smoothing
            Imgproc.GaussianBlur(grayMat, blurredMat, Size(5.0, 5.0), 0.0)

            // Apply threshold
            Imgproc.threshold(blurredMat, binaryMat, 100.0, 255.0, Imgproc.THRESH_BINARY_INV)

            // Apply Canny edge detection
            Imgproc.Canny(blurredMat, cannyMat, 50.0, 150.0)

            // Find contours
            Imgproc.findContours(
                cannyMat, contours, hierarchy,
                Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE
            )

            val bubbles = mutableListOf<SpeechBubble>()

            for (contour in contours) {
                val area = Imgproc.contourArea(contour)
                
                // Filter contours berdasarkan area (minimum size untuk bubble)
                if (area < 100 || area > bitmap.width * bitmap.height * 0.5) continue

                val boundingRect = Imgproc.boundingRect(contour)
                val approxCurve = Mat()
                val peri = Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true)
                Imgproc.approxPolyDP(MatOfPoint2f(*contour.toArray()), approxCurve, 0.02 * peri, true)

                val approxPoints = approxCurve.toList()
                if (approxPoints.size >= 4) {
                    val rect = Rect(
                        boundingRect.x,
                        boundingRect.y,
                        boundingRect.width,
                        boundingRect.height
                    )

                    val bubbleType = determineBubbleType(approxPoints.size)
                    val confidence = calculateConfidence(area, approxPoints.size)

                    bubbles.add(
                        SpeechBubble(
                            boundingBox = rect,
                            type = bubbleType,
                            confidence = confidence
                        )
                    )
                }
            }

            mat.release()
            grayMat.release()
            blurredMat.release()
            binaryMat.release()
            cannyMat.release()
            hierarchy.release()

            bubbles
        } catch (e: Exception) {
            Log.e(TAG, "Speech bubble detection error: ${e.message}")
            emptyList()
        }
    }

    /**
     * Tentukan tipe bubble berdasarkan jumlah corner points
     */
    private fun determineBubbleType(pointCount: Int): BubbleType {
        return when {
            pointCount > 8 -> BubbleType.THOUGHT
            pointCount > 6 -> BubbleType.SHOUT
            pointCount > 4 -> BubbleType.NARRATIVE
            else -> BubbleType.SPEECH
        }
    }

    /**
     * Calculate confidence score untuk bubble detection
     */
    private fun calculateConfidence(area: Double, pointCount: Int): Float {
        val areaScore = (area / 100000.0).coerceIn(0.0, 1.0).toFloat()
        val shapeScore = when {
            pointCount >= 4 && pointCount <= 8 -> 1f
            else -> 0.7f
        }
        return (areaScore + shapeScore) / 2
    }
}

// Extension function untuk convert Mat ke list
private fun Mat.toList(): List<Point> {
    val list = mutableListOf<Point>()
    for (i in 0 until rows()) {
        list.add(Point(get(i, 0)[0], get(i, 1)[0]))
    }
    return list
}
