package com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list

import android.os.Bundle
import android.text.Spanned
import android.text.SpannedString
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.text.HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantiesBinding
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_CATEGORY_LIST
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_SEARCH_RESULT_LIST
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_WARRANTY_LIST
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WarrantiesFragment : Fragment(R.layout.fragment_warranties), WarrantyListClickInterface {

    private var _binding: FragmentWarrantiesBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: WarrantiesViewModel

    @Inject
    lateinit var warrantyAdapter: WarrantyAdapter

    private var warrantiesListBeforeSearch: MutableList<Warranty>? = null
    private lateinit var searchQuery: String

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWarrantiesBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[WarrantiesViewModel::class.java]

        subscribeToObservers()
        getAllCategoriesList()
        retryNetworkBtn()
        setupRecyclerView()
        setupSearchView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.onActionViewCollapsed()
    }

    private fun subscribeToObservers() {
        categoriesListObserver()
        warrantiesListObserver()
        searchWarrantiesObserver()
    }

    private fun getAllCategoriesList() = viewModel.getAllCategoriesList()

    private fun categoriesListObserver() =
        viewModel.categoriesListLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.peekContent().let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> getWarrantiesListFromFirestore()
                    is Resource.Error -> {
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_EMPTY_CATEGORY_LIST) -> {
                                    binding.tvNetworkError.text = requireContext().getString(
                                        R.string.error_something_went_wrong
                                    )
                                    showNetworkError()
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
                                    binding.tvNetworkError.text = requireContext().getString(
                                        R.string.error_network_failure
                                    )
                                    showNetworkError()
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun getWarrantiesListFromFirestore() = viewModel.getWarrantiesListFromFirestore()

    private fun warrantiesListObserver() {
        viewModel.warrantiesListLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        response.data?.let { warrantiesList ->
                            warrantiesListBeforeSearch = warrantiesList
                            showWarrantiesList(warrantiesList)
                        }
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_EMPTY_WARRANTY_LIST) -> showEmptyWarrantiesListError()
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
                                    binding.tvNetworkError.text = requireContext().getString(
                                        R.string.error_network_failure
                                    )
                                    showNetworkError()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun retryNetworkBtn() = binding.btnNetworkRetry.setOnClickListener {
        getAllCategoriesList()
    }

    private fun setupRecyclerView() {
        warrantyAdapter.apply {
            setOnWarrantyItemClickListener(this@WarrantiesFragment)
            activity = requireActivity()
            context = requireContext()
            warrantiesViewModel = viewModel
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
                    searchQuery = newText.trim().lowercase()
                    viewModel.searchWarrantiesByTitle(searchQuery)
                }

                return false
            }
        })
    }

    private fun searchWarrantiesObserver() =
        viewModel.searchWarrantiesLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        /* NO-OP */
                    }
                    is Resource.Success -> {
                        response.data?.let { searchResult ->
                            showWarrantiesList(searchResult)
                        }
                    }
                    is Resource.Error -> {
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_EMPTY_SEARCH_RESULT_LIST) -> showEmptySearchResultListError()
                                else -> {
                                    snackbar = showSomethingWentWrongError(
                                        requireContext(), requireView()
                                    )
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

    private fun showEmptyWarrantiesListError() = binding.apply {
        searchView.visibility = GONE
        groupNetwork.visibility = GONE
        rv.visibility = GONE
        groupEmptyWarrantiesList.visibility = VISIBLE
        lavEmptyWarrantiesList.playAnimation()
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

    private fun showEmptySearchResultListError() = binding.apply {
        tvEmptySearchResultList.text = getEmptySearchResultErrorText()
        if (groupEmptySearchResultList.visibility != VISIBLE) {
            groupNetwork.visibility = GONE
            rv.visibility = GONE
            groupEmptySearchResultList.visibility = VISIBLE
            lavEmptySearchResultList.playAnimation()
        }
    }

    private fun getEmptySearchResultErrorText(): Spanned = HtmlCompat.fromHtml(
        String.format(
            HtmlCompat.toHtml(
                SpannedString(requireContext().getText(R.string.warranties_text_empty_search_result_list)),
                TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
            ),
            TextUtils.htmlEncode(searchQuery)
        ),
        FROM_HTML_MODE_LEGACY
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