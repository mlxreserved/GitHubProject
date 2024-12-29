package com.example.githubproject.screens.reposList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubproject.domain.usecase.GetRepositoriesUseCase

class ReposListViewModelFactory(
    private val getRepositoriesUseCase: GetRepositoriesUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReposListViewModel(
            getRepositoriesUseCase = getRepositoriesUseCase
        ) as T
    }

}