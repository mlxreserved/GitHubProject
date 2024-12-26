package com.example.githubproject.data

import com.example.githubproject.domain.model.user.UserInfoDomain
import com.example.githubproject.data.model.user.UserInfo
import com.example.githubproject.data.retrofit.GitHubService
import com.example.githubproject.domain.AppRepository
import retrofit2.Call

class AppRepositoryImpl (private val client: GitHubService) : AppRepository {

    override suspend fun signIn(token: String): UserInfoDomain {
        val res = client.signIn(token)
        return mapUserInfoToUserInfoDomain(res)
    }

    private fun mapUserInfoToUserInfoDomain(userInfo: UserInfo): UserInfoDomain {
        return UserInfoDomain(
            login = userInfo.login,
            id = userInfo.id,
            avatarUrl = userInfo.avatarUrl,
            htmlUrl = userInfo.htmlUrl
        )
    }
}