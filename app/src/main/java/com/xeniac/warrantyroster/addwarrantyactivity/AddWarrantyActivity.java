package com.xeniac.warrantyroster.addwarrantyactivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.LocaleModifier;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.ActivityAddWarrantyBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddWarrantyActivity extends AppCompatActivity {

    private ActivityAddWarrantyBinding addWarrantyBinding;
    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addWarrantyBinding = ActivityAddWarrantyBinding.inflate(getLayoutInflater());
        setContentView(addWarrantyBinding.getRoot());
        addWarrantyInit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void addWarrantyInit() {
        LocaleModifier localeModifier = new LocaleModifier(this);
        localeModifier.setLocale();

        textInputsBackgroundColor();
        categoryDropDown();
        categoryDropDownSelection();
        startingDatePicker();
        expiryDatePicker();
        returnToMainActivity();
        addWarrantyOnClick();
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
        List<String> categoryList = new ArrayList<>();
        categoryList.add("Accessory");
        categoryList.add("Air Conditioner");
        categoryList.add("Audio Device");
        categoryList.add("Camera");
        categoryList.add("Game Console");
        categoryList.add("Headset");
        categoryList.add("Healthcare");
        categoryList.add("Home Appliance");
        categoryList.add("Memory and Storage");
        categoryList.add("Miscellaneous");
        categoryList.add("Monitor");
        categoryList.add("Musical Instrument");
        categoryList.add("Peripheral Device");
        categoryList.add("Personal Care");
        categoryList.add("Personal Computer");
        categoryList.add("Phone and Tablet");
        categoryList.add("Smart Home");
        categoryList.add("Smart Watch");
        categoryList.add("Sport and Fitness");
        categoryList.add("Tool");
        categoryList.add("Vehicle");

        addWarrantyBinding.tiAddWarrantyDdCategory.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_category, categoryList));
    }

    private void categoryDropDownSelection() {
        addWarrantyBinding.tiAddWarrantyDdCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                switch (editable.toString()) {
                    case "AC":
                        break;
                    case "Accessory":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_accessory, getTheme()));
                        break;
                    case "Air Conditioner":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_air_conditioner, getTheme()));
                        break;
                    case "Audio Device":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_audio_device, getTheme()));
                        break;
                    case "Camera":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_camera, getTheme()));
                        break;
                    case "Game Console":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_game_console, getTheme()));
                        break;
                    case "Headset":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_headset, getTheme()));
                        break;
                    case "Healthcare":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_healthcare, getTheme()));
                        break;
                    case "Home Appliance":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_home_appliance, getTheme()));
                        break;
                    case "Memory and Storage":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_memory_and_storage, getTheme()));
                        break;
                    case "Miscellaneous":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_miscellaneous, getTheme()));
                        break;
                    case "Monitor":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_monitor, getTheme()));
                        break;
                    case "Musical Instrument":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_musical_instrument, getTheme()));
                        break;
                    case "Peripheral Device":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_peripheral_device, getTheme()));
                        break;
                    case "Personal Care":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_personal_care, getTheme()));
                        break;
                    case "Personal Computer":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_personal_computer, getTheme()));
                        break;
                    case "Phone and Tablet":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_phone_and_tablet, getTheme()));
                        break;
                    case "Smart Home":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_smart_home, getTheme()));
                        break;
                    case "Smart Watch":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_smart_watch, getTheme()));
                        break;
                    case "Sport and Fitness":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_sport_and_fitness, getTheme()));
                        break;
                    case "Tool":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_tool, getTheme()));
                        break;
                    case "Vehicle":
                        addWarrantyBinding.ivAddWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_category_vehicle, getTheme()));
                        break;
                }
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
                .setTitleText(getResources().getString(R.string.add_warranty_title_date_starting))
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        startingDP.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ADD_CALENDAR_STARTING);

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
                .setTitleText(getResources().getString(R.string.add_warranty_title_date_starting))
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();
        expiryDP.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ADD_CALENDAR_EXPIRY);

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
                AddWarrantyActivity.super.onBackPressed());
    }

    private void addWarrantyOnClick() {
        addWarrantyBinding.toolbarAddWarranty.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            AddWarrantyActivity.super.onNavigateUp();
            return false;
        });
    }
}