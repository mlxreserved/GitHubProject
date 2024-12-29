package com.example.githubproject.screens.reposList

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubproject.R
import com.example.githubproject.data.AppRepositoryImpl
import com.example.githubproject.data.retrofit.RetrofitClient
import com.example.githubproject.databinding.FragmentReposListBinding
import com.example.githubproject.domain.model.repos.RepoDomain
import com.example.githubproject.domain.usecase.GetRepositoriesUseCase
import com.example.githubproject.screens.auth.AuthFragment
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReposListFragment: Fragment() {


    // ВСЕ В ЗАВИСИМОСТИ
    private val client by lazy { RetrofitClient.getAuthenticatedClient(token) }
    private val appRepository by lazy { AppRepositoryImpl(client) }
    private val getRepositoriesUseCase by lazy { GetRepositoriesUseCase(appRepository) }
    private val reposListViewModelFactory by lazy { ReposListViewModelFactory(getRepositoriesUseCase) }
    private val reposListViewModel: ReposListViewModel by viewModels {
        reposListViewModelFactory
    }
    private val menuHost: MenuHost get() = requireActivity()

    private lateinit var binding: FragmentReposListBinding
    private lateinit var stateRepos: StateFlow<ReposListViewModel.StateRepos>
    private lateinit var adapter: ReposListAdapter
    private lateinit var token: String
    private var navController: NavController? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navController = findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = arguments?.getString(AuthFragment.TOKEN_KEY) ?: ""
        stateRepos = reposListViewModel.stateRepos
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReposListBinding.inflate(inflater, container, false)


        val activity: AppCompatActivity = activity as AppCompatActivity

        // Устанавливаем кастомный toolbar
        activity.apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.title = getString(R.string.title_repos)
        }


        binding.apply {
            // Устанавливаем layoutManager для recyclerView
            reposListRv.layoutManager = LinearLayoutManager(context)

            // Устанавливаем прослушку нажатия
            emptyReposBtn.setOnClickListener {
                reposListViewModel.onRefreshButtonPressed()
            }
            // Устанавливаем прослушку нажатия
            errorLoadingBtn.setOnClickListener {
                reposListViewModel.onRetryButtonPressed()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuHost.addMenuProvider(object : MenuProvider {
            // Закрепляем в toolbar наши кнопки
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            // Прослушка нажатий на кнопки в toolbar
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_exit) {
                    navController?.navigate(R.id.action_reposListFragment_to_authFragment)
                    return true
                }

                return false
            }
        }, viewLifecycleOwner)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reposListViewModel.stateRepos.collect { stateRepos ->
                    when(stateRepos) {
                        // Если репозиториев нет
                        ReposListViewModel.StateRepos.Empty -> updateUIEmpty()
                        // Если произошла ошибка при загрузке репозиториев
                        is ReposListViewModel.StateRepos.Error -> updateUIError(errorName = stateRepos.error)
                        // Если загрузка прошла успешно
                        is ReposListViewModel.StateRepos.Loaded ->  updateUISuccess(reposList = stateRepos.repos)
                        // Идёт загрузка
                        ReposListViewModel.StateRepos.Loading -> updateUILoading()
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        navController = null
    }

    private fun updateUISuccess(reposList: List<RepoDomain>) {
        binding.loading.visibility = View.GONE
        binding.emptyRepos.visibility = View.GONE
        binding.emptyReposBtn.visibility = View.GONE
        binding.errorLoading.visibility = View.GONE
        binding.errorLoadingBtn.visibility = View.GONE

        binding.reposListRv.visibility = View.VISIBLE
        adapter = ReposListAdapter(reposList)
        binding.reposListRv.adapter = adapter

        adapter.setOnClickListener(object :
            ReposListAdapter.OnClickListener {
            override fun onClick(position: Int, model: RepoDomain) {
                val bundle = bundleOf(REPO_KEY to model.name, OWNER_KEY to model.owner.login, AuthFragment.TOKEN_KEY to token)
                navController?.navigate(R.id.action_reposListFragment_to_detailInfoFragment, bundle)
            }

        })
    }

    private fun updateUIEmpty() {
        binding.loading.visibility = View.GONE
        binding.reposListRv.visibility = View.GONE
        binding.errorLoading.visibility = View.GONE
        binding.errorLoadingBtn.visibility = View.GONE
        binding.emptyRepos.visibility = View.VISIBLE
        binding.emptyReposBtn.visibility = View.VISIBLE
    }

    private fun updateUIError(errorName: String) {
        binding.reposListRv.visibility = View.GONE
        binding.emptyRepos.visibility = View.GONE
        binding.emptyReposBtn.visibility = View.GONE
        binding.loading.visibility = View.GONE
        binding.errorLoading.visibility = View.VISIBLE
        binding.errorLoadingBtn.visibility = View.VISIBLE

        // Проверка связана ошибка с интернетом или с сервером
        if(errorName == "IOException") {
            binding.errorTv.text = getString(R.string.connection_error)
            binding.errorHintTv.text = getString(R.string.connection_error_hint)
            binding.errorImage.setImageResource(R.drawable.ic_connection_error)
        } else {
            binding.errorTv.text = getString(R.string.server_error)
            binding.errorHintTv.text = getString(R.string.server_error_hint)
            binding.errorImage.setImageResource(R.drawable.ic_server_error)
        }
    }

    private fun updateUILoading() {
        binding.reposListRv.visibility = View.GONE
        binding.emptyRepos.visibility = View.GONE
        binding.emptyReposBtn.visibility = View.GONE
        binding.loading.visibility = View.VISIBLE
        binding.errorLoading.visibility = View.GONE
        binding.errorLoadingBtn.visibility = View.GONE
    }


    companion object {
        const val REPO_KEY = "repo"
        const val OWNER_KEY = "owner"
    }
}