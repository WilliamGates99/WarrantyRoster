package com.xeniac.warrantyrostermanager.mainactivity.warrantiesfragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xeniac.warrantyrostermanager.Constants
import com.xeniac.warrantyrostermanager.NetworkHelper
import com.xeniac.warrantyrostermanager.R
import com.xeniac.warrantyrostermanager.database.WarrantyRosterDatabase
import com.xeniac.warrantyrostermanager.databinding.FragmentWarrantiesBinding
import com.xeniac.warrantyrostermanager.mainactivity.MainActivity
import com.xeniac.warrantyrostermanager.model.Category
import com.xeniac.warrantyrostermanager.model.ListItemType
import com.xeniac.warrantyrostermanager.model.Warranty
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WarrantiesFragment : Fragment(R.layout.fragment_warranties), WarrantyListClickInterface {

    private var _binding: FragmentWarrantiesBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var database: WarrantyRosterDatabase
    private val categoriesCollectionRef =
        Firebase.firestore.collection(Constants.COLLECTION_CATEGORIES)
    private val warrantiesCollectionRef =
        Firebase.firestore.collection(Constants.COLLECTION_WARRANTIES)
    private var warrantiesQuery: ListenerRegistration? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWarrantiesBinding.bind(view)
        navController = Navigation.findNavController(view)
        database = WarrantyRosterDatabase.getInstance(requireContext())
        (requireContext() as MainActivity).showNavBar()

        adInit()
        seedCategories()
        getWarrantiesList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        WarrantyRosterDatabase.destroyInstance()
        warrantiesQuery?.remove()
        _binding = null
    }

    private fun adInit() {
        TapsellPlus.initialize(
            requireContext(), Constants.TAPSELL_KEY, object : TapsellPlusInitListener {
                override fun onInitializeSuccess(adNetworks: AdNetworks?) {
                    Log.i("adInit", "onInitializeSuccess: ${adNetworks?.name}")
                }

                override fun onInitializeFailed(
                    adNetworks: AdNetworks?, adNetworkError: AdNetworkError?
                ) {
                    Log.e(
                        "adInit",
                        "onInitializeFailed: ${adNetworks?.name}, error: ${adNetworkError?.errorMessage}"
                    )
                }
            })
    }

    private fun seedCategories() {
        val itemCount = database.categoryDAO().countItems()
        if (itemCount in 0..20) {
            database.categoryDAO().deleteAllCategories()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val categoriesQuery: QuerySnapshot = categoriesCollectionRef.get().await()
                    Log.i("seedCategories", "Categories Data successfully retrieved.")

                    val categoriesList = mutableListOf<Category>()
                    for (document in categoriesQuery.documents) {
                        val category = document.toObject<Category>()
                        category?.let { categoriesList.add(it) }
                    }
                    database.categoryDAO().insertAllCategories(categoriesList)
                } catch (e: Exception) {
                    Log.e("seedCategories", "Exception: " + e.message)
                }
            }
        }
    }

    private fun getWarrantiesList() {
        if (NetworkHelper.hasNetworkAccess(requireContext())) {
            getWarrantiesListFromFirestore()
        } else {
            binding.tvWarrantiesNetworkError.text =
                requireContext().getString(R.string.network_error_connection)
            showNetworkError()
        }
    }

    private fun getWarrantiesListFromFirestore() {
        showLoadingAnimation()

        warrantiesQuery = warrantiesCollectionRef
            .whereEqualTo(Constants.WARRANTIES_UUID, Firebase.auth.currentUser?.uid)
            .orderBy(Constants.WARRANTIES_TITLE, Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e("getWarrantiesList", "Exception: ${error.message}")
                    binding.tvWarrantiesNetworkError.text =
                        requireContext().getString(R.string.network_error_failure)
                    showNetworkError()
                }

                value?.let {
                    Log.i("getWarrantiesList", "Warranties List successfully retrieved.")
                    hideLoadingAnimation()

                    if (it.documents.size == 0) {
                        showWarrantiesEmptyList()
                    } else {
                        val warrantiesList = mutableListOf<Warranty>()
                        for (document in it.documents) {
                            val warranty = Warranty(
                                document.id,
                                document.get(Constants.WARRANTIES_TITLE).toString(),
                                document.get(Constants.WARRANTIES_BRAND).toString(),
                                document.get(Constants.WARRANTIES_MODEL).toString(),
                                document.get(Constants.WARRANTIES_SERIAL_NUMBER).toString(),
                                document.get(Constants.WARRANTIES_STARTING_DATE).toString(),
                                document.get(Constants.WARRANTIES_EXPIRY_DATE).toString(),
                                document.get(Constants.WARRANTIES_DESCRIPTION).toString(),
                                document.get(Constants.WARRANTIES_CATEGORY_ID).toString(),
                                ListItemType.WARRANTY
                            )
                            warrantiesList.add(warranty)
                        }
                        showWarrantiesList(warrantiesList)
                    }
                }
            }
    }

    private fun showNetworkError() {
        hideLoadingAnimation()
        binding.groupWarrantiesEmptyList.visibility = GONE
        binding.rvWarranties.visibility = GONE
        binding.groupWarrantiesNetwork.visibility = VISIBLE
        retryNetworkBtn()
    }

    private fun retryNetworkBtn() {
        binding.btnWarrantiesNetworkRetry.setOnClickListener {
            getWarrantiesList()
        }
    }

    private fun showLoadingAnimation() {
        binding.searchWarranties.visibility = GONE
        binding.groupWarrantiesNetwork.visibility = GONE
        binding.groupWarrantiesEmptyList.visibility = GONE
        binding.rvWarranties.visibility = GONE
        binding.cpiWarranties.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiWarranties.visibility = GONE
    }

    private fun showWarrantiesEmptyList() {
        binding.searchWarranties.visibility = GONE
        binding.groupWarrantiesNetwork.visibility = GONE
        binding.rvWarranties.visibility = GONE
        binding.groupWarrantiesEmptyList.visibility = VISIBLE
    }

    private fun showWarrantiesList(warrantiesList: MutableList<Warranty>) {
        binding.groupWarrantiesNetwork.visibility = GONE
        binding.groupWarrantiesEmptyList.visibility = GONE
        binding.rvWarranties.visibility = VISIBLE

        var adIndex = 5
        for (i in 0..warrantiesList.size) {
            if (i == adIndex) {
                adIndex += 6
                warrantiesList.add(
                    i, Warranty(
                        null, null, null, null, null,
                        null, null, null, null,
                        ListItemType.AD
                    )
                )
            }
        }

        val warrantyAdapter = WarrantyAdapter(
            activity, requireContext(), database, warrantiesList, this
        )
        binding.rvWarranties.adapter = warrantyAdapter

        //TODO remove comment after adding search function
//        searchWarrantiesList();
    }

    override fun onItemClick(warranty: Warranty, daysUntilExpiry: Long) {
        val action = WarrantiesFragmentDirections
            .actionWarrantiesFragmentToWarrantyDetailsFragment(warranty, daysUntilExpiry)
        navController.navigate(action)
    }

//    private fun searchWarrantiesList() {
//        warrantiesBinding.searchWarranties.setVisibility(VISIBLE);
//
//        warrantiesBinding.searchWarranties.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
//            if (hasFocus) {
//                warrantiesBinding.toolbarWarranties.setTitle(null);
//            } else {
//                warrantiesBinding.toolbarWarranties.setTitle(
//                    context.getResources().getString(R.string.warranties_text_title)
//                );
//            }
//        });
//
//        warrantiesBinding.searchWarranties.setOnQueryTextListener(new SearchView . OnQueryTextListener () {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (!TextUtils.isEmpty(query)) {
//                    Toast.makeText(context, "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
//                    warrantiesBinding.searchWarranties.onActionViewCollapsed();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (!TextUtils.isEmpty(newText)) {
//                    Toast.makeText(context, "Input: " + newText, Toast.LENGTH_SHORT).show();
//                }
//                return false;
//            }
//        });
//    }
}