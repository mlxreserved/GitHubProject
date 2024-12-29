package com.example.githubproject.domain

import com.example.githubproject.data.model.repo.RepoDetails
import com.example.githubproject.domain.model.repo.RepoDetailsDomain
import com.example.githubproject.domain.model.repos.RepoDomain
import com.example.githubproject.domain.model.user.UserInfoDomain

interface AppRepository {

    suspend fun signIn(token: String): UserInfoDomain

    suspend fun getRepositories(): List<RepoDomain>

    suspend fun getRepository(owner: String, repo: String): RepoDetailsDomain
}