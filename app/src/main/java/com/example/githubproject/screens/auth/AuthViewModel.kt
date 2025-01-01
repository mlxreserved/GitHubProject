package com.example.githubproject.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubproject.domain.usecase.SignInUseCase
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _token: MutableStateFlow<String> =
        MutableStateFlow("") // Инкапсуляция токена пользователя
    val token: StateFlow<String> =
        _token.asStateFlow() // Токен, который нельзя изменить в фрагменте


    private val _state: MutableStateFlow<State> =
        MutableStateFlow(State.Initial) // Инкапсуляция состояния
    val state: StateFlow<State> =
        _state.asStateFlow() // Состояние, которое нельзя изменять в фрагменте


    // Обработка нажатия на signButton
    fun onSignButtonPressed() {
        _state.update { State.Loading }
        if (token.value != "") {
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
    }

    // Обработка изменения token
    fun onChangeToken(newToken: String) {
        _token.update { newToken }
        _state.update { State.Initial }
    }

    sealed interface State {
        object Idle : State // Успешный вход
        object Initial : State // Базовое состояние
        object Loading : State // Загрузка
        data class InvalidInput(val reason: String) : State // Ошибка
    }

}