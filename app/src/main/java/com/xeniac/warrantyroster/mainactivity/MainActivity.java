package com.xeniac.warrantyroster.mainactivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.xeniac.warrantyroster.LocaleModifier;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.addwarrantyactivity.AddWarrantyActivity;
import com.xeniac.warrantyroster.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        mainInit();
    }

    private void mainInit() {
        LocaleModifier localeModifier = new LocaleModifier(this);
        localeModifier.setLocale();

        bottomAppBarStyle();
        bottomNavActions();
        fabAction();
    }

    private void bottomAppBarStyle() {
        mainBinding.bnvMain.setBackground(null);
        float radius = getResources().getDimension(R.dimen.dimen_bottom_nav_radius);

        MaterialShapeDrawable shapeDrawable = (MaterialShapeDrawable) mainBinding.appbarMain.getBackground();
        shapeDrawable.setShapeAppearanceModel(shapeDrawable.getShapeAppearanceModel()
                .toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                .build());
    }

    private void bottomNavActions() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fcv_main);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(mainBinding.bnvMain, navController);
        }
    }

    private void fabAction() {
        mainBinding.fabMain.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, AddWarrantyActivity.class)));
    }
}