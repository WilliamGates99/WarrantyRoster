package com.xeniac.warrantyrostermanager.mainactivity.addwarrantyfragment

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
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
import com.xeniac.warrantyrostermanager.databinding.FragmentAddWarrantyBinding
import com.xeniac.warrantyrostermanager.mainactivity.MainActivity
import com.xeniac.warrantyrostermanager.model.Category
import com.xeniac.warrantyrostermanager.model.WarrantyInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.*

class AddWarrantyFragment : Fragment(R.layout.fragment_add_warranty) {

    private var _binding: FragmentAddWarrantyBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var database: WarrantyRosterDatabase
    private val warrantiesCollectionRef =
        Firebase.firestore.collection(Constants.COLLECTION_WARRANTIES)

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
        database = WarrantyRosterDatabase.getInstance(requireContext())
        (requireContext() as MainActivity).hideNavBar()

        textInputsBackgroundColor()
        textInputsStrokeColor()
        categoryDropDownSelection()
        startingDatePicker()
        expiryDatePicker()
        returnToMainActivity()
        addWarrantyOnClick()
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
        binding.tiAddWarrantyEditTitle.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiAddWarrantyLayoutTitle.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiAddWarrantyLayoutTitle.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiAddWarrantyDdCategory.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiAddWarrantyLayoutCategory.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiAddWarrantyLayoutCategory.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiAddWarrantyEditBrand.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiAddWarrantyLayoutBrand.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiAddWarrantyLayoutBrand.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiAddWarrantyEditModel.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiAddWarrantyLayoutModel.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiAddWarrantyLayoutModel.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiAddWarrantyEditSerial.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiAddWarrantyLayoutSerial.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiAddWarrantyLayoutSerial.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiAddWarrantyEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiAddWarrantyLayoutDateStarting.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiAddWarrantyLayoutDateStarting.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiAddWarrantyEditDateExpiry.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiAddWarrantyLayoutDateExpiry.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiAddWarrantyLayoutDateExpiry.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiAddWarrantyEditDescription.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiAddWarrantyLayoutDescription.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiAddWarrantyLayoutDescription.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiAddWarrantyEditDateStarting.addTextChangedListener(object : TextWatcher {
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

        binding.tiAddWarrantyEditDateExpiry.addTextChangedListener(object : TextWatcher {
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

        binding.tiAddWarrantyDdCategory.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_category, titlesList)
        )
    }

    private fun categoryDropDownSelection() {
        binding.tiAddWarrantyDdCategory.setOnItemClickListener { _, _, index, _ ->
            selectedCategory = database.categoryDAO().getCategoryById((index + 1).toString())

            selectedCategory?.let {
                binding.ivAddWarrantyIconCategory.setImageResource(it.icon)
            }
        }
    }

    private fun startingDatePicker() {
        binding.tiAddWarrantyEditDateStarting.inputType = InputType.TYPE_NULL
        binding.tiAddWarrantyEditDateStarting.keyListener = null

        binding.tiAddWarrantyEditDateStarting.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                openStartingDatePicker()
            }
        }
    }

    private fun expiryDatePicker() {
        binding.tiAddWarrantyEditDateExpiry.inputType = InputType.TYPE_NULL
        binding.tiAddWarrantyEditDateExpiry.keyListener = null

        binding.tiAddWarrantyEditDateExpiry.setOnFocusChangeListener { _, isFocused ->
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

        startingDP.show(parentFragmentManager, Constants.FRAGMENT_TAG_ADD_CALENDAR_STARTING)

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

                binding.tiAddWarrantyEditDateStarting.setText(startingDateInput)
                binding.tiAddWarrantyEditDateStarting.clearFocus()
            }
        }

        startingDP.addOnDismissListener {
            binding.tiAddWarrantyEditDateStarting.clearFocus()
        }
    }

    private fun openExpiryDatePicker() {
        val expiryDP = MaterialDatePicker.Builder.datePicker()
            .setTitleText(requireContext().getString(R.string.add_warranty_title_date_picker_expiry))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        expiryDP.show(parentFragmentManager, Constants.FRAGMENT_TAG_ADD_CALENDAR_EXPIRY)

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

                binding.tiAddWarrantyEditDateExpiry.setText(expiryDateInput)
                binding.tiAddWarrantyEditDateExpiry.clearFocus()
            }
        }

        expiryDP.addOnDismissListener {
            binding.tiAddWarrantyEditDateExpiry.clearFocus()
        }
    }

    private fun returnToMainActivity() {
        binding.toolbarAddWarranty.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun addWarrantyOnClick() {
        binding.toolbarAddWarranty.menu[0]
            .setOnMenuItemClickListener {
                addWarranty()
                false
            }
    }

    private fun addWarranty() {
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
                setAction(requireContext().getString(R.string.network_error_retry)) { addWarranty() }
                show()
            }
        }
    }

    private fun getWarrantyInput() {
        val title = binding.tiAddWarrantyEditTitle.text.toString().trim()
        val brand = binding.tiAddWarrantyEditBrand.text?.toString()?.trim()
        val model = binding.tiAddWarrantyEditModel.text?.toString()?.trim()
        val serialNumber = binding.tiAddWarrantyEditSerial.text?.toString()?.trim()
        val description = binding.tiAddWarrantyEditDescription.text?.toString()?.trim()
        val categoryId = selectedCategory?.id ?: "10"

        if (title.isBlank()) {
            binding.tiAddWarrantyLayoutTitle.requestFocus()
            binding.tiAddWarrantyLayoutTitle.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (startingCalendar == null) {
            binding.tiAddWarrantyLayoutDateStarting.requestFocus()
            binding.tiAddWarrantyLayoutDateStarting.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (expiryCalendar == null) {
            binding.tiAddWarrantyLayoutDateExpiry.requestFocus()
            binding.tiAddWarrantyLayoutDateExpiry.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (!isStartingDateValid(startingCalendar!!, expiryCalendar!!)) {
            showDateError()
        } else {
            val warrantyInput = WarrantyInput(
                title, brand, model, serialNumber, startingDateInput,
                expiryDateInput, description, categoryId, Firebase.auth.currentUser?.uid.toString()
            )
            addWarrantyToFirestore(warrantyInput)
        }
    }

    private fun addWarrantyToFirestore(warrantyInput: WarrantyInput) {
        showLoadingAnimation()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                warrantiesCollectionRef.add(warrantyInput).await()
                Log.i("addWarranty", "Warranty successfully added.")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    navController.navigate(R.id.action_addWarrantyFragment_to_warrantiesFragment)
                }
            } catch (e: Exception) {
                Log.e("addWarranty", "Exception: ${e.message}")
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
        binding.toolbarAddWarranty.menu[0].isVisible = false
        binding.tiAddWarrantyEditTitle.isEnabled = false
        binding.tiAddWarrantyDdCategory.isEnabled = false
        binding.tiAddWarrantyEditBrand.isEnabled = false
        binding.tiAddWarrantyEditModel.isEnabled = false
        binding.tiAddWarrantyEditSerial.isEnabled = false
        binding.tiAddWarrantyEditDateStarting.isEnabled = false
        binding.tiAddWarrantyEditDateExpiry.isEnabled = false
        binding.tiAddWarrantyEditDescription.isEnabled = false
        binding.cpiAddWarranty.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.toolbarAddWarranty.menu[0].isVisible = true
        binding.tiAddWarrantyEditTitle.isEnabled = true
        binding.tiAddWarrantyDdCategory.isEnabled = true
        binding.tiAddWarrantyEditBrand.isEnabled = true
        binding.tiAddWarrantyEditModel.isEnabled = true
        binding.tiAddWarrantyEditSerial.isEnabled = true
        binding.tiAddWarrantyEditDateStarting.isEnabled = true
        binding.tiAddWarrantyEditDateExpiry.isEnabled = true
        binding.tiAddWarrantyEditDescription.isEnabled = true
        binding.cpiAddWarranty.visibility = GONE
    }

    private fun showDateError() {
        binding.tiAddWarrantyLayoutDateStarting.requestFocus()
        binding.tiAddWarrantyLayoutDateStarting.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.red)
        binding.tvAddWarrantyDateError.visibility = VISIBLE
    }

    private fun hideDateError() {
        binding.tiAddWarrantyLayoutDateStarting.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.blue)
        binding.tvAddWarrantyDateError.visibility = GONE
    }

    private fun isStartingDateValid(startingCalendar: Calendar, expiryCalendar: Calendar): Boolean =
        expiryCalendar >= startingCalendar
}