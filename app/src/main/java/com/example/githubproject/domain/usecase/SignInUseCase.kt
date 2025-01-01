package com.example.githubproject.domain.usecase

import com.example.githubproject.domain.GitHubRepository
import com.example.githubproject.domain.model.user.UserInfoDomain
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val appRepository: GitHubRepository
) {
    suspend fun execute(token: String): UserInfoDomain = appRepository.signIn(token)
}