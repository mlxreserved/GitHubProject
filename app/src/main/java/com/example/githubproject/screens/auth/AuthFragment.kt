package com.example.githubproject.screens.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.githubproject.R
import com.example.githubproject.screens.auth.AuthViewModel.State
import com.example.githubproject.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment: Fragment() {


    val authViewModel: AuthViewModel by viewModels()

    private lateinit var binding: FragmentAuthBinding // viewBinding
    private lateinit var state: StateFlow<State> // Состояние авторизации
    private var navController: NavController? = null // Навигационный контроллер

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navController = findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state = authViewModel.state
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        binding = FragmentAuthBinding.inflate(inflater, container, false)

        binding.applyButton.setOnClickListener {
            authViewModel.onSignButtonPressed()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.state.collect { state ->
                   when(state) {
                       // Если все ОК, то переходим к экрану с репозиториями
                       State.Idle -> updateUISuccess()
                       // Базовое состояние
                       State.Initial -> updateUIInit()
                       // Если не ОК, то показываем ошибку
                       is State.InvalidInput -> updateUIError(state)
                       // Пока идет загрузка включаем индикатор прогресса на кнопке
                       State.Loading -> updateUILoading()
                   }
                }
            }
        }

        // TextWatcher нужен, чтобы изменять token во viewModel
        val tokenWatcher = object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                authViewModel.onChangeToken(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.textInputET.addTextChangedListener(tokenWatcher)
    }

    override fun onDetach() {
        super.onDetach()
        navController = null
    }

    private fun updateUISuccess() {
        navController?.navigate(R.id.action_authFragment_to_reposListFragment) // Вместе с переходом передаем token
    }

    private fun updateUIError(state: State.InvalidInput) {
        binding.textInputLayout.error = state.reason
        binding.loading.visibility = View.GONE
        binding.applyButton.text = getString(R.string.sign_in)
    }

    private fun updateUILoading() {
        binding.textInputLayout.error = null
        binding.loading.visibility = View.VISIBLE
        binding.applyButton.text = null
    }

    private fun updateUIInit() {
        binding.textInputLayout.error = null
        binding.loading.visibility = View.GONE
        binding.applyButton.text = getString(R.string.sign_in)
    }
}