package com.example.githubproject.domain.usecase

import com.example.githubproject.domain.AppRepository
import com.example.githubproject.domain.model.repos.RepoDomain

class GetRepositoriesUseCase (private val appRepository: AppRepository) {

    suspend fun execute(): List<RepoDomain> = appRepository.getRepositories()

}