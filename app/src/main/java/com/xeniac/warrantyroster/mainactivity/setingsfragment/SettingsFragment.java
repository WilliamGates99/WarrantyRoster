package com.xeniac.warrantyroster.mainactivity.setingsfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.xeniac.warrantyroster.Constants;
import com.xeniac.warrantyroster.R;
import com.xeniac.warrantyroster.databinding.FragmentSettingsBinding;
import com.xeniac.warrantyroster.landingactivity.LandingActivity;
import com.xeniac.warrantyroster.mainactivity.MainActivity;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding settingsBinding;
    private View view;
    private Activity activity;
    private Context context;
    private NavController navController;

    private SharedPreferences settingsPrefs;
    private String currentLanguage;
    private String currentCountry;
    private int currentTheme;

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        view = settingsBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        settingsBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        context = getContext();
        navController = Navigation.findNavController(view);
        ((MainActivity) context).showNavBar();

        settingsPrefs = context.getSharedPreferences(Constants.PREFERENCE_SETTINGS, Context.MODE_PRIVATE);
        currentLanguage = settingsPrefs.getString(Constants.PREFERENCE_LANGUAGE_KEY, "en");
        currentCountry = settingsPrefs.getString(Constants.PREFERENCE_COUNTRY_KEY, "US");
        currentTheme = settingsPrefs.getInt(Constants.PREFERENCE_THEME_KEY, 0);

        setCurrentLanguageText();
        setCurrentThemeText(currentTheme);
        verifyOnClick();
        changeEmailOnClick();
        changePasswordOnClick();
        languageOnClick();
        themeOnClick();
        privacyPolicyOnClick();
        logoutOnClick();
    }

    private void setCurrentLanguageText() {
        switch (currentLanguage) {
            case "en":
                settingsBinding.tvSettingsSettingsLanguageCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_language_english));
                break;
            case "fa":
                settingsBinding.tvSettingsSettingsLanguageCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_language_persian));
                break;
        }
    }

    private void setCurrentThemeText(int currentTheme) {
        switch (currentTheme) {
            case 0:
                settingsBinding.tvSettingsSettingsThemeCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_theme_default));
                break;
            case 1:
                settingsBinding.tvSettingsSettingsThemeCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_theme_light));
                break;
            case 2:
                settingsBinding.tvSettingsSettingsThemeCurrent.setText(
                        context.getResources().getString(R.string.settings_text_settings_theme_dark));
                break;
        }
    }

    private void verifyOnClick() {
        settingsBinding.btnSettingsAccountEmail.setOnClickListener(view -> {
            Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
            settingsBinding.btnSettingsAccountEmail.setClickable(false);
            settingsBinding.btnSettingsAccountEmail.setText(context.getResources().getString(R.string.settings_btn_account_verified));
            settingsBinding.btnSettingsAccountEmail.setTextColor(context.getResources().getColor(R.color.green));
            settingsBinding.btnSettingsAccountEmail.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green20)));
            settingsBinding.ivSettingsAccountEmail.setBackgroundColor(context.getResources().getColor(R.color.green10));
            settingsBinding.lavSettingsAccountEmail.setRepeatCount(0);
            settingsBinding.lavSettingsAccountEmail.setSpeed(0.60f);
            settingsBinding.lavSettingsAccountEmail.setAnimation(R.raw.anim_account_verified);
            settingsBinding.lavSettingsAccountEmail.playAnimation();
        });
    }

    private void changeEmailOnClick() {
        settingsBinding.clSettingsAccountChangeEmail.setOnClickListener(view ->
                navController.navigate(R.id.action_settingsFragment_to_changeEmailFragment));
    }

    private void changePasswordOnClick() {
        settingsBinding.clSettingsAccountChangePassword.setOnClickListener(view ->
                navController.navigate(R.id.action_settingsFragment_to_changePasswordFragment));
    }

    private void languageOnClick() {
        settingsBinding.clSettingsSettingsLanguage.setOnClickListener(view ->
                Toast.makeText(context, "Language", Toast.LENGTH_SHORT).show());
    }

    private void themeOnClick() {
        settingsBinding.clSettingsSettingsTheme.setOnClickListener(view -> {

            String[] themeItems = new String[]{context.getResources().getString(R.string.settings_text_settings_theme_default),
                    context.getResources().getString(R.string.settings_text_settings_theme_light),
                    context.getResources().getString(R.string.settings_text_settings_theme_dark)};

            MaterialAlertDialogBuilder themeDialogBuilder = new MaterialAlertDialogBuilder(context)
                    .setTitle(context.getResources().getString(R.string.settings_text_settings_theme))
                    .setSingleChoiceItems(themeItems, currentTheme, (dialogInterface, i) -> {
                        switch (i) {
                            case 0:
                                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                                    settingsPrefs.edit().putInt(Constants.PREFERENCE_THEME_KEY, i).apply();
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                }
                                break;
                            case 1:
                                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                                    settingsPrefs.edit().putInt(Constants.PREFERENCE_THEME_KEY, i).apply();
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                }
                                break;
                            case 2:
                                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                                    settingsPrefs.edit().putInt(Constants.PREFERENCE_THEME_KEY, i).apply();
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                }
                                break;
                        }
                        setCurrentThemeText(i);
                        dialogInterface.dismiss();
                    });
            themeDialogBuilder.show();
        });
    }

    private void privacyPolicyOnClick() {
        settingsBinding.clSettingsSettingsPrivacyPolicy.setOnClickListener(view ->
                Toast.makeText(context, "Privacy Policy", Toast.LENGTH_SHORT).show());
    }

    private void logoutOnClick() {
        settingsBinding.btnSettingsLogout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = context
                    .getSharedPreferences(Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE).edit();
            editor.remove(Constants.PREFERENCE_USER_TOKEN_KEY).apply();
            editor.remove(Constants.PREFERENCE_IS_LOGGED_IN_KEY).apply();

            startActivity(new Intent(context, LandingActivity.class));
            activity.finish();
        });
    }
}