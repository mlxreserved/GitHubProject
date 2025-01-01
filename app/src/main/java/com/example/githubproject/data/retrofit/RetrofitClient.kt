package com.example.githubproject.data.retrofit

import com.example.githubproject.data.retrofit.interceptor.AuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Singleton

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

//    // Авторизованный клиент
//    fun getAuthenticatedClient(token: String): GitHubService {
//        val json = Json { ignoreUnknownKeys = true }
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor(token))
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL_GIT_HUB)
//            .client(client)
//            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
//            .build()
//
//        return retrofit.create()
//    }

    // Клиент для получения Readme
    fun getClientReadme(): UserContentService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_USER_CONTENT)
            .build()

        return retrofit.create()
    }
}