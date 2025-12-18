package com.example.comictranslator.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.comictranslator.model.TranslationResult

/**
 * Custom View untuk render overlay terjemahan dengan latar putih di atas teks asli
 */
class OverlayView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val translations = mutableListOf<TranslationResult>()
    private val lock = Any()

    init {
        setBackgroundColor(Color.TRANSPARENT)
        bgPaint.color = Color.WHITE
        bgPaint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        paint.textSize = 14f
    }

    fun updateTranslations(results: List<TranslationResult>) {
        synchronized(lock) {
            translations.clear()
            translations.addAll(results)
        }
        postInvalidate()
    }

    fun clearTranslations() {
        synchronized(lock) {
            translations.clear()
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(lock) {
            for (translation in translations) {
                drawTranslation(canvas, translation)
            }
        }
    }

    private fun drawTranslation(canvas: Canvas, translation: TranslationResult) {
        val bounds = translation.boundingBox
        val text = translation.translatedText

        if (text.isEmpty()) return

        // Apply rotation jika text tidak horizontal
        val angle = if (translation.isVertical) translation.angle else 0f

        canvas.save()
        
        // Translate ke center point untuk rotation
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        canvas.translate(centerX, centerY)
        canvas.rotate(angle)
        canvas.translate(-centerX, -centerY)

        // Calculate text layout
        val textLayout = StaticLayout.Builder.obtain(
            text,
            0,
            text.length,
            paint,
            bounds.width()
        ).apply {
            setAlignment(Layout.Alignment.ALIGN_CENTER)
            setLineSpacing(2f, 1.1f)
            setIncludePad(true)
        }.build()

        val textHeight = textLayout.height
        val padding = 8

        // Draw background (white box)
        val bgRect = RectF(
            bounds.left.toFloat() - padding,
            bounds.top.toFloat() - padding,
            bounds.right.toFloat() + padding,
            bounds.top.toFloat() + textHeight + padding + padding
        )
        
        // Rounded corners untuk aesthetic
        canvas.drawRoundRect(bgRect, 6f, 6f, bgPaint)

        // Draw border untuk lebih visible
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(bgRect, 6f, 6f, borderPaint)

        // Draw text
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        textLayout.draw(canvas)

        canvas.restore()
    }
}
