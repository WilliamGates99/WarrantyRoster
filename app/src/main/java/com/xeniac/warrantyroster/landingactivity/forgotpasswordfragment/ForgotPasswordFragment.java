package com.xeniac.warrantyroster.landingactivity.forgotpasswordfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.FragmentForgotPasswordBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding forgotPasswordBinding;
    private Activity activity;
    private Context context;
    private NavController navController;

    public ForgotPasswordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        forgotPasswordBinding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return forgotPasswordBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        forgotPasswordBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);

        textInputBackground();
        sendOnClick();
        returnOnClick();
    }

    private void textInputBackground() {
        forgotPasswordBinding.tiForgotPwEditEmail.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                forgotPasswordBinding.tiForgotPwLayoutEmail.setBoxBackgroundColorResource(R.color.background);
            } else {
                forgotPasswordBinding.tiForgotPwLayoutEmail.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void sendOnClick() {
        forgotPasswordBinding.btnForgotPwSend.setOnClickListener(view -> {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
//                navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });
    }

    private void returnOnClick() {
        forgotPasswordBinding.btnForgotPwReturn.setOnClickListener(view ->
                activity.onBackPressed());
    }
}