package com.example.githubproject.di

import com.example.githubproject.data.GithubRepositoryImpl
import com.example.githubproject.data.UserContentRepositoryImpl
import com.example.githubproject.data.retrofit.GitHubService
import com.example.githubproject.data.retrofit.RetrofitClient
import com.example.githubproject.data.retrofit.UserContentService
import com.example.githubproject.domain.GitHubRepository
import com.example.githubproject.domain.UserContentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideGithubRepository(gitHubClient: GitHubService): GitHubRepository {
        return GithubRepositoryImpl(gitHubClient = gitHubClient)
    }

    @Provides
    fun provideUserContentRepository(userContentClient: UserContentService): UserContentRepository {
        return UserContentRepositoryImpl(userContentClient = userContentClient)
    }

    @Provides
    fun provideGitHubService(): GitHubService {
        return RetrofitClient.getClient()
    }

    @Provides
    fun provideUserContentService(): UserContentService {
        return RetrofitClient.getClientReadme()
    }

}