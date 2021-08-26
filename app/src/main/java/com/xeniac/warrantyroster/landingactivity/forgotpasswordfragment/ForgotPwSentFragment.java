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

import com.xeniac.warrantyroster.databinding.FragmentForgotPwSentBinding;

public class ForgotPwSentFragment extends Fragment {

    private FragmentForgotPwSentBinding forgotPwSentBinding;
    private Activity activity;
    private Context context;
    private NavController navController;

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
        forgotPwSentBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);
    }
}