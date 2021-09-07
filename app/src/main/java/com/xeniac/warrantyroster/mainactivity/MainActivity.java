package com.xeniac.warrantyroster.mainactivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xeniac.warrantyroster.LocaleModifier;
import com.xeniac.warrantyroster.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
    }

    private void mainInit() {
        LocaleModifier localeModifier = new LocaleModifier(this);
        localeModifier.setLocale();
    }
}