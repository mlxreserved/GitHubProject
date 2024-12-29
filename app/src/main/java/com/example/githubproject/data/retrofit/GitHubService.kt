package com.example.githubproject.data.retrofit

import com.example.githubproject.data.model.repo.RepoDetails
import com.example.githubproject.data.model.repos.Repo
import com.example.githubproject.data.model.user.UserInfo
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface GitHubService {
    @GET("user")
    suspend fun signIn(@Header("Authorization") token: String): UserInfo

    @GET("/user/repos")
    suspend fun getRepositories(@Query("per_page") perPage: Int = 10): List<Repo>

    @GET("/repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path(value = "owner") owner: String,
        @Path(value = "repo") repo: String): RepoDetails
}