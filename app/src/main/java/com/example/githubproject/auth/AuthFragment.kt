package com.example.githubproject.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.githubproject.auth.AuthViewModel.State
import com.example.githubproject.databinding.FragmentAuthBinding
import kotlinx.coroutines.flow.StateFlow

class AuthFragment: Fragment() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentAuthBinding
    private lateinit var state: StateFlow<State>


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

    }

}