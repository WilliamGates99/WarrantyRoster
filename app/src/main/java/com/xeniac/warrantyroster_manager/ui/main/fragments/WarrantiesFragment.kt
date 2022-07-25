package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantiesBinding
import com.xeniac.warrantyroster_manager.ui.main.adapters.WarrantyAdapter
import com.xeniac.warrantyroster_manager.ui.main.adapters.WarrantyListClickInterface
import com.xeniac.warrantyroster_manager.ui.viewmodels.MainViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_EMPTY_CATEGORY_LIST
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_EMPTY_SEARCH_RESULT_LIST
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_EMPTY_WARRANTY_LIST
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WarrantiesFragment : Fragment(R.layout.fragment_warranties), WarrantyListClickInterface {

    private var _binding: FragmentWarrantiesBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var warrantyAdapter: WarrantyAdapter

    private var warrantiesListBeforeSearch: MutableList<Warranty>? = null
    private lateinit var searchQuery: String

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWarrantiesBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        getCategoriesFromFirestore()
        setupRecyclerView()
        setupSearchView()
        retryNetworkBtn()
        subscribeToObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    private fun setupRecyclerView() {
        warrantyAdapter.apply {
            setOnWarrantyItemClickListener(this@WarrantiesFragment)
            activity = requireActivity()
            context = requireContext()
            mainViewModel = viewModel
        }
        binding.rv.adapter = warrantyAdapter
    }

    private fun setupSearchView() = binding.apply {
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                toolbar.title = null
            } else {
                toolbar.title = requireContext().getString(R.string.warranties_text_title)
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrBlank()) {
                    searchView.onActionViewCollapsed()
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    warrantiesListBeforeSearch?.let {
                        showWarrantiesList(it)
                    }
                } else {
                    searchQuery = newText.trim()
                    viewModel.searchWarrantiesByTitle(searchQuery.lowercase())
                }

                return false
            }
        })
    }

    private fun subscribeToObservers() {
        categoriesListObserver()
        warrantiesListObserver()
        searchWarrantiesObserver()
    }

    private fun getCategoriesFromFirestore() = viewModel.getCategoriesFromFirestore()

    private fun getWarrantiesListFromFirestore() = viewModel.getWarrantiesListFromFirestore()

    private fun categoriesListObserver() {
        viewModel.categoriesLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.peekContent().let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> getWarrantiesListFromFirestore()
                    Status.ERROR -> {
                        response.message?.let {
                            when {
                                it.contains(ERROR_EMPTY_CATEGORY_LIST) -> {
                                    getCategoriesFromFirestore()
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    binding.tvNetworkError.text =
                                        requireContext().getString(R.string.error_firebase_403)
                                    showNetworkError()
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(), requireView()
                                    )
                                }
                                else -> {
                                    binding.tvNetworkError.text =
                                        requireContext().getString(R.string.error_network_connection)
                                    showNetworkError()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun warrantiesListObserver() {
        viewModel.warrantiesLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        response.data?.let { warrantiesList ->
                            warrantiesListBeforeSearch = warrantiesList
                            showWarrantiesList(warrantiesList)
                        }
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_EMPTY_WARRANTY_LIST) -> {
                                    showEmptyWarrantiesListError()
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    binding.tvNetworkError.text =
                                        requireContext().getString(R.string.error_firebase_403)
                                    showNetworkError()
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(), requireView()
                                    )
                                }
                                else -> {
                                    binding.tvNetworkError.text =
                                        requireContext().getString(R.string.error_network_connection)
                                    showNetworkError()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun searchWarrantiesObserver() =
        viewModel.searchWarrantiesLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> {
                        /* NO-OP */
                    }
                    Status.SUCCESS -> {
                        response.data?.let { searchResult ->
                            showWarrantiesList(searchResult)
                        }
                    }
                    Status.ERROR -> {
                        response.message?.let {
                            when {
                                it.contains(ERROR_EMPTY_SEARCH_RESULT_LIST) -> {
                                    showEmptySearchResultListError()
                                }
                                else -> {
                                    Toast.makeText(
                                        requireContext(),
                                        "Something went wrong!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun showNetworkError() = binding.apply {
        groupEmptyWarrantiesList.visibility = GONE
        groupEmptySearchResultList.visibility = GONE
        rv.visibility = GONE
        groupNetwork.visibility = VISIBLE
    }

    private fun retryNetworkBtn() = binding.btnNetworkRetry.setOnClickListener {
        getWarrantiesListFromFirestore()
    }

    private fun showEmptyWarrantiesListError() = binding.apply {
        searchView.visibility = GONE
        groupNetwork.visibility = GONE
        rv.visibility = GONE
        groupEmptyWarrantiesList.visibility = VISIBLE
        lavEmptyWarrantiesList.playAnimation()
    }

    private fun showEmptySearchResultListError() = binding.apply {
        // TODO TRY THESE
        tvEmptySearchResultList.text = requireContext().getString(
            R.string.warranties_text_empty_search_result_list,
            searchQuery
        ).parseAsHtml()
//        String.format(
//            requireContext().getString(R.string.warranties_text_empty_search_result_list),
//            searchQuery
//        ).parseAsHtml()

        groupNetwork.visibility = GONE
        rv.visibility = GONE
        groupEmptySearchResultList.visibility = VISIBLE
        lavEmptySearchResultList.playAnimation()
    }

    private fun showWarrantiesList(warrantiesList: MutableList<Warranty>) = binding.apply {
        groupNetwork.visibility = GONE
        groupEmptyWarrantiesList.visibility = GONE
        groupEmptySearchResultList.visibility = GONE
        rv.visibility = VISIBLE
        searchView.visibility = VISIBLE
        warrantyAdapter.warrantiesList = warrantiesList
    }

    override fun onItemClick(warranty: Warranty) = findNavController().navigate(
        WarrantiesFragmentDirections.actionWarrantiesFragmentToWarrantyDetailsFragment(warranty)
    )

    private fun showLoadingAnimation() = binding.apply {
        searchView.visibility = GONE
        groupNetwork.visibility = GONE
        groupEmptyWarrantiesList.visibility = GONE
        groupEmptySearchResultList.visibility = GONE
        rv.visibility = GONE
        cpi.visibility = VISIBLE
        cpi.show()
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpi.hide()
        cpi.setVisibilityAfterHide(GONE)
    }
}