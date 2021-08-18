package com.xeniac.warrantyroster.landingactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.xeniac.warrantyroster.databinding.ActivityLandingBinding;

public class LandingActivity extends AppCompatActivity {

    ActivityLandingBinding landingBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        landingBinding = ActivityLandingBinding.inflate(getLayoutInflater());
        View view = landingBinding.getRoot();
        setContentView(view);
    }
}