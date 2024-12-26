package com.example.githubproject.data.retrofit

import com.example.githubproject.data.model.user.UserInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


interface GitHubService {
    @GET("/user")
    suspend fun signIn(@Header("Authorization") token: String): UserInfo
}