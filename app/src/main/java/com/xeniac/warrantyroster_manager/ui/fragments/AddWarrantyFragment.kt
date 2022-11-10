package com.xeniac.warrantyroster_manager.ui.fragments

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.remote.models.Category
import com.xeniac.warrantyroster_manager.databinding.FragmentAddWarrantyBinding
import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.ui.MainActivity
import com.xeniac.warrantyroster_manager.ui.main.fragments.AddWarrantyFragmentDirections
import com.xeniac.warrantyroster_manager.ui.viewmodels.WarrantyViewModel
import com.xeniac.warrantyroster_manager.utils.CoilHelper.loadCategoryImage
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_TITLE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_INVALID_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.FRAGMENT_TAG_ADD_CALENDAR_EXPIRY
import com.xeniac.warrantyroster_manager.utils.Constants.FRAGMENT_TAG_ADD_CALENDAR_STARTING
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_ADD_WARRANTY_BRAND
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_ADD_WARRANTY_CATEGORY_ID
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_ADD_WARRANTY_DESCRIPTION
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_ADD_WARRANTY_EXPIRY_DATE_IN_MILLIS
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_ADD_WARRANTY_IS_LIFETIME
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_ADD_WARRANTY_MODEL
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_ADD_WARRANTY_SERIAL
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_ADD_WARRANTY_STARTING_DATE_IN_MILLIS
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_ADD_WARRANTY_TITLE
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddWarrantyFragment : Fragment(R.layout.fragment_add_warranty) {

    private var _binding: FragmentAddWarrantyBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: WarrantyViewModel

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var decimalFormat: DecimalFormat

    private var selectedCategory: Category? = null

    private var selectedStartingDateInMillis = 0L
    var startingDateInput: String? = null

    private var selectedExpiryDateInMillis = 0L
    private var expiryDateInput: String? = null

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddWarrantyBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[WarrantyViewModel::class.java]

        textInputsBackgroundColor()
        textInputsStrokeColor()
        categoryDropDownSelection()
        categoryDropDownOnDismiss()
        lifetimeWarrantyCheckBoxListener()
        startingDatePickerOnFocusListener()
        expiryDatePickerOnFocusListener()
        returnToMainActivity()
        addWarrantyOnClick()
        addWarrantyObserver()
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
                outState.putString(SAVE_INSTANCE_ADD_WARRANTY_TITLE, title)
            }

            if (brand.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_ADD_WARRANTY_BRAND, brand)
            }

            if (model.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_ADD_WARRANTY_MODEL, model)
            }

            if (serialNumber.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_ADD_WARRANTY_SERIAL, serialNumber)
            }

            if (description.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_ADD_WARRANTY_DESCRIPTION, description)
            }

            outState.putString(SAVE_INSTANCE_ADD_WARRANTY_CATEGORY_ID, categoryId)

            outState.putBoolean(SAVE_INSTANCE_ADD_WARRANTY_IS_LIFETIME, isLifetime)

            outState.putLong(
                SAVE_INSTANCE_ADD_WARRANTY_STARTING_DATE_IN_MILLIS,
                selectedStartingDateInMillis
            )

            outState.putLong(
                SAVE_INSTANCE_ADD_WARRANTY_EXPIRY_DATE_IN_MILLIS,
                selectedExpiryDateInMillis
            )
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            savedInstanceState?.let {
                it.getString(SAVE_INSTANCE_ADD_WARRANTY_TITLE)?.let { restoredTitle ->
                    binding.tiEditTitle.setText(restoredTitle)
                }

                it.getString(SAVE_INSTANCE_ADD_WARRANTY_BRAND)?.let { restoredBrand ->
                    binding.tiEditBrand.setText(restoredBrand)
                }

                it.getString(SAVE_INSTANCE_ADD_WARRANTY_MODEL)?.let { restoredModel ->
                    binding.tiEditModel.setText(restoredModel)
                }

                it.getString(SAVE_INSTANCE_ADD_WARRANTY_SERIAL)?.let { restoredSerial ->
                    binding.tiEditSerial.setText(restoredSerial)
                }

                it.getString(SAVE_INSTANCE_ADD_WARRANTY_DESCRIPTION)?.let { restoredDescription ->
                    binding.tiEditDescription.setText(restoredDescription)
                }

                it.getString(SAVE_INSTANCE_ADD_WARRANTY_CATEGORY_ID)?.let { restoredCategoryId ->
                    selectedCategory = viewModel.getCategoryById(restoredCategoryId)

                    selectedCategory?.let { category ->
                        binding.tiDdCategory.setText(category.title[preferencesRepository.getCategoryTitleMapKey()])
                        loadCategoryIcon(category.icon)
                    }
                }

                it.getBoolean(SAVE_INSTANCE_ADD_WARRANTY_IS_LIFETIME).let { restoredIsLifetime ->
                    binding.cbLifetime.isChecked = restoredIsLifetime
                    setExpiryDateActivation(restoredIsLifetime)
                }

                it.getLong(SAVE_INSTANCE_ADD_WARRANTY_STARTING_DATE_IN_MILLIS).let { restoredDate ->
                    if (restoredDate != 0L) {
                        selectedStartingDateInMillis = restoredDate
                        setStartingDate()
                    }
                }

                it.getLong(SAVE_INSTANCE_ADD_WARRANTY_EXPIRY_DATE_IN_MILLIS).let { restoredDate ->
                    if (restoredDate != 0L) {
                        selectedExpiryDateInMillis = restoredDate
                        setExpiryDate()
                    }
                }
            }
        }
        super.onViewStateRestored(savedInstanceState)
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

    private fun startingDatePickerOnFocusListener() = binding.apply {
        tiEditDateStarting.inputType = InputType.TYPE_NULL
        tiEditDateStarting.keyListener = null

        tiEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                openStartingDatePicker()
            }
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

    private fun openStartingDatePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(requireContext().getString(R.string.add_warranty_title_date_picker_starting))
            setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        }

        val startingDP = if (selectedStartingDateInMillis == 0L) {
            datePickerBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
        } else {
            datePickerBuilder.setSelection(selectedStartingDateInMillis).build()
        }

        startingDP.show(parentFragmentManager, FRAGMENT_TAG_ADD_CALENDAR_STARTING)

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
            setTitleText(requireContext().getString(R.string.add_warranty_title_date_picker_expiry))
            setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        }

        val expiryDP = if (selectedExpiryDateInMillis == 0L) {
            datePickerBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
        } else {
            datePickerBuilder.setSelection(selectedExpiryDateInMillis).build()
        }

        expiryDP.show(parentFragmentManager, FRAGMENT_TAG_ADD_CALENDAR_EXPIRY)

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
            timeInMillis = selectedStartingDateInMillis

            val day = decimalFormat.format(get(Calendar.DAY_OF_MONTH))
            val month = decimalFormat.format((get(Calendar.MONTH)) + 1)
            val year = get(Calendar.YEAR)

            startingDateInput = "$year-$month-$day"

            val startingDateText = requireContext().getString(
                R.string.add_warranty_format_date,
                month, day, year
            )

            binding.tiEditDateStarting.setText(startingDateText)
            binding.tiEditDateStarting.clearFocus()
        }
    }

    private fun setExpiryDate() {
        Calendar.getInstance().apply {
            timeInMillis = selectedExpiryDateInMillis

            val day = decimalFormat.format(get(Calendar.DAY_OF_MONTH))
            val month = decimalFormat.format((get(Calendar.MONTH)) + 1)
            val year = get(Calendar.YEAR)

            expiryDateInput = "$year-$month-$day"

            val expiryDateText = requireContext().getString(
                R.string.add_warranty_format_date,
                month, day, year
            )

            binding.tiEditDateExpiry.setText(expiryDateText)
            binding.tiEditDateExpiry.clearFocus()
        }
    }

    private fun loadCategoryIcon(categoryIcon: String) = loadCategoryImage(
        requireContext(), categoryIcon, imageLoader, binding.ivIconCategory, binding.cpiIconCategory
    )

    private fun returnToMainActivity() = binding.toolbar.setNavigationOnClickListener {
        findNavController().popBackStack()
    }

    private fun addWarrantyOnClick() = binding.toolbar.menu[0].setOnMenuItemClickListener {
        getWarrantyInput()
        false
    }

    private fun getWarrantyInput() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            requireView().applicationWindowToken,
            0
        )

        val title = binding.tiEditTitle.text.toString().trim()
        val brand = binding.tiEditBrand.text?.toString()?.trim()
        val model = binding.tiEditModel.text?.toString()?.trim()
        val serialNumber = binding.tiEditSerial.text?.toString()?.trim()
        val isLifetime = binding.cbLifetime.isChecked
        val description = binding.tiEditDescription.text?.toString()?.trim()
        val categoryId = selectedCategory?.id ?: "10"

        viewModel.checkAddWarrantyInputs(
            title, brand, model, serialNumber, isLifetime, startingDateInput, expiryDateInput,
            description, categoryId, selectedStartingDateInMillis, selectedExpiryDateInMillis
        )
    }

    private fun addWarrantyObserver() =
        viewModel.addWarrantyLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        findNavController().navigate(
                            AddWarrantyFragmentDirections.actionAddWarrantyFragmentToWarrantiesFragment()
                        )
                        (requireActivity() as MainActivity).showRateAppDialog()
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            val message = it.asString(requireContext())
                            when {
                                message.contains(ERROR_INPUT_BLANK_TITLE) -> {
                                    binding.tiLayoutTitle.error =
                                        requireContext().getString(R.string.add_warranty_error_blank_title)
                                    binding.tiLayoutTitle.requestFocus()
                                    binding.tiLayoutTitle.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                message.contains(ERROR_INPUT_BLANK_STARTING_DATE) -> {
                                    binding.tiLayoutDateStarting.requestFocus()
                                    binding.tiLayoutDateStarting.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                message.contains(ERROR_INPUT_BLANK_EXPIRY_DATE) -> {
                                    binding.tiLayoutDateExpiry.requestFocus()
                                    binding.tiLayoutDateExpiry.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                message.contains(ERROR_INPUT_INVALID_STARTING_DATE) -> showDateError()
                                message.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { getWarrantyInput() }
                                }
                                message.contains(ERROR_FIREBASE_403) -> {
                                    snackbar = show403Error(requireContext(), requireView())
                                }
                                message.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(),
                                        requireView()
                                    )
                                }
                                else -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(),
                                        requireView()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun showLoadingAnimation() = binding.apply {
        toolbar.menu[0].isVisible = false
        tiEditTitle.isEnabled = false
        tiDdCategory.isEnabled = false
        tiEditBrand.isEnabled = false
        tiEditModel.isEnabled = false
        tiEditSerial.isEnabled = false
        tiEditDateStarting.isEnabled = false
        tiEditDateExpiry.isEnabled = false
        tiEditDescription.isEnabled = false
        cpiAdd.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiAdd.visibility = GONE
        toolbar.menu[0].isVisible = true
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

    private fun setExpiryDateActivation(isLifeTime: Boolean) {
        binding.tiLayoutDateExpiry.isEnabled = !isLifeTime
    }
}