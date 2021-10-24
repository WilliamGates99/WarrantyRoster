package com.xeniac.warrantyrostermanager.mainactivity.editwarrantyfragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.request.RequestHeaders;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.xeniac.warrantyrostermanager.Constants;
import com.xeniac.warrantyrostermanager.NetworkHelper;
import com.xeniac.warrantyrostermanager.R;
import com.xeniac.warrantyrostermanager.database.CategoryDataModel;
import com.xeniac.warrantyrostermanager.database.WarrantyRosterDatabase;
import com.xeniac.warrantyrostermanager.databinding.FragmentEditWarrantyBinding;
import com.xeniac.warrantyrostermanager.mainactivity.MainActivity;
import com.xeniac.warrantyrostermanager.mainactivity.warrantiesfragment.ListItemType;
import com.xeniac.warrantyrostermanager.mainactivity.warrantiesfragment.WarrantyDataModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EditWarrantyFragment extends Fragment {

    private FragmentEditWarrantyBinding editWarrantyBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;
    private WarrantyRosterDatabase database;
    private WarrantyDataModel warranty;

    private final DecimalFormat decimalFormat = new DecimalFormat("00");
    private CategoryDataModel selectedCategory;
    private Calendar startingCalendar, expiryCalendar;
    private String startingDateInput, expiryDateInput;

    public EditWarrantyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        editWarrantyBinding = FragmentEditWarrantyBinding.inflate(inflater, container, false);
        view = editWarrantyBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WarrantyRosterDatabase.destroyInstance();
        editWarrantyBinding = null;
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
        textInputsStrokeColor();
        categoryDropDownSelection();
        startingDatePicker();
        expiryDatePicker();
        returnToWarrantyDetailsFragment();
        getWarranty();
        editWarrantyOnClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryDropDown();
    }

    private void textInputsBackgroundColor() {
        editWarrantyBinding.tiEditWarrantyEditTitle.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                editWarrantyBinding.tiEditWarrantyLayoutTitle.setBoxBackgroundColorResource(R.color.background);
            } else {
                editWarrantyBinding.tiEditWarrantyLayoutTitle.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        editWarrantyBinding.tiEditWarrantyDdCategory.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                editWarrantyBinding.tiEditWarrantyLayoutCategory.setBoxBackgroundColorResource(R.color.background);
            } else {
                editWarrantyBinding.tiEditWarrantyLayoutCategory.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        editWarrantyBinding.tiEditWarrantyEditBrand.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                editWarrantyBinding.tiEditWarrantyLayoutBrand.setBoxBackgroundColorResource(R.color.background);
            } else {
                editWarrantyBinding.tiEditWarrantyLayoutBrand.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        editWarrantyBinding.tiEditWarrantyEditModel.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                editWarrantyBinding.tiEditWarrantyLayoutModel.setBoxBackgroundColorResource(R.color.background);
            } else {
                editWarrantyBinding.tiEditWarrantyLayoutModel.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        editWarrantyBinding.tiEditWarrantyEditSerial.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                editWarrantyBinding.tiEditWarrantyLayoutSerial.setBoxBackgroundColorResource(R.color.background);
            } else {
                editWarrantyBinding.tiEditWarrantyLayoutSerial.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        editWarrantyBinding.tiEditWarrantyEditDateStarting.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                editWarrantyBinding.tiEditWarrantyLayoutDateStarting.setBoxBackgroundColorResource(R.color.background);
            } else {
                editWarrantyBinding.tiEditWarrantyLayoutDateStarting.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        editWarrantyBinding.tiEditWarrantyEditDateExpiry.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                editWarrantyBinding.tiEditWarrantyLayoutDateExpiry.setBoxBackgroundColorResource(R.color.background);
            } else {
                editWarrantyBinding.tiEditWarrantyLayoutDateExpiry.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        editWarrantyBinding.tiEditWarrantyEditDescription.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                editWarrantyBinding.tiEditWarrantyLayoutDescription.setBoxBackgroundColorResource(R.color.background);
            } else {
                editWarrantyBinding.tiEditWarrantyLayoutDescription.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void textInputsStrokeColor() {
        editWarrantyBinding.tiEditWarrantyEditDateStarting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hideDateError();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editWarrantyBinding.tiEditWarrantyEditDateExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hideDateError();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void categoryDropDown() {
        List<String> titlesList = new ArrayList<>();
        for (Integer categoryTitleId : database.categoryDAO().getAllCategoryTitles()) {
            titlesList.add(context.getResources().getString(categoryTitleId));
        }

        editWarrantyBinding.tiEditWarrantyDdCategory.setAdapter(new ArrayAdapter<>(context, R.layout.dropdown_category, titlesList));
    }

    private void categoryDropDownSelection() {
        editWarrantyBinding.tiEditWarrantyDdCategory.setOnItemClickListener((adapterView, view, index, l) -> {
            selectedCategory = database.categoryDAO().getCategoryById(String.valueOf(index + 1));
            if (selectedCategory != null) {
                editWarrantyBinding.ivEditWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                        getResources(), selectedCategory.getIcon(), context.getTheme()));
            }
        });
    }

    private void startingDatePicker() {
        editWarrantyBinding.tiEditWarrantyEditDateStarting.setInputType(InputType.TYPE_NULL);
        editWarrantyBinding.tiEditWarrantyEditDateStarting.setKeyListener(null);

        editWarrantyBinding.tiEditWarrantyEditDateStarting.setOnFocusChangeListener((view, isFocused) -> {
            if (isFocused) {
                openStartingDatePicker();
            }
        });
    }

    private void expiryDatePicker() {
        editWarrantyBinding.tiEditWarrantyEditDateExpiry.setInputType(InputType.TYPE_NULL);
        editWarrantyBinding.tiEditWarrantyEditDateExpiry.setKeyListener(null);

        editWarrantyBinding.tiEditWarrantyEditDateExpiry.setOnFocusChangeListener((view, isFocused) -> {
            if (isFocused) {
                openExpiryDatePicker();
            }
        });
    }

    private void openStartingDatePicker() {
        MaterialDatePicker<Long> startingDP = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getResources().getString(R.string.edit_warranty_title_date_picker_starting))
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        startingDP.show(getParentFragmentManager(), Constants.FRAGMENT_TAG_ADD_CALENDAR_STARTING);

        startingDP.addOnPositiveButtonClickListener(selection -> {
            startingCalendar = Calendar.getInstance();
            startingCalendar.setTimeInMillis(selection);
            startingDateInput = String.format("%s-%s-%s",
                    startingCalendar.get(Calendar.YEAR),
                    decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1),
                    decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH)));

            String startingDateInput = String.format("%s/%s/%s",
                    decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1),
                    decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH)),
                    startingCalendar.get(Calendar.YEAR));

            editWarrantyBinding.tiEditWarrantyEditDateStarting.setText(startingDateInput);
            editWarrantyBinding.tiEditWarrantyEditDateStarting.clearFocus();
        });

        startingDP.addOnDismissListener(dialogInterface ->
                editWarrantyBinding.tiEditWarrantyEditDateStarting.clearFocus());
    }

    private void openExpiryDatePicker() {
        MaterialDatePicker<Long> expiryDP = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getResources().getString(R.string.edit_warranty_title_date_picker_expiry))
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();
        expiryDP.show(getParentFragmentManager(), Constants.FRAGMENT_TAG_ADD_CALENDAR_EXPIRY);

        expiryDP.addOnPositiveButtonClickListener(selection -> {
            expiryCalendar = Calendar.getInstance();
            expiryCalendar.setTimeInMillis(selection);
            expiryDateInput = String.format("%s-%s-%s",
                    expiryCalendar.get(Calendar.YEAR),
                    decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1),
                    decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH)));

            String expiryDateInput = String.format("%s/%s/%s",
                    decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1),
                    decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH)),
                    expiryCalendar.get(Calendar.YEAR));

            editWarrantyBinding.tiEditWarrantyEditDateExpiry.setText(expiryDateInput);
            editWarrantyBinding.tiEditWarrantyEditDateExpiry.clearFocus();
        });

        expiryDP.addOnDismissListener(dialogInterface ->
                editWarrantyBinding.tiEditWarrantyEditDateExpiry.clearFocus());
    }

    private void returnToWarrantyDetailsFragment() {
        editWarrantyBinding.toolbarEditWarranty.setNavigationOnClickListener(view ->
                activity.onBackPressed());
    }

    private void getWarranty() {
        if (getArguments() != null) {
            EditWarrantyFragmentArgs args = EditWarrantyFragmentArgs.fromBundle(getArguments());
            warranty = args.getWarranty();
            setWarrantyDetails();
        }
    }

    private void setWarrantyDetails() {
        selectedCategory = database.categoryDAO().getCategoryById(warranty.getCategoryId());
        editWarrantyBinding.tiEditWarrantyEditTitle.setText(warranty.getTitle());
        editWarrantyBinding.tiEditWarrantyDdCategory.setText(context.getResources().getString(selectedCategory.getTitle()));
        editWarrantyBinding.ivEditWarrantyIconCategory.setImageDrawable(ResourcesCompat.getDrawable(
                context.getResources(), selectedCategory.getIcon(), context.getTheme()));
        editWarrantyBinding.tiEditWarrantyEditBrand.setText(warranty.getBrand());
        editWarrantyBinding.tiEditWarrantyEditModel.setText(warranty.getModel());
        editWarrantyBinding.tiEditWarrantyEditSerial.setText(warranty.getSerialNumber());
        editWarrantyBinding.tiEditWarrantyEditDescription.setText(warranty.getDescription());

        startingCalendar = Calendar.getInstance();
        expiryCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault());
        DecimalFormat decimalFormat = new DecimalFormat("00");

        try {
            startingCalendar.setTime(Objects.requireNonNull(dateFormat.parse(warranty.getStartingDate())));
            expiryCalendar.setTime(Objects.requireNonNull(dateFormat.parse(warranty.getExpiryDate())));

            startingDateInput = String.format("%s-%s-%s",
                    startingCalendar.get(Calendar.YEAR),
                    decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1),
                    decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH)));

            expiryDateInput = String.format("%s-%s-%s",
                    expiryCalendar.get(Calendar.YEAR),
                    decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1),
                    decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH)));

            editWarrantyBinding.tiEditWarrantyEditDateStarting.setText(String.format(("%s/%s/%s"),
                    decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH)),
                    decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1),
                    startingCalendar.get(Calendar.YEAR)));

            editWarrantyBinding.tiEditWarrantyEditDateExpiry.setText(String.format(("%s/%s/%s"),
                    decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH)),
                    decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1),
                    expiryCalendar.get(Calendar.YEAR)));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("setWarrantyDetails", "ParseException: " + e.getMessage());
        }
    }

    private void editWarrantyOnClick() {
        editWarrantyBinding.toolbarEditWarranty.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            editWarranty();
            return false;
        });
    }

    private void editWarranty() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        if (NetworkHelper.hasNetworkAccess(context)) {
            getWarrantyInput();
        } else {
            hideLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> editWarranty()).show();
        }
    }

    private void getWarrantyInput() {
        if (TextUtils.isEmpty(editWarrantyBinding.tiEditWarrantyEditTitle.getText())) {
            editWarrantyBinding.tiEditWarrantyLayoutTitle.requestFocus();
            editWarrantyBinding.tiEditWarrantyLayoutTitle.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (startingCalendar == null) {
            editWarrantyBinding.tiEditWarrantyLayoutDateStarting.requestFocus();
            editWarrantyBinding.tiEditWarrantyLayoutDateStarting.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (expiryCalendar == null) {
            editWarrantyBinding.tiEditWarrantyLayoutDateExpiry.requestFocus();
            editWarrantyBinding.tiEditWarrantyLayoutDateExpiry.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (!isStartingDateValid(startingCalendar, expiryCalendar)) {
            showDateError();
        } else {
            String title = editWarrantyBinding.tiEditWarrantyEditTitle.getText().toString().trim();
            String brand = null;
            String model = null;
            String serialNumber = null;
            String description = null;
            String categoryId = "10";

            if (!TextUtils.isEmpty(editWarrantyBinding.tiEditWarrantyEditBrand.getText())) {
                brand = editWarrantyBinding.tiEditWarrantyEditBrand.getText().toString().trim();
            }
            if (!TextUtils.isEmpty(editWarrantyBinding.tiEditWarrantyEditModel.getText())) {
                model = editWarrantyBinding.tiEditWarrantyEditModel.getText().toString().trim();
            }
            if (!TextUtils.isEmpty(editWarrantyBinding.tiEditWarrantyEditSerial.getText())) {
                serialNumber = editWarrantyBinding.tiEditWarrantyEditSerial.getText().toString().trim();
            }
            if (!TextUtils.isEmpty(editWarrantyBinding.tiEditWarrantyEditDescription.getText())) {
                description = editWarrantyBinding.tiEditWarrantyEditDescription.getText().toString().trim();
            }
            if (selectedCategory != null) {
                categoryId = selectedCategory.getId();
            }

            EditWarrantyInput warrantyInput = EditWarrantyInput.builder()
                    .title(title)
                    .brand(brand)
                    .model(model)
                    .serial_number(serialNumber)
                    .starting_date(startingDateInput)
                    .expiry_date(expiryDateInput)
                    .description(description)
                    .category_id(categoryId)
                    .build();

            editWarrantyMutation(warrantyInput);
        }
    }

    private void editWarrantyMutation(EditWarrantyInput warrantyInput) {
        showLoadingAnimation();

        SharedPreferences loginPrefs = context.getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        String userToken = loginPrefs.getString(Constants.PREFERENCE_USER_TOKEN_KEY, null);

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new EditWarrantyMutation(warranty.getId(), warrantyInput))
                .toBuilder().requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + userToken).build())
                .build()
                .enqueue(new ApolloCall.Callback<EditWarrantyMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<EditWarrantyMutation.Data> response) {
                        if (editWarrantyBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("editWarranty", "onResponse: " + response);
                                    WarrantyDataModel editedWarranty = new WarrantyDataModel(
                                            response.getData().editWarranty().id(),
                                            response.getData().editWarranty().title(),
                                            response.getData().editWarranty().brand(),
                                            response.getData().editWarranty().model(),
                                            response.getData().editWarranty().serial_number(),
                                            response.getData().editWarranty().starting_date().toString(),
                                            response.getData().editWarranty().expiry_date().toString(),
                                            response.getData().editWarranty().description(),
                                            response.getData().editWarranty().category_id(),
                                            ListItemType.WARRANTY
                                    );

                                    long daysUntilExpiry = getDaysUntilExpiry(editedWarranty.getExpiryDate());
                                    EditWarrantyFragmentDirections.ActionEditWarrantyFragmentToWarrantyDetailsFragment action =
                                            EditWarrantyFragmentDirections.actionEditWarrantyFragmentToWarrantyDetailsFragment(editedWarranty, daysUntilExpiry);
                                    navController.navigate(action);
                                } else {
                                    Log.e("editWarranty", "onResponse Errors: " + response.getErrors());
                                    if (Arrays.toString(response.getErrors().get(0).getCustomAttributes().values().toArray()).contains("code=422")) {
                                        Snackbar.make(view, context.getResources().getString(R.string.edit_warranty_error_date),
                                                BaseTransientBottomBar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(view, context.getResources().getString(R.string.network_error_response),
                                                BaseTransientBottomBar.LENGTH_INDEFINITE)
                                                .setAction(context.getResources().getString(R.string.network_error_retry), v -> editWarranty()).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("editWarranty", "onFailure: " + e.getMessage());
                        if (editWarrantyBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();
                                Snackbar.make(view, context.getResources().getString(R.string.network_error_failure),
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                            });
                        }
                    }
                });
    }

    private void showLoadingAnimation() {
        editWarrantyBinding.toolbarEditWarranty.getMenu().getItem(0).setVisible(false);
        editWarrantyBinding.tiEditWarrantyEditTitle.setEnabled(false);
        editWarrantyBinding.tiEditWarrantyDdCategory.setEnabled(false);
        editWarrantyBinding.tiEditWarrantyEditBrand.setEnabled(false);
        editWarrantyBinding.tiEditWarrantyEditModel.setEnabled(false);
        editWarrantyBinding.tiEditWarrantyEditSerial.setEnabled(false);
        editWarrantyBinding.tiEditWarrantyEditDateStarting.setEnabled(false);
        editWarrantyBinding.tiEditWarrantyEditDateExpiry.setEnabled(false);
        editWarrantyBinding.tiEditWarrantyEditDescription.setEnabled(false);
        editWarrantyBinding.cpiEditWarranty.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        editWarrantyBinding.toolbarEditWarranty.getMenu().getItem(0).setVisible(true);
        editWarrantyBinding.tiEditWarrantyEditTitle.setEnabled(true);
        editWarrantyBinding.tiEditWarrantyDdCategory.setEnabled(true);
        editWarrantyBinding.tiEditWarrantyEditBrand.setEnabled(true);
        editWarrantyBinding.tiEditWarrantyEditModel.setEnabled(true);
        editWarrantyBinding.tiEditWarrantyEditSerial.setEnabled(true);
        editWarrantyBinding.tiEditWarrantyEditDateStarting.setEnabled(true);
        editWarrantyBinding.tiEditWarrantyEditDateExpiry.setEnabled(true);
        editWarrantyBinding.tiEditWarrantyEditDescription.setEnabled(true);
        editWarrantyBinding.cpiEditWarranty.setVisibility(View.GONE);
    }

    private void showDateError() {
        editWarrantyBinding.tiEditWarrantyLayoutDateStarting.requestFocus();
        editWarrantyBinding.tiEditWarrantyLayoutDateStarting.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        editWarrantyBinding.tvEditWarrantyDateError.setVisibility(View.VISIBLE);
    }

    private void hideDateError() {
        editWarrantyBinding.tiEditWarrantyLayoutDateStarting.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
        editWarrantyBinding.tvEditWarrantyDateError.setVisibility(View.GONE);
    }

    private boolean isStartingDateValid(Calendar startingCalendar, Calendar expiryCalendar) {
        return expiryCalendar.compareTo(startingCalendar) >= 0;
    }

    private long getDaysUntilExpiry(String expiryDate) {
        Calendar expiryCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault());

        try {
            expiryCalendar.setTime(Objects.requireNonNull(dateFormat.parse(expiryDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);

        return TimeUnit.MILLISECONDS.toDays(expiryCalendar.getTimeInMillis() - todayCalendar.getTimeInMillis());
    }
}