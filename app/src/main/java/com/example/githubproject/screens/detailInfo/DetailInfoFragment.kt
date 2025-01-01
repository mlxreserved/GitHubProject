package com.example.githubproject.screens.detailInfo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.example.githubproject.data.GithubRepositoryImpl
import com.example.githubproject.data.UserContentRepositoryImpl
import com.example.githubproject.data.retrofit.RetrofitClient
import com.example.githubproject.databinding.FragmentDetailInfoBinding
import com.example.githubproject.domain.model.repo.RepoDetailsDomain
import com.example.githubproject.domain.usecase.GetRepoDetailsInfoUseCase
import com.example.githubproject.domain.usecase.GetRepositoryReadmeUseCase
import com.example.githubproject.screens.auth.AuthFragment
import com.example.githubproject.screens.reposList.ReposListFragment
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DetailInfoFragment : Fragment() {

    val detailInfoViewModel: DetailInfoViewModel by viewModels()

    private val menuHost: MenuHost get() = requireActivity()

    private lateinit var binding: FragmentDetailInfoBinding
    private lateinit var repoId: String
    private lateinit var repoName: String
    private lateinit var defaultBranch: String
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
        token = arguments?.getString(AuthFragment.TOKEN_KEY) ?: ""
        repoId = arguments?.getString(ReposListFragment.REPO_ID_KEY) ?: ""
        repoName = arguments?.getString(ReposListFragment.REPO_NAME_KEY) ?: ""
        owner = arguments?.getString(ReposListFragment.REPO_OWNER) ?: ""
        defaultBranch = arguments?.getString(ReposListFragment.REPO_BRANCH) ?: ""
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
            supportActionBar?.title = repoName
        }

        binding.apply {
            errorReadmeBtn.setOnClickListener {
                detailInfoViewModel.onRefreshButtonPressed(
                    ownerName = owner,
                    repositoryName = repoName,
                    branchName = defaultBranch,
                    token = token
                )
            }

            errorLoadingBtn.setOnClickListener {
                detailInfoViewModel.onRetryButtonPressed(
                    repoId = repoId,
                    ownerName = owner,
                    repositoryName = repoName,
                    branchName = defaultBranch,
                    token = token
                )
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

        detailInfoViewModel.onOpenRepoDetailInfo(
            repoId = repoId,
            ownerName = owner,
            repositoryName = repoName,
            branchName = defaultBranch,
            token = token
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                state.collect { state ->
                    when (state) {
                        is DetailInfoViewModel.State.Error -> updateUIError(error = state.error)
                        is DetailInfoViewModel.State.Loaded -> {
                            updateUISuccess(repoDetails = state.githubRepo)
                            when (state.readmeState) {
                                DetailInfoViewModel.ReadmeState.Empty -> updateUIReadmeEmpty()
                                is DetailInfoViewModel.ReadmeState.Error -> updateUIReadmeError(
                                    error = state.readmeState.error
                                )

                                is DetailInfoViewModel.ReadmeState.Loaded -> updateUIReadmeSuccess(
                                    markdown = state.readmeState.markdown
                                )

                                DetailInfoViewModel.ReadmeState.Loading -> updateUIReadmeLoading()
                            }
                        }

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
            linkTv.apply {
                text = repoDetails.htmlUrl
                setOnClickListener {
                    goToUrl(repoDetails.htmlUrl)
                }
                visibility = View.VISIBLE
            }
            linkImg.visibility = View.VISIBLE
            watchersCountTv.text = repoDetails.watchersCount.toString()
            forksCountTv.text = repoDetails.forksCount.toString()
            starsCountTv.text = repoDetails.stargazersCount.toString()
            watchers.visibility = View.VISIBLE
            forks.visibility = View.VISIBLE
            stars.visibility = View.VISIBLE
            if (repoDetails.license != null) {
                licenseImg.visibility = View.VISIBLE
                licenseTv.visibility = View.VISIBLE
                licenseTv.text = repoDetails.license.spdxId
            } else {
                licenseImg.visibility = View.GONE
                licenseTv.visibility = View.GONE
            }
            loading.visibility = View.GONE
            errorLoading.visibility = View.GONE
            errorLoadingBtn.visibility = View.GONE
            errorReadmeBtn.visibility = View.GONE
        }
    }

    private fun updateUILoading() {
        binding.apply {
            linkTv.visibility = View.GONE
            linkImg.visibility = View.GONE
            licenseTv.visibility = View.GONE
            licenseImg.visibility = View.GONE
            watchers.visibility = View.GONE
            forks.visibility = View.GONE
            stars.visibility = View.GONE
            errorLoading.visibility = View.GONE
            errorLoadingBtn.visibility = View.GONE
            errorReadmeBtn.visibility = View.GONE
            loading.visibility = View.VISIBLE
            emptyReadme.visibility = View.GONE
        }
    }

    private fun updateUIError(error: String) {
        binding.apply {
            linkTv.visibility = View.GONE
            linkImg.visibility = View.GONE
            licenseTv.visibility = View.GONE
            licenseImg.visibility = View.GONE
            watchers.visibility = View.GONE
            forks.visibility = View.GONE
            stars.visibility = View.GONE
            errorLoading.visibility = View.VISIBLE
            errorLoadingBtn.visibility = View.VISIBLE
            errorReadmeBtn.visibility = View.GONE
            loading.visibility = View.GONE
            readme.visibility = View.GONE
            emptyReadme.visibility = View.GONE
            if (error == DetailInfoViewModel.IOEXCEPTION_NAME) {
                errorTv.text = getString(R.string.connection_error)
                errorHintTv.text = getString(R.string.connection_error_hint)
                errorImage.setImageResource(R.drawable.ic_connection_error)
            } else {
                errorTv.text = getString(R.string.server_error)
                errorHintTv.text = getString(R.string.server_error_hint)
                errorImage.setImageResource(R.drawable.ic_server_error)
            }
        }
    }

    private fun updateUIReadmeSuccess(markdown: String) {
        binding.readme.visibility = View.VISIBLE
        binding.loadingReadme.visibility = View.GONE
        binding.emptyReadme.visibility = View.GONE
        val markwon = Markwon.create(requireContext())
        markwon.setMarkdown(binding.readme, markdown)
    }

    private fun updateUIReadmeError(error: String) {
        binding.apply {
            loadingReadme.visibility = View.GONE
            errorLoading.visibility = View.VISIBLE
            errorLoadingBtn.visibility = View.GONE
            errorReadmeBtn.visibility = View.VISIBLE
            emptyReadme.visibility = View.GONE
            loading.visibility = View.GONE
            readme.visibility = View.GONE
            if (error == DetailInfoViewModel.IOEXCEPTION_NAME) {
                errorTv.text = getString(R.string.connection_error)
                errorHintTv.text = getString(R.string.connection_error_hint)
                errorImage.setImageResource(R.drawable.ic_connection_error)
            } else {
                errorTv.text = getString(R.string.server_error)
                errorHintTv.text = getString(R.string.server_error_hint)
                errorImage.setImageResource(R.drawable.ic_server_error)
            }
        }
    }

    private fun updateUIReadmeEmpty() {
        binding.readme.visibility = View.GONE
        binding.loadingReadme.visibility = View.GONE
        binding.emptyReadme.text = getString(R.string.empty_readme)
        binding.emptyReadme.visibility = View.VISIBLE
    }

    private fun updateUIReadmeLoading() {
        binding.readme.visibility = View.GONE
        binding.loadingReadme.visibility = View.VISIBLE
        binding.emptyReadme.visibility = View.GONE
    }

    private fun goToUrl(url: String) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }
}