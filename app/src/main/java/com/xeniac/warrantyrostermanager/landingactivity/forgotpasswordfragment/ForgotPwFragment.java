package com.xeniac.warrantyrostermanager.landingactivity.forgotpasswordfragment;

import android.app.Activity;
import android.content.Context;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.xeniac.warrantyrostermanager.Constants;
import com.xeniac.warrantyrostermanager.NetworkHelper;
import com.xeniac.warrantyrostermanager.R;
import com.xeniac.warrantyrostermanager.databinding.FragmentForgotPwBinding;

import java.util.Arrays;

public class ForgotPwFragment extends Fragment {

    private FragmentForgotPwBinding forgotPwBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;

    public ForgotPwFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        forgotPwBinding = FragmentForgotPwBinding.inflate(inflater, container, false);
        view = forgotPwBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        forgotPwBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);

        textInputsBackgroundColor();
        textInputsStrokeColor();
        returnOnClick();
        sendOnClick();
        sendActionDone();
    }

    private void textInputsBackgroundColor() {
        forgotPwBinding.tiForgotPwEditEmail.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                forgotPwBinding.tiForgotPwLayoutEmail.setBoxBackgroundColorResource(R.color.background);
            } else {
                forgotPwBinding.tiForgotPwLayoutEmail.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void textInputsStrokeColor() {
        forgotPwBinding.tiForgotPwEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputEmail, int start, int before, int count) {
                forgotPwBinding.tiForgotPwLayoutEmail.setErrorEnabled(false);
                forgotPwBinding.tiForgotPwLayoutEmail.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void returnOnClick() {
        forgotPwBinding.btnForgotPwReturn.setOnClickListener(view ->
                activity.onBackPressed());
    }

    private void sendOnClick() {
        forgotPwBinding.btnForgotPwSend.setOnClickListener(view ->
                sendResetPasswordEmail());
    }

    private void sendActionDone() {
        forgotPwBinding.tiForgotPwEditEmail.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendResetPasswordEmail();
            }
            return false;
        });
    }

    private void sendResetPasswordEmail() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        if (NetworkHelper.hasNetworkAccess(context)) {
            getResetPasswordInput();
        } else {
            hideLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> sendResetPasswordEmail()).show();
        }
    }

    private void getResetPasswordInput() {
        String email;

        if (TextUtils.isEmpty(forgotPwBinding.tiForgotPwEditEmail.getText())) {
            forgotPwBinding.tiForgotPwLayoutEmail.requestFocus();
            forgotPwBinding.tiForgotPwLayoutEmail.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else {
            email = forgotPwBinding.tiForgotPwEditEmail.getText().toString().trim().toLowerCase();

            if (!isEmailValid(email)) {
                forgotPwBinding.tiForgotPwLayoutEmail.requestFocus();
                forgotPwBinding.tiForgotPwLayoutEmail.setError(context.getResources().getString(R.string.forgot_pw_error_email));
            } else {
                sendResetPasswordEmailMutation(email);
            }
        }
    }

    private void sendResetPasswordEmailMutation(String email) {
        showLoadingAnimation();

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new SendResetPasswordEmailMutation(email))
                .enqueue(new ApolloCall.Callback<SendResetPasswordEmailMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<SendResetPasswordEmailMutation.Data> response) {
                        if (forgotPwBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("sendResetPasswordEmail", "onResponse: " + response);
                                    ForgotPwFragmentDirections.ActionForgotPasswordFragmentToForgotPwSentFragment action =
                                            ForgotPwFragmentDirections.actionForgotPasswordFragmentToForgotPwSentFragment(email);
                                    navController.navigate(action);
                                } else {
                                    Log.e("sendResetPasswordEmail", "onResponse Errors: " + response.getErrors());
                                    if (Arrays.toString(response.getErrors().get(0).getCustomAttributes().values().toArray()).contains("code=500")) {
                                        Snackbar.make(view, context.getResources().getString(R.string.forgot_pw_error_not_found),
                                                BaseTransientBottomBar.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Snackbar.make(view, context.getResources().getString(R.string.network_error_response),
                                                BaseTransientBottomBar.LENGTH_INDEFINITE)
                                                .setAction(context.getResources().getString(R.string.network_error_retry),
                                                        v -> sendResetPasswordEmail()).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("sendResetPasswordEmail", "onFailure: " + e.getMessage());
                        if (forgotPwBinding != null) {
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
        forgotPwBinding.tiForgotPwEditEmail.setEnabled(false);
        forgotPwBinding.btnForgotPwSend.setClickable(false);
        forgotPwBinding.btnForgotPwSend.setText(null);
        forgotPwBinding.cpiForgotPw.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        forgotPwBinding.cpiForgotPw.setVisibility(View.GONE);
        forgotPwBinding.tiForgotPwEditEmail.setEnabled(true);
        forgotPwBinding.btnForgotPwSend.setClickable(true);
        forgotPwBinding.btnForgotPwSend.setText(context.getResources().getString(R.string.login_btn_login));
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}