package com.xeniac.warrantyroster_manager.mainactivity.warrantydetailsfragment

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xeniac.warrantyroster_manager.Constants
import com.xeniac.warrantyroster_manager.NetworkHelper
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.database.WarrantyRosterDatabase
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantyDetailsBinding
import com.xeniac.warrantyroster_manager.mainactivity.MainActivity
import com.xeniac.warrantyroster_manager.model.Warranty
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
        Firebase.firestore.collection(Constants.COLLECTION_WARRANTIES)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWarrantyDetailsBinding.bind(view)
        navController = Navigation.findNavController(view)
        database = WarrantyRosterDatabase.getInstance(requireContext())
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
        WarrantyRosterDatabase.destroyInstance()
        _binding = null
    }

    private fun returnToMainActivity() {
        binding.toolbarWarrantyDetails.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun handleExtendedFAB() {
        binding.svWarrantyDetails.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY > oldScrollY) {
                    binding.fabWarrantyDetails.shrink()
                } else {
                    binding.fabWarrantyDetails.extend()
                }
            })
    }

    private fun getWarranty() {
        val args: WarrantyDetailsFragmentArgs by navArgs()
        warranty = args.warranty
        val daysUntilExpiry = args.daysUntilExpiry
        setWarrantyDetails(daysUntilExpiry)
    }

    private fun setWarrantyDetails(daysUntilExpiry: Long) {
        if (warranty.brand != null) {
            binding.tvWarrantyDetailsBrand.text = warranty.brand
        } else {
            binding.tvWarrantyDetailsBrand.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.grayDark)
            )
            binding.tvWarrantyDetailsBrand.text =
                requireContext().getString(R.string.warranty_details_empty_device)
        }

        if (warranty.model != null) {
            binding.tvWarrantyDetailsModel.text = warranty.model
        } else {
            binding.tvWarrantyDetailsModel.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.grayDark)
            )
            binding.tvWarrantyDetailsModel.text =
                requireContext().getString(R.string.warranty_details_empty_device)
        }

        if (warranty.serialNumber != null) {
            binding.tvWarrantyDetailsSerial.text = warranty.serialNumber
        } else {
            binding.tvWarrantyDetailsSerial.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.grayDark)
            )
            binding.tvWarrantyDetailsSerial.text =
                requireContext().getString(R.string.warranty_details_empty_device)
        }

        if (warranty.description != null) {
            binding.tvWarrantyDetailsDescription.text = warranty.description
        } else {
            binding.tvWarrantyDetailsDescription.gravity = Gravity.CENTER
            binding.tvWarrantyDetailsDescription.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.grayDark)
            )
            binding.tvWarrantyDetailsDescription.text =
                requireContext().getString(R.string.warranty_details_empty_description)
        }

        binding.toolbarWarrantyDetails.title = warranty.title
        binding.ivWarrantyDetailsIcon.setImageResource(
            database.categoryDAO().getCategoryById(warranty.categoryId).icon
        )

        val startingCalendar = Calendar.getInstance()
        val expiryCalendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-M-dd")
        val decimalFormat = DecimalFormat("00")

        dateFormat.parse(warranty.startingDate)?.let {
            startingCalendar.time = it
        }

        dateFormat.parse(warranty.expiryDate)?.let {
            expiryCalendar.time = it
        }

        binding.tvWarrantyDetailsDateStarting.text =
            "${decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1)}/" +
                    "${decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                    "${startingCalendar.get(Calendar.YEAR)}"

        binding.tvWarrantyDetailsDateExpiry.text =
            "${decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1)}/" +
                    "${decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                    "${expiryCalendar.get(Calendar.YEAR)}"

        when {
            daysUntilExpiry < 0 -> {
                binding.tvWarrantyDetailsStatus.text =
                    requireContext().getString(R.string.warranty_details_status_expired)
                binding.tvWarrantyDetailsStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )
                binding.tvWarrantyDetailsStatus.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red20)
            }
            daysUntilExpiry <= 30 -> {
                binding.tvWarrantyDetailsStatus.text =
                    requireContext().getString(R.string.warranty_details_status_soon)
                binding.tvWarrantyDetailsStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.orange)
                )
                binding.tvWarrantyDetailsStatus.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.orange20)
            }
            else -> {
                binding.tvWarrantyDetailsStatus.text =
                    requireContext().getString(R.string.warranty_details_status_valid)
                binding.tvWarrantyDetailsStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.green)
                )
                binding.tvWarrantyDetailsStatus.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.green20)
            }
        }
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

    private fun editWarrantyOnClick() {
        binding.fabWarrantyDetails.setOnClickListener {
            val action = WarrantyDetailsFragmentDirections
                .actionWarrantyDetailsFragmentToEditWarrantyFragment(warranty)
            navController.navigate(action)
        }
    }

    private fun deleteWarrantyOnClick() {
        binding.toolbarWarrantyDetails.menu.getItem(0).setOnMenuItemClickListener {
            deleteWarranty()
            false
        }
    }

    private fun deleteWarranty() {
        if (NetworkHelper.hasNetworkAccess(requireContext())) {
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

    private fun showDeleteWarrantyDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
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
    }

    private fun deleteWarrantyFromFirestore() {
        showLoadingAnimation()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                warrantiesCollectionRef.document(warranty.id).delete().await()
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
        binding.toolbarWarrantyDetails.menu.getItem(0).isVisible = false
        binding.fabWarrantyDetails.isClickable = false
        binding.cpiWarrantyDetails.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.toolbarWarrantyDetails.menu.getItem(0).isVisible = true
        binding.fabWarrantyDetails.isClickable = true
        binding.cpiWarrantyDetails.visibility = GONE
    }
}