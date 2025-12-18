package com.example.comictranslator.api

import com.example.comictranslator.model.TranslationRequest
import com.example.comictranslator.model.TranslationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TranslationAPI {
    @POST("/translate")
    suspend fun translate(@Body request: TranslationRequest): TranslationResponse
}
