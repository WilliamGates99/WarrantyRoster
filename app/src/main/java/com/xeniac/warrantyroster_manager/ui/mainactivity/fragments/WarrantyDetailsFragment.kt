package com.xeniac.warrantyroster_manager.ui.mainactivity.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.CachePolicy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantyDetailsBinding
import com.xeniac.warrantyroster_manager.db.WarrantyRosterDatabase
import com.xeniac.warrantyroster_manager.ui.mainactivity.MainActivity
import com.xeniac.warrantyroster_manager.models.Warranty
import com.xeniac.warrantyroster_manager.util.Constants.Companion.COLLECTION_WARRANTIES
import com.xeniac.warrantyroster_manager.util.Constants.Companion.TAPSELL_KEY
import com.xeniac.warrantyroster_manager.util.NetworkHelper.Companion.hasInternetConnection
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class WarrantyDetailsFragment : Fragment(R.layout.fragment_warranty_details) {

    private var _binding: FragmentWarrantyDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private lateinit var database: WarrantyRosterDatabase
    private lateinit var warranty: Warranty
    private val warrantiesCollectionRef =
        Firebase.firestore.collection(COLLECTION_WARRANTIES)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWarrantyDetailsBinding.bind(view)
        navController = Navigation.findNavController(view)
        database = WarrantyRosterDatabase(requireContext())
        (requireContext() as MainActivity).hideNavBar()

        returnToMainActivity()
        handleExtendedFAB()
        getWarranty()
        adInit()
        editWarrantyOnClick()
        deleteWarrantyOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun returnToMainActivity() =
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

    private fun handleExtendedFAB() = binding.nsv.setOnScrollChangeListener(
        NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.fab.shrink()
            } else {
                binding.fab.extend()
            }
        })

    private fun getWarranty() {
        val args: WarrantyDetailsFragmentArgs by navArgs()
        warranty = args.warranty
        val daysUntilExpiry = args.daysUntilExpiry
        setWarrantyDetails(daysUntilExpiry)
    }

    private fun setWarrantyDetails(daysUntilExpiry: Long) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val category = database.getCategoryDao().getCategoryById(warranty.categoryId!!)

            withContext(Dispatchers.Main) {
                binding.toolbar.title = warranty.title

                if (warranty.brand != null) {
                    binding.tvBrand.text = warranty.brand
                } else {
                    binding.tvBrand.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.grayDark)
                    )
                    binding.tvBrand.text =
                        requireContext().getString(R.string.warranty_details_empty_device)
                }

                if (warranty.model != null) {
                    binding.tvModel.text = warranty.model
                } else {
                    binding.tvModel.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.grayDark)
                    )
                    binding.tvModel.text =
                        requireContext().getString(R.string.warranty_details_empty_device)
                }

                if (warranty.serialNumber != null) {
                    binding.tvSerial.text = warranty.serialNumber
                } else {
                    binding.tvSerial.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.grayDark)
                    )
                    binding.tvSerial.text =
                        requireContext().getString(R.string.warranty_details_empty_device)
                }

                if (warranty.description != null) {
                    binding.tvDescription.text = warranty.description
                } else {
                    binding.tvDescription.gravity = Gravity.CENTER
                    binding.tvDescription.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.grayDark)
                    )
                    binding.tvDescription.text =
                        requireContext().getString(R.string.warranty_details_empty_description)
                }

                val startingCalendar = Calendar.getInstance()
                val expiryCalendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-M-dd")
                val decimalFormat = DecimalFormat("00")

                dateFormat.parse(warranty.startingDate!!)?.let {
                    startingCalendar.time = it
                }

                dateFormat.parse(warranty.expiryDate!!)?.let {
                    expiryCalendar.time = it
                }

                binding.tvDateStarting.text =
                    "${decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1)}/" +
                            "${decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                            "${startingCalendar.get(Calendar.YEAR)}"

                binding.tvDateExpiry.text =
                    "${decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1)}/" +
                            "${decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                            "${expiryCalendar.get(Calendar.YEAR)}"

                when {
                    daysUntilExpiry < 0 -> {
                        binding.tvStatus.text =
                            requireContext().getString(R.string.warranty_details_status_expired)
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.red)
                        )
                        binding.tvStatus.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.red20)
                    }
                    daysUntilExpiry <= 30 -> {
                        binding.tvStatus.text =
                            requireContext().getString(R.string.warranty_details_status_soon)
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.orange)
                        )
                        binding.tvStatus.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.orange20)
                    }
                    else -> {
                        binding.tvStatus.text =
                            requireContext().getString(R.string.warranty_details_status_valid)
                        binding.tvStatus.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.green)
                        )
                        binding.tvStatus.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.green20)
                    }
                }

                val imageLoader = ImageLoader.Builder(requireContext())
                    .componentRegistry { add(SvgDecoder(requireContext())) }.build()
                binding.ivIcon.load(category.icon, imageLoader) {
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                    networkCachePolicy(CachePolicy.ENABLED)
                }
            }
        } catch (e: Exception) {
            Log.e("setWarrantyDetails", "Exception: ${e.message}")
        }
    }

    private fun adInit() = TapsellPlus.initialize(
        requireContext(), TAPSELL_KEY, object : TapsellPlusInitListener {
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

    private fun editWarrantyOnClick() = binding.fab.setOnClickListener {
        val action = WarrantyDetailsFragmentDirections
            .actionWarrantyDetailsFragmentToEditWarrantyFragment(warranty)
        navController.navigate(action)
    }

    private fun deleteWarrantyOnClick() =
        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener {
            deleteWarranty()
            false
        }

    private fun deleteWarranty() {
        if (hasInternetConnection(requireContext())) {
            showDeleteWarrantyDialog()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { deleteWarranty() }
                show()
            }
        }
    }

    private fun showDeleteWarrantyDialog() = MaterialAlertDialogBuilder(requireContext()).apply {
        setTitle(requireContext().getString(R.string.warranty_details_delete_dialog_title))
        setMessage(
            String.format(
                requireContext().getString(R.string.warranty_details_delete_dialog_message),
                warranty.title
            )
        )
        setPositiveButton(requireContext().getString(R.string.warranty_details_delete_dialog_positive)) { _, _ -> deleteWarrantyFromFirestore() }
        setNegativeButton(requireContext().getText(R.string.warranty_details_delete_dialog_negative)) { _, _ -> }
        show()
    }

    private fun deleteWarrantyFromFirestore() {
        showLoadingAnimation()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                warrantiesCollectionRef.document(warranty.id!!).delete().await()
                Log.i("deleteWarranty", "${warranty.id} successfully deleted.")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    Toast.makeText(
                        requireContext(),
                        String.format(
                            requireContext().getString(
                                R.string.warranty_details_delete_success,
                                warranty.title
                            )
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                    (requireContext() as MainActivity).requestInterstitialAd()
                    requireActivity().onBackPressed()
                }
            } catch (e: Exception) {
                Log.e("deleteWarranty", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    Snackbar.make(
                        binding.root,
                        requireContext().getString(R.string.network_error_failure),
                        LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showLoadingAnimation() {
        binding.toolbar.menu.getItem(0).isVisible = false
        binding.fab.isClickable = false
        binding.cpiDelete.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiDelete.visibility = GONE
        binding.toolbar.menu.getItem(0).isVisible = true
        binding.fab.isClickable = true
    }
}