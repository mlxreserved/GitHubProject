package com.example.githubproject.domain.model.repo

data class RepoDetailsDomain (
    val name: String,
    val stargazersCount: Int,
    val watchersCount: Int,
    val forksCount: Int,
    val license: LicenseDomain?,
    val htmlUrl: String
)

data class LicenseDomain (
    val spdxId: String
)