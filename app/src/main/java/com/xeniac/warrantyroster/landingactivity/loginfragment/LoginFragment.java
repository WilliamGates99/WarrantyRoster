package com.xeniac.warrantyroster.landingactivity.loginfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.xeniac.warrantyroster.databinding.FragmentLoginBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding loginBinding;
    private Activity activity;
    private Context context;
    private NavController navController;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        return loginBinding.getRoot();
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

        textInputBackground();
        loginOnClick();
        registerOnClick();
        forgotPwOnClick();
    }

    private void textInputBackground() {
        loginBinding.tiLoginEditEmail.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                loginBinding.tiLoginLayoutEmail.setBoxBackgroundColorResource(R.color.background);
            } else {
                loginBinding.tiLoginLayoutEmail.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        loginBinding.tiLoginEditPassword.setOnFocusChangeListener((view, focused) -> {
            if (focused) {
                loginBinding.tiLoginLayoutPassword.setBoxBackgroundColorResource(R.color.background);
            } else {
                loginBinding.tiLoginLayoutPassword.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void forgotPwOnClick() {
        loginBinding.tvLoginForgotPw.setOnClickListener(view ->
                navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
    }

    private void loginOnClick() {
        loginBinding.btnLoginLogin.setOnClickListener(view -> {
            startActivity(new Intent(context, MainActivity.class));
            activity.finish();
        });
    }

    private void registerOnClick() {
        loginBinding.tvLoginRegister.setOnClickListener(view ->
                navController.navigate(R.id.action_loginFragment_to_registerFragment));
    }
}