package com.xeniac.warrantyroster_manager.ui.main.fragments

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentWarrantyDetailsBinding
import com.xeniac.warrantyroster_manager.models.Status
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.models.Warranty
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.MainViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class WarrantyDetailsFragment : Fragment(R.layout.fragment_warranty_details) {

    private var _binding: FragmentWarrantyDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var decimalFormat: DecimalFormat

    @Inject
    lateinit var dateFormat: SimpleDateFormat

    private lateinit var warranty: Warranty

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWarrantyDetailsBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        returnToMainActivity()
        handleExtendedFAB()
        getWarranty()
        editWarrantyOnClick()
        deleteWarrantyOnClick()
        deleteWarrantyObserver()
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

    private fun setWarrantyDetails(daysUntilExpiry: Long) {
        binding.toolbar.title = warranty.title

        if (warranty.brand.isNullOrBlank()) {
            binding.tvBrand.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.grayDark)
            )
            binding.tvBrand.text =
                requireContext().getString(R.string.warranty_details_empty_device)
        } else {
            binding.tvBrand.text = warranty.brand
        }

        if (warranty.model.isNullOrBlank()) {
            binding.tvModel.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.grayDark)
            )
            binding.tvModel.text =
                requireContext().getString(R.string.warranty_details_empty_device)
        } else {
            binding.tvModel.text = warranty.model
        }

        if (warranty.serialNumber.isNullOrBlank()) {
            binding.tvSerial.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.grayDark)
            )
            binding.tvSerial.text =
                requireContext().getString(R.string.warranty_details_empty_device)
        } else {
            binding.tvSerial.text = warranty.serialNumber
        }

        if (warranty.description.isNullOrBlank()) {
            binding.tvDescription.gravity = Gravity.CENTER
            binding.tvDescription.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.grayDark)
            )
            binding.tvDescription.text =
                requireContext().getString(R.string.warranty_details_empty_description)
        } else {
            binding.tvDescription.text = warranty.description
        }

        val startingCalendar = Calendar.getInstance()
        val expiryCalendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())

        dateFormat.parse(warranty.startingDate!!)?.let {
            startingCalendar.time = it
        }

        dateFormat.parse(warranty.expiryDate!!)?.let {
            expiryCalendar.time = it
        }

        val startingDate =
            "${decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1)}/" +
                    "${decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                    "${startingCalendar.get(Calendar.YEAR)}"
        binding.tvDateStarting.text = startingDate

        val expiryDate =
            "${decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1)}/" +
                    "${decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                    "${expiryCalendar.get(Calendar.YEAR)}"
        binding.tvDateExpiry.text = expiryDate

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

        val category = viewModel.getCategoryById(warranty.categoryId!!)

        category?.let {
            binding.ivIcon.load(it.icon, imageLoader) {
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                networkCachePolicy(CachePolicy.ENABLED)
            }
        }
    }

    private fun editWarrantyOnClick() = binding.fab.setOnClickListener {
        val action = WarrantyDetailsFragmentDirections
            .actionWarrantyDetailsFragmentToEditWarrantyFragment(warranty)
        findNavController().navigate(action)
    }

    private fun deleteWarrantyOnClick() =
        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener {
            showDeleteWarrantyDialog()
            false
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

    private fun deleteWarrantyFromFirestore() = viewModel.deleteWarrantyFromFirestore(warranty.id)

    private fun deleteWarrantyObserver() =
        viewModel.deleteWarrantyLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        Toast.makeText(
                            requireContext(),
                            String.format(
                                requireContext().getString(
                                    R.string.warranty_details_delete_success,
                                    warranty.title
                                )
                            ), Toast.LENGTH_LONG
                        ).show()

                        when {
                            (requireActivity() as MainActivity).adColonyAd != null -> {
                                (requireActivity() as MainActivity).adColonyAd?.show()
                            }
                            (requireActivity() as MainActivity).tapsellResponseId != null -> {
                                (requireActivity() as MainActivity).tapsellResponseId?.let {
                                    showInterstitialAd(it)
                                }
                            }
                        }
                        requireActivity().onBackPressed()
                        (requireActivity() as MainActivity).requestAdColonyInterstitial()
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_connection),
                                        LENGTH_LONG
                                    ).apply {
                                        setAction(requireContext().getString(R.string.network_error_retry)) { deleteWarrantyFromFirestore() }
                                        show()
                                    }
                                }
                                it.contains(ERROR_NETWORK_403) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_403),
                                        LENGTH_LONG
                                    ).show()
                                }
                                else -> {
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

    private fun showInterstitialAd(responseId: String) = TapsellPlus.showInterstitialAd(
        requireActivity(),
        responseId,
        object : AdShowListener() {
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