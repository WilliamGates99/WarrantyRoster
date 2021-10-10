package com.xeniac.warrantyroster.mainactivity.changepasswordfragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.request.RequestHeaders;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.NetworkHelper;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.FragmentChangePasswordBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding changePasswordBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;

    public ChangePasswordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        changePasswordBinding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        view = changePasswordBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        changePasswordBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);
        ((MainActivity) context).hideNavBar();

        textInputsBackgroundColor();
        textInputsStrokeColor();
        returnToMainActivity();
        changePasswordOnClick();
        changePasswordActionDone();
    }

    private void textInputsBackgroundColor() {
        changePasswordBinding.tiChangePasswordEditOld.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                changePasswordBinding.tiChangePasswordLayoutOld.setBoxBackgroundColorResource(R.color.background);
            } else {
                changePasswordBinding.tiChangePasswordLayoutOld.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        changePasswordBinding.tiChangePasswordEditNew.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                changePasswordBinding.tiChangePasswordLayoutNew.setBoxBackgroundColorResource(R.color.background);
            } else {
                changePasswordBinding.tiChangePasswordLayoutNew.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        changePasswordBinding.tiChangePasswordEditRetype.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                changePasswordBinding.tiChangePasswordLayoutRetype.setBoxBackgroundColorResource(R.color.background);
            } else {
                changePasswordBinding.tiChangePasswordLayoutRetype.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void textInputsStrokeColor() {
        changePasswordBinding.tiChangePasswordEditOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputEmail, int start, int before, int count) {
                changePasswordBinding.tiChangePasswordLayoutOld.setErrorEnabled(false);
                changePasswordBinding.tiChangePasswordLayoutOld.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        changePasswordBinding.tiChangePasswordEditNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputNewPassword, int start, int before, int count) {
                if (changePasswordBinding.tiChangePasswordLayoutNew.hasFocus()) {
                    switch (newPasswordStrength(inputNewPassword.toString())) {
                        case -1:
                            changePasswordBinding.tiChangePasswordLayoutNew.setHelperText(context.getResources().getString(R.string.change_password_helper_password_weak));
                            changePasswordBinding.tiChangePasswordLayoutNew.setHelperTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
                            changePasswordBinding.tiChangePasswordLayoutNew.setBoxStrokeColor(context.getResources().getColor(R.color.red));
                            break;
                        case 0:
                            changePasswordBinding.tiChangePasswordLayoutNew.setHelperText(context.getResources().getString(R.string.change_password_helper_password_mediocre));
                            changePasswordBinding.tiChangePasswordLayoutNew.setHelperTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));
                            changePasswordBinding.tiChangePasswordLayoutNew.setBoxStrokeColor(context.getResources().getColor(R.color.orange));
                            break;
                        case 1:
                            changePasswordBinding.tiChangePasswordLayoutNew.setHelperText(context.getResources().getString(R.string.change_password_helper_password_strong));
                            changePasswordBinding.tiChangePasswordLayoutNew.setHelperTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
                            changePasswordBinding.tiChangePasswordLayoutNew.setBoxStrokeColor(context.getResources().getColor(R.color.green));
                            break;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        changePasswordBinding.tiChangePasswordEditRetype.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputRetypePassword, int start, int before, int count) {
                changePasswordBinding.tiChangePasswordLayoutRetype.setErrorEnabled(false);
                changePasswordBinding.tiChangePasswordLayoutRetype.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void returnToMainActivity() {
        changePasswordBinding.toolbarChangePassword.setNavigationOnClickListener(view ->
                activity.onBackPressed());
    }

    private void changePasswordOnClick() {
        changePasswordBinding.btnChangePassword.setOnClickListener(view ->
                changeUserPassword());
    }

    private void changePasswordActionDone() {
        changePasswordBinding.tiChangePasswordEditRetype.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changeUserPassword();
            }
            return false;
        });
    }

    private void changeUserPassword() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        if (NetworkHelper.hasNetworkAccess(context)) {
            getChangeUserPasswordInputs();
        } else {
            hideLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> changeUserPassword()).show();
        }
    }

    private void getChangeUserPasswordInputs() {
        String oldPassword;
        String newPassword;
        String retypeNewPassword;

        if (TextUtils.isEmpty(changePasswordBinding.tiChangePasswordEditOld.getText())) {
            changePasswordBinding.tiChangePasswordLayoutOld.requestFocus();
            changePasswordBinding.tiChangePasswordLayoutOld.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (TextUtils.isEmpty(changePasswordBinding.tiChangePasswordEditNew.getText())) {
            changePasswordBinding.tiChangePasswordLayoutNew.requestFocus();
            changePasswordBinding.tiChangePasswordLayoutNew.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (TextUtils.isEmpty(changePasswordBinding.tiChangePasswordEditRetype.getText())) {
            changePasswordBinding.tiChangePasswordLayoutRetype.requestFocus();
            changePasswordBinding.tiChangePasswordLayoutRetype.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else {
            oldPassword = changePasswordBinding.tiChangePasswordEditOld.getText().toString().trim();
            newPassword = changePasswordBinding.tiChangePasswordEditNew.getText().toString().trim();
            retypeNewPassword = changePasswordBinding.tiChangePasswordEditRetype.getText().toString().trim();

            if (!isRetypePasswordValid(newPassword, retypeNewPassword)) {
                changePasswordBinding.tiChangePasswordLayoutRetype.requestFocus();
                changePasswordBinding.tiChangePasswordLayoutRetype.setError(context.getResources().getString(R.string.change_password_error_match));
            } else {
                changeEmailMutation(oldPassword, newPassword);
            }
        }
    }

    private void changeEmailMutation(String oldPassword, String newPassword) {
        showLoadingAnimation();

        SharedPreferences loginPrefs = context.getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        String userToken = loginPrefs.getString(Constants.PREFERENCE_USER_TOKEN_KEY, null);

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new ChangeUserPasswordMutation(oldPassword, newPassword))
                .toBuilder().requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + userToken).build())
                .build()
                .enqueue(new ApolloCall.Callback<ChangeUserPasswordMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<ChangeUserPasswordMutation.Data> response) {
                        if (changePasswordBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("changeUserPassword", "onResponse: " + response);
                                    MaterialAlertDialogBuilder changeUserPasswordDialogBuilder = new MaterialAlertDialogBuilder(context)
                                            .setMessage(context.getResources().getString(R.string.change_password_dialog_message))
                                            .setPositiveButton(context.getResources().getString(R.string.change_password_dialog_positive), (dialog, which) -> {
                                            })
                                            .setOnDismissListener(dialog -> activity.onBackPressed());
                                    changeUserPasswordDialogBuilder.show();
                                } else {
                                    Log.e("changeUserPassword", "onResponse Errors: " + response.getErrors());
                                    if (Arrays.toString(response.getErrors().get(0).getCustomAttributes().values().toArray()).contains("code=400")) {
                                        Snackbar.make(view, context.getResources().getString(R.string.change_password_error_old),
                                                BaseTransientBottomBar.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Snackbar.make(view, context.getResources().getString(R.string.network_error_response),
                                                BaseTransientBottomBar.LENGTH_INDEFINITE)
                                                .setAction(context.getResources().getString(R.string.network_error_retry), v -> changeUserPassword())
                                                .show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("changeUserPassword", "onFailure: " + e.getMessage());
                        if (changePasswordBinding != null) {
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
        changePasswordBinding.tiChangePasswordEditOld.setEnabled(false);
        changePasswordBinding.tiChangePasswordEditNew.setEnabled(false);
        changePasswordBinding.tiChangePasswordEditRetype.setEnabled(false);
        changePasswordBinding.btnChangePassword.setClickable(false);
        changePasswordBinding.btnChangePassword.setText(null);
        changePasswordBinding.cpiChangePassword.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        changePasswordBinding.cpiChangePassword.setVisibility(View.GONE);
        changePasswordBinding.tiChangePasswordEditOld.setEnabled(true);
        changePasswordBinding.tiChangePasswordEditNew.setEnabled(true);
        changePasswordBinding.tiChangePasswordEditRetype.setEnabled(true);
        changePasswordBinding.btnChangePassword.setClickable(true);
        changePasswordBinding.btnChangePassword.setText(context.getResources().getString(R.string.change_password_btn_change));
    }

    private byte newPasswordStrength(String newPassword) {
        Pattern PASSWORD = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=(.*[\\W])*).{8,}$");

        if (newPassword.length() < 4) {
            return -1;
        } else if (newPassword.length() < 8) {
            return 0;
        } else {
            if (PASSWORD.matcher(newPassword).matches()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private boolean isRetypePasswordValid(String password, String retypePassword) {
        return password.equals(retypePassword);
    }
}