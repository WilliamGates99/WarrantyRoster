package com.xeniac.warrantyroster.landingactivity.loginfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.NetworkHelper;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.FragmentLoginBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

import java.util.Arrays;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding loginBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        view = loginBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loginBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);

        textInputsBackgroundColor();
        textInputsStrokeColor();
        forgotPwOnClick();
        registerOnClick();
        loginOnClick();
        loginActionDone();
    }

    private void textInputsBackgroundColor() {
        loginBinding.tiLoginEditEmail.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                loginBinding.tiLoginLayoutEmail.setBoxBackgroundColorResource(R.color.windowBG);
            } else {
                loginBinding.tiLoginLayoutEmail.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        loginBinding.tiLoginEditPassword.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                loginBinding.tiLoginLayoutPassword.setBoxBackgroundColorResource(R.color.windowBG);
            } else {
                loginBinding.tiLoginLayoutPassword.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void textInputsStrokeColor() {
        loginBinding.tiLoginEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputEmail, int start, int before, int count) {
                loginBinding.tiLoginLayoutEmail.setErrorEnabled(false);
                loginBinding.tiLoginLayoutEmail.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginBinding.tiLoginEditPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputEmail, int start, int before, int count) {
                loginBinding.tiLoginLayoutPassword.setErrorEnabled(false);
                loginBinding.tiLoginLayoutPassword.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void forgotPwOnClick() {
        loginBinding.btnLoginForgotPw.setOnClickListener(view ->
                navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
    }

    private void registerOnClick() {
        loginBinding.btnLoginRegister.setOnClickListener(view ->
                navController.navigate(R.id.action_loginFragment_to_registerFragment));
    }

    private void loginOnClick() {
        loginBinding.btnLoginLogin.setOnClickListener(view ->
                loginViaEmail());
    }

    private void loginActionDone() {
        loginBinding.tiLoginEditPassword.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViaEmail();
            }
            return false;
        });
    }

    private void loginViaEmail() {
        if (NetworkHelper.hasNetworkAccess(context)) {
            getLoginInputs();
        } else {
            hideLoadingAnimation();
            Snackbar snackbar = Snackbar.make(view,
                    context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> loginViaEmail())
                    .setBackgroundTint(context.getResources().getColor(R.color.red))
                    .setTextColor(context.getResources().getColor(R.color.white))
                    .setActionTextColor(context.getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            TextView snackText = (snackbar.getView()).findViewById(R.id.snackbar_text);
            TextView snackAction = (snackbar.getView()).findViewById(R.id.snackbar_action);
            snackText.setTypeface(ResourcesCompat.getFont(context, R.font.nunito_semi_bold));
            snackAction.setTypeface(ResourcesCompat.getFont(context, R.font.nunito_bold));
            snackbar.show();
        }
    }

    private void getLoginInputs() {
        String email;
        String password;
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;

        if (TextUtils.isEmpty(loginBinding.tiLoginEditEmail.getText())) {
            loginBinding.tiLoginLayoutEmail.requestFocus();
            loginBinding.tiLoginLayoutEmail.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else if (TextUtils.isEmpty(loginBinding.tiLoginEditPassword.getText())) {
            loginBinding.tiLoginLayoutPassword.requestFocus();
            loginBinding.tiLoginLayoutPassword.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else {
            email = loginBinding.tiLoginEditEmail.getText().toString().trim().toLowerCase();
            password = loginBinding.tiLoginEditPassword.getText().toString().trim();

            if (!isEmailValid(email)) {
                loginBinding.tiLoginLayoutEmail.requestFocus();
                loginBinding.tiLoginLayoutEmail.setError(context.getResources().getString(R.string.login_error_email));
            } else {
                loginViaEmailMutation(email, password, deviceName);
            }
        }
    }

    private void loginViaEmailMutation(String email, String password, String deviceName) {
        showLoadingAnimation();

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new LoginViaEmailMutation(email, password, deviceName))
                .enqueue(new ApolloCall.Callback<LoginViaEmailMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<LoginViaEmailMutation.Data> response) {
                        activity.runOnUiThread(() -> {
                            hideLoadingAnimation();

                            if (!response.hasErrors()) {
                                Log.i("loginViaEmail", "onResponse: " + response);

                                SharedPreferences.Editor editor = context
                                        .getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE).edit();
                                editor.putString(Constants.PREFERENCE_USER_TOKEN_KEY, response.getData().loginViaEmail().token()).apply();
                                editor.putBoolean(Constants.PREFERENCE_IS_LOGGED_IN_KEY, true).apply();

                                startActivity(new Intent(context, MainActivity.class));
                                activity.finish();
                            } else {
                                Log.e("loginViaEmail", "Errors: " + response.getErrors());
                                if (Arrays.toString(response.getErrors().get(0).getCustomAttributes().values().toArray()).contains("code=401")) {
                                    Snackbar snackbar = Snackbar.make(view,
                                            context.getResources().getString(R.string.login_error_credentials),
                                            BaseTransientBottomBar.LENGTH_LONG)
                                            .setBackgroundTint(context.getResources().getColor(R.color.red))
                                            .setTextColor(context.getResources().getColor(R.color.white))
                                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                                    TextView snackText = (snackbar.getView()).findViewById(R.id.snackbar_text);
                                    snackText.setTypeface(ResourcesCompat.getFont(context, R.font.nunito_semi_bold));
                                    snackbar.show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("loginViaEmail", "onFailure: " + e.getMessage());
                        activity.runOnUiThread(() -> {
                            Snackbar snackbar = Snackbar.make(view,
                                    context.getResources().getString(R.string.network_error_failure),
                                    BaseTransientBottomBar.LENGTH_LONG)
                                    .setBackgroundTint(context.getResources().getColor(R.color.red))
                                    .setTextColor(context.getResources().getColor(R.color.white))
                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                            TextView snackText = (snackbar.getView()).findViewById(R.id.snackbar_text);
                            snackText.setTypeface(ResourcesCompat.getFont(context, R.font.nunito_semi_bold));
                            snackbar.show();
                        });
                    }
                });
    }

    private void showLoadingAnimation() {
        loginBinding.btnLoginLogin.setText(null);
        loginBinding.btnLoginLogin.setClickable(false);
        loginBinding.cpiLogin.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        loginBinding.cpiLogin.setVisibility(View.GONE);
        loginBinding.btnLoginLogin.setClickable(true);
        loginBinding.btnLoginLogin.setText(context.getResources().getString(R.string.login_btn_login));
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}