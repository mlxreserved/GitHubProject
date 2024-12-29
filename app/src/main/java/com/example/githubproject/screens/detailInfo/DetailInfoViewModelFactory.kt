package com.example.githubproject.screens.detailInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubproject.domain.usecase.GetRepoDetailsInfoUseCase

class DetailInfoViewModelFactory(
    private val getRepoDetailsInfoUseCase: GetRepoDetailsInfoUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailInfoViewModel(
            getRepoDetailsInfoUseCase
        ) as T
    }

}