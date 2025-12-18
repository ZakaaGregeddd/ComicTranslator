package com.example.comictranslator.model

import android.graphics.Rect
import androidx.annotation.Keep

@Keep
data class TextBlock(
    val text: String,
    val boundingBox: Rect,
    val confidence: Float,
    val angle: Float = 0f,
    val isVertical: Boolean = false,
    val languageCode: String = "en"
)

@Keep
data class TranslationResult(
    val originalText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val boundingBox: Rect,
    val confidence: Float = 1f,
    val angle: Float = 0f,
    val isVertical: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Keep
data class SpeechBubble(
    val boundingBox: Rect,
    val type: BubbleType = BubbleType.SPEECH,
    val confidence: Float = 0.8f
)

enum class BubbleType {
    SPEECH, THOUGHT, NARRATIVE, SHOUT
}

@Keep
data class LanguageOption(
    val name: String,
    val code: String,
    val nativeName: String = ""
)

@Keep
data class TranslationRequest(
    val q: String,
    val source: String = "auto",
    val target: String = "id"
)

@Keep
data class TranslationResponse(
    val translatedText: String
)
