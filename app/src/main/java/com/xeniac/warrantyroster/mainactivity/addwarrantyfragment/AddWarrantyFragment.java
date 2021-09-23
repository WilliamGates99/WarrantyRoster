package com.xeniac.warrantyroster.mainactivity.addwarrantyfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.database.CategoryDataModel;
import com.xeniac.warrantyroster.database.WarrantyRosterDatabase;
import com.xeniac.warrantyroster.databinding.FragmentAddWarrantyBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddWarrantyFragment extends Fragment {

    private FragmentAddWarrantyBinding addWarrantyBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;
    private WarrantyRosterDatabase database;

    private CategoryDataModel selectedCategory;
    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    public AddWarrantyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addWarrantyBinding = FragmentAddWarrantyBinding.inflate(inflater, container, false);
        view = addWarrantyBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WarrantyRosterDatabase.destroyInstance();
        addWarrantyBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);
        database = WarrantyRosterDatabase.getInstance(context);

        ((MainActivity) context).hideNavBar();

        textInputsBackgroundColor();
        categoryDropDownSelection();
        startingDatePicker();
        expiryDatePicker();
        returnToMainActivity();
        addWarrantyOnClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryDropDown();
    }

    private void textInputsBackgroundColor() {
        addWarrantyBinding.tiAddWarrantyEditTitle.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                addWarrantyBinding.tiAddWarrantyLayoutTitle.setBoxBackgroundColorResource(R.color.background);
            } else {
                addWarrantyBinding.tiAddWarrantyLayoutTitle.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        addWarrantyBinding.tiAddWarrantyDdCategory.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                addWarrantyBinding.tiAddWarrantyLayoutCategory.setBoxBackgroundColorResource(R.color.background);
            } else {
                addWarrantyBinding.tiAddWarrantyLayoutCategory.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        addWarrantyBinding.tiAddWarrantyEditBrand.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                addWarrantyBinding.tiAddWarrantyLayoutBrand.setBoxBackgroundColorResource(R.color.background);
            } else {
                addWarrantyBinding.tiAddWarrantyLayoutBrand.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        addWarrantyBinding.tiAddWarrantyEditModel.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                addWarrantyBinding.tiAddWarrantyLayoutModel.setBoxBackgroundColorResource(R.color.background);
            } else {
                addWarrantyBinding.tiAddWarrantyLayoutModel.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        addWarrantyBinding.tiAddWarrantyEditSerial.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                addWarrantyBinding.tiAddWarrantyLayoutSerial.setBoxBackgroundColorResource(R.color.background);
            } else {
                addWarrantyBinding.tiAddWarrantyLayoutSerial.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        addWarrantyBinding.tiAddWarrantyEditDateStarting.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                addWarrantyBinding.tiAddWarrantyLayoutDateStarting.setBoxBackgroundColorResource(R.color.background);
            } else {
                addWarrantyBinding.tiAddWarrantyLayoutDateStarting.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        addWarrantyBinding.tiAddWarrantyEditDateExpiry.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                addWarrantyBinding.tiAddWarrantyLayoutDateExpiry.setBoxBackgroundColorResource(R.color.background);
            } else {
                addWarrantyBinding.tiAddWarrantyLayoutDateExpiry.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        addWarrantyBinding.tiAddWarrantyEditDescription.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                addWarrantyBinding.tiAddWarrantyLayoutDescription.setBoxBackgroundColorResource(R.color.background);
            } else {
                addWarrantyBinding.tiAddWarrantyLayoutDescription.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void categoryDropDown() {
        List<String> titlesList = new ArrayList<>();
        for (Integer categoryTitleId : database.categoryDAO().getAllCategoryTitles()) {
            titlesList.add(context.getResources().getString(categoryTitleId));
        }

        addWarrantyBinding.tiAddWarrantyDdCategory.setAdapter(new ArrayAdapter<>(context, R.layout.dropdown_category, titlesList));
    }

    private void categoryDropDownSelection() {
        addWarrantyBinding.tiAddWarrantyDdCategory.setOnItemClickListener((adapterView, view, index, l) -> {
            selectedCategory = database.categoryDAO().getCategoryById(String.valueOf(index + 1));
            if (selectedCategory != null) {
                addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                        getResources(), selectedCategory.getIcon(), context.getTheme()));
            }
        });
    }

    private void startingDatePicker() {
        addWarrantyBinding.tiAddWarrantyEditDateStarting.setInputType(InputType.TYPE_NULL);
        addWarrantyBinding.tiAddWarrantyEditDateStarting.setKeyListener(null);

        addWarrantyBinding.tiAddWarrantyEditDateStarting.setOnFocusChangeListener((view, isFocused) -> {
            if (isFocused) {
                openStartingDatePicker();
            }
        });
    }

    private void expiryDatePicker() {
        addWarrantyBinding.tiAddWarrantyEditDateExpiry.setInputType(InputType.TYPE_NULL);
        addWarrantyBinding.tiAddWarrantyEditDateExpiry.setKeyListener(null);

        addWarrantyBinding.tiAddWarrantyEditDateExpiry.setOnFocusChangeListener((view, isFocused) -> {
            if (isFocused) {
                openExpiryDatePicker();
            }
        });
    }

    private void openStartingDatePicker() {
        MaterialDatePicker<Long> startingDP = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getResources().getString(R.string.add_warranty_title_date_picker_starting))
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        startingDP.show(getParentFragmentManager(), Constants.FRAGMENT_TAG_ADD_CALENDAR_STARTING);

        startingDP.addOnPositiveButtonClickListener(selection -> {
            Calendar startingCalendar = Calendar.getInstance();
            startingCalendar.setTimeInMillis(selection);

            String startingDateInput = String.format("%s/%s/%s",
                    decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH)),
                    decimalFormat.format(startingCalendar.get(Calendar.MONTH)),
                    startingCalendar.get(Calendar.YEAR));

            addWarrantyBinding.tiAddWarrantyEditDateStarting.setText(startingDateInput);
            addWarrantyBinding.tiAddWarrantyEditDateStarting.clearFocus();
        });

        startingDP.addOnDismissListener(dialogInterface ->
                addWarrantyBinding.tiAddWarrantyEditDateStarting.clearFocus());
    }

    private void openExpiryDatePicker() {
        MaterialDatePicker<Long> expiryDP = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getResources().getString(R.string.add_warranty_title_date_picker_expiry))
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();
        expiryDP.show(getParentFragmentManager(), Constants.FRAGMENT_TAG_ADD_CALENDAR_EXPIRY);

        expiryDP.addOnPositiveButtonClickListener(selection -> {
            Calendar expiryCalendar = Calendar.getInstance();
            expiryCalendar.setTimeInMillis(selection);

            String expiryDateInput = String.format("%s/%s/%s",
                    decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH)),
                    decimalFormat.format(expiryCalendar.get(Calendar.MONTH)),
                    expiryCalendar.get(Calendar.YEAR));

            addWarrantyBinding.tiAddWarrantyEditDateExpiry.setText(expiryDateInput);
            addWarrantyBinding.tiAddWarrantyEditDateExpiry.clearFocus();
        });

        expiryDP.addOnDismissListener(dialogInterface ->
                addWarrantyBinding.tiAddWarrantyEditDateExpiry.clearFocus());
    }

    private void returnToMainActivity() {
        addWarrantyBinding.toolbarAddWarranty.setNavigationOnClickListener(view ->
                activity.onBackPressed());
    }

    private void addWarrantyOnClick() {
        addWarrantyBinding.toolbarAddWarranty.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            navController.navigate(R.id.action_addWarrantyFragment_to_warrantiesFragment);
            return false;
        });
    }
}