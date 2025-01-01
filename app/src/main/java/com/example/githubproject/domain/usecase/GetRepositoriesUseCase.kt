package com.example.githubproject.domain.usecase

import com.example.githubproject.domain.GitHubRepository
import com.example.githubproject.domain.model.repos.RepoDomain
import javax.inject.Inject

class GetRepositoriesUseCase @Inject constructor(
    private val appRepository: GitHubRepository
) {
    suspend fun execute(token: String): List<RepoDomain> = appRepository.getRepositories(token = token)
}