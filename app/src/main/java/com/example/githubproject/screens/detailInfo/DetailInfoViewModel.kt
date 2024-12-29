package com.example.githubproject.screens.detailInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubproject.domain.model.repo.RepoDetailsDomain
import com.example.githubproject.domain.usecase.GetRepoDetailsInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class DetailInfoViewModel (
    private val getRepoDetailsInfoUseCase: GetRepoDetailsInfoUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _stateReadme: MutableStateFlow<ReadmeState> = MutableStateFlow(ReadmeState.Loading)
    val stateReadme: StateFlow<ReadmeState> = _stateReadme.asStateFlow()

    fun onOpenRepoDetailInfo(owner: String, repo: String) {
        getRepo(owner = owner, repo = repo)
    }

    private fun getRepo(owner: String, repo: String) {
        viewModelScope.launch {
            _state.update { State.Loading }
            try {
                val githubRepo = getRepoDetailsInfoUseCase.execute(owner = owner, repo = repo)
                getReadme()
                _state.update { State.Loaded(githubRepo = githubRepo, readmeState = _stateReadme.value) }
            } catch (e: IOException) {
                _state.update { State.Error(e.javaClass.name) }
            } catch (e: Exception) {
                _state.update { State.Error(e.javaClass.name) }
            }
        }

    }

    private suspend fun getReadme() {

    }

    sealed interface State {
        object Loading : State // Состояние загрузки информации о репозитории
        data class Error(val error: String) : State // Состояние ошибки при загрузке

        data class Loaded(
            val githubRepo: RepoDetailsDomain, // Информация об успешно загруженном репозитории
            val readmeState: ReadmeState // Состояние загрузки readme
        ) : State // Состояние успешной загрузки
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState // Состояние загрузки readme
        object Empty : ReadmeState // Состояние отсутсвия readme
        data class Error(val error: String) : ReadmeState // Состояние ошибки при загрузке readme
        data class Loaded(val markdown: String) : ReadmeState // Состояние успешной загрузки readme

    }
}