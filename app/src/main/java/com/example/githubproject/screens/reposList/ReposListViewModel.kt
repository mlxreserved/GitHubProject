package com.example.githubproject.screens.reposList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubproject.domain.model.repos.RepoDomain
import com.example.githubproject.domain.usecase.GetRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ReposListViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
) : ViewModel() {

    private val _stateRepos: MutableStateFlow<StateRepos> = MutableStateFlow(StateRepos.Loading) // Инкапсуляция состояния
    val stateRepos: StateFlow<StateRepos> = _stateRepos.asStateFlow() // Состояние, которое нельзя изменить в фрагменте

    // При первом запуске фрагмента
    fun onOpenReposList(token: String) {
        getRepos(token = token)
    }

    // Обработка нажатия на кнопку refresh
    fun onRefreshButtonPressed(token: String) {
        getRepos(token = token)
    }

    // Обработка нажатия на кнопку retry
    fun onRetryButtonPressed(token: String) {
        getRepos(token = token)
    }

    // Получение всех репозиториев
    private fun getRepos(token: String) {
        viewModelScope.launch {
            _stateRepos.update { StateRepos.Loading }
            try {
                val listRepos = getRepositoriesUseCase.execute("Bearer $token")
                if(listRepos.isNotEmpty()) {
                    _stateRepos.update { StateRepos.Loaded(listRepos) }
                } else {
                    _stateRepos.update { StateRepos.Empty }
                }
            } catch (e: IOException) {
                _stateRepos.update { StateRepos.Error(IOEXCEPTION_NAME) }
            } catch (e: Exception) {
                _stateRepos.update { StateRepos.Error(EXCEPTION_NAME) }
            }
        }
    }

    sealed interface StateRepos {
        object Loading : StateRepos
        data class Loaded(val repos: List<RepoDomain>) : StateRepos
        data class Error(val error: String) : StateRepos
        object Empty : StateRepos
    }

    companion object {
        const val EXCEPTION_NAME = "Exception" // константа для названия Exception
        const val IOEXCEPTION_NAME = "IOException" // константа для названия IOException
    }
}