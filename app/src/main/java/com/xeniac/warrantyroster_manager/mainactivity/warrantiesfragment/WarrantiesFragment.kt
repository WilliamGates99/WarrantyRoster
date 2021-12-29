package com.xeniac.warrantyroster_manager.mainactivity.warrantiesfragment

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
import com.xeniac.warrantyroster_manager.Constants
import com.xeniac.warrantyroster_manager.NetworkHelper
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.database.WarrantyRosterDatabase
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantiesBinding
import com.xeniac.warrantyroster_manager.mainactivity.MainActivity
import com.xeniac.warrantyroster_manager.model.Category
import com.xeniac.warrantyroster_manager.model.ListItemType
import com.xeniac.warrantyroster_manager.model.Warranty
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

    private fun adInit() = TapsellPlus.initialize(
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
            binding.tvNetworkError.text =
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
                    binding.tvNetworkError.text =
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
        binding.groupEmptyList.visibility = GONE
        binding.rv.visibility = GONE
        binding.groupNetwork.visibility = VISIBLE
        retryNetworkBtn()
    }

    private fun retryNetworkBtn() = binding.btnNetworkRetry.setOnClickListener {
        getWarrantiesList()
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
        binding.rv.adapter = warrantyAdapter

        //TODO remove comment after adding search function
//        searchWarrantiesList();
    }

    override fun onItemClick(warranty: Warranty, daysUntilExpiry: Long) {
        val action = WarrantiesFragmentDirections
            .actionWarrantiesFragmentToWarrantyDetailsFragment(warranty, daysUntilExpiry)
        navController.navigate(action)
    }

//    private fun searchWarrantiesList() {
//        binding.svWarranties.setVisibility(VISIBLE);
//
//        binding.svWarranties.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
//            if (hasFocus) {
//                warrantiesBinding.toolbarWarranties.setTitle(null);
//            } else {
//                warrantiesBinding.toolbarWarranties.setTitle(
//                    context.getResources().getString(R.string.warranties_text_title)
//                );
//            }
//        });
//
//        warrantiesBinding.svWarranties.setOnQueryTextListener(new SearchView . OnQueryTextListener () {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (!TextUtils.isEmpty(query)) {
//                    Toast.makeText(context, "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
//                    warrantiesBinding.svWarranties.onActionViewCollapsed();
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