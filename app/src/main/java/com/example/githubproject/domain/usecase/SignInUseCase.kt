package com.example.githubproject.domain.usecase

import com.example.githubproject.domain.AppRepository
import com.example.githubproject.domain.model.user.UserInfoDomain

class SignInUseCase(private val appRepository: AppRepository) {

    suspend fun execute(token: String): UserInfoDomain = appRepository.signIn(token)

}