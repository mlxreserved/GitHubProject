package com.example.githubproject.service

import com.example.githubproject.model.UserInfo
import retrofit2.http.GET


interface GitHubService {
    @GET("user/repos")
    suspend fun signIn(token: String): UserInfo
}