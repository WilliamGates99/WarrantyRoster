package com.xeniac.warrantyroster.addwarrantyactivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xeniac.warrantyroster.LocaleModifier;
import com.xeniac.warrantyroster.databinding.ActivityAddWarrantyBinding;

public class AddWarrantyActivity extends AppCompatActivity {

    private ActivityAddWarrantyBinding addWarrantyBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addWarrantyBinding = ActivityAddWarrantyBinding.inflate(getLayoutInflater());
        setContentView(addWarrantyBinding.getRoot());
        addWarrantyInit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void addWarrantyInit() {
        LocaleModifier localeModifier = new LocaleModifier(this);
        localeModifier.setLocale();

        returnToMainActivity();
        addWarrantyOnClick();
    }

    private void returnToMainActivity() {
        addWarrantyBinding.toolbarWarranties.setNavigationOnClickListener(view ->
                AddWarrantyActivity.super.onBackPressed());
    }

    private void addWarrantyOnClick() {
        addWarrantyBinding.toolbarWarranties.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            AddWarrantyActivity.super.onNavigateUp();
            return false;
        });
    }
}