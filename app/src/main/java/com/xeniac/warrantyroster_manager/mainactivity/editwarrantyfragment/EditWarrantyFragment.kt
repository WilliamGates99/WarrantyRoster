package com.xeniac.warrantyroster_manager.mainactivity.editwarrantyfragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xeniac.warrantyroster_manager.Constants
import com.xeniac.warrantyroster_manager.NetworkHelper
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.database.WarrantyRosterDatabase
import com.xeniac.warrantyroster_manager.databinding.FragmentEditWarrantyBinding
import com.xeniac.warrantyroster_manager.mainactivity.MainActivity
import com.xeniac.warrantyroster_manager.model.Category
import com.xeniac.warrantyroster_manager.model.ListItemType
import com.xeniac.warrantyroster_manager.model.Warranty
import com.xeniac.warrantyroster_manager.model.WarrantyInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EditWarrantyFragment : Fragment(R.layout.fragment_edit_warranty) {

    private var _binding: FragmentEditWarrantyBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var database: WarrantyRosterDatabase
    private lateinit var warranty: Warranty
    private val warrantiesCollectionRef =
        Firebase.firestore.collection(Constants.COLLECTION_WARRANTIES)

    private val decimalFormat = DecimalFormat("00")
    private val dateFormat = SimpleDateFormat("yyyy-M-dd")
    private var selectedCategory: Category? = null
    private var startingCalendarInput: Calendar? = null
    private var expiryCalendarInput: Calendar? = null
    private lateinit var startingDateInput: String
    private lateinit var expiryDateInput: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditWarrantyBinding.bind(view)
        navController = Navigation.findNavController(view)
        database = WarrantyRosterDatabase.getInstance(context)
        (requireContext() as MainActivity).hideNavBar()

        textInputsBackgroundColor()
        textInputsStrokeColor()
        categoryDropDownSelection()
        startingDatePicker()
        expiryDatePicker()
        returnToWarrantyDetailsFragment()
        getWarranty()
        editWarrantyOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        WarrantyRosterDatabase.destroyInstance()
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
        binding.tiEditDateStarting.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                hideDateError()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiEditDateExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                hideDateError()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun categoryDropDown() {
        val titlesList = mutableListOf<String>()
        for (categoryTitleId in database.categoryDAO().allCategoryTitles) {
            titlesList.add(requireContext().getString(categoryTitleId))
        }

        binding.tiDdCategory.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_category, titlesList)
        )
    }

    private fun categoryDropDownSelection() =
        binding.tiDdCategory.setOnItemClickListener { _, _, index, _ ->
            selectedCategory = database.categoryDAO().getCategoryById((index + 1).toString())
            selectedCategory?.let {
                binding.ivIconCategory.setImageResource(it.icon)
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

        startingDP.show(parentFragmentManager, Constants.FRAGMENT_TAG_EDIT_CALENDAR_STARTING)

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

        expiryDP.show(parentFragmentManager, Constants.FRAGMENT_TAG_EDIT_CALENDAR_EXPIRY)

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

    private fun setWarrantyDetails() {
        selectedCategory = database.categoryDAO().getCategoryById(warranty.categoryId)
        binding.tiEditTitle.setText(warranty.title)
        binding.tiEditBrand.setText(warranty.brand)
        binding.tiEditModel.setText(warranty.model)
        binding.tiEditSerial.setText(warranty.serialNumber)
        binding.tiEditDescription.setText(warranty.description)
        selectedCategory?.let {
            binding.tiDdCategory.setText(requireContext().getString(it.title))
            binding.ivIconCategory.setImageResource(it.icon)
        }

        val startingCalendar = Calendar.getInstance()
        val expiryCalendar = Calendar.getInstance()

        dateFormat.parse(warranty.startingDate)?.let {
            startingCalendar.time = it
        }

        dateFormat.parse(warranty.expiryDate)?.let {
            expiryCalendar.time = it
        }

        startingDateInput = "${startingCalendar.get(Calendar.YEAR)}-" +
                "${decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1)}-" +
                decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH))

        expiryDateInput = "${expiryCalendar.get(Calendar.YEAR)}-" +
                "${decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1)}-" +
                decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH))

        binding.tiEditDateStarting.setText(
            "${decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1)}/" +
                    "${decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                    "${startingCalendar.get(Calendar.YEAR)}"
        )

        binding.tiEditDateExpiry.setText(
            "${decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1)}/" +
                    "${decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                    "${expiryCalendar.get(Calendar.YEAR)}"
        )
    }

    private fun editWarrantyOnClick() =
        binding.toolbar.menu.getItem(0).setOnMenuItemClickListener {
            editWarranty()
            false
        }

    private fun editWarranty() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        if (NetworkHelper.hasNetworkAccess(requireContext())) {
            getWarrantyInput()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { editWarranty() }
                show()
            }
        }
    }

    private fun getWarrantyInput() {
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

    private fun updateWarrantyInFirestore(warrantyInput: WarrantyInput) {
        showLoadingAnimation()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                warrantiesCollectionRef.document(warranty.id).set(warrantyInput).await()
                Log.i("editWarranty", "Warranty successfully updated.")
                val documentSnapshot = warrantiesCollectionRef.document(warranty.id).get().await()
                Log.i("editWarranty", "documentSnapshot: $documentSnapshot")
                val updatedWarranty = Warranty(
                    documentSnapshot.id,
                    documentSnapshot.get(Constants.WARRANTIES_TITLE).toString(),
                    documentSnapshot.get(Constants.WARRANTIES_BRAND).toString(),
                    documentSnapshot.get(Constants.WARRANTIES_MODEL).toString(),
                    documentSnapshot.get(Constants.WARRANTIES_SERIAL_NUMBER).toString(),
                    documentSnapshot.get(Constants.WARRANTIES_STARTING_DATE).toString(),
                    documentSnapshot.get(Constants.WARRANTIES_EXPIRY_DATE).toString(),
                    documentSnapshot.get(Constants.WARRANTIES_DESCRIPTION).toString(),
                    documentSnapshot.get(Constants.WARRANTIES_CATEGORY_ID).toString(),
                    ListItemType.WARRANTY
                )
                val daysUntilExpiry = getDaysUntilExpiry(updatedWarranty.expiryDate)

                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()

                    val action = EditWarrantyFragmentDirections
                        .actionEditWarrantyFragmentToWarrantyDetailsFragment(
                            updatedWarranty, daysUntilExpiry
                        )
                    navController.navigate(action)
                }
            } catch (e: Exception) {
                Log.e("editWarranty", "Exception: ${e.message}")
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