package com.xeniac.warrantyroster.landingactivity.registerfragment;

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

import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.FragmentRegisterBinding;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding registerBinding;
    private Activity activity;
    private Context context;

    public RegisterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        return registerBinding.getRoot();
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

        textInputBackground();
        registerOnClick();
        loginOnClick();
    }

    private void textInputBackground() {
        registerBinding.tiRegisterEditEmail.setOnFocusChangeListener((view1, focused) -> {
            if (focused) {
                registerBinding.tiRegisterLayoutEmail.setBoxBackgroundColorResource(R.color.background);
            } else {
                registerBinding.tiRegisterLayoutEmail.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        registerBinding.tiRegisterEditPassword.setOnFocusChangeListener((view1, focused) -> {
            if (focused) {
                registerBinding.tiRegisterLayoutPassword.setBoxBackgroundColorResource(R.color.background);
            } else {
                registerBinding.tiRegisterLayoutPassword.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        registerBinding.tiRegisterEditRetypePassword.setOnFocusChangeListener((view1, focused) -> {
            if (focused) {
                registerBinding.tiRegisterLayoutRetypePassword.setBoxBackgroundColorResource(R.color.background);
            } else {
                registerBinding.tiRegisterLayoutRetypePassword.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });
    }

    private void registerOnClick() {
        registerBinding.btnRegisterLogin.setOnClickListener(view12 -> {
            startActivity(new Intent(context, MainActivity.class));
            activity.finish();
        });
    }

    private void loginOnClick() {
        registerBinding.tvRegisterRegister.setOnClickListener(view1 ->
                activity.onBackPressed());
    }
}