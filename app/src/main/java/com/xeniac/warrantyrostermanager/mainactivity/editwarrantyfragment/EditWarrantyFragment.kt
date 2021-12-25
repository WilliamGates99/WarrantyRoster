package com.xeniac.warrantyrostermanager.mainactivity.editwarrantyfragment

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
import com.xeniac.warrantyrostermanager.Constants
import com.xeniac.warrantyrostermanager.NetworkHelper
import com.xeniac.warrantyrostermanager.R
import com.xeniac.warrantyrostermanager.database.WarrantyRosterDatabase
import com.xeniac.warrantyrostermanager.databinding.FragmentEditWarrantyBinding
import com.xeniac.warrantyrostermanager.mainactivity.MainActivity
import com.xeniac.warrantyrostermanager.model.Category
import com.xeniac.warrantyrostermanager.model.ListItemType
import com.xeniac.warrantyrostermanager.model.Warranty
import com.xeniac.warrantyrostermanager.model.WarrantyInput
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
        binding.tiEditWarrantyEditTitle.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiEditWarrantyLayoutTitle.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiEditWarrantyLayoutTitle.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditWarrantyDdCategory.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiEditWarrantyLayoutCategory.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiEditWarrantyLayoutCategory.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditWarrantyEditBrand.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiEditWarrantyLayoutBrand.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiEditWarrantyLayoutBrand.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditWarrantyEditModel.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiEditWarrantyLayoutModel.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiEditWarrantyLayoutModel.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditWarrantyEditSerial.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiEditWarrantyLayoutSerial.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiEditWarrantyLayoutSerial.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditWarrantyEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiEditWarrantyLayoutDateStarting.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiEditWarrantyLayoutDateStarting.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditWarrantyEditDateExpiry.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiEditWarrantyLayoutDateExpiry.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiEditWarrantyLayoutDateExpiry.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditWarrantyEditDescription.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiEditWarrantyLayoutDescription.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiEditWarrantyLayoutDescription.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiEditWarrantyEditDateStarting.addTextChangedListener(object : TextWatcher {
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

        binding.tiEditWarrantyEditDateExpiry.addTextChangedListener(object : TextWatcher {
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

        binding.tiEditWarrantyDdCategory.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_category, titlesList)
        )
    }

    private fun categoryDropDownSelection() {
        binding.tiEditWarrantyDdCategory.setOnItemClickListener { _, _, index, _ ->
            selectedCategory = database.categoryDAO().getCategoryById((index + 1).toString())
            selectedCategory?.let {
                binding.ivEditWarrantyIconCategory.setImageResource(it.icon)
            }
        }
    }

    private fun startingDatePicker() {
        binding.tiEditWarrantyEditDateStarting.inputType = InputType.TYPE_NULL
        binding.tiEditWarrantyEditDateStarting.keyListener = null

        binding.tiEditWarrantyEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                openStartingDatePicker()
            }
        }
    }

    private fun expiryDatePicker() {
        binding.tiEditWarrantyEditDateExpiry.inputType = InputType.TYPE_NULL
        binding.tiEditWarrantyEditDateExpiry.keyListener = null

        binding.tiEditWarrantyEditDateExpiry.setOnFocusChangeListener { _, isFocused ->
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

                binding.tiEditWarrantyEditDateStarting.setText(startingDateInput)
                binding.tiEditWarrantyEditDateStarting.clearFocus()
            }
        }

        startingDP.addOnDismissListener {
            binding.tiEditWarrantyEditDateStarting.clearFocus()
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

                binding.tiEditWarrantyEditDateExpiry.setText(expiryDateInput)
                binding.tiEditWarrantyEditDateExpiry.clearFocus()
            }
        }

        expiryDP.addOnDismissListener {
            binding.tiEditWarrantyEditDateExpiry.clearFocus()
        }
    }

    private fun returnToWarrantyDetailsFragment() {
        binding.toolbarEditWarranty.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun getWarranty() {
        val args: EditWarrantyFragmentArgs by navArgs()
        warranty = args.warranty
        setWarrantyDetails()
    }

    private fun setWarrantyDetails() {
        selectedCategory = database.categoryDAO().getCategoryById(warranty.categoryId)
        binding.tiEditWarrantyEditTitle.setText(warranty.title)
        binding.tiEditWarrantyEditBrand.setText(warranty.brand)
        binding.tiEditWarrantyEditModel.setText(warranty.model)
        binding.tiEditWarrantyEditSerial.setText(warranty.serialNumber)
        binding.tiEditWarrantyEditDescription.setText(warranty.description)
        selectedCategory?.let {
            binding.tiEditWarrantyDdCategory.setText(requireContext().getString(it.title))
            binding.ivEditWarrantyIconCategory.setImageResource(it.icon)
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

        binding.tiEditWarrantyEditDateStarting.setText(
            "${decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1)}/" +
                    "${decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                    "${startingCalendar.get(Calendar.YEAR)}"
        )

        binding.tiEditWarrantyEditDateExpiry.setText(
            "${decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1)}/" +
                    "${decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH))}/" +
                    "${expiryCalendar.get(Calendar.YEAR)}"
        )
    }

    private fun editWarrantyOnClick() {
        binding.toolbarEditWarranty.menu.getItem(0).setOnMenuItemClickListener {
            editWarranty()
            false
        }
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
        val title = binding.tiEditWarrantyEditTitle.text.toString().trim()

        if (title.isBlank()) {
            binding.tiEditWarrantyLayoutTitle.requestFocus()
            binding.tiEditWarrantyLayoutTitle.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (startingCalendarInput == null) {
            binding.tiEditWarrantyLayoutDateStarting.requestFocus()
            binding.tiEditWarrantyLayoutDateStarting.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (expiryCalendarInput == null) {
            binding.tiEditWarrantyLayoutDateExpiry.requestFocus()
            binding.tiEditWarrantyLayoutDateExpiry.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (!isStartingDateValid(startingCalendarInput!!, expiryCalendarInput!!)) {
            showDateError()
        } else {
            val brand = binding.tiEditWarrantyEditBrand.text?.toString()?.trim()
            val model = binding.tiEditWarrantyEditModel.text?.toString()?.trim()
            val serialNumber = binding.tiEditWarrantyEditSerial.text?.toString()?.trim()
            val description = binding.tiEditWarrantyEditDescription.text?.toString()?.trim()
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
        binding.toolbarEditWarranty.menu.getItem(0).isVisible = false
        binding.tiEditWarrantyEditTitle.isEnabled = false
        binding.tiEditWarrantyDdCategory.isEnabled = false
        binding.tiEditWarrantyEditBrand.isEnabled = false
        binding.tiEditWarrantyEditModel.isEnabled = false
        binding.tiEditWarrantyEditSerial.isEnabled = false
        binding.tiEditWarrantyEditDateStarting.isEnabled = false
        binding.tiEditWarrantyEditDateExpiry.isEnabled = false
        binding.tiEditWarrantyEditDescription.isEnabled = false
        binding.cpiEditWarranty.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.toolbarEditWarranty.menu.getItem(0).isVisible = true
        binding.tiEditWarrantyEditTitle.isEnabled = true
        binding.tiEditWarrantyDdCategory.isEnabled = true
        binding.tiEditWarrantyEditBrand.isEnabled = true
        binding.tiEditWarrantyEditModel.isEnabled = true
        binding.tiEditWarrantyEditSerial.isEnabled = true
        binding.tiEditWarrantyEditDateStarting.isEnabled = true
        binding.tiEditWarrantyEditDateExpiry.isEnabled = true
        binding.tiEditWarrantyEditDescription.isEnabled = true
        binding.cpiEditWarranty.visibility = GONE
    }

    private fun showDateError() {
        binding.tiEditWarrantyLayoutDateStarting.requestFocus()
        binding.tiEditWarrantyLayoutDateStarting.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.red)
        binding.tvEditWarrantyDateError.visibility = VISIBLE
    }

    private fun hideDateError() {
        binding.tiEditWarrantyLayoutDateStarting.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.blue)
        binding.tvEditWarrantyDateError.visibility = GONE
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