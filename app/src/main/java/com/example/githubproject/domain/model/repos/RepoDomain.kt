package com.example.githubproject.domain.model.repos


data class RepoDomain (
    val name: String,
    val owner: OwnerDomain,
    val language: String,
    val description: String?
)

data class OwnerDomain(
    val login: String
)