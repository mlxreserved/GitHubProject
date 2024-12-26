package com.example.githubproject.domain.model.user

data class UserInfoDomain(
    val login: String,
    val id: Long,
    val avatarUrl: String,
    val htmlUrl: String
)