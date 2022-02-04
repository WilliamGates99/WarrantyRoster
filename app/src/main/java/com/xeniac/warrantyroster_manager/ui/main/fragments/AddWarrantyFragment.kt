package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.CachePolicy
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentAddWarrantyBinding
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.models.Category
import com.xeniac.warrantyroster_manager.models.Status
import com.xeniac.warrantyroster_manager.models.WarrantyInput
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.MainViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.FRAGMENT_TAG_ADD_CALENDAR_EXPIRY
import com.xeniac.warrantyroster_manager.utils.Constants.FRAGMENT_TAG_ADD_CALENDAR_STARTING
import java.text.DecimalFormat
import java.util.*

class AddWarrantyFragment : Fragment(R.layout.fragment_add_warranty) {

    private var _binding: FragmentAddWarrantyBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel

    private val decimalFormat = DecimalFormat("00")
    private var selectedCategory: Category? = null
    private var startingCalendar: Calendar? = null
    private var expiryCalendar: Calendar? = null
    private lateinit var startingDateInput: String
    private lateinit var expiryDateInput: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddWarrantyBinding.bind(view)
        navController = Navigation.findNavController(view)
        (requireContext() as MainActivity).hideNavBar()
        viewModel = (activity as MainActivity).viewModel

        textInputsBackgroundColor()
        textInputsStrokeColor()
        categoryDropDownSelection()
        startingDatePicker()
        expiryDatePicker()
        returnToMainActivity()
        addWarrantyOnClick()
        addWarrantyObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        categoryDropDown()
    }

    private fun textInputsBackgroundColor() {
        binding.tiEditTitle.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutTitle.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutTitle.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiDdCategory.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutCategory.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutCategory.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditBrand.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutBrand.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutBrand.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditModel.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutModel.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutModel.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditSerial.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutSerial.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutSerial.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutDateStarting.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutDateStarting.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditDateExpiry.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutDateExpiry.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutDateExpiry.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditDescription.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutDescription.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutDescription.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiEditDateStarting.addTextChangedListener {
            hideDateError()
        }

        binding.tiEditDateExpiry.addTextChangedListener {
            hideDateError()
        }
    }

    private fun categoryDropDown() = binding.tiDdCategory.setAdapter(
        ArrayAdapter(requireContext(), R.layout.dropdown_category, viewModel.getAllCategoryTitles())
    )

    private fun categoryDropDownSelection() =
        binding.tiDdCategory.setOnItemClickListener { adapterView, _, position, _ ->
            val categoryTitle = adapterView.getItemAtPosition(position).toString()
            selectedCategory = viewModel.getCategoryByTitle(categoryTitle)

            selectedCategory?.let {
                val imageLoader = ImageLoader.Builder(requireContext())
                    .componentRegistry { add(SvgDecoder(requireContext())) }.build()
                binding.ivIconCategory.load(it.icon, imageLoader) {
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                    networkCachePolicy(CachePolicy.ENABLED)
                }
            }
        }

    private fun startingDatePicker() {
        binding.tiEditDateStarting.inputType = InputType.TYPE_NULL
        binding.tiEditDateStarting.keyListener = null

        binding.tiEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                openStartingDatePicker()
            }
        }
    }

    private fun expiryDatePicker() {
        binding.tiEditDateExpiry.inputType = InputType.TYPE_NULL
        binding.tiEditDateExpiry.keyListener = null

        binding.tiEditDateExpiry.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                openExpiryDatePicker()
            }
        }
    }

    private fun openStartingDatePicker() {
        val startingDP = MaterialDatePicker.Builder.datePicker()
            .setTitleText(requireContext().getString(R.string.add_warranty_title_date_picker_starting))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        startingDP.show(parentFragmentManager, FRAGMENT_TAG_ADD_CALENDAR_STARTING)

        startingDP.addOnPositiveButtonClickListener { selection ->
            startingCalendar = Calendar.getInstance()
            startingCalendar?.let {
                it.timeInMillis = selection
                startingDateInput = "${it.get(Calendar.YEAR)}-" +
                        "${decimalFormat.format((it.get(Calendar.MONTH)) + 1)}-" +
                        decimalFormat.format(it.get(Calendar.DAY_OF_MONTH))

                val startingDateInput =
                    "${decimalFormat.format((it.get(Calendar.MONTH)) + 1)}/" +
                            "${decimalFormat.format(it.get(Calendar.DAY_OF_MONTH))}/" +
                            "${it.get(Calendar.YEAR)}"

                binding.tiEditDateStarting.setText(startingDateInput)
                binding.tiEditDateStarting.clearFocus()
            }
        }

        startingDP.addOnDismissListener {
            binding.tiEditDateStarting.clearFocus()
        }
    }

    private fun openExpiryDatePicker() {
        val expiryDP = MaterialDatePicker.Builder.datePicker()
            .setTitleText(requireContext().getString(R.string.add_warranty_title_date_picker_expiry))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        expiryDP.show(parentFragmentManager, FRAGMENT_TAG_ADD_CALENDAR_EXPIRY)

        expiryDP.addOnPositiveButtonClickListener { selection ->
            expiryCalendar = Calendar.getInstance()
            expiryCalendar?.let {
                it.timeInMillis = selection
                expiryDateInput = "${it.get(Calendar.YEAR)}-" +
                        "${decimalFormat.format((it.get(Calendar.MONTH)) + 1)}-" +
                        decimalFormat.format(it.get(Calendar.DAY_OF_MONTH))

                val expiryDateInput =
                    "${decimalFormat.format((it.get(Calendar.MONTH)) + 1)}/" +
                            "${decimalFormat.format(it.get(Calendar.DAY_OF_MONTH))}/" +
                            "${it.get(Calendar.YEAR)}"

                binding.tiEditDateExpiry.setText(expiryDateInput)
                binding.tiEditDateExpiry.clearFocus()
            }
        }

        expiryDP.addOnDismissListener {
            binding.tiEditDateExpiry.clearFocus()
        }
    }

    private fun returnToMainActivity() =
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

    private fun addWarrantyOnClick() = binding.toolbar.menu[0]
        .setOnMenuItemClickListener {
            getWarrantyInput()
            false
        }

    private fun getWarrantyInput() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            binding.root.applicationWindowToken,
            0
        )

        val title = binding.tiEditTitle.text.toString().trim()

        if (title.isBlank()) {
            binding.tiLayoutTitle.requestFocus()
            binding.tiLayoutTitle.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (startingCalendar == null) {
            binding.tiLayoutDateStarting.requestFocus()
            binding.tiLayoutDateStarting.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (expiryCalendar == null) {
            binding.tiLayoutDateExpiry.requestFocus()
            binding.tiLayoutDateExpiry.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (!isStartingDateValid(startingCalendar!!, expiryCalendar!!)) {
            showDateError()
        } else {
            val brand = binding.tiEditBrand.text?.toString()?.trim()
            val model = binding.tiEditModel.text?.toString()?.trim()
            val serialNumber = binding.tiEditSerial.text?.toString()?.trim()
            val description = binding.tiEditDescription.text?.toString()?.trim()
            val categoryId = selectedCategory?.id ?: "10"

            val warrantyInput = WarrantyInput(
                title,
                brand,
                model,
                serialNumber,
                startingDateInput,
                expiryDateInput,
                description,
                categoryId,
                Firebase.auth.currentUser?.uid.toString()
            )
            addWarrantyToFirestore(warrantyInput)
        }
    }

    private fun addWarrantyToFirestore(warrantyInput: WarrantyInput) =
        viewModel.addWarrantyToFirestore(warrantyInput)

    private fun addWarrantyObserver() =
        viewModel.addWarrantyLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        navController.navigate(R.id.action_addWarrantyFragment_to_warrantiesFragment)
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
                                        setAction(requireContext().getString(R.string.network_error_retry)) { getWarrantyInput() }
                                        show()
                                    }
                                }
                                else -> {
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
        binding.toolbar.menu[0].isVisible = false
        binding.tiEditTitle.isEnabled = false
        binding.tiDdCategory.isEnabled = false
        binding.tiEditBrand.isEnabled = false
        binding.tiEditModel.isEnabled = false
        binding.tiEditSerial.isEnabled = false
        binding.tiEditDateStarting.isEnabled = false
        binding.tiEditDateExpiry.isEnabled = false
        binding.tiEditDescription.isEnabled = false
        binding.cpiAdd.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiAdd.visibility = GONE
        binding.toolbar.menu[0].isVisible = true
        binding.tiEditTitle.isEnabled = true
        binding.tiDdCategory.isEnabled = true
        binding.tiEditBrand.isEnabled = true
        binding.tiEditModel.isEnabled = true
        binding.tiEditSerial.isEnabled = true
        binding.tiEditDateStarting.isEnabled = true
        binding.tiEditDateExpiry.isEnabled = true
        binding.tiEditDescription.isEnabled = true
    }

    private fun showDateError() {
        binding.tiLayoutDateStarting.requestFocus()
        binding.tiLayoutDateStarting.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.red)
        binding.tvDateError.visibility = VISIBLE
    }

    private fun hideDateError() {
        binding.tiLayoutDateStarting.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.blue)
        binding.tvDateError.visibility = GONE
    }

    private fun isStartingDateValid(startingCalendar: Calendar, expiryCalendar: Calendar): Boolean =
        expiryCalendar >= startingCalendar
}