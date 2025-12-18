package com.example.comictranslator.util

import android.content.Context
import com.example.comictranslator.api.TranslationAPI
import com.example.comictranslator.model.TranslationRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private const val LIBRE_TRANSLATE_BASE_URL = "https://api.libretranslate.de/"
    
    // Untuk local self-hosted instance, gunakan:
    // private const val LIBRE_TRANSLATE_BASE_URL = "http://localhost:5000/"

    fun getRetrofitInstance(): Retrofit {
        if (retrofit == null) {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(LIBRE_TRANSLATE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return retrofit!!
    }

    fun getTranslationAPI(): TranslationAPI {
        return getRetrofitInstance().create(TranslationAPI::class.java)
    }
}

/**
 * Translation service dengan caching dan error handling
 */
object TranslationService {
    private val translationCache = mutableMapOf<String, String>()
    private val translationAPI = RetrofitClient.getTranslationAPI()

    suspend fun translate(
        text: String,
        sourceLanguage: String = "auto",
        targetLanguage: String = "id"
    ): String {
        // Jika text kosong, return kosong
        if (text.isBlank()) return ""

        // Buat cache key
        val cacheKey = "$text|$sourceLanguage|$targetLanguage"

        // Check cache
        translationCache[cacheKey]?.let { return it }

        return try {
            val request = TranslationRequest(
                q = text,
                source = sourceLanguage,
                target = targetLanguage
            )
            val response = translationAPI.translate(request)
            response.translatedText.also {
                // Simpan ke cache
                translationCache[cacheKey] = it
            }
        } catch (e: Exception) {
            // Fallback: return text asli jika error
            android.util.Log.e("TranslationService", "Translation error: ${e.message}")
            text
        }
    }

    fun clearCache() {
        translationCache.clear()
    }
}
