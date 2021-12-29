package com.xeniac.warrantyroster_manager.landingactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.xeniac.warrantyroster_manager.Constants;
import com.xeniac.warrantyroster_manager.LocaleModifier;
import com.xeniac.warrantyroster_manager.R;
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding;
import com.xeniac.warrantyroster_manager.mainactivity.MainActivity;

public class LandingActivity extends AppCompatActivity {

    private ActivityLandingBinding landingBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashScreen();
    }

    private void splashScreen() {
        SplashScreen.installSplashScreen(this);

        SharedPreferences loginPrefs = getSharedPreferences(Constants.PREFERENCE_LOGIN, MODE_PRIVATE);
        boolean isLoggedIn = loginPrefs.getBoolean(Constants.PREFERENCE_IS_LOGGED_IN_KEY, false);

        if (isLoggedIn) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            landingInit();
        }
    }

    private void landingInit() {
        landingBinding = ActivityLandingBinding.inflate(getLayoutInflater());
        setContentView(landingBinding.getRoot());

        LocaleModifier.setLocale(this);
        setTitle();
    }

    private void setTitle() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(landingBinding.fcv.getId());

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getLabel() != null) {
                    if (destination.getLabel().equals("LoginFragment")) {
                        landingBinding.tvTitle.setText(getString(R.string.login_text_title));
                    } else if (destination.getLabel().equals("RegisterFragment")) {
                        landingBinding.tvTitle.setText(getString(R.string.register_text_title));
                    } else if (destination.getLabel().equals("ForgotPwFragment")) {
                        landingBinding.tvTitle.setText(getString(R.string.forgot_pw_text_title));
                    } else if (destination.getLabel().equals("ForgotPwSentFragment")) {
                        landingBinding.tvTitle.setText(getString(R.string.forgot_pw_sent_text_title));
                    }
                }
            });
        }
    }
}