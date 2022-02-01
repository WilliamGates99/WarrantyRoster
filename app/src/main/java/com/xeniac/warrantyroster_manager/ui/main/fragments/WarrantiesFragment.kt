package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.ui.main.adapters.WarrantyAdapter
import com.xeniac.warrantyroster_manager.ui.main.adapters.WarrantyListClickInterface
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantiesBinding
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.models.Warranty
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.MainViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_EMPTY_WARRANTY_LIST
import com.xeniac.warrantyroster_manager.utils.Constants.TAPSELL_KEY
import com.xeniac.warrantyroster_manager.utils.Status
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks

class WarrantiesFragment : Fragment(R.layout.fragment_warranties), WarrantyListClickInterface {

    private var _binding: FragmentWarrantiesBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel

    private lateinit var warrantyAdapter: WarrantyAdapter

    private val TAG = "WarrantiesFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWarrantiesBinding.bind(view)
        navController = Navigation.findNavController(view)
        (requireContext() as MainActivity).showNavBar()
        viewModel = (activity as MainActivity).viewModel

        adInit()
        seedCategories()
        setupRecyclerView()
        getWarrantiesListFromFirestore()
        warrantiesListObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun adInit() = TapsellPlus.initialize(
        requireContext(), TAPSELL_KEY, object : TapsellPlusInitListener {
            override fun onInitializeSuccess(adNetworks: AdNetworks?) {
                Log.i(TAG, "onInitializeSuccess: ${adNetworks?.name}")
            }

            override fun onInitializeFailed(
                adNetworks: AdNetworks?, adNetworkError: AdNetworkError?
            ) {
                Log.e(
                    TAG,
                    "onInitializeFailed: ${adNetworks?.name}, error: ${adNetworkError?.errorMessage}"
                )
            }
        })

    private fun seedCategories() = viewModel.seedCategories()

    private fun setupRecyclerView() {
        warrantyAdapter = WarrantyAdapter(requireActivity(), requireContext(), viewModel, this)
        binding.rv.adapter = warrantyAdapter
    }

    private fun getWarrantiesListFromFirestore() = viewModel.getWarrantiesListFromFirestore()

    private fun warrantiesListObserver() {
        viewModel.warrantiesLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        response.data?.let { warrantiesList ->
                            showWarrantiesList(warrantiesList)
                        }
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_EMPTY_WARRANTY_LIST) -> {
                                    showWarrantiesEmptyList()
                                }
                                else -> {
                                    binding.tvNetworkError.text =
                                        requireContext().getString(R.string.network_error_connection)
                                    showNetworkError()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showNetworkError() {
        binding.groupEmptyList.visibility = GONE
        binding.rv.visibility = GONE
        binding.groupNetwork.visibility = VISIBLE
        retryNetworkBtn()
    }

    private fun retryNetworkBtn() = binding.btnNetworkRetry.setOnClickListener {
        getWarrantiesListFromFirestore()
    }

    private fun showLoadingAnimation() {
        binding.svWarranties.visibility = GONE
        binding.groupNetwork.visibility = GONE
        binding.groupEmptyList.visibility = GONE
        binding.rv.visibility = GONE
        binding.cpi.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpi.visibility = GONE
    }

    private fun showWarrantiesEmptyList() {
        binding.svWarranties.visibility = GONE
        binding.groupNetwork.visibility = GONE
        binding.rv.visibility = GONE
        binding.groupEmptyList.visibility = VISIBLE
    }

    private fun showWarrantiesList(warrantiesList: MutableList<Warranty>) {
        binding.groupNetwork.visibility = GONE
        binding.groupEmptyList.visibility = GONE
        binding.rv.visibility = VISIBLE
        warrantyAdapter.warrantyListDiffer.submitList(warrantiesList)

        //TODO remove comment after adding search function
//        searchWarrantiesList();
    }

    override fun onItemClick(warranty: Warranty, daysUntilExpiry: Long) {
        val action = WarrantiesFragmentDirections
            .actionWarrantiesFragmentToWarrantyDetailsFragment(warranty, daysUntilExpiry)
        navController.navigate(action)
    }

    /*
    private fun searchWarrantiesList() {
        binding.svWarranties.setVisibility(VISIBLE);

        binding.svWarranties.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                warrantiesBinding.toolbarWarranties.setTitle(null);
            } else {
                warrantiesBinding.toolbarWarranties.setTitle(
                    context.getResources().getString(R.string.warranties_text_title)
                );
            }
        });

        warrantiesBinding.svWarranties.setOnQueryTextListener(new SearchView . OnQueryTextListener () {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    Toast.makeText(context, "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                    warrantiesBinding.svWarranties.onActionViewCollapsed();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    Toast.makeText(context, "Input: " + newText, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
     */
}