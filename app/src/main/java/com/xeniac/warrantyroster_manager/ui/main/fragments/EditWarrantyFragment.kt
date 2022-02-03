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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
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
import com.xeniac.warrantyroster_manager.databinding.FragmentEditWarrantyBinding
import com.xeniac.warrantyroster_manager.models.*
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.MainViewModel
import com.xeniac.warrantyroster_manager.utils.CategoryHelper.getCategoryTitleMapKey
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.FRAGMENT_TAG_EDIT_CALENDAR_EXPIRY
import com.xeniac.warrantyroster_manager.utils.Constants.FRAGMENT_TAG_EDIT_CALENDAR_STARTING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EditWarrantyFragment : Fragment(R.layout.fragment_edit_warranty) {

    private var _binding: FragmentEditWarrantyBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel

    private lateinit var imageLoader: ImageLoader
    private lateinit var warranty: Warranty

    private val decimalFormat = DecimalFormat("00")
    private val dateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
    private var selectedCategory: Category? = null
    private var startingCalendarInput: Calendar? = null
    private var expiryCalendarInput: Calendar? = null
    private lateinit var startingDateInput: String
    private lateinit var expiryDateInput: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditWarrantyBinding.bind(view)
        navController = Navigation.findNavController(view)
        (requireContext() as MainActivity).hideNavBar()
        viewModel = (activity as MainActivity).viewModel

        imageLoader = ImageLoader.Builder(requireContext())
            .componentRegistry { add(SvgDecoder(requireContext())) }.build()

        textInputsBackgroundColor()
        textInputsStrokeColor()
        categoryDropDownSelection()
        startingDatePicker()
        expiryDatePicker()
        returnToWarrantyDetailsFragment()
        getWarranty()
        editWarrantyOnClick()
        updateWarrantyObserver()
        getUpdatedWarrantyObserver()
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

    private fun categoryDropDown() = CoroutineScope(Dispatchers.IO).launch {
        val titlesList = viewModel.getAllCategoryTitles()
        withContext(Dispatchers.Main) {
            binding.tiDdCategory.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_category, titlesList)
            )
        }
    }

    private fun categoryDropDownSelection() =
        binding.tiDdCategory.setOnItemClickListener { _, _, index, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                selectedCategory = viewModel.getCategoryById((index + 1).toString())

                withContext(Dispatchers.Main) {
                    selectedCategory?.let {
                        binding.ivIconCategory.load(it.icon, imageLoader) {
                            memoryCachePolicy(CachePolicy.ENABLED)
                            diskCachePolicy(CachePolicy.ENABLED)
                            networkCachePolicy(CachePolicy.ENABLED)
                        }
                    }
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
            .setTitleText(requireContext().getString(R.string.edit_warranty_title_date_picker_starting))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        startingDP.show(parentFragmentManager, FRAGMENT_TAG_EDIT_CALENDAR_STARTING)

        startingDP.addOnPositiveButtonClickListener { selection ->
            startingCalendarInput = Calendar.getInstance()
            startingCalendarInput?.let {
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
            .setTitleText(requireContext().getString(R.string.edit_warranty_title_date_picker_expiry))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        expiryDP.show(parentFragmentManager, FRAGMENT_TAG_EDIT_CALENDAR_EXPIRY)

        expiryDP.addOnPositiveButtonClickListener { selection ->
            expiryCalendarInput = Calendar.getInstance()
            expiryCalendarInput?.let {
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

    private fun returnToWarrantyDetailsFragment() =
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

    private fun getWarranty() {
        val args: EditWarrantyFragmentArgs by navArgs()
        warranty = args.warranty
        setWarrantyDetails()
    }

    private fun setWarrantyDetails() = CoroutineScope(Dispatchers.IO).launch {
        selectedCategory = warranty.categoryId?.let {
            viewModel.getCategoryById(it)
        }

        withContext(Dispatchers.Main) {
            binding.tiEditTitle.setText(warranty.title)
            binding.tiEditBrand.setText(warranty.brand)
            binding.tiEditModel.setText(warranty.model)
            binding.tiEditSerial.setText(warranty.serialNumber)
            binding.tiEditDescription.setText(warranty.description)

            val startingCalendar = Calendar.getInstance()
            val expiryCalendar = Calendar.getInstance()

            dateFormat.parse(warranty.startingDate!!)?.let {
                startingCalendar.time = it
            }

            dateFormat.parse(warranty.expiryDate!!)?.let {
                expiryCalendar.time = it
            }

            startingDateInput = "${startingCalendar.get(Calendar.YEAR)}-" +
                    "${decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1)}-" +
                    decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH))

            expiryDateInput = "${expiryCalendar.get(Calendar.YEAR)}-" +
                    "${decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1)}-" +
                    decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH))

            val startingDate =
                "${decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1)}/" +
                        "${decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                        "${startingCalendar.get(Calendar.YEAR)}"
            binding.tiEditDateStarting.setText(startingDate)

            val expiryDate =
                "${decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1)}/" +
                        "${decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                        "${expiryCalendar.get(Calendar.YEAR)}"
            binding.tiEditDateExpiry.setText(expiryDate)

            selectedCategory?.let {
                binding.tiDdCategory.setText(it.title[getCategoryTitleMapKey(requireContext())])
                binding.ivIconCategory.load(it.icon, imageLoader) {
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                    networkCachePolicy(CachePolicy.ENABLED)
                }
            }
        }
    }

    private fun editWarrantyOnClick() =
        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener {
            getWarrantyInput()
            false
        }

    private fun getWarrantyInput() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        val title = binding.tiEditTitle.text.toString().trim()

        if (title.isBlank()) {
            binding.tiLayoutTitle.requestFocus()
            binding.tiLayoutTitle.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (startingCalendarInput == null) {
            binding.tiLayoutDateStarting.requestFocus()
            binding.tiLayoutDateStarting.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (expiryCalendarInput == null) {
            binding.tiLayoutDateExpiry.requestFocus()
            binding.tiLayoutDateExpiry.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (!isStartingDateValid(startingCalendarInput!!, expiryCalendarInput!!)) {
            showDateError()
        } else {
            val brand = binding.tiEditBrand.text?.toString()?.trim()
            val model = binding.tiEditModel.text?.toString()?.trim()
            val serialNumber = binding.tiEditSerial.text?.toString()?.trim()
            val description = binding.tiEditDescription.text?.toString()?.trim()
            val categoryId = selectedCategory?.id ?: "10"

            val warrantyInput = WarrantyInput(
                title, brand, model, serialNumber, startingDateInput,
                expiryDateInput, description, categoryId, Firebase.auth.currentUser?.uid.toString()
            )
            updateWarrantyInFirestore(warrantyInput)
        }
    }

    private fun updateWarrantyInFirestore(warrantyInput: WarrantyInput) =
        viewModel.updateWarrantyInFirestore(warranty.id, warrantyInput)

    private fun updateWarrantyObserver() =
        viewModel.updateWarrantyLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        getUpdatedWarrantyFromFirestore()
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
                            val daysUntilExpiry = getDaysUntilExpiry(updatedWarranty.expiryDate!!)

                            val action = EditWarrantyFragmentDirections
                                .actionEditWarrantyFragmentToWarrantyDetailsFragment(
                                    updatedWarranty, daysUntilExpiry
                                )
                            navController.navigate(action)
                        }
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
                                        setAction(requireContext().getString(R.string.network_error_retry)) {
                                            getUpdatedWarrantyFromFirestore()
                                        }
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

    private fun isStartingDateValid(startingCalendar: Calendar, expiryCalendar: Calendar): Boolean =
        expiryCalendar >= startingCalendar

    private fun getDaysUntilExpiry(expiryDate: String): Long {
        val expiryCalendar = Calendar.getInstance()

        dateFormat.parse(expiryDate)?.let {
            expiryCalendar.time = it
        }

        val todayCalendar = Calendar.getInstance()
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
        todayCalendar.set(Calendar.MINUTE, 0)
        todayCalendar.set(Calendar.SECOND, 0)
        todayCalendar.set(Calendar.MILLISECOND, 0)

        return TimeUnit.MILLISECONDS.toDays(expiryCalendar.timeInMillis - todayCalendar.timeInMillis)
    }
}