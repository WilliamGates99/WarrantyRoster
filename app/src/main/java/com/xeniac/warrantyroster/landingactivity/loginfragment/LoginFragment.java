package com.xeniac.warrantyroster.landingactivity.loginfragment;

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

public class LoginFragment extends Fragment {

    private FragmentLoginBinding loginBinding;

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

        NavController navController = Navigation.findNavController(view);

        loginBinding.tiLoginEditEmail.setOnFocusChangeListener((view1, focused) -> {
            if (focused) {
                loginBinding.tiLoginLayoutEmail.setBoxBackgroundColorResource(R.color.background);
            } else {
                loginBinding.tiLoginLayoutEmail.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

        loginBinding.tiLoginEditPassword.setOnFocusChangeListener((view1, focused) -> {
            if (focused) {
                loginBinding.tiLoginLayoutPassword.setBoxBackgroundColorResource(R.color.background);
            } else {
                loginBinding.tiLoginLayoutPassword.setBoxBackgroundColorResource(R.color.grayLight);
            }
        });

//        MaterialButton login = view.findViewById(R.id.btn_login_login);
//        login.setOnClickListener(view12 -> {
//            startActivity(new Intent(getContext(), MainActivity.class));
//            getActivity().finish();
//        });
//
//        MaterialButton register = view.findViewById(R.id.btn_login_register);
//        register.setOnClickListener(view1 ->
//                navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
//
//        MaterialButton forgot = view.findViewById(R.id.btn_login_forgot);
//        forgot.setOnClickListener(view1 ->
//                navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
    }
}
