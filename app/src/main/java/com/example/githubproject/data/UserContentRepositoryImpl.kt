package com.example.githubproject.data

import com.example.githubproject.data.retrofit.UserContentService
import com.example.githubproject.domain.UserContentRepository
import okhttp3.ResponseBody
import retrofit2.Call
import javax.inject.Inject

class UserContentRepositoryImpl @Inject constructor(
    private val userContentClient: UserContentService
) : UserContentRepository {

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String,
        token: String
    ): Call<ResponseBody> {
        val call = userContentClient.getRepositoryReadme(
            ownerName = ownerName,
            repositoryName = repositoryName,
            branchName = branchName,
            token = token
        )
        return call
    }

}