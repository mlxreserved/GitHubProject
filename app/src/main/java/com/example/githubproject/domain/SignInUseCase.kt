package com.example.githubproject.domain

import com.example.githubproject.domain.model.user.UserInfoDomain
import retrofit2.Call

class SignInUseCase(private val appRepository: AppRepository) {

    suspend fun execute(token: String): UserInfoDomain {
        return appRepository.signIn(token)
    }

}