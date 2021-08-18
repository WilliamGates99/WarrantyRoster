package com.xeniac.warrantyroster;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleModifier {

    private final Context mContext;

    public LocaleModifier(Context context) {
        this.mContext = context;
    }

    public void setLocale() {
        SharedPreferences localePrefs = mContext.getSharedPreferences(Constants.PREFERENCE_SETTINGS, Context.MODE_PRIVATE);
        String language = localePrefs.getString(Constants.PREFERENCE_LANGUAGE_KEY, "en");
        String country = localePrefs.getString(Constants.PREFERENCE_COUNTRY_KEY, "US");

        Resources resources = mContext.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        Locale newLocale = new Locale(language, country);
        Locale.setDefault(newLocale);
        configuration.setLocale(newLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }
}