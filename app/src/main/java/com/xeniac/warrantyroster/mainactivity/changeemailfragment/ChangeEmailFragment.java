package com.xeniac.warrantyroster.mainactivity.changeemailfragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.apollographql.apollo.request.RequestHeaders;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.NetworkHelper;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.FragmentChangeEmailBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

import java.util.Arrays;

public class ChangeEmailFragment extends Fragment {

    private FragmentChangeEmailBinding changeEmailBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;
    private String currentEmail;

    public ChangeEmailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        changeEmailBinding = FragmentChangeEmailBinding.inflate(inflater, container, false);
        view = changeEmailBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        changeEmailBinding = null;
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
        getAccountEmail();
        changeEmailOnClick();
        changeEmailActionDone();
    }

    private void textInputsBackgroundColor() {
        changeEmailBinding.tiChangeEmailEditEmail.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                changeEmailBinding.tiChangeEmailLayoutEmail.setBoxBackgroundColorResource(R.color.background);
            } else {
                changeEmailBinding.tiChangeEmailLayoutEmail.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void textInputsStrokeColor() {
        changeEmailBinding.tiChangeEmailEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputEmail, int start, int before, int count) {
                changeEmailBinding.tiChangeEmailLayoutEmail.setErrorEnabled(false);
                changeEmailBinding.tiChangeEmailLayoutEmail.setBoxStrokeColor(context.getResources().getColor(R.color.blue));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void returnToMainActivity() {
        changeEmailBinding.toolbarChangeEmail.setNavigationOnClickListener(view ->
                activity.onBackPressed());
    }

    private void getAccountEmail() {
        if (getArguments() != null) {
            ChangeEmailFragmentArgs args = ChangeEmailFragmentArgs.fromBundle(getArguments());
            currentEmail = args.getAccountEmail();
        }
    }

    private void changeEmailOnClick() {
        changeEmailBinding.btnChangeEmail.setOnClickListener(view ->
                changeUserEmail());
    }

    private void changeEmailActionDone() {
        changeEmailBinding.tiChangeEmailEditEmail.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changeUserEmail();
            }
            return false;
        });
    }

    private void changeUserEmail() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        if (NetworkHelper.hasNetworkAccess(context)) {
            getChangeUserEmailInputs();
        } else {
            hideLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry), v -> changeUserEmail()).show();
        }
    }

    private void getChangeUserEmailInputs() {
        String newEmail;

        if (TextUtils.isEmpty(changeEmailBinding.tiChangeEmailEditEmail.getText())) {
            changeEmailBinding.tiChangeEmailLayoutEmail.requestFocus();
            changeEmailBinding.tiChangeEmailLayoutEmail.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        } else {
            newEmail = changeEmailBinding.tiChangeEmailEditEmail.getText().toString().trim().toLowerCase();

            if (!isEmailValid(newEmail)) {
                changeEmailBinding.tiChangeEmailLayoutEmail.requestFocus();
                changeEmailBinding.tiChangeEmailLayoutEmail.setError(context.getResources().getString(R.string.change_email_error_email));
            } else if (newEmail.equals(currentEmail)) {
                changeEmailBinding.tiChangeEmailLayoutEmail.requestFocus();
                changeEmailBinding.tiChangeEmailLayoutEmail.setError(context.getResources().getString(R.string.change_email_error_email_same));
            } else {
                changeUserEmailMutation(newEmail);
            }
        }
    }

    private void changeUserEmailMutation(String newEmail) {
        showLoadingAnimation();

        SharedPreferences loginPrefs = context.getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        String userToken = loginPrefs.getString(Constants.PREFERENCE_USER_TOKEN_KEY, null);

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new ChangeUserEmailMutation(newEmail))
                .toBuilder().requestHeaders(RequestHeaders.builder().addHeader("Authorization", "Bearer " + userToken).build())
                .build()
                .enqueue(new ApolloCall.Callback<ChangeUserEmailMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<ChangeUserEmailMutation.Data> response) {
                        if (changeEmailBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("changeUserEmail", "onResponse: " + response);
                                    MaterialAlertDialogBuilder changeUserEmailDialogBuilder = new MaterialAlertDialogBuilder(context)
                                            .setMessage(context.getResources().getString(R.string.change_email_dialog_message))
                                            .setPositiveButton(context.getResources().getString(R.string.change_email_dialog_positive), (dialog, which) -> {
                                            })
                                            .setOnDismissListener(dialog -> activity.onBackPressed());
                                    changeUserEmailDialogBuilder.show();
                                } else {
                                    Log.e("changeUserEmail", "onResponse Errors: " + response.getErrors());
                                    if (Arrays.toString(response.getErrors().get(0).getCustomAttributes().values().toArray()).contains("already exists")) {
                                        Snackbar.make(view, context.getResources().getString(R.string.change_email_error_email_exists),
                                                BaseTransientBottomBar.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Snackbar.make(view, context.getResources().getString(R.string.network_error_response),
                                                BaseTransientBottomBar.LENGTH_INDEFINITE)
                                                .setAction(context.getResources().getString(R.string.network_error_retry), v -> changeUserEmail()).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("changeUserEmail", "onFailure: " + e.getMessage());
                        if (changeEmailBinding != null) {
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
        changeEmailBinding.tiChangeEmailEditEmail.setEnabled(false);
        changeEmailBinding.btnChangeEmail.setClickable(false);
        changeEmailBinding.btnChangeEmail.setText(null);
        changeEmailBinding.cpiChangeEmail.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        changeEmailBinding.cpiChangeEmail.setVisibility(View.GONE);
        changeEmailBinding.tiChangeEmailEditEmail.setEnabled(true);
        changeEmailBinding.btnChangeEmail.setClickable(true);
        changeEmailBinding.btnChangeEmail.setText(context.getResources().getString(R.string.change_email_btn_change));
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}