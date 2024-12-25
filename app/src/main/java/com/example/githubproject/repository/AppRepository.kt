package com.example.githubproject.repository

import com.example.githubproject.model.UserInfo
import com.example.githubproject.service.GitHubService
import com.example.githubproject.service.RetrofitClient
import retrofit2.Retrofit

class AppRepository (private val client: GitHubService){

    suspend fun signIn(token: String): UserInfo {
        client.signIn(token)
        return UserInfo("123")
    }

}