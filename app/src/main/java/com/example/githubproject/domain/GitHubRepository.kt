package com.example.githubproject.domain

import com.example.githubproject.domain.model.repo.RepoDetailsDomain
import com.example.githubproject.domain.model.repos.RepoDomain
import com.example.githubproject.domain.model.user.UserInfoDomain

interface GitHubRepository {

    suspend fun signIn(token: String): UserInfoDomain

    suspend fun getRepositories(token: String): List<RepoDomain>

    suspend fun getRepository(repoId: String, token: String): RepoDetailsDomain

}