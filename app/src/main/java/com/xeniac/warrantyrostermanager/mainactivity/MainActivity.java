package com.xeniac.warrantyrostermanager.mainactivity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.xeniac.warrantyrostermanager.Constants;
import com.xeniac.warrantyrostermanager.LocaleModifier;
import com.xeniac.warrantyrostermanager.R;
import com.xeniac.warrantyrostermanager.databinding.ActivityMainBinding;

import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.plus.model.TapsellPlusAdModel;
import ir.tapsell.plus.model.TapsellPlusErrorModel;

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

    public void requestInterstitialAd() {
        TapsellPlus.requestInterstitialAd(this, Constants.DELETE_WARRANTY_Interstitial_ZONE_ID,
                new AdRequestCallback() {
                    @Override
                    public void response(TapsellPlusAdModel tapsellPlusAdModel) {
                        super.response(tapsellPlusAdModel);
                        Log.i("requestInterstitialAd", "response: " + tapsellPlusAdModel.toString());
                        showInterstitialAd(tapsellPlusAdModel.getResponseId());
                    }

                    @Override
                    public void error(String s) {
                        super.error(s);
                        Log.e("requestInterstitialAd", "error: " + s);
                    }
                });
    }

    private void showInterstitialAd(String responseId) {
        TapsellPlus.showInterstitialAd(this, responseId, new AdShowListener() {
            @Override
            public void onOpened(TapsellPlusAdModel tapsellPlusAdModel) {
                super.onOpened(tapsellPlusAdModel);
                Log.i("showInterstitialAd", "onOpened: " + tapsellPlusAdModel);
            }

            @Override
            public void onClosed(TapsellPlusAdModel tapsellPlusAdModel) {
                super.onClosed(tapsellPlusAdModel);
                Log.i("showInterstitialAd", "onClosed: " + tapsellPlusAdModel);
            }

            @Override
            public void onError(TapsellPlusErrorModel tapsellPlusErrorModel) {
                super.onError(tapsellPlusErrorModel);
                Log.e("showInterstitialAd", "onError: " + tapsellPlusErrorModel);
            }
        });
    }
}