package com.xeniac.warrantyroster.mainactivity.addwarrantyfragment;

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
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.NetworkHelper;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.database.CategoryDataModel;
import com.xeniac.warrantyroster.database.WarrantyRosterDatabase;
import com.xeniac.warrantyroster.databinding.FragmentAddWarrantyBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddWarrantyFragment extends Fragment {

    private FragmentAddWarrantyBinding addWarrantyBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;
    private WarrantyRosterDatabase database;

    private final DecimalFormat decimalFormat = new DecimalFormat("00");
    private CategoryDataModel selectedCategory;
    private Calendar startingCalendar, expiryCalendar;
    private String startingDateInput, expiryDateInput;

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
        textInputsStrokeColor();
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

    private void textInputsStrokeColor() {
        addWarrantyBinding.tiAddWarrantyEditDateStarting.addTextChangedListener(new TextWatcher() {
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

        addWarrantyBinding.tiAddWarrantyEditDateExpiry.addTextChangedListener(new TextWatcher() {
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
            startingCalendar = Calendar.getInstance();
            startingCalendar.setTimeInMillis(selection);
            startingDateInput = String.format("%s-%s-%s",
                    startingCalendar.get(Calendar.YEAR),
                    decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1),
                    decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH)));

            String startingDateInput = String.format("%s/%s/%s",
                    decimalFormat.format(startingCalendar.get(Calendar.DAY_OF_MONTH)),
                    decimalFormat.format((startingCalendar.get(Calendar.MONTH)) + 1),
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
            expiryCalendar = Calendar.getInstance();
            expiryCalendar.setTimeInMillis(selection);
            expiryDateInput = String.format("%s-%s-%s",
                    expiryCalendar.get(Calendar.YEAR),
                    decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1),
                    decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH)));

            String expiryDateInput = String.format("%s/%s/%s",
                    decimalFormat.format(expiryCalendar.get(Calendar.DAY_OF_MONTH)),
                    decimalFormat.format((expiryCalendar.get(Calendar.MONTH)) + 1),
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
            addWarranty();
            return false;
        });
    }

    private void addWarranty() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        if (NetworkHelper.hasNetworkAccess(context)) {
            getWarrantyInput();
        } else {
            hideLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> addWarranty()).show();
        }
    }

    private void getWarrantyInput() {
        if (TextUtils.isEmpty(addWarrantyBinding.tiAddWarrantyEditTitle.getText())) {
            addWarrantyBinding.tiAddWarrantyLayoutTitle.requestFocus();
            addWarrantyBinding.tiAddWarrantyLayoutTitle.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (startingCalendar == null) {
            addWarrantyBinding.tiAddWarrantyLayoutDateStarting.requestFocus();
            addWarrantyBinding.tiAddWarrantyLayoutDateStarting.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (expiryCalendar == null) {
            addWarrantyBinding.tiAddWarrantyLayoutDateExpiry.requestFocus();
            addWarrantyBinding.tiAddWarrantyLayoutDateExpiry.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (!isStartingDateValid(startingCalendar, expiryCalendar)) {
            showDateError();
        } else {
            String title = addWarrantyBinding.tiAddWarrantyEditTitle.getText().toString().trim();
            String brand = null;
            String model = null;
            String serialNumber = null;
            String description = null;
            String categoryId = "10";

            if (!TextUtils.isEmpty(addWarrantyBinding.tiAddWarrantyEditBrand.getText())) {
                brand = addWarrantyBinding.tiAddWarrantyEditBrand.getText().toString().trim();
            }
            if (!TextUtils.isEmpty(addWarrantyBinding.tiAddWarrantyEditModel.getText())) {
                model = addWarrantyBinding.tiAddWarrantyEditModel.getText().toString().trim();
            }
            if (!TextUtils.isEmpty(addWarrantyBinding.tiAddWarrantyEditSerial.getText())) {
                serialNumber = addWarrantyBinding.tiAddWarrantyEditSerial.getText().toString().trim();
            }
            if (!TextUtils.isEmpty(addWarrantyBinding.tiAddWarrantyEditDescription.getText())) {
                description = addWarrantyBinding.tiAddWarrantyEditDescription.getText().toString().trim();
            }
            if (selectedCategory != null) {
                categoryId = selectedCategory.getId();
            }

            AddWarrantyInput warrantyInput = AddWarrantyInput.builder()
                    .title(title)
                    .brand(brand)
                    .model(model)
                    .serial_number(serialNumber)
                    .starting_date(startingDateInput)
                    .expiry_date(expiryDateInput)
                    .description(description)
                    .category_id(categoryId)
                    .build();

            addWarrantyMutation(warrantyInput);
        }
    }

    private void addWarrantyMutation(AddWarrantyInput warrantyInput) {
        showLoadingAnimation();

        SharedPreferences loginPrefs = context.getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        String userToken = loginPrefs.getString(Constants.PREFERENCE_USER_TOKEN_KEY, null);

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new AddWarrantyMutation(warrantyInput))
                .toBuilder().requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + userToken).build())
                .build()
                .enqueue(new ApolloCall.Callback<AddWarrantyMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<AddWarrantyMutation.Data> response) {
                        if (addWarrantyBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("addWarranty", "onResponse: " + response);
                                    navController.navigate(R.id.action_addWarrantyFragment_to_warrantiesFragment);
                                } else {
                                    Log.e("addWarranty", "onResponse Errors: " + response.getErrors());
                                    if (Arrays.toString(response.getErrors().get(0).getCustomAttributes().values().toArray()).contains("code=422")) {
                                        Snackbar.make(view, context.getResources().getString(R.string.add_warranty_error_date),
                                                BaseTransientBottomBar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(view, context.getResources().getString(R.string.network_error_response),
                                                BaseTransientBottomBar.LENGTH_INDEFINITE)
                                                .setAction(context.getResources().getString(R.string.network_error_retry), v -> addWarranty()).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("addWarranty", "onFailure: " + e.getMessage());
                        if (addWarrantyBinding != null) {
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
        addWarrantyBinding.tiAddWarrantyEditTitle.setEnabled(false);
        addWarrantyBinding.tiAddWarrantyDdCategory.setEnabled(false);
        addWarrantyBinding.tiAddWarrantyEditBrand.setEnabled(false);
        addWarrantyBinding.tiAddWarrantyEditModel.setEnabled(false);
        addWarrantyBinding.tiAddWarrantyEditSerial.setEnabled(false);
        addWarrantyBinding.tiAddWarrantyEditDateStarting.setEnabled(false);
        addWarrantyBinding.tiAddWarrantyEditDateExpiry.setEnabled(false);
        addWarrantyBinding.tiAddWarrantyEditDescription.setEnabled(false);
        addWarrantyBinding.cpiAddWarranty.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        addWarrantyBinding.tiAddWarrantyEditTitle.setEnabled(true);
        addWarrantyBinding.tiAddWarrantyDdCategory.setEnabled(true);
        addWarrantyBinding.tiAddWarrantyEditBrand.setEnabled(true);
        addWarrantyBinding.tiAddWarrantyEditModel.setEnabled(true);
        addWarrantyBinding.tiAddWarrantyEditSerial.setEnabled(true);
        addWarrantyBinding.tiAddWarrantyEditDateStarting.setEnabled(true);
        addWarrantyBinding.tiAddWarrantyEditDateExpiry.setEnabled(true);
        addWarrantyBinding.tiAddWarrantyEditDescription.setEnabled(true);
        addWarrantyBinding.cpiAddWarranty.setVisibility(View.GONE);
    }

    private void showDateError() {
        addWarrantyBinding.tiAddWarrantyLayoutDateStarting.requestFocus();
        addWarrantyBinding.tiAddWarrantyLayoutDateStarting.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        addWarrantyBinding.tvAddWarrantyDateError.setVisibility(View.VISIBLE);
    }

    private void hideDateError() {
        addWarrantyBinding.tiAddWarrantyLayoutDateStarting.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
        addWarrantyBinding.tvAddWarrantyDateError.setVisibility(View.GONE);
    }

    private boolean isStartingDateValid(Calendar startingCalendar, Calendar expiryCalendar) {
        return expiryCalendar.compareTo(startingCalendar) >= 0;
    }
}