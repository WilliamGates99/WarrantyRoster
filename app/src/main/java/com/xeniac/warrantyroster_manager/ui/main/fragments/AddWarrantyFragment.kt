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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentAddWarrantyBinding
import com.xeniac.warrantyroster_manager.di.CategoryTitleMapKey
import com.xeniac.warrantyroster_manager.models.Category
import com.xeniac.warrantyroster_manager.models.Resource
import com.xeniac.warrantyroster_manager.models.WarrantyInput
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.MainViewModel
import com.xeniac.warrantyroster_manager.utils.CoilHelper.loadCategoryImage
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_403
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
import com.xeniac.warrantyroster_manager.utils.DateHelper.isStartingDateValid
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddWarrantyFragment : Fragment(R.layout.fragment_add_warranty) {

    private var _binding: FragmentAddWarrantyBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    @CategoryTitleMapKey
    lateinit var categoryTitleMapKey: String

    @Inject
    lateinit var decimalFormat: DecimalFormat

    private var selectedCategory: Category? = null

    private var selectedStartingDateInMillis = 0L
    private var startingDateInput: String? = null

    private var selectedExpiryDateInMillis = 0L
    private var expiryDateInput: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddWarrantyBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

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
                    binding.tiDdCategory.setText(category.title[categoryTitleMapKey])
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

            startingDateInput = "${get(Calendar.YEAR)}-" +
                    "${decimalFormat.format((get(Calendar.MONTH)) + 1)}-" +
                    decimalFormat.format(get(Calendar.DAY_OF_MONTH))

            val startingDateText =
                "${decimalFormat.format((get(Calendar.MONTH)) + 1)}/" +
                        "${decimalFormat.format(get(Calendar.DAY_OF_MONTH))}/" +
                        "${get(Calendar.YEAR)}"

            binding.tiEditDateStarting.setText(startingDateText)
            binding.tiEditDateStarting.clearFocus()
        }
    }

    private fun setExpiryDate() {
        Calendar.getInstance().apply {
            timeInMillis = selectedExpiryDateInMillis

            expiryDateInput = "${get(Calendar.YEAR)}-" +
                    "${decimalFormat.format((get(Calendar.MONTH)) + 1)}-" +
                    decimalFormat.format(get(Calendar.DAY_OF_MONTH))

            val expiryDateText =
                "${decimalFormat.format((get(Calendar.MONTH)) + 1)}/" +
                        "${decimalFormat.format(get(Calendar.DAY_OF_MONTH))}/" +
                        "${get(Calendar.YEAR)}"

            binding.tiEditDateExpiry.setText(expiryDateText)
            binding.tiEditDateExpiry.clearFocus()
        }
    }

    private fun loadCategoryIcon(categoryIcon: String) = loadCategoryImage(
        requireContext(), categoryIcon, imageLoader, binding.ivIconCategory, binding.cpiIconCategory
    )

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
        val isLifetime = binding.cbLifetime.isChecked

        if (title.isBlank()) {
            binding.tiLayoutTitle.requestFocus()
            binding.tiLayoutTitle.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (startingDateInput.isNullOrBlank()) {
            binding.tiLayoutDateStarting.requestFocus()
            binding.tiLayoutDateStarting.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (!isLifetime && expiryDateInput.isNullOrBlank()) {
            binding.tiLayoutDateExpiry.requestFocus()
            binding.tiLayoutDateExpiry.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (!isLifetime &&
            !isStartingDateValid(selectedStartingDateInMillis, selectedExpiryDateInMillis)
        ) {
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
                isLifetime,
                startingDateInput!!,
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
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        findNavController().navigate(R.id.action_addWarrantyFragment_to_warrantiesFragment)
                    }
                    is Resource.Error -> {
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
                                it.contains(ERROR_NETWORK_403) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_403),
                                        LENGTH_LONG
                                    ).show()
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.firebase_error_device_blocked),
                                        LENGTH_LONG
                                    ).show()
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

    private fun setExpiryDateActivation(isLifeTime: Boolean) {
        binding.tiLayoutDateExpiry.isEnabled = !isLifeTime
    }
}