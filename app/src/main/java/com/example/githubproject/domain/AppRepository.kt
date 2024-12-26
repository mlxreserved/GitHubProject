package com.example.githubproject.domain

import com.example.githubproject.domain.model.user.UserInfoDomain

interface AppRepository {

    suspend fun signIn(token: String): UserInfoDomain

}