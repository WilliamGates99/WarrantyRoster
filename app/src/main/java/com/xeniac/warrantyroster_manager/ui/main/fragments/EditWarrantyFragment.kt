package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.remote.models.Category
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.databinding.FragmentEditWarrantyBinding
import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.ui.viewmodels.MainViewModel
import com.xeniac.warrantyroster_manager.utils.CoilHelper.loadCategoryImage
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_TITLE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_INVALID_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.FRAGMENT_TAG_EDIT_CALENDAR_EXPIRY
import com.xeniac.warrantyroster_manager.utils.Constants.FRAGMENT_TAG_EDIT_CALENDAR_STARTING
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_EDIT_WARRANTY_BRAND
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_EDIT_WARRANTY_CATEGORY_ID
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_EDIT_WARRANTY_DESCRIPTION
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_EDIT_WARRANTY_EXPIRY_DATE_IN_MILLIS
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_EDIT_WARRANTY_IS_LIFETIME
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_EDIT_WARRANTY_MODEL
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_EDIT_WARRANTY_SERIAL
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_EDIT_WARRANTY_STARTING_DATE_IN_MILLIS
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_EDIT_WARRANTY_TITLE
import com.xeniac.warrantyroster_manager.utils.DateHelper.getTimeZoneOffsetInMillis
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EditWarrantyFragment : Fragment(R.layout.fragment_edit_warranty) {

    private var _binding: FragmentEditWarrantyBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var decimalFormat: DecimalFormat

    @Inject
    lateinit var dateFormat: SimpleDateFormat

    private lateinit var warranty: Warranty
    private var selectedCategory: Category? = null

    private var selectedStartingDateInMillis = 0L
    private var startingDateInput: String? = null

    private var selectedExpiryDateInMillis = 0L
    private var expiryDateInput: String? = null

    private val offset = getTimeZoneOffsetInMillis()

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditWarrantyBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        onBackPressed()
        textInputsBackgroundColor()
        textInputsStrokeColor()
        categoryDropDownSelection()
        categoryDropDownOnDismiss()
        lifetimeWarrantyCheckBoxListener()
        startingDatePickerOnFocusListener()
        expiryDatePickerOnFocusListener()
        returnToWarrantyDetailsFragment()
        getWarranty()
        editWarrantyOnClick()
        subscribeToObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        categoryDropDown()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val title = binding.tiEditTitle.text.toString().trim()
            val brand = binding.tiEditBrand.text.toString().trim()
            val model = binding.tiEditModel.text.toString().trim()
            val serialNumber = binding.tiEditSerial.text.toString().trim()
            val description = binding.tiEditDescription.text.toString().trim()
            val categoryId = selectedCategory?.id ?: "10"
            val isLifetime = binding.cbLifetime.isChecked

            if (title.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_EDIT_WARRANTY_TITLE, title)
            }

            if (brand.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_EDIT_WARRANTY_BRAND, brand)
            }

            if (model.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_EDIT_WARRANTY_MODEL, model)
            }

            if (serialNumber.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_EDIT_WARRANTY_SERIAL, serialNumber)
            }

            if (description.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_EDIT_WARRANTY_DESCRIPTION, description)
            }

            outState.putString(SAVE_INSTANCE_EDIT_WARRANTY_CATEGORY_ID, categoryId)

            outState.putBoolean(SAVE_INSTANCE_EDIT_WARRANTY_IS_LIFETIME, isLifetime)

            outState.putLong(
                SAVE_INSTANCE_EDIT_WARRANTY_STARTING_DATE_IN_MILLIS,
                selectedStartingDateInMillis
            )

            outState.putLong(
                SAVE_INSTANCE_EDIT_WARRANTY_EXPIRY_DATE_IN_MILLIS,
                selectedExpiryDateInMillis
            )
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            savedInstanceState?.let {
                it.getString(SAVE_INSTANCE_EDIT_WARRANTY_TITLE)?.let { restoredTitle ->
                    binding.tiEditTitle.setText(restoredTitle)
                }

                it.getString(SAVE_INSTANCE_EDIT_WARRANTY_BRAND)?.let { restoredBrand ->
                    binding.tiEditBrand.setText(restoredBrand)
                }

                it.getString(SAVE_INSTANCE_EDIT_WARRANTY_MODEL)?.let { restoredModel ->
                    binding.tiEditModel.setText(restoredModel)
                }

                it.getString(SAVE_INSTANCE_EDIT_WARRANTY_SERIAL)?.let { restoredSerial ->
                    binding.tiEditSerial.setText(restoredSerial)
                }

                it.getString(SAVE_INSTANCE_EDIT_WARRANTY_DESCRIPTION)?.let { restoredDescription ->
                    binding.tiEditDescription.setText(restoredDescription)
                }

                it.getString(SAVE_INSTANCE_EDIT_WARRANTY_CATEGORY_ID)?.let { restoredCategoryId ->
                    selectedCategory = viewModel.getCategoryById(restoredCategoryId)

                    selectedCategory?.let { category ->
                        binding.tiDdCategory.setText(category.title[preferencesRepository.getCategoryTitleMapKey()])
                        loadCategoryIcon(category.icon)
                    }
                }

                it.getBoolean(SAVE_INSTANCE_EDIT_WARRANTY_IS_LIFETIME).let { restoredIsLifetime ->
                    binding.cbLifetime.isChecked = restoredIsLifetime
                    setExpiryDateActivation(restoredIsLifetime)
                }

                it.getLong(SAVE_INSTANCE_EDIT_WARRANTY_STARTING_DATE_IN_MILLIS)
                    .let { restoredDate ->
                        if (restoredDate != 0L) {
                            selectedStartingDateInMillis = restoredDate
                            setStartingDate()
                        }
                    }

                it.getLong(SAVE_INSTANCE_EDIT_WARRANTY_EXPIRY_DATE_IN_MILLIS).let { restoredDate ->
                    if (restoredDate != 0L) {
                        selectedExpiryDateInMillis = restoredDate
                        setExpiryDate()
                    }
                }
            }
        }
        super.onViewStateRestored(savedInstanceState)
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
            selectedCategory?.let { loadCategoryIcon(it.icon) }
        }

    private fun categoryDropDownOnDismiss() = binding.tiDdCategory.setOnDismissListener {
        binding.tiDdCategory.clearFocus()
    }

    private fun lifetimeWarrantyCheckBoxListener() =
        binding.cbLifetime.setOnCheckedChangeListener { _, isChecked ->
            setExpiryDateActivation(isChecked)
        }

    private fun startingDatePickerOnFocusListener() {
        binding.tiEditDateStarting.inputType = InputType.TYPE_NULL
        binding.tiEditDateStarting.keyListener = null

        binding.tiEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                openStartingDatePicker()
            }
        }
    }

    private fun expiryDatePickerOnFocusListener() {
        binding.tiEditDateExpiry.inputType = InputType.TYPE_NULL
        binding.tiEditDateExpiry.keyListener = null

        binding.tiEditDateExpiry.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                openExpiryDatePicker()
            }
        }
    }

    private fun openStartingDatePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(requireContext().getString(R.string.edit_warranty_title_date_picker_starting))
            setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        }

        val startingDP = if (selectedStartingDateInMillis == 0L) {
            datePickerBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
        } else {
            datePickerBuilder.setSelection(selectedStartingDateInMillis).build()
        }

        startingDP.show(parentFragmentManager, FRAGMENT_TAG_EDIT_CALENDAR_STARTING)

        startingDP.addOnPositiveButtonClickListener { selectionInMillis ->
            selectedStartingDateInMillis = selectionInMillis
            setStartingDate()
        }

        startingDP.addOnDismissListener {
            binding.tiEditDateStarting.clearFocus()
        }
    }

    private fun openExpiryDatePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(requireContext().getString(R.string.edit_warranty_title_date_picker_expiry))
            setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        }

        val expiryDP = if (selectedExpiryDateInMillis == 0L) {
            datePickerBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
        } else {
            datePickerBuilder.setSelection(selectedExpiryDateInMillis).build()
        }

        expiryDP.show(parentFragmentManager, FRAGMENT_TAG_EDIT_CALENDAR_EXPIRY)

        expiryDP.addOnPositiveButtonClickListener { selectionInMillis ->
            selectedExpiryDateInMillis = selectionInMillis
            setExpiryDate()
        }

        expiryDP.addOnDismissListener {
            binding.tiEditDateExpiry.clearFocus()
        }
    }

    private fun setStartingDate() {
        Calendar.getInstance().apply {
            timeInMillis = if (offset < 0) {
                selectedStartingDateInMillis - offset
            } else {
                selectedStartingDateInMillis
            }

            val day = decimalFormat.format(get(Calendar.DAY_OF_MONTH))
            val month = decimalFormat.format((get(Calendar.MONTH)) + 1)
            val year = get(Calendar.YEAR)

            startingDateInput = "$year-$month-$day"

            val startingDateText = requireContext().getString(
                R.string.edit_warranty_format_date,
                month, day, year
            )

            binding.tiEditDateStarting.setText(startingDateText)
            binding.tiEditDateStarting.clearFocus()
        }
    }

    private fun setExpiryDate() {
        Calendar.getInstance().apply {
            timeInMillis = if (offset < 0) {
                selectedExpiryDateInMillis - offset
            } else {
                selectedExpiryDateInMillis
            }

            val day = decimalFormat.format(get(Calendar.DAY_OF_MONTH))
            val month = decimalFormat.format((get(Calendar.MONTH)) + 1)
            val year = get(Calendar.YEAR)

            expiryDateInput = "$year-$month-$day"

            val expiryDateText = requireContext().getString(
                R.string.edit_warranty_format_date,
                month, day, year
            )

            binding.tiEditDateExpiry.setText(expiryDateText)
            binding.tiEditDateExpiry.clearFocus()
        }
    }

    private fun returnToWarrantyDetailsFragment() = binding.toolbar.setNavigationOnClickListener {
        findNavController().popBackStack()
    }

    private fun getWarranty() {
        val args: EditWarrantyFragmentArgs by navArgs()
        warranty = args.warranty
        setWarrantyDetails()
    }

    private fun setWarrantyDetails() = lifecycleScope.launch {
        binding.tiEditTitle.setText(warranty.title)
        binding.tiEditBrand.setText(warranty.brand)
        binding.tiEditModel.setText(warranty.model)
        binding.tiEditSerial.setText(warranty.serialNumber)
        binding.tiEditDescription.setText(warranty.description)

        setStartingDateText(warranty.startingDate!!)
        setExpiryDateText()

        selectedCategory = viewModel.getCategoryById(warranty.categoryId!!)
        selectedCategory?.let {
            binding.tiDdCategory.setText(it.title[preferencesRepository.getCategoryTitleMapKey()])
            loadCategoryIcon(it.icon)
        }
    }

    private fun loadCategoryIcon(categoryIcon: String) = loadCategoryImage(
        requireContext(), categoryIcon, imageLoader, binding.ivIconCategory, binding.cpiIconCategory
    )

    private fun setStartingDateText(startingDate: String) {
        Calendar.getInstance().apply {
            dateFormat.parse(startingDate)?.let { time = it }

            val day = decimalFormat.format(get(Calendar.DAY_OF_MONTH))
            val month = decimalFormat.format((get(Calendar.MONTH)) + 1)
            val year = get(Calendar.YEAR)

            startingDateInput = "$year-$month-$day"

            val startingDateText = requireContext().getString(
                R.string.edit_warranty_format_date,
                month, day, year
            )

            binding.tiEditDateStarting.setText(startingDateText)

            // Add/Subtract offset to Prevent the Calendar to Show the Previous Day
            selectedStartingDateInMillis = if (offset < 0) {
                timeInMillis - offset
            } else {
                timeInMillis + offset
            }
        }
    }

    private fun setExpiryDateText() {
        val isLifetime = warranty.isLifetime ?: false
        if (isLifetime) {
            binding.cbLifetime.isChecked = true
        } else {
            binding.cbLifetime.isChecked = false
            Calendar.getInstance().apply {
                dateFormat.parse(warranty.expiryDate!!)?.let { time = it }

                val day = decimalFormat.format(get(Calendar.DAY_OF_MONTH))
                val month = decimalFormat.format((get(Calendar.MONTH)) + 1)
                val year = get(Calendar.YEAR)

                expiryDateInput = "$year-$month-$day"

                val expiryDateText = requireContext().getString(
                    R.string.edit_warranty_format_date,
                    month, day, year
                )

                binding.tiEditDateExpiry.setText(expiryDateText)

                // Add/Subtract offset to Prevent the Calendar to Show the Previous Day
                selectedExpiryDateInMillis = if (offset < 0) {
                    timeInMillis - offset
                } else {
                    timeInMillis + offset
                }
            }
        }
    }

    private fun editWarrantyOnClick() = binding.toolbar.menu.getItem(0).setOnMenuItemClickListener {
        getWarrantyInput()
        false
    }

    private fun subscribeToObservers() {
        updateWarrantyObserver()
        getUpdatedWarrantyObserver()
    }

    private fun getWarrantyInput() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        val title = binding.tiEditTitle.text.toString().trim()
        val brand = binding.tiEditBrand.text?.toString()?.trim()
        val model = binding.tiEditModel.text?.toString()?.trim()
        val serialNumber = binding.tiEditSerial.text?.toString()?.trim()
        val description = binding.tiEditDescription.text?.toString()?.trim()
        val isLifetime = binding.cbLifetime.isChecked
        val categoryId = selectedCategory?.id ?: "10"

        viewModel.checkEditWarrantyInputs(
            warranty.id, title, brand, model, serialNumber,
            isLifetime, startingDateInput, expiryDateInput, description, categoryId,
            selectedStartingDateInMillis, selectedExpiryDateInMillis
        )
    }

    private fun updateWarrantyObserver() =
        viewModel.updateWarrantyLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> getUpdatedWarrantyFromFirestore()
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_TITLE) -> {
                                    binding.tiLayoutTitle.requestFocus()
                                    binding.tiLayoutTitle.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_BLANK_STARTING_DATE) -> {
                                    binding.tiLayoutDateStarting.requestFocus()
                                    binding.tiLayoutDateStarting.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_BLANK_EXPIRY_DATE) -> {
                                    binding.tiLayoutDateExpiry.requestFocus()
                                    binding.tiLayoutDateExpiry.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_INVALID_STARTING_DATE) -> showDateError()
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkConnectionError(
                                        requireContext(), binding.root
                                    ) { getWarrantyInput() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    snackbar = show403Error(requireContext(), binding.root)
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(),
                                        binding.root
                                    )
                                }
                                else -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(),
                                        binding.root
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun getUpdatedWarrantyFromFirestore() =
        viewModel.getUpdatedWarrantyFromFirestore(warranty.id)

    private fun getUpdatedWarrantyObserver() =
        viewModel.updatedWarrantyLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        response.data?.let { updatedWarranty ->
                            navigateToWarrantyDetails(updatedWarranty)
                        }
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkConnectionError(
                                        requireContext(),
                                        binding.root
                                    ) { getUpdatedWarrantyFromFirestore() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), binding.root)
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), binding.root)
                                }
                                else -> {
                                    showNetworkFailureError(requireContext(), binding.root)
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun navigateToWarrantyDetails(updatedWarranty: Warranty) = findNavController().navigate(
        EditWarrantyFragmentDirections.actionEditWarrantyFragmentToWarrantyDetailsFragment(
            updatedWarranty
        )
    )

    private fun showLoadingAnimation() {
        binding.toolbar.menu.getItem(0).isVisible = false
        binding.tiEditTitle.isEnabled = false
        binding.tiDdCategory.isEnabled = false
        binding.tiEditBrand.isEnabled = false
        binding.tiEditModel.isEnabled = false
        binding.tiEditSerial.isEnabled = false
        binding.tiEditDateStarting.isEnabled = false
        binding.tiEditDateExpiry.isEnabled = false
        binding.tiEditDescription.isEnabled = false
        binding.cpiEdit.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiEdit.visibility = GONE
        binding.toolbar.menu.getItem(0).isVisible = true
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

    private fun setExpiryDateActivation(isLifeTime: Boolean) {
        binding.tiLayoutDateExpiry.isEnabled = !isLifeTime
    }
}