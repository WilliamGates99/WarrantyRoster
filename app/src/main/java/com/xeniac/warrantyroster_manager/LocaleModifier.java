package com.xeniac.warrantyroster_manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleModifier {

    public static void setLocale(Context context) {
        SharedPreferences localePrefs = context.getSharedPreferences(Constants.PREFERENCE_SETTINGS, Context.MODE_PRIVATE);
        String language = localePrefs.getString(Constants.PREFERENCE_LANGUAGE_KEY, "en");
        String country = localePrefs.getString(Constants.PREFERENCE_COUNTRY_KEY, "US");

        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        Locale newLocale = new Locale(language, country);
        Locale.setDefault(newLocale);
        configuration.setLocale(newLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }
}