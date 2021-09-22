package com.xeniac.warrantyroster.landingactivity.registerfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.NetworkHelper;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.FragmentRegisterBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

import java.util.Arrays;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding registerBinding;
    private View view;
    private Activity activity;
    private Context context;

    public RegisterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        view = registerBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        registerBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();

        textInputsBackgroundColor();
        textInputsStrokeColor();
        loginOnClick();
        registerOnClick();
        registerActionDone();
    }

    private void textInputsBackgroundColor() {
        registerBinding.tiRegisterEditEmail.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                registerBinding.tiRegisterLayoutEmail.setBoxBackgroundColorResource(R.color.background);
            } else {
                registerBinding.tiRegisterLayoutEmail.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        registerBinding.tiRegisterEditPassword.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                registerBinding.tiRegisterLayoutPassword.setBoxBackgroundColorResource(R.color.background);
            } else {
                registerBinding.tiRegisterLayoutPassword.setBoxBackgroundColorResource(R.color.grayLight);
                registerBinding.tiRegisterLayoutPassword.setHelperTextEnabled(false);
            }
        });

        registerBinding.tiRegisterEditRetypePassword.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                registerBinding.tiRegisterLayoutRetypePassword.setBoxBackgroundColorResource(R.color.background);
            } else {
                registerBinding.tiRegisterLayoutRetypePassword.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void textInputsStrokeColor() {
        registerBinding.tiRegisterEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputEmail, int start, int before, int count) {
                registerBinding.tiRegisterLayoutEmail.setErrorEnabled(false);
                registerBinding.tiRegisterLayoutEmail.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerBinding.tiRegisterEditPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputPassword, int start, int before, int count) {
                if (registerBinding.tiRegisterLayoutPassword.hasFocus()) {
                    switch (passwordStrength(inputPassword.toString())) {
                        case -1:
                            registerBinding.tiRegisterLayoutPassword.setHelperText(context.getResources().getString(R.string.register_helper_password_weak));
                            registerBinding.tiRegisterLayoutPassword.setHelperTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
                            registerBinding.tiRegisterLayoutPassword.setBoxStrokeColor(context.getResources().getColor(R.color.red));
                            break;
                        case 0:
                            registerBinding.tiRegisterLayoutPassword.setHelperText(context.getResources().getString(R.string.register_helper_password_mediocre));
                            registerBinding.tiRegisterLayoutPassword.setHelperTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));
                            registerBinding.tiRegisterLayoutPassword.setBoxStrokeColor(context.getResources().getColor(R.color.orange));
                            break;
                        case 1:
                            registerBinding.tiRegisterLayoutPassword.setHelperText(context.getResources().getString(R.string.register_helper_password_strong));
                            registerBinding.tiRegisterLayoutPassword.setHelperTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
                            registerBinding.tiRegisterLayoutPassword.setBoxStrokeColor(context.getResources().getColor(R.color.green));
                            break;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerBinding.tiRegisterEditRetypePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputRetypePassword, int start, int before, int count) {
                registerBinding.tiRegisterLayoutRetypePassword.setErrorEnabled(false);
                registerBinding.tiRegisterLayoutRetypePassword.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loginOnClick() {
        registerBinding.btnRegisterLogin.setOnClickListener(view ->
                activity.onBackPressed());
    }

    private void registerOnClick() {
        registerBinding.btnRegisterRegister.setOnClickListener(view ->
                registerViaEmail());
    }

    private void registerActionDone() {
        registerBinding.tiRegisterEditRetypePassword.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerViaEmail();
            }
            return false;
        });
    }

    private void registerViaEmail() {
        if (NetworkHelper.hasNetworkAccess(context)) {
            getRegisterInputs();
        } else {
            hideLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> registerViaEmail()).show();
        }
    }

    private void getRegisterInputs() {
        String email;
        String password;
        String retypePassword;
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;

        if (TextUtils.isEmpty(registerBinding.tiRegisterEditEmail.getText())) {
            registerBinding.tiRegisterLayoutEmail.requestFocus();
            registerBinding.tiRegisterLayoutEmail.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (TextUtils.isEmpty(registerBinding.tiRegisterEditPassword.getText())) {
            registerBinding.tiRegisterLayoutPassword.requestFocus();
            registerBinding.tiRegisterLayoutPassword.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (TextUtils.isEmpty(registerBinding.tiRegisterEditRetypePassword.getText())) {
            registerBinding.tiRegisterLayoutRetypePassword.requestFocus();
            registerBinding.tiRegisterLayoutRetypePassword.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else {
            email = registerBinding.tiRegisterEditEmail.getText().toString().trim().toLowerCase();
            password = registerBinding.tiRegisterEditPassword.getText().toString().trim();
            retypePassword = registerBinding.tiRegisterEditRetypePassword.getText().toString().trim();

            if (!isEmailValid(email)) {
                registerBinding.tiRegisterLayoutEmail.requestFocus();
                registerBinding.tiRegisterLayoutEmail.setError(context.getResources().getString(R.string.register_error_email));
            } else if (!isRetypePasswordValid(password, retypePassword)) {
                registerBinding.tiRegisterLayoutRetypePassword.requestFocus();
                registerBinding.tiRegisterLayoutRetypePassword.setError(context.getResources().getString(R.string.register_error_password));
            } else {
                registerViaEmailMutation(email, password, deviceName);
            }
        }
    }

    private void registerViaEmailMutation(String email, String password, String deviceName) {
        showLoadingAnimation();

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new RegisterViaEmailMutation(email, password, deviceName))
                .enqueue(new ApolloCall.Callback<RegisterViaEmailMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RegisterViaEmailMutation.Data> response) {
                        activity.runOnUiThread(() -> {
                            hideLoadingAnimation();

                            if (!response.hasErrors()) {
                                Log.i("registerViaEmail", "onResponse: " + response);

                                SharedPreferences.Editor editor = context
                                        .getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE).edit();
                                editor.putString(Constants.PREFERENCE_USER_TOKEN_KEY, response.getData().registerViaEmail().token()).apply();
                                editor.putBoolean(Constants.PREFERENCE_IS_LOGGED_IN_KEY, true).apply();

                                startActivity(new Intent(context, MainActivity.class));
                                activity.finish();
                            } else {
                                Log.e("registerViaEmail", "onResponse Errors: " + response.getErrors());
                                if (Arrays.toString(response.getErrors().get(0).getCustomAttributes().values().toArray()).contains("code=400")) {
                                    Snackbar.make(view, context.getResources().getString(R.string.register_error_account_exists),
                                            BaseTransientBottomBar.LENGTH_INDEFINITE)
                                            .setAction(context.getResources().getString(R.string.register_btn_login), v -> activity.onBackPressed()).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("registerViaEmail", "onFailure: " + e.getMessage());
                        activity.runOnUiThread(() -> {
                            Snackbar.make(view, context.getResources().getString(R.string.network_error_failure),
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private void showLoadingAnimation() {
        registerBinding.btnRegisterRegister.setText(null);
        registerBinding.btnRegisterRegister.setClickable(false);
        registerBinding.cpiRegister.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        registerBinding.cpiRegister.setVisibility(View.GONE);
        registerBinding.btnRegisterRegister.setClickable(true);
        registerBinding.btnRegisterRegister.setText(context.getResources().getString(R.string.register_btn_register));
    }

    private byte passwordStrength(String password) {
        Pattern PASSWORD = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=(.*[\\W])*).{8,}$");

        if (password.length() < 4) {
            return -1;
        } else if (password.length() < 8) {
            return 0;
        } else {
            if (PASSWORD.matcher(password).matches()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isRetypePasswordValid(String password, String retypePassword) {
        return password.equals(retypePassword);
    }
}