package com.example.githubproject.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.githubproject.R
import com.example.githubproject.auth.AuthViewModel.State
import com.example.githubproject.data.AppRepositoryImpl
import com.example.githubproject.data.retrofit.GitHubService
import com.example.githubproject.data.retrofit.RetrofitClient
import com.example.githubproject.databinding.FragmentAuthBinding
import com.example.githubproject.domain.AppRepository
import com.example.githubproject.domain.SignInUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch


class AuthFragment: Fragment() {

    // ВСЕ В ЗАВИСИМОСТИ
    private val client = RetrofitClient.getClient()
    private val appRepository: AppRepository = AppRepositoryImpl(client)
    private val signInUseCase = SignInUseCase(appRepository)
    private val authViewModelFactory: AuthViewModelFactory = AuthViewModelFactory(signInUseCase = signInUseCase)
    private val authViewModel: AuthViewModel by viewModels {
        authViewModelFactory
    }

    private lateinit var binding: FragmentAuthBinding
    private lateinit var state: StateFlow<State>
    private var navController: NavController? = null


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
                       State.Idle -> {
                           val bundle = bundleOf("token" to authViewModel.token.value)
                           navController?.navigate(R.id.action_authFragment_to_reposListFragment, bundle)
                       }
                       is State.InvalidInput -> binding.textInputLayout.error = state.reason
                       State.Loading -> {}
                   }
                }
            }
        }

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

//    private fun initObserveAction(){
//        viewLifecycleOwner.lifecycleScope.launch {
//            val action = authViewModel.actions.lastOrNull()
//            if(action is AuthViewModel.Action.RouteToMain) {
//                navController?.navigate(R.id.action_authFragment_to_reposListFragment)
//            } else {
//                Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

}