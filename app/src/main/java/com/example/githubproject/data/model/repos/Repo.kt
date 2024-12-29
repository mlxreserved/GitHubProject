package com.example.githubproject.data.model.repos

import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val name: String,
    val owner: Owner,
    val language: String,
    val description: String?
)

@Serializable
data class Owner(
    val login: String
)