package com.example.githubproject.screens.detailInfo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.githubproject.R
import com.example.githubproject.data.AppRepositoryImpl
import com.example.githubproject.data.retrofit.RetrofitClient
import com.example.githubproject.databinding.FragmentAuthBinding
import com.example.githubproject.databinding.FragmentDetailInfoBinding
import com.example.githubproject.domain.model.repo.RepoDetailsDomain
import com.example.githubproject.domain.usecase.GetRepoDetailsInfoUseCase
import com.example.githubproject.screens.auth.AuthFragment
import com.example.githubproject.screens.reposList.ReposListFragment
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailInfoFragment: Fragment() {

    // В ЗАВИСИМОСТИ
    private val gitHubClient by lazy { RetrofitClient.getAuthenticatedClient(token) }
    private val appRepository by lazy { AppRepositoryImpl(gitHubClient) }
    private val getRepoDetailsInfoUseCase by lazy { GetRepoDetailsInfoUseCase(appRepository) }
    private val detailInfoViewModelFactory by lazy { DetailInfoViewModelFactory(getRepoDetailsInfoUseCase) }
    private val detailInfoViewModel: DetailInfoViewModel by viewModels {
        detailInfoViewModelFactory
    }

    private val menuHost: MenuHost get() = requireActivity()

    private lateinit var binding: FragmentDetailInfoBinding
    private lateinit var repo: String
    private lateinit var owner: String
    private lateinit var token: String
    private lateinit var state: StateFlow<DetailInfoViewModel.State>


    private var navController: NavController? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navController = findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repo = arguments?.getString(ReposListFragment.REPO_KEY) ?: ""
        owner = arguments?.getString(ReposListFragment.OWNER_KEY) ?: ""
        token = arguments?.getString(AuthFragment.TOKEN_KEY) ?: ""
        state = detailInfoViewModel.state
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailInfoBinding.inflate(inflater, container, false)

        val activity: AppCompatActivity = activity as AppCompatActivity

        // Устанавливаем кастомный toolbar
        activity.apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            supportActionBar?.title = repo
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
                    navController?.navigate(R.id.action_detailInfoFragment_to_authFragment)
                    return true
                }
                if (menuItem.itemId == android.R.id.home) {
                    navController?.navigateUp()
                    return true
                }

                return false
            }
        }, viewLifecycleOwner)

        detailInfoViewModel.onOpenRepoDetailInfo(owner = owner, repo = repo)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailInfoViewModel.state.collect { state ->
                    when(state) {
                        is DetailInfoViewModel.State.Error -> {}
                        is DetailInfoViewModel.State.Loaded -> updateUISuccess(repoDetails = state.githubRepo)
                        DetailInfoViewModel.State.Loading -> updateUILoading()
                    }
                }
            }
        }

    }

    override fun onDetach() {
        super.onDetach()
        navController = null
    }

    private fun updateUISuccess(repoDetails: RepoDetailsDomain) {
        binding.apply {
            linkTv.text = repoDetails.htmlUrl
            linkTv.setOnClickListener {
                goToUrl(repoDetails.htmlUrl)
            }
            watchersCountTv.text = repoDetails.watchersCount.toString()
            forksCountTv.text = repoDetails.forksCount.toString()
            starsCountTv.text = repoDetails.stargazersCount.toString()
            if(repoDetails.license != null) {
                licenseImg.visibility = View.VISIBLE
                licenseTv.visibility = View.VISIBLE
                licenseTv.text = repoDetails.license.spdxId
            } else {
                licenseImg.visibility = View.GONE
                licenseTv.visibility = View.GONE
            }
        }
    }

    private fun updateUILoading() {

    }

    private fun goToUrl(url: String) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }
}