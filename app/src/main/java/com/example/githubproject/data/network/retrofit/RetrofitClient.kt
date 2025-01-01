package com.example.githubproject.data.network.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create

object RetrofitClient {

    private const val BASE_URL_GIT_HUB = "https://api.github.com/"
    private const val BASE_URL_USER_CONTENT = "https://raw.githubusercontent.com/"

    // Неавторизованный клиент
    fun getClient(): GitHubService {
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_GIT_HUB)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create()
    }
    // Клиент для получения Readme
    fun getClientReadme(): UserContentService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_USER_CONTENT)
            .build()

        return retrofit.create()
    }
}