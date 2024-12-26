package com.example.githubproject.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubproject.domain.SignInUseCase

class AuthViewModelFactory (
    private val signInUseCase: SignInUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(
            signInUseCase = signInUseCase
        ) as T
    }
}