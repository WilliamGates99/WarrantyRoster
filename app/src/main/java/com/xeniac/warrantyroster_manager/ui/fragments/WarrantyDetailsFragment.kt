package com.xeniac.warrantyroster_manager.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.repository.NetworkConnectivityObserver
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantyDetailsBinding
import com.xeniac.warrantyroster_manager.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.ui.MainActivity
import com.xeniac.warrantyroster_manager.ui.viewmodels.WarrantyViewModel
import com.xeniac.warrantyroster_manager.util.AlertDialogHelper.showTwoBtnAlertDialog
import com.xeniac.warrantyroster_manager.util.CoilHelper.loadCategoryImage
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.DateHelper.getDaysUntilExpiry
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showUnavailableNetworkConnectionError
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class WarrantyDetailsFragment : Fragment(R.layout.fragment_warranty_details) {

    private var _binding: FragmentWarrantyDetailsBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: WarrantyViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var decimalFormat: DecimalFormat

    @Inject
    lateinit var dateFormat: SimpleDateFormat

    private lateinit var warranty: Warranty

    private lateinit var connectivityObserver: ConnectivityObserver
    private var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWarrantyDetailsBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[WarrantyViewModel::class.java]
        connectivityObserver = NetworkConnectivityObserver(requireContext())

        networkConnectivityObserver()
        subscribeToObservers()
        toolbarNavigationBackOnClick()
        handleExtendedFAB()
        getWarranty()
        editWarrantyOnClick()
        deleteWarrantyOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    private fun networkConnectivityObserver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityObserver.observe().onEach {
                networkStatus = it
                Timber.i("Network connectivity status inside of observer is $it")
            }.launchIn(lifecycleScope)
        } else {
            networkStatus = ConnectivityObserver.Status.AVAILABLE
        }
    }

    private fun subscribeToObservers() {
        deleteWarrantyObserver()
    }

    private fun toolbarNavigationBackOnClick() = binding.toolbar.setNavigationOnClickListener {
        returnToMainActivity()
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
        setWarrantyDetails()
    }

    private fun setWarrantyDetails() {
        binding.apply {
            val warranty = this@WarrantyDetailsFragment.warranty

            warrantyTitle = warranty.title

            warrantyBrand = if (warranty.brand.isNullOrBlank()) {
                tvBrand.setTextColor(ContextCompat.getColor(requireContext(), R.color.grayDark))
                requireContext().getString(R.string.warranty_details_empty_device)
            } else {
                warranty.brand
            }

            warrantyModel = if (warranty.model.isNullOrBlank()) {
                tvModel.setTextColor(ContextCompat.getColor(requireContext(), R.color.grayDark))
                requireContext().getString(R.string.warranty_details_empty_device)
            } else {
                warranty.model
            }

            warrantySerial = if (warranty.serialNumber.isNullOrBlank()) {
                tvSerial.setTextColor(ContextCompat.getColor(requireContext(), R.color.grayDark))
                requireContext().getString(R.string.warranty_details_empty_device)
            } else {
                warranty.serialNumber
            }

            warrantyDescription = if (warranty.description.isNullOrBlank()) {
                tvDescription.gravity = Gravity.CENTER
                tvDescription.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.grayDark)
                )
                requireContext().getString(R.string.warranty_details_empty_description)
            } else {
                warranty.description
            }

            val category = viewModel.getCategoryById(warranty.categoryId!!)
            category?.let {
                loadCategoryImage(requireContext(), it.icon, imageLoader, ivIcon, cpiIcon)
            }

            Calendar.getInstance().apply {
                dateFormat.parse(warranty.startingDate!!)?.let { time = it }

                val day = decimalFormat.format(get(Calendar.DAY_OF_MONTH))
                val month = decimalFormat.format((get(Calendar.MONTH)) + 1)
                val year = get(Calendar.YEAR)

                warrantyStartingDate = requireContext().getString(
                    R.string.warranty_details_format_date,
                    month, day, year
                )
            }

            val isLifetime = warranty.isLifetime ?: false
            if (isLifetime) {
                warrantyExpiryDate =
                    requireContext().getString(R.string.warranty_details_is_lifetime)
                warrantyStatus = requireContext().getString(R.string.warranty_details_status_valid)
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                tvStatus.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.green20)
            } else {
                val expiryCalendar = Calendar.getInstance().apply {
                    dateFormat.parse(warranty.expiryDate!!)?.let { time = it }

                    val day = decimalFormat.format(get(Calendar.DAY_OF_MONTH))
                    val month = decimalFormat.format((get(Calendar.MONTH)) + 1)
                    val year = get(Calendar.YEAR)

                    warrantyExpiryDate = requireContext().getString(
                        R.string.warranty_details_format_date,
                        month, day, year
                    )
                }

                val daysUntilExpiry = getDaysUntilExpiry(expiryCalendar)
                warrantyStatus = when {
                    daysUntilExpiry < 0 -> {
                        tvStatus.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.red)
                        )
                        tvStatus.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.red20)
                        requireContext().getString(R.string.warranty_details_status_expired)
                    }
                    daysUntilExpiry <= 30 -> {
                        tvStatus.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.orange)
                        )
                        tvStatus.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.orange20)
                        requireContext().getString(R.string.warranty_details_status_soon)
                    }
                    else -> {
                        tvStatus.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.green)
                        )
                        tvStatus.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.green20)
                        requireContext().getString(R.string.warranty_details_status_valid)
                    }
                }
            }
        }
    }

    private fun editWarrantyOnClick() = binding.fab.setOnClickListener {
        findNavController().navigate(
            WarrantyDetailsFragmentDirections.actionWarrantyDetailsFragmentToEditWarrantyFragment(
                warranty
            )
        )
    }

    private fun deleteWarrantyOnClick() =
        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener {
            showDeleteWarrantyDialog()
            false
        }

    private fun showDeleteWarrantyDialog() = showTwoBtnAlertDialog(
        requireContext(),
        R.string.warranty_details_delete_dialog_title,
        String.format(
            requireContext().getString(R.string.warranty_details_delete_dialog_message),
            warranty.title
        ),
        R.string.warranty_details_delete_dialog_positive,
        R.string.warranty_details_delete_dialog_negative,
        positiveAction = { deleteWarrantyFromFirestore() }
    )

    private fun deleteWarrantyFromFirestore() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            viewModel.deleteWarrantyFromFirestore(warranty.id)
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { deleteWarrantyFromFirestore() }
            Timber.e("deleteWarrantyFromFirestore Error: Offline")
        }
    }

    private fun deleteWarrantyObserver() =
        viewModel.deleteWarrantyLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        Toast.makeText(
                            requireContext(),
                            String.format(
                                requireContext().getString(
                                    R.string.warranty_details_delete_success, warranty.title
                                )
                            ),
                            Toast.LENGTH_LONG
                        ).show()

                        showInterstitialAd()
                        returnToMainActivity()
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                else -> showSomethingWentWrongError(requireContext(), requireView())
                            }
                        }
                    }
                }
            }
        }

    private fun returnToMainActivity() = findNavController().popBackStack()

    private fun showLoadingAnimation() = binding.apply {
        toolbar.menu.getItem(0).isVisible = false
        fab.isClickable = false
        cpiDelete.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiDelete.visibility = GONE
        toolbar.menu.getItem(0).isVisible = true
        fab.isClickable = true
    }

    private fun showInterstitialAd() = (requireActivity() as MainActivity).apply {
        when {
            appLovinAd.isReady -> appLovinAd.showAd()
            tapsellResponseId != null -> showTapsellInterstitialAd(tapsellResponseId!!)
        }

        requestAppLovinInterstitial()
    }

    private fun showTapsellInterstitialAd(responseId: String) = TapsellPlus.showInterstitialAd(
        requireActivity(), responseId, object : AdShowListener() {
            override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel?) {
                super.onOpened(tapsellPlusAdModel)
            }

            override fun onClosed(tapsellPlusAdModel: TapsellPlusAdModel?) {
                super.onClosed(tapsellPlusAdModel)
            }

            override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel?) {
                super.onError(tapsellPlusErrorModel)
            }
        })
}