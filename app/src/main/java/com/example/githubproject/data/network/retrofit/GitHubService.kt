package com.example.githubproject.data.network.retrofit

import com.example.githubproject.data.network.model.repo.RepoDetails
import com.example.githubproject.data.network.model.repos.Repo
import com.example.githubproject.data.network.model.user.UserInfo
import kotlinx.serialization.json.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface GitHubService {
    @GET("/user")
    suspend fun signIn(@Header("Authorization") token: String): UserInfo

    @GET("/user/repos")
    suspend fun getRepositories(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int = 10): List<Repo>

    @GET("/repositories/{repoId}")
    suspend fun getRepository(
        @Path(value = "repoId",) repoId: String,
        @Header("Authorization") token: String
    ): RepoDetails
}