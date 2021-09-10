package com.xeniac.warrantyroster.landingactivity.forgotpasswordfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.FragmentForgotPwBinding;

public class ForgotPwFragment extends Fragment {

    private FragmentForgotPwBinding forgotPwBinding;
    private Activity activity;
    private Context context;
    private NavController navController;

    public ForgotPwFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        forgotPwBinding = FragmentForgotPwBinding.inflate(inflater, container, false);
        return forgotPwBinding.getRoot();
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
        sendOnClick();
        returnOnClick();
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

    private void sendOnClick() {
        forgotPwBinding.btnForgotPwSend.setOnClickListener(view ->
                navController.navigate(R.id.action_forgotPasswordFragment_to_forgotPwSentFragment));
    }

    private void returnOnClick() {
        forgotPwBinding.btnForgotPwReturn.setOnClickListener(view ->
                activity.onBackPressed());
    }
}