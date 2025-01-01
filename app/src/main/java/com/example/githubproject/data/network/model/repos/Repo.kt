package com.example.githubproject.data.network.model.repos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val name: String,
    val id: Long,
    @SerialName("default_branch")
    val defaultBranch: String,
    val owner: Owner,
    val language: String,
    val description: String?
)

@Serializable
data class Owner(
    val login: String
)