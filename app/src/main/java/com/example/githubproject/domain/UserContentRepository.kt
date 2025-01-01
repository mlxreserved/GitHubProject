package com.example.githubproject.domain

import okhttp3.ResponseBody
import retrofit2.Call

interface UserContentRepository {

    suspend fun getRepositoryReadme(ownerName: String, repositoryName: String, branchName: String, token: String): Call<ResponseBody>

}