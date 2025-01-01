package com.example.githubproject.domain.model.repos


data class RepoDomain (
    val name: String,
    val id: Long,
    val owner: OwnerDomain,
    val defaultBranch: String,
    val language: String,
    val description: String?
)

data class OwnerDomain(
    val login: String
)