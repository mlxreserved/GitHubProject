package com.example.githubproject.domain.usecase

import com.example.githubproject.domain.UserContentRepository
import javax.inject.Inject

class GetRepositoryReadmeUseCase @Inject constructor(
    private val userContentRepository: UserContentRepository
) {
    suspend fun execute(
        ownerName: String,
        repositoryName: String,
        branchName: String,
        token: String
    ) = userContentRepository.getRepositoryReadme(
        ownerName = ownerName,
        repositoryName = repositoryName,
        branchName = branchName,
        token = token
    )
}