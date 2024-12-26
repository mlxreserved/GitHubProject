package com.example.githubproject.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubproject.domain.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AuthViewModel (private val signInUseCase: SignInUseCase): ViewModel() {

    val token: MutableStateFlow<String> = MutableStateFlow("")

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    //lateinit var actions: Flow<Action>

    fun onSignButtonPressed() {
        _state.update { State.Loading }
        if(token.value != "") {
            viewModelScope.launch {
                try {
                    signInUseCase.execute("Bearer ${token.value}")
                    _state.update { State.Idle }
                } catch (e: Exception) {
                    _state.update { State.InvalidInput("Invalid token") }
                }
            }
        } else {
            _state.update { State.InvalidInput("Invalid token") }
        }

//        actions = when(_state.value) {
//            is State.Idle -> flow {
//                emit(Action.RouteToMain)
//            }
//            is State.InvalidInput ->
//                flow {
//                    emit(Action.ShowError("invalid token"))
//                }
//            State.Loading -> flow{"1"}
//        }
    }

    fun onChangeToken(newToken: String) {
        token.update { newToken }
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(val reason: String) : State
    }

    sealed interface Action {
        data class ShowError(val message: String) : Action
        object RouteToMain : Action
    }

}