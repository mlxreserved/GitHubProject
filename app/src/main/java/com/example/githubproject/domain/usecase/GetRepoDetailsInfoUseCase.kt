package com.example.githubproject.domain.usecase

import com.example.githubproject.domain.GitHubRepository
import com.example.githubproject.domain.model.repo.RepoDetailsDomain
import javax.inject.Inject

class GetRepoDetailsInfoUseCase @Inject constructor(
    private val appRepository: GitHubRepository
) {
    suspend fun execute(repoId: String, token: String): RepoDetailsDomain = appRepository.getRepository(repoId = repoId, token = token)
}