package com.example.githubproject.data.network.model.repo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetails (
    val name: String,
    @SerialName("stargazers_count")
    val stargazersCount: Int,
    @SerialName("watchers_count")
    val watchersCount: Int,
    @SerialName("forks_count")
    val forksCount: Int,
    val license: License?,
    @SerialName("html_url")
    val htmlUrl: String
)

@Serializable
data class License (
    @SerialName("spdx_id")
    val spdxId: String
)