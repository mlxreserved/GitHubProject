package com.example.githubproject.di

import com.example.githubproject.data.network.repositories.GithubRepositoryImpl
import com.example.githubproject.data.network.repositories.UserContentRepositoryImpl
import com.example.githubproject.data.network.retrofit.GitHubService
import com.example.githubproject.data.network.retrofit.RetrofitClient
import com.example.githubproject.data.network.retrofit.UserContentService
import com.example.githubproject.domain.GitHubRepository
import com.example.githubproject.domain.UserContentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGithubRepository(gitHubClient: GitHubService): GitHubRepository {
        return GithubRepositoryImpl(gitHubClient = gitHubClient)
    }

    @Singleton
    @Provides
    fun provideUserContentRepository(userContentClient: UserContentService): UserContentRepository {
        return UserContentRepositoryImpl(userContentClient = userContentClient)
    }

    @Singleton
    @Provides
    fun provideGitHubService(): GitHubService {
        return RetrofitClient.getClient()
    }

    @Singleton
    @Provides
    fun provideUserContentService(): UserContentService {
        return RetrofitClient.getClientReadme()
    }

}