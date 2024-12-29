package com.example.githubproject.domain.usecase

import com.example.githubproject.domain.AppRepository
import com.example.githubproject.domain.model.repo.RepoDetailsDomain

class GetRepoDetailsInfoUseCase (private val appRepository: AppRepository) {

    suspend fun execute(owner: String, repo: String): RepoDetailsDomain = appRepository.getRepository(owner = owner, repo = repo)

}