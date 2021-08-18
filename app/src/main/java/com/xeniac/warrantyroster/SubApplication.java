package com.xeniac.warrantyroster;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class SubApplication extends Application {

    private SharedPreferences settingsPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        setNightMode();
        setLocale();
    }

    private void setNightMode() {
        settingsPrefs = getSharedPreferences(Constants.PREFERENCE_SETTINGS, MODE_PRIVATE);
        int nightMode = settingsPrefs.getInt(Constants.PREFERENCE_NIGHT_MODE_KEY, 0);

        switch (nightMode) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    private void setLocale() {
        settingsPrefs = getSharedPreferences(Constants.PREFERENCE_SETTINGS, MODE_PRIVATE);
        String language = settingsPrefs.getString(Constants.PREFERENCE_LANGUAGE_KEY, "en");
        String country = settingsPrefs.getString(Constants.PREFERENCE_COUNTRY_KEY, "US");

        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        Locale newLocale = new Locale(language, country);
        Locale.setDefault(newLocale);
        configuration.setLocale(newLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }
}