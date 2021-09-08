package com.xeniac.warrantyroster.mainactivity.warrantiesfragment;

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

import com.xeniac.warrantyroster.databinding.FragmentWarrantiesBinding;

public class WarrantiesFragment extends Fragment {

    private FragmentWarrantiesBinding warrantiesBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;

    public WarrantiesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        warrantiesBinding = FragmentWarrantiesBinding.inflate(inflater, container, false);
        view = warrantiesBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        warrantiesBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);
    }
}