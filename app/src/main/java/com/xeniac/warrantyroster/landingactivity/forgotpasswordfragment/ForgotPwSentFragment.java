package com.xeniac.warrantyroster.landingactivity.forgotpasswordfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.FragmentForgotPwSentBinding;

import java.text.DecimalFormat;

public class ForgotPwSentFragment extends Fragment {

    private FragmentForgotPwSentBinding forgotPwSentBinding;
    private Activity activity;
    private Context context;
    private NavController navController;

    private CountDownTimer countDownTimer;

    public ForgotPwSentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        forgotPwSentBinding = FragmentForgotPwSentBinding.inflate(inflater, container, false);
        return forgotPwSentBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        countDownTimer.cancel();
        forgotPwSentBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);

        resendOnClick();
        returnOnClick();
    }

    private void resendOnClick() {
        forgotPwSentBinding.btnForgotPwSentResend.setOnClickListener(view -> {
            countdown();
            forgotPwSentBinding.lavForgotPwSent.playAnimation();
        });
    }

    private void returnOnClick() {
        forgotPwSentBinding.btnForgotPwSentReturn.setOnClickListener(view ->
                activity.onBackPressed());
    }

    private void countdown() {
        forgotPwSentBinding.tvForgotPwSentResend.setVisibility(View.GONE);
        forgotPwSentBinding.btnForgotPwSentResend.setVisibility(View.GONE);
        forgotPwSentBinding.tvForgotPwSentResent.setVisibility(View.VISIBLE);
        forgotPwSentBinding.tvForgotPwSentTimer.setVisibility(View.VISIBLE);

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
                    forgotPwSentBinding.tvForgotPwSentResent.setVisibility(View.GONE);
                    forgotPwSentBinding.tvForgotPwSentTimer.setVisibility(View.GONE);
                    forgotPwSentBinding.tvForgotPwSentResend.setVisibility(View.VISIBLE);
                    forgotPwSentBinding.btnForgotPwSentResend.setVisibility(View.VISIBLE);
                }, 500);
            }
        }.start();
    }
}