package com.example.githubproject.data.retrofit

import com.example.githubproject.data.interceptor.AuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

object RetrofitClient{

    private const val BASE_URL = "https://api.github.com/"

    fun getClient(): GitHubService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json {ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create()
    }

}