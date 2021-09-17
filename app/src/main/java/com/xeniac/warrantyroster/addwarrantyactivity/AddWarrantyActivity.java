package com.xeniac.warrantyroster.addwarrantyactivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xeniac.warrantyroster.LocaleModifier;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.ActivityAddWarrantyBinding;

import java.util.ArrayList;
import java.util.List;

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

        List<String> categoryList = new ArrayList<>();
        categoryList.add("Accessory");
        categoryList.add("Air Conditioner");
        categoryList.add("Audio Device");
        categoryList.add("Camera");
        categoryList.add("Game Console");
        categoryList.add("Headset");
        categoryList.add("Healthcare");
        categoryList.add("Home Appliance");
        categoryList.add("Memory and Storage");
        categoryList.add("Miscellaneous");
        categoryList.add("Monitor");
        categoryList.add("Musical Instrument");
        categoryList.add("Peripheral Device");
        categoryList.add("Personal Care");
        categoryList.add("Personal Computer");
        categoryList.add("Phone and Tablet");
        categoryList.add("Smart Home");
        categoryList.add("Smart Watch");
        categoryList.add("Sport and Fitness");
        categoryList.add("Tool");
        categoryList.add("Vehicle");

        addWarrantyBinding.tiAddWarrantyDdCategory.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_category, categoryList));
    }

    private void returnToMainActivity() {
        addWarrantyBinding.toolbarAddWarranty.setNavigationOnClickListener(view ->
                AddWarrantyActivity.super.onBackPressed());
    }

    private void addWarrantyOnClick() {
        addWarrantyBinding.toolbarAddWarranty.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            AddWarrantyActivity.super.onNavigateUp();
            return false;
        });
    }
}