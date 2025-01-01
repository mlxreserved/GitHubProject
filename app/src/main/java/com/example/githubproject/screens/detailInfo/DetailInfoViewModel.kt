package com.example.githubproject.screens.detailInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubproject.data.sharedPref.MyPreference
import com.example.githubproject.domain.model.repo.RepoDetailsDomain
import com.example.githubproject.domain.usecase.GetRepoDetailsInfoUseCase
import com.example.githubproject.domain.usecase.GetRepositoryReadmeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailInfoViewModel @Inject constructor(
    private val getRepoDetailsInfoUseCase: GetRepoDetailsInfoUseCase,
    private val getRepositoryReadmeUseCase: GetRepositoryReadmeUseCase,
    private val myPreference: MyPreference
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val token = myPreference.getStoredToken()

    fun onOpenRepoDetailInfo(
        repoId: String,
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        getRepo(
            repoId = repoId,
            ownerName = ownerName,
            repositoryName = repositoryName,
            branchName = branchName
        )
    }

    private fun getRepo(
        repoId: String,
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        viewModelScope.launch {
            _state.update { State.Loading }
            try {
                val githubRepo = getRepoDetailsInfoUseCase.execute(repoId = repoId, token = "Bearer $token")
                _state.update {
                    State.Loaded(
                        githubRepo = githubRepo,
                        readmeState = ReadmeState.Loading
                    )
                }
                getReadme(
                    ownerName = ownerName,
                    repositoryName = repositoryName,
                    branchName = branchName
                )
            } catch (e: IOException) {
                _state.update { State.Error(IOEXCEPTION_NAME) }
            } catch (e: Exception) {
                _state.update { State.Error(EXCEPTION_NAME) }
            }
        }

    }

    private fun getReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        _state.update { (it as State.Loaded).copy(readmeState = ReadmeState.Loading) }

        viewModelScope.launch {
            val call = getRepositoryReadmeUseCase.execute(
                ownerName = ownerName,
                repositoryName = repositoryName,
                branchName = branchName,
                token = "Bearer $token"
            )


            call.enqueue(object : Callback<ResponseBody> {
                // Реакция на ответ от сервера
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val rawResponse = responseBody?.string() ?: ""
                        _state.update {
                            (it as State.Loaded).copy(
                                readmeState = ReadmeState.Loaded(
                                    markdown = rawResponse
                                )
                            )
                        }
                    } else {
                        _state.update { (it as State.Loaded).copy(readmeState = ReadmeState.Empty) }
                    }
                }

                // Реакция на ошибку при обращении к серверу
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (t is IOException) {
                        _state.update {
                            (it as State.Loaded).copy(
                                readmeState = ReadmeState.Error(
                                    IOEXCEPTION_NAME
                                )
                            )
                        }
                    } else {
                        _state.update {
                            (it as State.Loaded).copy(
                                readmeState = ReadmeState.Error(
                                    EXCEPTION_NAME
                                )
                            )
                        }
                    }
                }

            })
        }
    }

    fun onRetryButtonPressed(
        repoId: String,
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        getRepo(
            repoId = repoId,
            ownerName = ownerName,
            repositoryName = repositoryName,
            branchName = branchName
        )
    }

    fun onRefreshButtonPressed(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        getReadme(
            ownerName = ownerName,
            repositoryName = repositoryName,
            branchName = branchName
        )

    }

    fun onExitButtonPressed() {
        myPreference.removeStoredToken()
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

    companion object {
        const val EXCEPTION_NAME = "Exception" // константа для названия Exception
        const val IOEXCEPTION_NAME = "IOException" // константа для названия IOException
    }
}