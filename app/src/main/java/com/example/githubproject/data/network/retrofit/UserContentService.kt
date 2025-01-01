package com.example.githubproject.data.network.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface UserContentService {

    @GET("/{owner}/{repo}/{branch}/README.md")
    fun getRepositoryReadme(
        @Path(value = "owner") ownerName: String,
        @Path(value = "repo") repositoryName: String,
        @Path(value = "branch") branchName: String,
        @Header("Authorization") token: String
    ): Call<ResponseBody>

}