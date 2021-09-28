package com.xeniac.warrantyroster.mainactivity;

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
import com.xeniac.warrantyroster.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        mainInit();
    }

    private void mainInit() {
        LocaleModifier.setLocale(this);
        bottomAppBarStyle();
        bottomNavActions();
        fabOnClick();
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
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(mainBinding.bnvMain, navController);
        }
    }

    private void fabOnClick() {
        mainBinding.fabMain.setOnClickListener(view ->
                navController.navigate(R.id.action_mainActivity_to_addWarrantyFragment));
    }

    public void showNavBar() {
        mainBinding.appbarMain.performShow();
        mainBinding.fabMain.show();
    }

    public void hideNavBar() {
        mainBinding.fabMain.hide();
        mainBinding.appbarMain.performHide();
    }
}