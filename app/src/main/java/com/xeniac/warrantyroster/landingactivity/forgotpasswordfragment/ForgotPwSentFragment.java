package com.xeniac.warrantyroster.landingactivity.forgotpasswordfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.xeniac.warrantyroster.databinding.FragmentForgotPwSentBinding;

import java.text.DecimalFormat;
import java.util.Arrays;

public class ForgotPwSentFragment extends Fragment {

    private FragmentForgotPwSentBinding forgotPwSentBinding;
    private View view;
    private Activity activity;
    private Context context;

    private String email;
    private CountDownTimer countDownTimer;

    public ForgotPwSentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        forgotPwSentBinding = FragmentForgotPwSentBinding.inflate(inflater, container, false);
        view = forgotPwSentBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        forgotPwSentBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();

        getEmailFromArgs();
        returnOnClick();
        resendOnClick();
    }

    private void getEmailFromArgs() {
        if (getArguments() != null) {
            ForgotPwSentFragmentArgs args = ForgotPwSentFragmentArgs.fromBundle(getArguments());
            email = args.getEmail();
        }
    }

    private void returnOnClick() {
        forgotPwSentBinding.btnForgotPwSentReturn.setOnClickListener(view ->
                activity.onBackPressed());
    }

    private void resendOnClick() {
        forgotPwSentBinding.btnForgotPwSentResend.setOnClickListener(view ->
                resendResetPasswordEmail());
    }

    private void resendResetPasswordEmail() {
        if (NetworkHelper.hasNetworkAccess(context)) {
            resendResetPasswordEmailMutation();
        } else {
            hideLoadingAnimation();
            Snackbar.make(view, context.getResources().getString(R.string.network_error_connection),
                    BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(context.getResources().getString(R.string.network_error_retry),
                            v -> resendResetPasswordEmail()).show();
        }
    }

    private void resendResetPasswordEmailMutation() {
        showLoadingAnimation();

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(Constants.URL_GRAPHQL)
                .build();

        apolloClient.mutate(new SendResetPasswordEmailMutation(email))
                .enqueue(new ApolloCall.Callback<SendResetPasswordEmailMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<SendResetPasswordEmailMutation.Data> response) {
                        if (forgotPwSentBinding != null) {
                            activity.runOnUiThread(() -> {
                                hideLoadingAnimation();

                                if (!response.hasErrors()) {
                                    Log.i("resendResetPwEmail", "onResponse: " + response);
                                    countdown();
                                    forgotPwSentBinding.lavForgotPwSent.playAnimation();
                                } else {
                                    Log.e("resendResetPwEmail", "onResponse Errors: " + response.getErrors());
                                    if (Arrays.toString(response.getErrors().get(0).getCustomAttributes().values().toArray()).contains("code=500")) {
                                        Snackbar.make(view, context.getResources().getString(R.string.forgot_pw_sent_error_wait),
                                                BaseTransientBottomBar.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Snackbar.make(view, context.getResources().getString(R.string.network_error_response),
                                                BaseTransientBottomBar.LENGTH_INDEFINITE)
                                                .setAction(context.getResources().getString(R.string.network_error_retry),
                                                        v -> resendResetPasswordEmail()).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        Log.e("resendResetPwEmail", "onFailure: " + e.getMessage());
                        if (forgotPwSentBinding != null) {
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
        forgotPwSentBinding.btnForgotPwSentResend.setVisibility(View.GONE);
        forgotPwSentBinding.cpiForgotPwSent.setVisibility(View.VISIBLE);
    }

    private void hideLoadingAnimation() {
        forgotPwSentBinding.cpiForgotPwSent.setVisibility(View.GONE);
        forgotPwSentBinding.btnForgotPwSentResend.setVisibility(View.VISIBLE);
    }

    private void countdown() {
        forgotPwSentBinding.groupForgotPwSentResend.setVisibility(View.GONE);
        forgotPwSentBinding.groupForgotPwSentTimer.setVisibility(View.VISIBLE);

        DecimalFormat decimalFormat = new DecimalFormat("00");
        int startTime = 120 * 1000;

        countDownTimer = new CountDownTimer(startTime, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished / 1000) % 60;

                forgotPwSentBinding.tvForgotPwSentTimer.setText(String.format("(%s:%s)",
                        decimalFormat.format(minutes), decimalFormat.format(seconds)));
            }

            @Override
            public void onFinish() {
                new Handler().postDelayed(() -> {
                    forgotPwSentBinding.groupForgotPwSentTimer.setVisibility(View.GONE);
                    forgotPwSentBinding.groupForgotPwSentResend.setVisibility(View.VISIBLE);
                }, 500);
            }
        }.start();
    }
}