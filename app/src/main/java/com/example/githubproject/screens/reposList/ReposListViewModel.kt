package com.example.githubproject.screens.reposList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubproject.domain.model.repos.RepoDomain
import com.example.githubproject.domain.usecase.GetRepositoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class ReposListViewModel(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
) : ViewModel() {

    private val _stateRepos: MutableStateFlow<StateRepos> = MutableStateFlow(StateRepos.Loading) // Инкапсуляция состояния
    val stateRepos: StateFlow<StateRepos> = _stateRepos.asStateFlow() // Состояние, которое нельзя изменить в фрагменте

    // При первом запуске фрагмента
    init {
        getRepos()
    }

    // Обработка нажатия на кнопку refresh
    fun onRefreshButtonPressed() {
        getRepos()
    }

    // Обработка нажатия на кнопку retry
    fun onRetryButtonPressed() {
        getRepos()
    }

    // Получение всех репозиториев
    private fun getRepos() {
        viewModelScope.launch {
            _stateRepos.update { StateRepos.Loading }
            try {
                val listRepos = getRepositoriesUseCase.execute()
                if(listRepos.isNotEmpty()) {
                    _stateRepos.update { StateRepos.Loaded(listRepos) }
                } else {
                    _stateRepos.update { StateRepos.Empty }
                }
            } catch (e: IOException) {
                _stateRepos.update { StateRepos.Error(e.javaClass.name) }
            } catch (e: Exception) {
                _stateRepos.update { StateRepos.Error(e.javaClass.name) }
            }
        }
    }

    sealed interface StateRepos {
        object Loading : StateRepos
        data class Loaded(val repos: List<RepoDomain>) : StateRepos
        data class Error(val error: String) : StateRepos
        object Empty : StateRepos
    }
}