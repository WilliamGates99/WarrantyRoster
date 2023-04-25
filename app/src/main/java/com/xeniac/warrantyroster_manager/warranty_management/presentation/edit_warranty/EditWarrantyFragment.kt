package com.xeniac.warrantyroster_manager.warranty_management.presentation.edit_warranty

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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
import com.xeniac.warrantyroster_manager.core.data.repository.NetworkConnectivityObserver
import com.xeniac.warrantyroster_manager.core.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.databinding.FragmentEditWarrantyBinding
import com.xeniac.warrantyroster_manager.util.CoilHelper.loadCategoryImage
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_STARTING_DATE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_TITLE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_INVALID_STARTING_DATE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.Constants.FRAGMENT_TAG_EDIT_CALENDAR_EXPIRY
import com.xeniac.warrantyroster_manager.util.Constants.FRAGMENT_TAG_EDIT_CALENDAR_STARTING
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_EDIT_WARRANTY_BRAND
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_EDIT_WARRANTY_CATEGORY_ID
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_EDIT_WARRANTY_DESCRIPTION
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_EDIT_WARRANTY_EXPIRY_DATE_IN_MILLIS
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_EDIT_WARRANTY_IS_LIFETIME
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_EDIT_WARRANTY_MODEL
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_EDIT_WARRANTY_SERIAL
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_EDIT_WARRANTY_STARTING_DATE_IN_MILLIS
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_EDIT_WARRANTY_TITLE
import com.xeniac.warrantyroster_manager.util.DateHelper.getTimeZoneOffsetInMillis
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showUnavailableNetworkConnectionError
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.Category
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.presentation.WarrantyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class EditWarrantyFragment : Fragment(R.layout.fragment_edit_warranty) {

    private var _binding: FragmentEditWarrantyBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: WarrantyViewModel

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

    private lateinit var connectivityObserver: ConnectivityObserver
    private var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditWarrantyBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[WarrantyViewModel::class.java]
        connectivityObserver = NetworkConnectivityObserver(requireContext())

        networkConnectivityObserver()
        textInputsBackgroundColor()
        textInputsStrokeColor()
        subscribeToObservers()
        categoryDropDownSelection()
        categoryDropDownOnDismiss()
        lifetimeWarrantyCheckBoxListener()
        startingDatePickerOnFocusListener()
        expiryDatePickerOnFocusListener()
        returnToWarrantyDetailsFragment()
        getWarranty()
        editWarrantyOnClick()
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
                        binding.tiDdCategory.setText(category.title[viewModel.getCategoryTitleMapKey()])
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

    private fun textInputsBackgroundColor() = binding.apply {
        tiEditTitle.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutTitle.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutTitle.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiDdCategory.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutCategory.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutCategory.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditBrand.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutBrand.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutBrand.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditModel.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutModel.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutModel.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditSerial.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutSerial.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutSerial.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutDateStarting.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutDateStarting.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditDateExpiry.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutDateExpiry.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutDateExpiry.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditDescription.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutDescription.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutDescription.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() = binding.apply {
        tiEditTitle.addTextChangedListener {
            tiLayoutTitle.isErrorEnabled = false
            tiLayoutTitle.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
        }

        tiEditDateStarting.addTextChangedListener {
            hideDateError()
        }

        tiEditDateExpiry.addTextChangedListener {
            hideDateError()
        }
    }

    private fun subscribeToObservers() {
        updateWarrantyObserver()
        getUpdatedWarrantyObserver()
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

    private fun setExpiryDateActivation(isLifeTime: Boolean) {
        binding.tiLayoutDateExpiry.isEnabled = !isLifeTime
    }

    private fun startingDatePickerOnFocusListener() = binding.apply {
        tiEditDateStarting.inputType = InputType.TYPE_NULL
        tiEditDateStarting.keyListener = null

        tiEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                openStartingDatePicker()
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

    private fun expiryDatePickerOnFocusListener() = binding.apply {
        tiEditDateExpiry.inputType = InputType.TYPE_NULL
        tiEditDateExpiry.keyListener = null

        tiEditDateExpiry.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                openExpiryDatePicker()
            }
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
        binding.apply {
            tiEditTitle.setText(warranty.title)
            tiEditBrand.setText(warranty.brand)
            tiEditModel.setText(warranty.model)
            tiEditSerial.setText(warranty.serialNumber)
            tiEditDescription.setText(warranty.description)

            setStartingDateText(warranty.startingDate!!)
            setExpiryDateText()

            selectedCategory = viewModel.getCategoryById(warranty.categoryId!!)
            selectedCategory?.let {
                tiDdCategory.setText(it.title[viewModel.getCategoryTitleMapKey()])
                loadCategoryIcon(it.icon)
            }
        }
    }

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
        validateWarrantyInputs()
        false
    }

    private fun validateWarrantyInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().applicationWindowToken, 0)

        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            val title = binding.tiEditTitle.text.toString().trim()
            val brand = binding.tiEditBrand.text?.toString()?.trim()
            val model = binding.tiEditModel.text?.toString()?.trim()
            val serialNumber = binding.tiEditSerial.text?.toString()?.trim()
            val description = binding.tiEditDescription.text?.toString()?.trim()
            val isLifetime = binding.cbLifetime.isChecked
            val categoryId = selectedCategory?.id ?: "10"

            viewModel.validateEditWarrantyInputs(
                warranty.id, title, brand, model, serialNumber,
                isLifetime, startingDateInput, expiryDateInput, description, categoryId,
                selectedStartingDateInMillis, selectedExpiryDateInMillis
            )
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { validateWarrantyInputs() }
            Timber.e("validateWarrantyInputs Error: Offline")
        }
    }

    private fun updateWarrantyObserver() =
        viewModel.updateWarrantyLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> getUpdatedWarrantyFromFirestore()
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_TITLE) -> {
                                    binding.tiLayoutTitle.error =
                                        requireContext().getString(R.string.edit_warranty_error_blank_title)
                                    binding.tiLayoutTitle.requestFocus()
                                }
                                it.contains(ERROR_INPUT_BLANK_STARTING_DATE) -> binding.tiLayoutDateStarting.requestFocus()
                                it.contains(ERROR_INPUT_BLANK_EXPIRY_DATE) -> binding.tiLayoutDateExpiry.requestFocus()
                                it.contains(ERROR_INPUT_INVALID_STARTING_DATE) -> showDateError()
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(), requireView()
                                    )
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    snackbar = show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(),
                                        requireView()
                                    )
                                }
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

    private fun getUpdatedWarrantyFromFirestore() =
        viewModel.getUpdatedWarrantyFromFirestore(warranty.id)

    private fun getUpdatedWarrantyObserver() =
        viewModel.updatedWarrantyLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        response.data?.let { updatedWarranty ->
                            navigateToWarrantyDetails(updatedWarranty)
                        }
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

    private fun navigateToWarrantyDetails(updatedWarranty: Warranty) = findNavController().navigate(
        EditWarrantyFragmentDirections.actionEditWarrantyFragmentToWarrantyDetailsFragment(
            updatedWarranty
        )
    )

    private fun loadCategoryIcon(categoryIcon: String) = loadCategoryImage(
        requireContext(), categoryIcon, imageLoader, binding.ivIconCategory, binding.cpiIconCategory
    )

    private fun showLoadingAnimation() = binding.apply {
        toolbar.menu.getItem(0).isVisible = false
        tiEditTitle.isEnabled = false
        tiDdCategory.isEnabled = false
        tiEditBrand.isEnabled = false
        tiEditModel.isEnabled = false
        tiEditSerial.isEnabled = false
        tiEditDateStarting.isEnabled = false
        tiEditDateExpiry.isEnabled = false
        tiEditDescription.isEnabled = false
        cpiEdit.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiEdit.visibility = GONE
        toolbar.menu.getItem(0).isVisible = true
        tiEditTitle.isEnabled = true
        tiDdCategory.isEnabled = true
        tiEditBrand.isEnabled = true
        tiEditModel.isEnabled = true
        tiEditSerial.isEnabled = true
        tiEditDateStarting.isEnabled = true
        tiEditDateExpiry.isEnabled = true
        tiEditDescription.isEnabled = true
    }

    private fun showDateError() = binding.apply {
        tiLayoutDateStarting.requestFocus()
        tiLayoutDateStarting.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.red)
        tvDateError.visibility = VISIBLE
    }

    private fun hideDateError() = binding.apply {
        tiLayoutDateStarting.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
        tvDateError.visibility = GONE
    }
}