package com.example.githubproject.data

import com.example.githubproject.data.model.repo.License
import com.example.githubproject.data.model.repo.RepoDetails
import com.example.githubproject.data.model.repos.Owner
import com.example.githubproject.data.model.repos.Repo
import com.example.githubproject.domain.model.user.UserInfoDomain
import com.example.githubproject.data.model.user.UserInfo
import com.example.githubproject.data.retrofit.GitHubService
import com.example.githubproject.domain.GitHubRepository
import com.example.githubproject.domain.model.repo.LicenseDomain
import com.example.githubproject.domain.model.repo.RepoDetailsDomain
import com.example.githubproject.domain.model.repos.OwnerDomain
import com.example.githubproject.domain.model.repos.RepoDomain
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
        private val gitHubClient: GitHubService
) : GitHubRepository {

    override suspend fun signIn(token: String): UserInfoDomain {
        val res = gitHubClient.signIn(token)
        return mapUserInfoToUserInfoDomain(res)
    }

    override suspend fun getRepositories(token: String): List<RepoDomain> {
        val res = gitHubClient.getRepositories(token = token)
        return res.map { mapRepoToRepoDomain(it) }
    }

    override suspend fun getRepository(repoId: String, token: String): RepoDetailsDomain {
        val res = gitHubClient.getRepository(repoId = repoId, token = token)
        return mapRepoDetailsToRepoDetailsDomain(res)
    }


    // Маппер для преобразования UserInfo в UserInfoDomain
    private fun mapUserInfoToUserInfoDomain(userInfo: UserInfo): UserInfoDomain {
        return UserInfoDomain(
            login = userInfo.login,
            id = userInfo.id,
            avatarUrl = userInfo.avatarUrl,
            htmlUrl = userInfo.htmlUrl
        )
    }

    // Маппер для преобразования Repo в RepoDomain
    private fun mapRepoToRepoDomain(repo: Repo): RepoDomain {
        return RepoDomain(
            name = repo.name,
            id = repo.id,
            owner = mapOwnerToOwnerDomain(repo.owner),
            defaultBranch = repo.defaultBranch,
            language = repo.language,
            description = repo.description
        )
    }
    // Маппер для преобразования Owner в OwnerDomain
    private fun mapOwnerToOwnerDomain(owner: Owner): OwnerDomain {
        return OwnerDomain(login = owner.login)
    }

    // Маппер для преобразования RepoDetails в RepoDetailsDomain
    private fun mapRepoDetailsToRepoDetailsDomain(repoDetails: RepoDetails): RepoDetailsDomain {
        return RepoDetailsDomain(
            name = repoDetails.name,
            stargazersCount = repoDetails.stargazersCount,
            watchersCount = repoDetails.watchersCount,
            forksCount = repoDetails.forksCount,
            license = mapLicenseToLicenseDomain(repoDetails.license),
            htmlUrl = repoDetails.htmlUrl
        )
    }
    // Маппер для преобразования License в LicenseDomain
    private fun mapLicenseToLicenseDomain(license: License?): LicenseDomain? {
        return if(license != null) {
            LicenseDomain(license.spdxId)
        } else {
            null
        }
    }

}